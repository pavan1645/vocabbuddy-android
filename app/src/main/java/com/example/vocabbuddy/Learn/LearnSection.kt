package com.example.vocabbuddy.Learn

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import androidx.core.animation.doOnEnd
import com.example.vocabbuddy.Models.Word
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
    private var sectionId: Int = 0
    private lateinit var questionWord: Word
    private var selectFromMaster: Boolean = false;
    private lateinit var tts: TextToSpeech;

    private lateinit var vocabDb: VocabDb

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learn_section)
        window.statusBarColor = resources.getColor(R.color.SuccessText, null);

        vocabDb = VocabDb(this)
        sectionId = intent.extras?.getInt("id") ?: 0
        getWords()

        tts = TextToSpeech(applicationContext, TextToSpeech.OnInitListener { status ->
            if (status != TextToSpeech.ERROR) {
                tts.language = Locale.US
                tts.setSpeechRate(0.75f)
            }
        })

        remember_btn.setOnClickListener { wordRemembered() }
        not_remember_btn.setOnClickListener { wordNotRemembered() }

        var dx = 0f
        var dy = 0f;
        var startX = 0f
        var startY = 0f;
        display_card.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = v.x
                    startY = v.y
                    dx = v.x - event.rawX;
                    dy = v.y - event.rawY
                }
                MotionEvent.ACTION_MOVE -> {
                    v.x = event.rawX + dx
                    v.y = event.rawY + dy

                    val diffX = v.x - startX
                    var diffY = v.y - startY
                    if (diffX > 0) diffY *= -1;
                    v.rotation = (diffY) / 8
                }
                MotionEvent.ACTION_UP -> {
                    val diffX = v.x - startX
                    val diffY = v.y - startY

                    if (!(abs(diffX) > 10 || abs(diffY) > 10)) {
                        onCardClick()
                        return@setOnTouchListener false
                    }

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

    private fun getWords() {
        GlobalScope.launch {
            allWords = vocabDb.WordDao().getAllWords(sectionId)


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
            }
        }
    }

    private fun generateWordCard() {
        if (learningWords.size + reviewingWords.size == 0) return;

        if (learningWords.size + reviewingWords.size == 1) {
            selectFromMaster = !selectFromMaster;
            if (selectFromMaster) questionWord = masteredWords[Random.nextInt(0, masteredWords.size)]
        }

        if (learningWords.size + reviewingWords.size > 1 || !selectFromMaster) {
            val currWordIndex = Random.nextInt(0, learningWords.size + reviewingWords.size)
            if (currWordIndex < learningWords.size) questionWord = learningWords[currWordIndex]
            else questionWord = reviewingWords[currWordIndex - learningWords.size]
        }

        word_text.text = questionWord.word
        definition.text = questionWord.definition
        type_phonetic.text = "${questionWord.type} ● ${questionWord.phonetic}"
        definition.text = questionWord.definition

        /* Overlay layout's values */
        word_text_2.text = questionWord.word
        definition_2.text = questionWord.definition
        type_phonetic_2.text = "${questionWord.type} ● ${questionWord.phonetic}"
        example_text.text = questionWord.example
        origin_text.text = questionWord.origin

        GlobalScope.launch {
            val synonyms = vocabDb.SynonymsDao().getSynonyms(questionWord.id)
            runOnUiThread { synonyms_text.text = synonyms.toString().replace("[", "").replace("]", "") }
        }

        resetCard()
    }

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

    fun onCardClick() {
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
    }

    fun openOverlay(v: View) {
        overlay.visibility = View.VISIBLE
        overlay.animate().scaleX(1f).scaleY(1f)
    }

    fun closeOverlay(v: View) {
        overlay.apply {
            visibility = View.GONE
            scaleX = 0f
            scaleY = 0f
        }
    }

    fun onSpeakBtnClick(v: View) {
        tts.speak(questionWord.word, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    fun wordRemembered() {
        val status = questionWord.learning_status ?: 0
        val newStatus = if ((status + 1) > 3) 3 else (status + 1)
        GlobalScope.launch {
            vocabDb.WordDao().setLearningStatus(questionWord.id, newStatus)
            swipeCard("right");
        }
    }

    fun wordNotRemembered() {
        val status = questionWord.learning_status ?: 0
        val newStatus = if ((status - 1) < 0) 0 else (status - 1)
        GlobalScope.launch {
            vocabDb.WordDao().setLearningStatus(questionWord.id, newStatus)
            swipeCard("left");
        }
    }

    private fun swipeCard(direction: String) {
        runOnUiThread {
            val yLen = -resources.displayMetrics.heightPixels.toFloat();
            var xLen = resources.displayMetrics.widthPixels.toFloat();
            if (direction == "left") xLen *= -1;
            var rotateDegree = 90f;
            if (direction == "left") rotateDegree *= -1;

            val xStart = display_card.x - display_card.width/2
            val yStart = display_card.y - display_card.height/2

            val xAnimation = ObjectAnimator.ofFloat(display_card, View.TRANSLATION_X, xStart, xLen)
            val yAnimation = ObjectAnimator.ofFloat(display_card, View.TRANSLATION_Y, yStart, yLen)
            val rotateAnimation = ObjectAnimator.ofFloat(display_card, View.ROTATION, display_card.rotation, rotateDegree)
            val alphaAnimation = ObjectAnimator.ofFloat(display_card, View.ALPHA, 1f, 0f)

            val animatorSet = AnimatorSet();
            animatorSet.apply {
                play(yAnimation).with(xAnimation).with(rotateAnimation).with(alphaAnimation);
                duration = 500
                doOnEnd { getWords() }
                start()
            }
        }
    }

    private fun setProgressBarValues() {
        master_count.text = "${masteredWords.size} out of ${allWords.size}"
        master_progress_bar.max = allWords.size
        master_progress_bar.progress = (masteredWords.size)

        review_count.text = "${reviewingWords.size} out of ${allWords.size}"
        review_progress_bar.max = allWords.size
        review_progress_bar.progress = (reviewingWords.size)

        learn_count.text = "${learningWords.size} out of ${allWords.size}"
        learn_progress_bar.max = allWords.size
        learn_progress_bar.progress = (learningWords.size)
    }
}
