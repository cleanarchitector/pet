package com.bajiuk.pet.bash.model

import io.reactivex.Completable

interface BashManager {
    fun load(): Completable
    fun get(index: Int): String
    fun size(): Int
}