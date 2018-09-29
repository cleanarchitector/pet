package com.bajiuk.pet

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.bajiuk.pet.bash.view.FeedAdapter
import com.bajiuk.pet.bash.view.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import io.reactivex.internal.disposables.DisposableContainer
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import android.support.v7.widget.RecyclerView
import com.bajiuk.pet.bash.view.PostViewHolder
import com.bajiuk.pet.recyclerview.MarginItemDecoration


class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: ViewModel
    private lateinit var adapter: FeedAdapter
    private lateinit var layoutManager: LinearLayoutManager

    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bashManager = (application as App).bashManager
        viewModel = (application as App).bashViewModel


        adapter = FeedAdapter(viewModel, bashManager)
        layoutManager = recycler_bash.layoutManager as LinearLayoutManager

        recycler_bash.adapter = adapter
        recycler_bash.setHasFixedSize(true)
        recycler_bash.addItemDecoration(
            MarginItemDecoration(resources.getDimension(R.dimen.indent_half).toInt())
        )

        viewModel.load()

        with(disposables) {
            add(viewModel.feedSizeSubject.subscribe { adapter.notifyDataSetChanged() })
            add(viewModel.mainStateSubject.subscribe {
                progress_bash.visibility = if (it.isLoading) View.VISIBLE else View.GONE
                text_bash_error.visibility = if (it.throwable != null) View.VISIBLE else View.GONE
                text_bash_error.text = it.throwable.toString()
            })
        }

        recycler_bash.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val last = layoutManager.findLastVisibleItemPosition()
                val holder = recyclerView.findViewHolderForLayoutPosition(last)
                if (last + 1 == adapter.itemCount && holder is PostViewHolder) {
                    viewModel.load()
                }
            }
        })

    }

    override fun onDestroy() {
        adapter.reset()
        viewModel.reset()
        disposables.clear()
        super.onDestroy()
    }
}
