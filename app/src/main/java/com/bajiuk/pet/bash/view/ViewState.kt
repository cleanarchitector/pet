package com.bajiuk.pet.bash.view

class ViewState(val isLoading: Boolean, val throwable: Throwable? = null) {
    companion object {
        fun errorState(throwable: Throwable) = ViewState(false, throwable)
        fun loadingState(isLoading: Boolean) = ViewState(isLoading)
    }
}