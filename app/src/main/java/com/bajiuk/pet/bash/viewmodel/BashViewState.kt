package com.bajiuk.pet.bash.viewmodel

class BashViewState(val isLoading: Boolean, val throwable: Throwable? = null) {
    companion object {
        fun errorState(throwable: Throwable) = BashViewState(false, throwable)
        fun loadingState(isLoading: Boolean) = BashViewState(isLoading)
    }
}