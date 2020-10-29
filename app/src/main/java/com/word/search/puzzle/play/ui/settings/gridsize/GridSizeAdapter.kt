package com.word.search.puzzle.play.ui.settings.gridsize

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.word.search.puzzle.play.database.entity.GridSizeEntity
import com.word.search.puzzle.play.databinding.ItemListSelectorBinding

class GridSizeAdapter(private val gridSizeClicked: (GridSizeEntity) -> Unit) :
        RecyclerView.Adapter<GridSizeAdapter.GridSizeItemViewHolder>() {

    private val items = mutableListOf<GridSizeEntity>()
    private var selectedGridSize = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridSizeItemViewHolder =
            GridSizeItemViewHolder(
                    ItemListSelectorBinding.inflate(
                            LayoutInflater.from(parent.context),
                            parent,
                            false
                    )
            )

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: GridSizeItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun setGridSizes(gridSizes: List<GridSizeEntity>, selectedGridSize: String) {
        items.clear()
        items.addAll(gridSizes)
        this.selectedGridSize = selectedGridSize
        notifyDataSetChanged()
    }

    inner class GridSizeItemViewHolder(
        private val binding: ItemListSelectorBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                selectedGridSize = items[position].grid_value
                gridSizeClicked.invoke(items[position])
                notifyDataSetChanged()
            }
        }

        fun bind(gridSize: GridSizeEntity) {
            binding.apply {
                actionTitle.text = gridSize.grid_size
                actionSelector.isChecked = gridSize.grid_value.equals(selectedGridSize, ignoreCase = true)
            }
        }
    }
}
