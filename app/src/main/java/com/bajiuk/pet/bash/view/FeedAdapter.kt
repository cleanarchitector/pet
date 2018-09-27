package com.bajiuk.pet.bash.view

import android.support.v4.text.HtmlCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bajiuk.pet.R
import com.bajiuk.pet.bash.model.Post
import kotlinx.android.synthetic.main.item_post.view.*

class FeedAdapter : RecyclerView.Adapter<PostViewHolder>() {

    var items: List<Post> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(viewHolder: PostViewHolder, position: Int) {
        viewHolder.bind(items[position])
    }
}

class PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun bind(post: Post) {
        itemView.text_body.text = HtmlCompat.fromHtml(post.text, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }
}
