package com.bajiuk.pet.network

import com.google.gson.annotations.SerializedName
import io.reactivex.Flowable
import retrofit2.http.GET


interface BashApi {
    @GET("api/get?site=bash.im&name=bash&num=5")
    fun get(): Flowable<List<Post>>
}

class Post(
        @SerializedName("link") val url: String?,
        @SerializedName("elementPureHtml") val text: String
)