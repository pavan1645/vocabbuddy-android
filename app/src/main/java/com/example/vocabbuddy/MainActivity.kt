package com.example.vocabbuddy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.vocabbuddy.Practice.SectionList
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = VocabDb(this)

        GlobalScope.launch {
            val sections = db.SectionDao().getAllSections();
            Log.i("db version", sections.size.toString())
        }


    }

    fun openSectionList(v: View) {
        val intent = Intent(this, SectionList::class.java)
        startActivity(intent)
    }
}
