package com.example.vocabbuddy.Learn

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.vocabbuddy.Models.Section
import com.example.vocabbuddy.R
import kotlinx.android.synthetic.main.learn_section_list_item.view.*

class LearnSectionListAdapter(private val sections: List<Section>, private val mainContext: Context): RecyclerView.Adapter<LearnSectionListAdapter.ViewHolder>() {

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LearnSectionListAdapter.ViewHolder {
        return LearnSectionListAdapter.ViewHolder(
            LayoutInflater.from(
                mainContext
            ).inflate(R.layout.learn_section_list_item, parent, false)
        )
    }

    override fun getItemCount(): Int { return sections.size; }

    override fun onBindViewHolder(holder: LearnSectionListAdapter.ViewHolder, position: Int) {
        val section = sections[position];
        holder.itemView.apply {
            section_name.text = section.name;
            /* Styling section status */
            progress_status.apply {
                when (section.progress_status) {
                    0 -> {
                        this.text = mainContext.getString(R.string.not_started)
                        this.setBackgroundColor(resources.getColor(R.color.LightBg, null))
                        this.setTextColor(resources.getColor(R.color.LightText, null))
                    }
                    1 -> {
                        this.text = mainContext.getString(R.string.started)
                        this.setBackgroundColor(resources.getColor(R.color.WarningBg, null))
                        this.setTextColor(resources.getColor(R.color.WarningText, null))
                    }
                    else -> {
                        this.text = mainContext.getString(R.string.mastered)
                        this.setBackgroundColor(resources.getColor(R.color.SuccessBg, null))
                        this.setTextColor(resources.getColor(R.color.SuccessText, null))
                    }
                }
            }
            setOnClickListener {
                val intent = Intent(mainContext, LearnSection::class.java).apply {
                    putExtra("id", section.id)
                }
                mainContext.startActivity(intent)
            }
        }
    }
}