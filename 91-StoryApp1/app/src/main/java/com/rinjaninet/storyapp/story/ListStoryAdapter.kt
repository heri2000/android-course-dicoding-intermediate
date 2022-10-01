package com.rinjaninet.storyapp.story

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.rinjaninet.storyapp.databinding.ItemRowStoryBinding

class ListStoryAdapter(
    private val listStory: ArrayList<ListStoryItem>
): RecyclerView.Adapter<ListStoryAdapter.ListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemRowStoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val listStoryItem = listStory[position]
        holder.binding.apply {
            tvStoryItemName.text = listStoryItem.name
            tvStoryItemDescriptionPart.text = listStoryItem.description
            if (listStoryItem.photoUrl != null)
                ivStoryItemPhoto.loadImage(listStoryItem.photoUrl)
        }
    }

    override fun getItemCount(): Int {
        return listStory.size
    }

    private fun ImageView.loadImage(url: String) {
        Glide.with(this.context)
            .load(url)
            .apply(RequestOptions.centerCropTransform())
            .into(this)
    }

    class ListViewHolder(var binding: ItemRowStoryBinding): RecyclerView.ViewHolder(binding.root)
}