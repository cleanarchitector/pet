package com.bajiuk.pet.bash.view

import android.support.annotation.LayoutRes
import android.support.v4.text.HtmlCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bajiuk.pet.R
import com.bajiuk.pet.bash.model.BashManager
import com.bajiuk.pet.bash.viewmodel.BashViewModel
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.item_bashcard.view.*
import kotlinx.android.synthetic.main.item_error.view.*

class BashRecyclerAdapter(
    private val viewModel: BashViewModel,
    private val manager: BashManager
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var size = 0
    private var error: Throwable? = null
    private var loading: Boolean = false

    private val disposables = CompositeDisposable()

    init {
        with(disposables) {
            add(viewModel.listStateSubject.subscribe {
                error = it.throwable
                loading = it.isLoading
                notifyDataSetChanged()
            })
            add(viewModel.feedSizeSubject.subscribe {
                size = it
                notifyDataSetChanged()
            })
        }
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): RecyclerView.ViewHolder {
        return when (type) {
            PostViewHolder.type -> PostViewHolder.create(parent)
            ErrorViewHolder.type -> ErrorViewHolder.create(parent) { viewModel.load() }
            LoadingViewHolder.type -> LoadingViewHolder.create(parent)
            else -> throw IllegalStateException()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position < size -> PostViewHolder.type
            error != null -> ErrorViewHolder.type
            loading -> LoadingViewHolder.type
            else -> throw IllegalStateException()
        }
    }

    override fun getItemCount(): Int {
        return size + if (loading || error != null) 1 else 0
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (viewHolder) {
            is PostViewHolder -> viewHolder.bind(manager.get(position))
            is ErrorViewHolder -> viewHolder.bind(error!!)
        }
    }

    override fun getItemId(position: Int): Long {
        return when {
            position < size -> position.toLong()
            error != null -> Long.MAX_VALUE
            loading -> Long.MAX_VALUE - 1
            else -> throw IllegalStateException()
        }
    }

    fun reset() {
        disposables.clear()
    }
}

class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    companion object {
        private const val layoutId = R.layout.item_progress
        fun create(parent: ViewGroup) = LoadingViewHolder(createView(parent, layoutId))
        const val type = layoutId
    }
}

class ErrorViewHolder(view: View, clickListener: () -> Unit) : RecyclerView.ViewHolder(view) {

    init {
        itemView.button_retry.setOnClickListener { clickListener() }
    }

    fun bind(throwable: Throwable) {
        itemView.text_error.text = throwable.message ?: itemView.context.getText(R.string.unknown_error)
    }

    companion object {
        private const val layoutId = R.layout.item_error
        fun create(parent: ViewGroup, clickListener: () -> Unit) =
            ErrorViewHolder(createView(parent, layoutId), clickListener)
        const val type = layoutId
    }
}

class PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun bind(post: String) {
        itemView.text_body.text = HtmlCompat.fromHtml(post, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    companion object {
        private const val layoutId = R.layout.item_bashcard
        fun create(parent: ViewGroup) = PostViewHolder(createView(parent, layoutId))
        const val type = layoutId
    }
}

fun createView(parent: ViewGroup, @LayoutRes layoutId: Int) =
    LayoutInflater.from(parent.context).inflate(layoutId, parent, false)!!
