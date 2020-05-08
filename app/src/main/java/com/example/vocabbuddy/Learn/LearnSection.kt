package com.example.vocabbuddy.Learn

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.vocabbuddy.Models.Word
import com.example.vocabbuddy.R
import com.example.vocabbuddy.VocabDb
import kotlinx.android.synthetic.main.activity_learn_section.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.random.Random

class LearnSection : AppCompatActivity() {

    private val learningWords: MutableList<Word> = ArrayList()
    private val reviewingWords: MutableList<Word> = ArrayList()
    private val masteredWords: MutableList<Word> = ArrayList()
    private lateinit var allWords: List<Word>
    private var sectionId: Int = 0
    private lateinit var questionWord: Word


    private lateinit var vocabDb: VocabDb

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learn_section)

        vocabDb = VocabDb(this)
        sectionId = intent.extras?.getInt("id") ?: 0
        getWords()
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
        val currWordIndex = Random.nextInt(0, learningWords.size + reviewingWords.size)
        if (currWordIndex < learningWords.size) questionWord = learningWords[currWordIndex]
        else questionWord = reviewingWords[currWordIndex - learningWords.size]

        word_text.text = questionWord.word
        definition.text = questionWord.definition
        type_phonetic.text = "${questionWord.type} ${questionWord.phonetic}"
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

    fun wordRemembered(v: View) {
        val status = questionWord.learning_status ?: 0
        val newStatus = if ((status + 1) > 3) 3 else (status + 1)
        GlobalScope.launch {
            vocabDb.WordDao().setLearningStatus(questionWord.id, newStatus)
            getWords()
        }
    }

    fun wordNotRemembered(v: View) {
        val status = questionWord.learning_status ?: 0
        val newStatus = if ((status - 1) < 0) 0 else (status - 1)
        GlobalScope.launch {
            vocabDb.WordDao().setLearningStatus(questionWord.id, newStatus)
            getWords()
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
