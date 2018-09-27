package com.bajiuk.pet.bash.model

import com.google.gson.annotations.SerializedName

class Post(
    @SerializedName("elementPureHtml") val text: String
)