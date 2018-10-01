package com.bajiuk.pet.bash.model

import io.reactivex.Completable
import io.reactivex.Flowable

class BashManagerImpl(private val api: BashApi) : BashManager {

    private var posts = mutableListOf<String>()

    override fun load(): Completable {
        return api.get()
            .flatMap { Flowable.fromIterable(it) }
            .map { it.text }
            .toList()
            .doOnSuccess { posts.addAll(it) }
            .ignoreElement()
    }

    override fun get(index: Int) = posts[index]

    override fun size() = posts.size
}