package com.example.vocabbuddy.Practice

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.vocabbuddy.Models.Section
import com.example.vocabbuddy.R
import kotlinx.android.synthetic.main.practice_section_list_item.view.*

class SectionListAdapter(private val sections: List<Section>, private val context: Context): RecyclerView.Adapter<SectionListAdapter.ViewHolder>() {

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
        if (section.progress_status == 2) holder.itemView.custom_overlay.visibility = View.GONE;
        holder.itemView.section_name.text = section.name;
        holder.itemView.section_locked.text = "${section.name} Locked";
        holder.itemView.best_score.text = "${section.best_score}/20"
        holder.itemView.section_learn.text = "Learn ${section.name}"
        holder.itemView.section_name.setOnClickListener {
            Toast.makeText(context, sections[position].name, Toast.LENGTH_SHORT).show()
        }
    }
}