package com.app.all_my_news

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.app.all_my_news.Adapter.RecAdapter
import com.app.all_my_news.DBHelper.DBHelper
import com.app.all_my_news.Model.NewsApiInfo
import com.app.all_my_news.Model.Section
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException


class MainActivity : AppCompatActivity()  {
    private val client = OkHttpClient()
    private var sectionList: MutableList<Section> = mutableListOf()
    private var adapter = RecAdapter(sectionList)
    private var apiKey: String = ""
    private lateinit var error_api: TextView
    private lateinit var refreshLayout: SwipeRefreshLayout
    internal lateinit var db: DBHelper
    internal var listNewsApiInfo: List<NewsApiInfo> = ArrayList<NewsApiInfo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        refreshLayout = findViewById(R.id.refreshLayout)
        error_api = findViewById(com.app.all_my_news.R.id.api_error)
        db = DBHelper(this)
        refreshApiKey()

        refreshLayout.setOnRefreshListener{
            fetchNews()
        }
        if(apiKey != "") {
            error_api.isVisible = false
            fetchNews()
        } else {
            error_api.isVisible = true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            R.id.settings -> {
                Log.d("Menu item", "Settings")
                val intent = Intent(this, SettingsActivity::class.java)
                startActivityForResult(intent,0)
                /*val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)*/
                return true
            }
        }
        return super.onOptionsItemSelected(item)
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
                runOnUiThread {
                    error_api.isVisible = true
                }
            }

            override fun onResponse(call: Call, response: Response) {
                refreshLayout.isRefreshing = false;
                sectionList.clear()
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
                        sectionList.add(Section(this@MainActivity,title, description, urlToImage, url, true))
                    }
                    runOnUiThread {
                        error_api.isVisible = false
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        })
    }

    fun refreshApiKey(){
        listNewsApiInfo = db.allNewsApiInfo
        apiKey = if(!listNewsApiInfo.isEmpty()){
            listNewsApiInfo[0].key
        } else {
            ""
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            Log.d("Settings", "Updated successfully")
            refreshApiKey()
            fetchNews()
        }
    }

}
