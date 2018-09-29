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



class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: ViewModel
    private lateinit var adapter: FeedAdapter

    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModel((application as App).bashManager)
        adapter = FeedAdapter(viewModel, (application as App).bashManager)

        recycler_bash.adapter = adapter


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
                val visibleItemCount = recyclerView.layoutManager!!.childCount
                val totalItemCount = recyclerView.layoutManager!!.getItemCount()
                val pastVisibleItems = (recyclerView.layoutManager as LinearLayoutManager?)!!.findFirstVisibleItemPosition()
                if (pastVisibleItems + visibleItemCount >= totalItemCount) {
                    viewModel.load()
                }
            }
        })

    }

    override fun onDestroy() {
        viewModel.reset()
        disposables.clear()
        super.onDestroy()
    }
}
