package com.rinjaninet.storyapp.story

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.rinjaninet.storyapp.R

class ListStoryAdapter(
    private var listStory: ArrayList<ListStoryItem>
): RecyclerView.Adapter<ListStoryAdapter.ListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(
            R.layout.item_row_story, parent, false
        )
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(listStory[position])
    }

    override fun getItemCount(): Int = listStory.size

    class ListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private var ivItemPhoto: ImageView =
            itemView.findViewById(R.id.iv_item_photo)
        private var tvItemName: TextView =
            itemView.findViewById(R.id.tv_item_name)
        private var tvItemDescription: TextView =
            itemView.findViewById(R.id.tv_item_description)

        fun bind(story: ListStoryItem) {
            story.photoUrl?.let { ivItemPhoto.loadImage(it) }
            tvItemName.text = story.name
            tvItemDescription.text = story.description

            itemView.setOnClickListener {
                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(ivItemPhoto, "photo"),
                        Pair(tvItemName, "name"),
                        Pair(tvItemDescription, "description")
                    )
                val storyIntent = Intent(itemView.context, StoryActivity::class.java)
                storyIntent.putExtra(StoryActivity.EXTRA_STORY, story)
                itemView.context.startActivity(storyIntent, optionsCompat.toBundle())
            }
        }

        private fun ImageView.loadImage(url: String) {
            Glide.with(this.context)
                .load(url)
                .apply(RequestOptions.centerCropTransform())
                .into(this)
        }
    }
}