package com.gericass.listview_sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    // lazyを使うとこの変数が初めて呼ばれるときに自動的に初期化される
    private val retrofit: Retrofit by lazy { createRetrofit() }
    private val client: QiitaClient by lazy { retrofit.create(QiitaClient::class.java) }

    // ListViewのAdapter
    private lateinit var adapter: ListAdapter
    // ListView
    private lateinit var listView: ListView

    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpListView()
        requestItem()
    }

    // ListViewにAdapterをセット
    private fun setUpListView() {
        listView = findViewById(R.id.list_view)
        adapter = ListAdapter(this)
        listView.adapter = adapter
    }

    // APIを叩く
    private fun requestItem() {
        client.search("Kotlin")
            .subscribeOn(Schedulers.io()) // 非同期処理にする
            .observeOn(AndroidSchedulers.mainThread()) // データの表示はメインスレッド
            .subscribe({ data ->
                // 受け取ったデータはここに流れる
                adapter.run {
                    // ListViewのadapterにapiから取ってきたデータを追加
                    articles = data
                    // データが追加されたことをadapterに通知する
                    notifyDataSetChanged()
                }
            }) {
                // エラー起きたらログ表示
                Log.e(MainActivity::class.java.simpleName, it.toString())
            }.addTo(disposable)
    }

    // retrofit(APIを叩くライブラリ)のインスタンス生成
    private fun createRetrofit(): Retrofit {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val okHttpClient = OkHttpClient()
            .newBuilder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
        return Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl(BASE_URL)
            .build()
    }

    // メモリリーク対策
    override fun onDestroy() {
        disposable.clear()
        super.onDestroy()
    }

    companion object {
        private const val BASE_URL = "https://qiita.com"
    }
}
