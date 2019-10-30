package com.gericass.listview_sample

import com.gericass.listview_sample.model.Article
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface QiitaClient {

    // APIのエンドポイントごとにメソッドを定義する
    @GET("/api/v2/items")
    fun search(@Query("query") query: String): Observable<List<Article>>
}