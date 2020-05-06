package com.example.vocabbuddy.Learn

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.vocabbuddy.R

class LearnSection : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learn_section)
        /*
        *
        *
            private val learningWords: MutableList<Word> = ArrayList();
            private val reviewingWords: MutableList<Word> = ArrayList();
            private val masteredWords: MutableList<Word> = ArrayList();
            for (word in allWords) {
                when (word.learning_status) {
                    0 -> learningWords.add(word)
                    1 -> reviewingWords.add(word)
                    else -> masteredWords.add(word)
                }
            }
            *
            val wordIndex = Random.nextInt(0, learningWords.size + reviewingWords.size)
            lateinit var questionWord: Word;
            if (wordIndex < learningWords.size) questionWord = learningWords[wordIndex]
            else questionWord = reviewingWords[wordIndex]
        *
        * */
    }
}
