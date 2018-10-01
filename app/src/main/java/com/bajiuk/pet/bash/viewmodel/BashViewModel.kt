package com.bajiuk.pet.bash.viewmodel

import com.bajiuk.pet.bash.model.BashManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject

class BashViewModel(var manager: BashManager) {
    val feedSizeSubject = BehaviorSubject.createDefault(0)
    val mainStateSubject = BehaviorSubject.createDefault(
        BashViewState.loadingState(
            false
        )
    )
    val listStateSubject = BehaviorSubject.createDefault(
        BashViewState.loadingState(
            false
        )
    )

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
            mainStateSubject.onNext(BashViewState.loadingState(false))
            listStateSubject.onNext(BashViewState.loadingState(false))
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
            mainStateSubject.onNext(BashViewState.loadingState(true))
        }

        override fun onError(throwable: Throwable) {
            mainStateSubject.onNext(BashViewState.errorState(throwable))
            state = ERROR()
        }

        override fun onData(size: Int) {
            feedSizeSubject.onNext(size)
            mainStateSubject.onNext(BashViewState.loadingState(false))
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
            listStateSubject.onNext(BashViewState.loadingState(true))
        }

        override fun onError(throwable: Throwable) {
            listStateSubject.onNext(BashViewState.errorState(throwable))
            state = DATA_ERROR()
        }

        override fun onData(size: Int) {
            feedSizeSubject.onNext(size)
            listStateSubject.onNext(BashViewState.loadingState(false))
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

