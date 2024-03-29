package com.himanshu.android.view.ui.location.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mapbox.search.result.SearchSuggestion
import com.himanshu.android.R
import com.himanshu.android.databinding.ItemSearchBinding
import com.himanshu.android.databinding.ItemSuggestionBinding
import com.himanshu.android.domain.db.search.SearchEntity
import com.himanshu.android.utils.ClickType

class SuggestionDiffUtil: DiffUtil.ItemCallback<SearchSuggestion>() {
    override fun areItemsTheSame(oldItem: SearchSuggestion, newItem: SearchSuggestion): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: SearchSuggestion, newItem: SearchSuggestion): Boolean {
        return oldItem == newItem
    }

}

class SuggestionAdapter : ListAdapter<SearchSuggestion, SuggestionViewHolder>(SuggestionDiffUtil()) {

    var onSuggestionClicked: ((SearchSuggestion) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestionViewHolder {
        val binding: ItemSuggestionBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_suggestion,
            parent,
            false
        )
        return SuggestionViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SuggestionViewHolder, position: Int) {
        val suggestion = getItem(position)
        holder.binding.suggestion = suggestion
        val name = suggestion.name
        val place = suggestion.address?.place.orEmpty()
        val street = suggestion.address?.street
        holder.binding.searchTerm.text =
            "$name $place" + if (!street.isNullOrEmpty()) ", $street" else ""
        holder.binding.root.setOnClickListener {
            onSuggestionClicked?.invoke(suggestion)
        }
    }
}

class SuggestionViewHolder(val binding: ItemSuggestionBinding): RecyclerView.ViewHolder(binding.root)
