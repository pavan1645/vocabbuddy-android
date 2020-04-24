package com.example.vocabbuddy.Practice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.vocabbuddy.R
import kotlinx.android.synthetic.main.activity_practice_section.*

class PracticeSection : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_practice_section)
        textView3.text = intent.extras?.getInt("id").toString()
    }
}
