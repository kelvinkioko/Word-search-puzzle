package com.word.search.puzzle.play.ui.settings.language

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.word.search.puzzle.play.database.entity.LanguageEntity
import com.word.search.puzzle.play.databinding.ItemListSelectorBinding

class LanguageAdapter(private val gridSizeClicked: (LanguageEntity) -> Unit) :
        RecyclerView.Adapter<LanguageAdapter.LanguageItemViewHolder>() {

    private val items = mutableListOf<LanguageEntity>()
    private var selectedLanguage = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageItemViewHolder =
            LanguageItemViewHolder(
                    ItemListSelectorBinding.inflate(
                            LayoutInflater.from(parent.context),
                            parent,
                            false
                    )
            )

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: LanguageItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun setLanguages(gridSizes: List<LanguageEntity>, selectedLanguage: String) {
        items.clear()
        items.addAll(gridSizes)
        this.selectedLanguage = selectedLanguage
        notifyDataSetChanged()
    }

    inner class LanguageItemViewHolder(
        private val binding: ItemListSelectorBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                selectedLanguage = items[position].initial
                gridSizeClicked.invoke(items[position])
                notifyDataSetChanged()
            }
        }

        fun bind(language: LanguageEntity) {
            binding.apply {
                actionTitle.text = language.language
                actionSelector.isChecked = language.initial.equals(selectedLanguage, ignoreCase = true)
            }
        }
    }
}
