package com.example.vocabbuddy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.vocabbuddy.Learn.LearnSectionList
import com.example.vocabbuddy.Practice.PracticeSectionList
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun openPracticeSectionList(v: View) {
        val intent = Intent(this, PracticeSectionList::class.java)
        startActivity(intent)
    }

    fun openLearnSectionList(v: View) {
        val intent = Intent(this, LearnSectionList::class.java)
        startActivity(intent)
    }
}
