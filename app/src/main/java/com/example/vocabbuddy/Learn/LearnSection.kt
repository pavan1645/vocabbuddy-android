package com.example.vocabbuddy.Learn

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.LinearLayout
import androidx.core.animation.doOnEnd
import com.example.vocabbuddy.Models.Word
import com.example.vocabbuddy.R
import com.example.vocabbuddy.VocabDb
import kotlinx.android.synthetic.main.activity_learn_section.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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

        vocabDb = VocabDb(this)
        sectionId = intent.extras?.getInt("id") ?: 0
        getWords()

        tts = TextToSpeech(applicationContext, TextToSpeech.OnInitListener { status ->
            if (status != TextToSpeech.ERROR) {
                tts.language = Locale.US
                tts.setSpeechRate(0.75f)
            }
        })
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
        type_phonetic.visibility = View.GONE
        definition.visibility = View.GONE
        show_def_text.visibility = View.VISIBLE
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

    fun onCardClick(v: View) {
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
        definition.visibility = View.VISIBLE
        show_def_text.visibility = View.GONE
        button_container.visibility = View.VISIBLE
    }

    fun openOverlay(v: View) {
        overlay.visibility = View.VISIBLE
    }

    fun closeOverlay(v: View) {
        overlay.visibility = View.GONE
    }

    fun onSpeakBtnClick(v: View) {
        tts.speak(questionWord.word, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    fun wordRemembered(v: View) {
        val status = questionWord.learning_status ?: 0
        val newStatus = if ((status + 1) > 3) 3 else (status + 1)
        GlobalScope.launch {
            vocabDb.WordDao().setLearningStatus(questionWord.id, newStatus)
            swipeCard("right");
        }
    }

    fun wordNotRemembered(v: View) {
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

            val yAnimation = ObjectAnimator.ofFloat(display_card, View.TRANSLATION_Y, 0f, yLen)
            val xAnimation = ObjectAnimator.ofFloat(display_card, View.TRANSLATION_X, 0f, xLen)
            val rotateAnimation = ObjectAnimator.ofFloat(display_card, View.ROTATION, 0f, rotateDegree)
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
