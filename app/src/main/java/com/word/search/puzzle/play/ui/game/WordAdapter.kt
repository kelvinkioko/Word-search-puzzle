package com.word.search.puzzle.play.ui.game

import android.graphics.Paint
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.word.search.puzzle.play.R
import com.word.search.puzzle.play.databinding.WordSearchItemBinding

class WordAdapter : RecyclerView.Adapter<WordAdapter.WordCountItemViewHolder>() {

    private var correctWords = mutableListOf<Word>()
    private var words = mutableListOf<Word>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordCountItemViewHolder =
            WordCountItemViewHolder(
                    WordSearchItemBinding.inflate(
                            LayoutInflater.from(parent.context),
                            parent,
                            false
                    )
            )

    override fun getItemCount(): Int = words.size

    override fun onBindViewHolder(holder: WordCountItemViewHolder, position: Int) {
        holder.bind(words[position])
    }

    fun setWordsFound(wordCounts: List<Word>) {
        words.clear()
        words.addAll(wordCounts)
        notifyDataSetChanged()
    }

    fun setWordFound(word: Word) {
        correctWords.add(word)
        notifyDataSetChanged()
    }

    inner class WordCountItemViewHolder(
        private val binding: WordSearchItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(word: Word) {
            binding.apply {
                textWord.apply {
                    text = word.text
                    if (!correctWords.contains(word)) {
                        typeface = Typeface.DEFAULT_BOLD
                        setTextColor(textWord.resources.getColor(R.color.primary_text))
                        paintFlags = paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    } else {
                        typeface = Typeface.DEFAULT
                        setTextColor(textWord.resources.getColor(R.color.colorPositive))
                        paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    }
                }
            }
        }
    }
}
