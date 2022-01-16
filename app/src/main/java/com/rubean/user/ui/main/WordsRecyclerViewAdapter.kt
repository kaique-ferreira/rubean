package com.rubean.user.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rubean.user.R

class WordsRecyclerViewAdapter(var words: List<String>) :
    RecyclerView.Adapter<WordsRecyclerViewAdapter.WordViewHolder>() {

    class WordViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.word_row, parent, false)) {
        private var textViewWord: TextView = itemView.findViewById(R.id.textViewWord)

        fun bind(word: String) {
            textViewWord.text = word
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return WordViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        holder.bind(words[position])
    }

    override fun getItemCount() = words.size
}