package com.bajiuk.pet

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.bajiuk.pet.bash.view.FeedAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var disposable: Disposable
    val adapter = FeedAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bash_recycler.adapter = adapter

        disposable = (applicationContext as App).bashApi.get()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({ adapter.items = it },
                        { it.printStackTrace()},
                        { })

    }

    override fun onDestroy() {
        disposable.dispose()
        super.onDestroy()
    }
}
