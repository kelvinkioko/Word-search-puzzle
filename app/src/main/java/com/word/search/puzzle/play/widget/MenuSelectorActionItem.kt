package com.word.search.puzzle.play.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.word.search.puzzle.play.R
import com.word.search.puzzle.play.databinding.ItemMenuSelectorActionBinding

class MenuSelectorActionItem @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val itemBinding =
        ItemMenuSelectorActionBinding.inflate(
            LayoutInflater.from(context),
            this,
            true
        )

    init {
        attrs?.let {
            context.obtainStyledAttributes(it, R.styleable.GenericClickableItem).apply {
                setItemText(getString(R.styleable.GenericClickableItem_text).orEmpty())
                setItemSubText(getString(R.styleable.GenericClickableItem_subText).orEmpty())
                recycle()
            }
        }
    }

    private fun setItemText(itemText: String) {
        itemBinding.actionTitle.text = itemText
    }

    private fun setItemSubText(itemSubText: String) {
        itemBinding.apply {
            actionSubTitle.text = itemSubText
            actionSubTitle.isVisible = actionSubTitle.text.toString().isNotEmpty()
        }
    }

    fun setItemChecked(checked: Boolean = false) {
        itemBinding.apply {
            actionSelector.isChecked = checked
        }
    }
}
