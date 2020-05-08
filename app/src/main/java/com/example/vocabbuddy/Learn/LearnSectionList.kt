package com.example.vocabbuddy.Learn

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vocabbuddy.R
import com.example.vocabbuddy.VocabDb
import kotlinx.android.synthetic.main.activity_learn_section_list.*
import kotlinx.android.synthetic.main.activity_practice_section_list.*
import kotlinx.android.synthetic.main.activity_practice_section_list.section_list
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LearnSectionList : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learn_section_list)

        val viewManager = LinearLayoutManager(this)
        section_list.layoutManager = viewManager
        loadWords()

    }

    private fun loadWords() {
        val vocabDb = VocabDb(this)
        val context = this;

        GlobalScope.launch {
            val sections = vocabDb.SectionDao().getAllSections()
            val viewAdapter = LearnSectionListAdapter(sections, context)
            runOnUiThread {
                section_list.adapter = viewAdapter
            }
        }
    }
}