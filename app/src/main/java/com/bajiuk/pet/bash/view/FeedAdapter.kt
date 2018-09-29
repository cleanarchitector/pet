package com.bajiuk.pet.bash.view

import android.support.annotation.LayoutRes
import android.support.v4.text.HtmlCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bajiuk.pet.R
import com.bajiuk.pet.bash.model.Manager
import kotlinx.android.synthetic.main.item_post.view.*

class FeedAdapter(val viewModel: ViewModel, val manager: Manager) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var size = 0
    var error : Throwable? = null
    var loading : Boolean = false

    init {
        viewModel.listStateSubject.subscribe(
            {
                error = it.throwable
                loading = it.isLoading
                notifyDataSetChanged()
            }
        )
        viewModel.feedSizeSubject.subscribe(
            {
                size = it
                notifyDataSetChanged()
            }
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): RecyclerView.ViewHolder {
        return when (type) {
            0 -> PostViewHolder.create(parent)
            1 -> ErrorViewHolder.create(parent)
            else -> LoadingViewHolder.create(parent)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position < size -> PostViewHolder.type
            error != null -> ErrorViewHolder.type
            else -> LoadingViewHolder.type
        }
    }

    override fun getItemCount(): Int {
        return size + if (loading || error != null) 1 else  0
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (viewHolder) {
            is PostViewHolder -> viewHolder.bind(manager.get(position))
        }
    }
}

class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    companion object {
        private val layoutId = R.layout.item_loading
        fun create(parent: ViewGroup) = LoadingViewHolder(createView(parent, layoutId))
        val type = 2
    }
}


class ErrorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    companion object {
        private val layoutId = R.layout.item_error
        fun create(parent: ViewGroup) = ErrorViewHolder(createView(parent, layoutId))
        val type = 1
    }
}

class PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun bind(post: String) {
        itemView.text_body.text = HtmlCompat.fromHtml(post, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    companion object {
        private val layoutId = R.layout.item_post
        fun create(parent: ViewGroup) = PostViewHolder(createView(parent, layoutId))
        val type = 0
    }
}

fun createView(parent: ViewGroup, @LayoutRes layoutId: Int) =
    LayoutInflater.from(parent.context).inflate(layoutId, parent, false)!!