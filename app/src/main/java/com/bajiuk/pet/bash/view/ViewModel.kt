package com.bajiuk.pet.bash.view

import android.text.Spannable
import com.bajiuk.pet.bash.model.Manager
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class ViewModel(var manager: Manager) {
    var feedSizeSubject = BehaviorSubject.createDefault(0)
    var mainStateSubject = BehaviorSubject.createDefault(ViewState.loadingState(false))
    var listStateSubject = BehaviorSubject.createDefault(ViewState.loadingState(false))

    var state: State = EMPTY()

    fun load() {
        state.load()
    }

    fun reset() {
        state.reset()
    }

    interface State {
        fun load() {}

        fun onError(throwable: Throwable) {}
        fun onData(size: Int) {}

        fun reset() {}
    }

    inner class EMPTY : State {
        override fun load() {
            state = LOADING()
        }
    }

    inner class LOADING : State {

        init {
            request()
            mainStateSubject.onNext(ViewState.loadingState(true))
        }

        override fun onError(throwable: Throwable) {
            mainStateSubject.onNext(ViewState.errorState(throwable))
            state = ERROR()
        }

        override fun onData(size: Int) {
            feedSizeSubject.onNext(size)
            mainStateSubject.onNext(ViewState.loadingState(false))
            state = DATA()
        }

        override fun reset() {
            resetRequest()
        }
    }

    inner class ERROR : State {
        override fun load() {
            state = LOADING()
        }
    }

    inner class DATA : State {
        override fun load() {
            state = DATA_LOADING()
        }
    }

    inner class DATA_LOADING : State {
        init {
            request()
            listStateSubject.onNext(ViewState.loadingState(true))
        }

        override fun onError(throwable: Throwable) {
            listStateSubject.onNext(ViewState.errorState(throwable))
            state = DATA_ERROR()
        }

        override fun onData(size: Int) {
            feedSizeSubject.onNext(size)
            listStateSubject.onNext(ViewState.loadingState(false))
            state = DATA()
        }

        override fun reset() {
            resetRequest()
        }
    }

    inner class DATA_ERROR : State {
        override fun load() {
            state = DATA_LOADING()
        }
    }

    private var disposable: Disposable? = null
    private fun request() {
        disposable = manager.load()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                state.onData(manager.size())
            }, {
                state.onError(it)
            })
    }
    private fun resetRequest() {
        disposable?.dispose()
    }
}

