package com.example.vocabbuddy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.android.synthetic.main.activity_section_list.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SectionList : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_section_list)
        val db = VocabDb(this)

        val viewManager = LinearLayoutManager(this)
        section_list.layoutManager = viewManager

        val context = this;

        GlobalScope.launch {
            val sections = db.SectionDao().getAllSections();
            val viewAdapter = SectionListAdapter(sections, context)
            runOnUiThread {
                textView2.setText(sections.size.toString())
                section_list.adapter = viewAdapter
            }
        }

    }
}
