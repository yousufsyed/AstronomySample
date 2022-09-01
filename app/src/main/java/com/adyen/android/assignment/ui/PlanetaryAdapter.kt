package com.adyen.android.assignment.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.adyen.android.assignment.data.AstronomyInfo
import com.adyen.android.assignment.databinding.AstronomyHeaderBinding
import com.adyen.android.assignment.databinding.AstronomyItemBinding
import com.adyen.android.assignment.inflater
import com.adyen.android.assignment.load

private const val HEADER = 0
private const val ITEM = 1

class PlanetaryAdapter(
    private val action: (AstronomyInfo.PlanetaryData) -> Unit
) : ListAdapter<AstronomyInfo, RecyclerView.ViewHolder>(PlanetaryDiffUtils()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == HEADER) {
            PlanetaryHeaderViewHolder(
                AstronomyHeaderBinding.inflate(parent.inflater, parent, false)
            )
        } else {
            PlanetaryItemViewHolder(
                AstronomyItemBinding.inflate(parent.inflater, parent, false),
                action
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is AstronomyInfo.PlanetaryData -> (holder as? PlanetaryItemViewHolder)?.bind(item)
            is AstronomyInfo.Header -> (holder as? PlanetaryHeaderViewHolder)?.bind(item.title)
        }
    }

    class PlanetaryItemViewHolder(
        private val binding: AstronomyItemBinding,
        private val action: (AstronomyInfo.PlanetaryData) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(astronomyPicture: AstronomyInfo.PlanetaryData) {
            itemView.setOnClickListener { action(astronomyPicture) }
            with(binding) {
                image.load(astronomyPicture.url)
                title.text = astronomyPicture.title
                date.text = astronomyPicture.date.toString()
            }
        }
    }

    class PlanetaryHeaderViewHolder(
        private val binding: AstronomyHeaderBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(title: String) {
            binding.title.text = title
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is AstronomyInfo.PlanetaryData -> ITEM
            is AstronomyInfo.Header -> HEADER
        }
    }
}

class PlanetaryDiffUtils : DiffUtil.ItemCallback<AstronomyInfo>() {
    override fun areItemsTheSame(oldItem: AstronomyInfo, newItem: AstronomyInfo): Boolean =
        oldItem == newItem

    override fun areContentsTheSame(oldItem: AstronomyInfo, newItem: AstronomyInfo): Boolean {
        return (oldItem is AstronomyInfo.PlanetaryData && newItem is AstronomyInfo.PlanetaryData && oldItem.title == newItem.title) ||
                (oldItem is AstronomyInfo.Header && newItem is AstronomyInfo.Header && oldItem.title == newItem.title)
    }
}
