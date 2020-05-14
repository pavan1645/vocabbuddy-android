package com.example.vocabbuddy.Practice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vocabbuddy.R
import com.example.vocabbuddy.VocabDb
import kotlinx.android.synthetic.main.activity_practice_section_list.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PracticeSectionList : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_practice_section_list)
        window.statusBarColor = resources.getColor(R.color.AccentDark, null);

        val viewManager = LinearLayoutManager(this)
        section_list.layoutManager = viewManager
        loadWords()
    }

    override fun onResume() {
        super.onResume()
        loadWords()
    }

    /* Get sections and add to adapter */
    private fun loadWords() {
        val db = VocabDb(this)
        val context = this;

        GlobalScope.launch {
            val sections = db.SectionDao().getAllSections();
            val viewAdapter = PracticeSectionListAdapter(sections, context)
            runOnUiThread {
                section_list.adapter = viewAdapter
            }
        }
    }
}
