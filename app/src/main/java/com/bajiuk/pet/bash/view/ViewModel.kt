package com.bajiuk.pet.bash.view

import android.text.Spannable

class ViewModel {
    var posts : MutableList<Spannable> = mutableListOf()

}

interface State {
    fun load() = {}
    fun newData(data : List<Spannable>) = {}
    fun fail() = {}
    fun release() = {}
}