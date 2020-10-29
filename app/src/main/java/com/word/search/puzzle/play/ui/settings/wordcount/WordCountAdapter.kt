package com.word.search.puzzle.play.ui.settings.wordcount

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.word.search.puzzle.play.database.entity.WordCountEntity
import com.word.search.puzzle.play.databinding.ItemListSelectorBinding

class WordCountAdapter(private val wordCountClicked: (WordCountEntity) -> Unit) :
        RecyclerView.Adapter<WordCountAdapter.WordCountItemViewHolder>() {

    private val items = mutableListOf<WordCountEntity>()
    private var selectedWordCount = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordCountItemViewHolder =
            WordCountItemViewHolder(
                    ItemListSelectorBinding.inflate(
                            LayoutInflater.from(parent.context),
                            parent,
                            false
                    )
            )

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: WordCountItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun setWordCounts(wordCounts: List<WordCountEntity>, selectedWordCount: String) {
        items.clear()
        items.addAll(wordCounts)
        this.selectedWordCount = selectedWordCount
        notifyDataSetChanged()
    }

    inner class WordCountItemViewHolder(
        private val binding: ItemListSelectorBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                selectedWordCount = items[position].word_count
                wordCountClicked.invoke(items[position])
                notifyDataSetChanged()
            }
        }

        fun bind(wordCount: WordCountEntity) {
            binding.apply {
                actionTitle.text = wordCount.word_count
                actionSelector.isChecked = wordCount.word_count.equals(selectedWordCount, ignoreCase = true)
            }
        }
    }
}
