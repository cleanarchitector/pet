package com.bajiuk.pet.bash.model

import io.reactivex.Flowable
import retrofit2.http.GET


interface Api {
    @GET("api/random?num=10")
    fun get(): Flowable<List<Post>>
}
