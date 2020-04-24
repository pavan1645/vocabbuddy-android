package com.example.vocabbuddy.Learn

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.vocabbuddy.R
import kotlinx.android.synthetic.main.activity_learn_section.*

class LearnSection : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learn_section)
        textView4.text = intent.extras?.getInt("id").toString()
    }
}
