package com.bajiuk.pet

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.bajiuk.pet.bash.view.FeedAdapter
import com.bajiuk.pet.bash.view.PostViewHolder
import com.bajiuk.pet.bash.view.ViewModel
import com.bajiuk.pet.recyclerview.MarginItemDecoration
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: ViewModel
    private lateinit var adapter: FeedAdapter

    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = (application as App).bashViewModel

        with (recycler_bash) {
            val layoutManager = LinearLayoutManager(context)
            val adapter = FeedAdapter(viewModel, (application as App).bashManager)
            setAdapter(adapter)
            setLayoutManager(layoutManager)
            setHasFixedSize(true)
            addItemDecoration(MarginItemDecoration(resources.getDimension(R.dimen.indent_half).toInt()))
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val last = layoutManager.findLastVisibleItemPosition()
                    val holder = recyclerView.findViewHolderForLayoutPosition(last)
                    if (last + 1 == adapter.itemCount && holder is PostViewHolder) {
                        viewModel.load()
                    }
                }
            })
        }

        viewModel.load()

        with(disposables) {
            add(viewModel.mainStateSubject.subscribe {
                progress_bash.visibility = if (it.isLoading) View.VISIBLE else View.GONE
                text_bash_error.visibility = if (it.throwable != null) View.VISIBLE else View.GONE
                text_bash_error.text = it.throwable.toString()
            })
        }
    }

    override fun onDestroy() {
        adapter.reset()
        viewModel.reset()
        disposables.clear()
        super.onDestroy()
    }
}
