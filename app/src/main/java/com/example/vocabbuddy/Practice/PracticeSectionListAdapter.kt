package com.example.vocabbuddy.Practice

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.vocabbuddy.Learn.LearnSection
import com.example.vocabbuddy.Models.Section
import com.example.vocabbuddy.R
import kotlinx.android.synthetic.main.practice_section_list_item.view.*

class PracticeSectionListAdapter(private val sections: List<Section>, private val context: Context): RecyclerView.Adapter<PracticeSectionListAdapter.ViewHolder>() {

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(
                context
            ).inflate(R.layout.practice_section_list_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return sections.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val section = sections[position];
        holder.itemView.section_name.text = section.name;
        holder.itemView.section_locked.text = context.getString(R.string.locked, section.name);
        holder.itemView.best_score.text = context.getString(R.string.by20, section.best_score)
        holder.itemView.section_learn.text = context.getString(R.string.learn_underlined, section.name)
        holder.itemView.custom_overlay.visibility = View.VISIBLE;

        if (section.progress_status == 2) {
            holder.itemView.custom_overlay.visibility = View.GONE;
            holder.itemView.setOnClickListener {
                val intent = Intent(context, PracticeSection::class.java).apply {
                    putExtra("id", section.id)
                }
                context.startActivity(intent)
            }
        } else {
            holder.itemView.section_learn.setOnClickListener {
                val intent = Intent(context, LearnSection::class.java).apply {
                    putExtra("id", section.id)
                }
                context.startActivity(intent)
            }
        }
    }
}