package com.bajiuk.pet.bash.view

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_bash.*
import android.support.v7.widget.RecyclerView
import com.bajiuk.pet.App
import com.bajiuk.pet.R
import com.bajiuk.pet.bash.viewmodel.BashViewModel
import com.bajiuk.pet.recyclerview.MarginItemDecoration


class BashActivity : AppCompatActivity() {

    private lateinit var viewModel: BashViewModel
    private lateinit var adapter: BashRecyclerAdapter

    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bash)

        viewModel = (application as App).bashViewModel

        with(recycler_bash) {
            val layoutManager = LinearLayoutManager(context)
            val adapter = BashRecyclerAdapter(viewModel, (application as App).bashManager)
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

        button_bash_retry.setOnClickListener { viewModel.load() }

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
