package com.bajiuk.pet.bash.model

import io.reactivex.Completable
import io.reactivex.Flowable
import java.lang.Thread.sleep

class Manager(private val api: Api) {

    private var posts = mutableListOf<String>()

    fun load(): Completable {
        return api.get()
            .flatMap { Flowable.fromIterable(it) }
            .map { it.text }
            .toList()
            .doOnSuccess {
                sleep(5000)
                posts.addAll(it) }
            .toCompletable()
    }

    fun get(index : Int) = posts[index]
    fun size() = posts.size
}