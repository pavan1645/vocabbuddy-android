package com.example.vocabbuddy.Learn

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.MotionEvent
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.AnticipateInterpolator
import android.widget.LinearLayout
import androidx.core.animation.doOnEnd
import com.example.vocabbuddy.Models.Section
import com.example.vocabbuddy.Models.Word
import com.example.vocabbuddy.Practice.PracticeSection
import com.example.vocabbuddy.R
import com.example.vocabbuddy.VocabDb
import kotlinx.android.synthetic.main.activity_learn_section.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Math.abs
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

class LearnSection : AppCompatActivity() {
    private val learningWords: MutableList<Word> = ArrayList()
    private val reviewingWords: MutableList<Word> = ArrayList()
    private val masteredWords: MutableList<Word> = ArrayList()
    private lateinit var allWords: List<Word>
    private lateinit var section: Section
    private lateinit var questionWord: Word
    private var selectFromMaster: Boolean = false;
    private lateinit var tts: TextToSpeech;
    private var onBoardingPhase = 0;

    private lateinit var vocabDb: VocabDb

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learn_section)
        window.statusBarColor = resources.getColor(R.color.SuccessText, null);

        vocabDb = VocabDb(this)
        val sectionId = intent.extras?.getInt("id") ?: 0
        getWords(sectionId)

        /* Initialize TTS Engine */
        tts = TextToSpeech(applicationContext, TextToSpeech.OnInitListener { status ->
            if (status != TextToSpeech.ERROR) {
                tts.language = Locale.US
                tts.setSpeechRate(0.75f)
            }
        })

        /* Start Onboarding */
        val sharedPreferences = getSharedPreferences(getString(R.string.global_pref), Context.MODE_PRIVATE);
        val onBoardingCompleted =  sharedPreferences.getBoolean(getString(R.string.onboarding_complete), false);
        animateOnboardingText()
        if (!onBoardingCompleted) onBoardUser();

        /* Set Explicit Click Listeners */
        remember_btn.setOnClickListener { wordRemembered() }
        not_remember_btn.setOnClickListener { wordNotRemembered() }
        back_btn.setOnClickListener { finish() }
        help_btn.setOnClickListener {
            onBoardingPhase = 0;
            sharedPreferences.edit().putBoolean(getString(R.string.onboarding_complete), false).apply()
            onBoardUser()
        }

        /* Handle Touch Events */
        setTouchListener()
    }

    override fun onBackPressed() {
        if (overlay.visibility == View.VISIBLE) closeOverlay(close_overlay_btn)
        else super.onBackPressed()
    }

    private fun getWords(sectionId: Int) {
        GlobalScope.launch {
            allWords = vocabDb.WordDao().getAllWords(sectionId)
            section = vocabDb.SectionDao().getSectionById(sectionId)

            learningWords.clear()
            reviewingWords.clear()
            masteredWords.clear()

            for (word in allWords) {
                when (word.learning_status) {
                    0 -> learningWords.add(word)
                    1 -> reviewingWords.add(word)
                    else -> masteredWords.add(word)
                }
            }
            runOnUiThread {
                setProgressBarValues()
                generateWordCard()
                activity_title.text = getString(R.string.learn, section.name)
            }
        }
    }

    private fun generateWordCard() {
        /* If all words are mastered */
        if (learningWords.size + reviewingWords.size == 0) {
            display_card.visibility = View.GONE
            quiz_card.visibility = View.VISIBLE;
            quiz_card.animate().alpha(1f);
            if (section.progress_status != 2) setSectionStatus(2);
            return
        }

        /* If only 1 word is yet to be mastered, select every alternative word from mastered word list */
        if (learningWords.size + reviewingWords.size == 1) {
            selectFromMaster = !selectFromMaster;
            if (selectFromMaster) questionWord = masteredWords[Random.nextInt(0, masteredWords.size)]
        }

        /* Select random word from learning and reviewing word list */
        if (learningWords.size + reviewingWords.size > 1 || !selectFromMaster) {
            val currWordIndex = Random.nextInt(0, learningWords.size + reviewingWords.size)
            if (currWordIndex < learningWords.size) questionWord = learningWords[currWordIndex]
            else questionWord = reviewingWords[currWordIndex - learningWords.size]
        }

        /* Set card text values */
        word_text.text = questionWord.word
        definition.text = questionWord.definition
        type_phonetic.text = getString(R.string.type_dot_phonetic, questionWord.type, questionWord.phonetic)
        definition.text = questionWord.definition

        /* Overlay layout's values */
        word_text_2.text = questionWord.word
        definition_2.text = questionWord.definition
        type_phonetic_2.text = getString(R.string.type_dot_phonetic, questionWord.type, questionWord.phonetic)
        example_text.text = questionWord.example
        origin_text.text = questionWord.origin

        GlobalScope.launch {
            val synonyms = vocabDb.SynonymsDao().getSynonyms(questionWord.id)
            runOnUiThread { synonyms_text.text = synonyms.toString().replace("[", "").replace("]", "") }
        }

        resetCard()
    }

    /* Hide definitions and buttons with aniamtions */
    private fun resetCard() {
        full_screen_btn.visibility = View.GONE
        word_wrapper.apply {
            this.layoutParams.apply {
                this as LinearLayout.LayoutParams
                this.weight = 6f
            }
            this.requestLayout()
        }
        speak_btn.apply {
            val size = (30 * context.resources.displayMetrics.density).toInt()
            this.layoutParams.apply {
                this.width = size
                this.height = size
            }
            this.requestLayout()
        }
        word_text.textSize = 22f
        type_phonetic.alpha = 0f;
        type_phonetic.visibility = View.GONE
        definition.visibility = View.GONE
        show_def_text.visibility = View.VISIBLE
        button_container.translationY = 100f
        button_container.visibility = View.GONE
        overlay.visibility = View.GONE

        display_card.apply {
            translationY = 0f;
            translationX = 0f
            rotation = 0f
            alpha = 1f;
        }

        val scaleXAnimation = ObjectAnimator.ofFloat(display_card, View.SCALE_X, 0.8f, 1f)
        val scaleYAnimation = ObjectAnimator.ofFloat(display_card, View.SCALE_Y, 0.8f, 1f)
        val animatorSet = AnimatorSet()
        animatorSet.apply {
            play(scaleXAnimation).with(scaleYAnimation)
            duration = 200;
            start()
        }
    }

    /* Show definitions and buttons with animations */
    private fun onCardClick() {
        full_screen_btn.visibility = View.VISIBLE
        word_wrapper.apply {
            this.layoutParams.apply {
                this as LinearLayout.LayoutParams
                this.weight = 3f
            }
            this.requestLayout()
        }
        speak_btn.apply {
            val size = (20 * context.resources.displayMetrics.density).toInt()
            this.layoutParams.apply {
                this.width = size
                this.height = size
            }
            this.requestLayout()
        }
        word_text.textSize = 16f
        type_phonetic.visibility = View.VISIBLE
        type_phonetic.animate().alpha(1f)
        definition.visibility = View.VISIBLE
        show_def_text.visibility = View.GONE
        button_container.visibility = View.VISIBLE
        button_container.animate().translationY(0f)
        onBoardUser()
    }

    fun openOverlay(v: View) {
        overlay.visibility = View.VISIBLE
        overlay.animate().scaleX(1f).scaleY(1f).withEndAction {
            onBoardUser()
        }
    }

    fun closeOverlay(v: View) {
        overlay.animate().scaleX(0f).scaleY(0f).withEndAction {
            overlay.visibility = View.GONE
        }
    }

    fun onSpeakBtnClick(v: View) {
        tts.speak(questionWord.word, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    private fun wordRemembered() {
        onBoardUser()
        val status = questionWord.learning_status ?: 0
        val newStatus = if ((status + 1) > 3) 3 else (status + 1)
        GlobalScope.launch {
            vocabDb.WordDao().setLearningStatus(questionWord.id, newStatus)
            swipeCard("right");
        }
    }

    private fun wordNotRemembered() {
        onBoardUser()
        val status = questionWord.learning_status ?: 0
        val newStatus = if ((status - 1) < 0) 0 else (status - 1)
        GlobalScope.launch {
            vocabDb.WordDao().setLearningStatus(questionWord.id, newStatus)
            swipeCard("left");
        }
    }

    fun openPracticeSection(v: View) {
        val intent = Intent(this, PracticeSection::class.java)
        intent.apply { putExtra("id", section.id ?: 0) }
        startActivity(intent)
    }

    /* Card swiping animation */
    private fun swipeCard(direction: String) {
        if (section.progress_status != 1) setSectionStatus(1);
        runOnUiThread {
            val yLen = -resources.displayMetrics.heightPixels.toFloat();
            var xLen = resources.displayMetrics.widthPixels.toFloat();
            if (direction == "left") xLen *= -1;
            var rotateDegree = 90f;
            if (direction == "left") rotateDegree *= -1;

            val xAnimation = ObjectAnimator.ofFloat(display_card, View.TRANSLATION_X, display_card.translationX, xLen)
            val yAnimation = ObjectAnimator.ofFloat(display_card, View.TRANSLATION_Y, display_card.translationY, yLen)
            val rotateAnimation = ObjectAnimator.ofFloat(display_card, View.ROTATION, display_card.rotation, rotateDegree)
            val alphaAnimation = ObjectAnimator.ofFloat(display_card, View.ALPHA, 1f, 0f)

            val animatorSet = AnimatorSet();
            animatorSet.apply {
                play(yAnimation).with(xAnimation).with(rotateAnimation).with(alphaAnimation);
                duration = 500
                doOnEnd { getWords(section.id ?: 0) }
                start()
            }
        }
    }

    private fun setProgressBarValues() {
        master_count.text = getString(R.string.out_of, masteredWords.size, allWords.size)
        master_progress_bar.max = allWords.size
        master_progress_bar.progress = (masteredWords.size)

        review_count.text = getString(R.string.out_of, reviewingWords.size, allWords.size)
        review_progress_bar.max = allWords.size
        review_progress_bar.progress = (reviewingWords.size)

        learn_count.text =  getString(R.string.out_of, learningWords.size, allWords.size)
        learn_progress_bar.max = allWords.size
        learn_progress_bar.progress = (learningWords.size)
    }

    private fun onBoardUser() {
        val onboardingTexts = listOf(getString(R.string.onboarding_1), getString(R.string.onboarding_2), getString(R.string.onboarding_3))
        val sharedPreferences = getSharedPreferences(getString(R.string.global_pref), Context.MODE_PRIVATE)

        if (onBoardingPhase > 2 || sharedPreferences.getBoolean(getString(R.string.onboarding_complete), false)) {
            onboarding_text.text = "";
            sharedPreferences.edit().putBoolean(getString(R.string.onboarding_complete), true).apply()
            return
        }
        onboarding_text.text = onboardingTexts[onBoardingPhase]
        ++onBoardingPhase;
    }

    private fun animateOnboardingText() {
        val fadeInOutAnimation = AlphaAnimation(0f, 1f);
        fadeInOutAnimation.duration = 750;
        fadeInOutAnimation.repeatMode = 2;
        fadeInOutAnimation.repeatCount = AlphaAnimation.INFINITE;
        onboarding_text.startAnimation(fadeInOutAnimation)
    }

    private fun setTouchListener() {
        var dx = 0f
        var dy = 0f;
        var startX = 0f
        var startY = 0f;
        val CLICK_THRESHOLD = 10;
        display_card.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = v.x
                    startY = v.y
                    dx = v.x - event.rawX;
                    dy = v.y - event.rawY
                }
                MotionEvent.ACTION_MOVE -> {
                    val diffX = v.x - startX
                    var diffY = v.y - startY
                    if (diffX > 0) diffY *= -1;

                    /* Move and rotate card with touch move */
                    v.rotation = (diffY) / 8
                    v.x = event.rawX + dx
                    v.y = event.rawY + dy
                }
                MotionEvent.ACTION_UP -> {
                    val diffX = v.x - startX
                    val diffY = v.y - startY

                    /* If move distance is less than CLICK_THRESHOLD consider it as a click */
                    if (!(abs(diffX) > CLICK_THRESHOLD || abs(diffY) > CLICK_THRESHOLD)) {
                        onCardClick()
                        return@setOnTouchListener false
                    }

                    /* If Y diff is upwards swipe card else reset it back to its original position */
                    if (diffY < -200) {
                        if (diffX > 0) wordRemembered()
                        else wordNotRemembered()
                    } else {
                        v.animate().x(startX).y(startY).rotation(0f)
                    }
                }
                else -> { return@setOnTouchListener false }
            }
            return@setOnTouchListener true
        }
    }

    private fun setSectionStatus(status: Int) {
        GlobalScope.launch { vocabDb.SectionDao().setSectionProgess(section.id ?: 0, status) }
    }
}
