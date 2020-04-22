package com.example.vocabbuddy

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.learning_section_list_item.view.*

class SectionListAdapter(private val sections: List<Section>, private val context: Context): RecyclerView.Adapter<SectionListAdapter.ViewHolder>() {

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        val textView = view.textView
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionListAdapter.ViewHolder{
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.learning_section_list_item, parent, false))
    }

    override fun getItemCount(): Int {
        return sections.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = sections[position].name;
    }
}