package com.example.vocabbuddy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.room.Room
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = VocabDb(this);

        GlobalScope.launch {
            val words: List<Word> = db.WordDao().getAllWords();
            tv1.setText(words.size.toString())
        }

    }
}
