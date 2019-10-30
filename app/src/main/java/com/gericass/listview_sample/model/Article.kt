package com.gericass.listview_sample.model

import java.io.Serializable

// APIのレスポンスをバインドするクラス
data class Article(
        val id: String,
        val title: String,
        val url: String,
        val user: User
) : Serializable {
    data class User(
            val id: String,
            val name: String,
            val profile_image_url: String
    ) : Serializable
}