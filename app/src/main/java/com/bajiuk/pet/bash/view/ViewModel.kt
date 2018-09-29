package com.bajiuk.pet.bash.view

import com.bajiuk.pet.bash.model.Manager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject

class ViewModel(var manager: Manager) {
    val feedSizeSubject = BehaviorSubject.createDefault(0)
    val mainStateSubject = BehaviorSubject.createDefault(ViewState.loadingState(false))
    val listStateSubject = BehaviorSubject.createDefault(ViewState.loadingState(false))

    var state: State = EMPTY()

    fun load() {
        state.load()
    }

    fun reset() {
        state.reset()
    }

    open inner class State {
        open fun load() {}

        open fun onError(throwable: Throwable) {}
        open fun onData(size: Int) {}

        open fun reset() {
            feedSizeSubject.onNext(0)
            mainStateSubject.onNext(ViewState.loadingState(false))
            listStateSubject.onNext(ViewState.loadingState(false))
            state = EMPTY()
        }
    }

    inner class EMPTY : State() {
        override fun load() {
            state = LOADING()
        }
    }

    inner class LOADING : State() {

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

    inner class ERROR : State() {
        override fun load() {
            state = LOADING()
        }
    }

    inner class DATA : State() {
        override fun load() {
            state = DATA_LOADING()
        }
    }

    inner class DATA_LOADING : State() {
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

    inner class DATA_ERROR : State() {
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

