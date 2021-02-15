package com.app.all_my_news

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.*


class MainActivity : AppCompatActivity()  {
    private val client = OkHttpClient()
    private var sectionList: MutableList<Section> = ArrayList()
    private var adapter = RecAdapter(sectionList)
    private val apiKey: String = ""
    private lateinit var refreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        refreshLayout = findViewById(R.id.refreshLayout)

        refreshLayout.setOnRefreshListener{
            fetchNews()
        }
        fetchNews()
    }

    fun fetchNews(){
        refreshLayout.isRefreshing = true;
        val recyclerView: RecyclerView = findViewById(R.id.recview)
        (recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)


        val url: String = "https://newsapi.org/v2/top-headlines?country=fr&apiKey=$apiKey"
        Log.d("API Key", apiKey)
        Log.d("URL", url)
        val request: Request = Request.Builder()
            .url(url)
            .build()
        var results : JSONObject
        var totalResults = 0
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                refreshLayout.isRefreshing = false;
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                refreshLayout.isRefreshing = false;
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
                    val result: String = response.body!!.string()
                    println(result)
                    results = JSONObject(result)
                    totalResults = results.get("totalResults") as Int
                    println(totalResults)
                    val listOfArticles = results.get("articles") as JSONArray
                    for (i in 0 until listOfArticles.length()) {
                        val jsonObject = listOfArticles.getJSONObject(i)
                        val source = jsonObject.getJSONObject("source").optString("name");
                        val author = jsonObject.optString("author")
                        val title = jsonObject.optString("title")
                        val description = jsonObject.optString("description")
                        val url = jsonObject.optString("url")
                        val urlToImage = jsonObject.optString("urlToImage")
                        sectionList.add(Section(title, description, urlToImage, false))
                    }
                    runOnUiThread {
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        })
    }

}
