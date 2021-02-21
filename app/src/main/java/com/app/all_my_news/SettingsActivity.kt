package com.app.all_my_news

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.app.all_my_news.DBHelper.DBHelper
import com.app.all_my_news.Model.NewsApiInfo
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class SettingsActivity : AppCompatActivity() {
    private val client = OkHttpClient()
    internal lateinit var db: DBHelper
    internal var listNewsApiInfo: List<NewsApiInfo> = ArrayList()
    private var selectedCountry: String = ""
    private lateinit var edit_key: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        db = DBHelper(this)
        listNewsApiInfo = db.allNewsApiInfo
        Log.d("List from db in Intent", "size = " + listNewsApiInfo.size)
        edit_key = findViewById(R.id.newsapikey_edit)
        val apply_button: Button = findViewById(R.id.apply)
        if(listNewsApiInfo.isEmpty()){
            edit_key.setText("")
        } else {
            edit_key.setText(listNewsApiInfo[0].key)
            fetchCountries()
        }
        apply_button.setOnClickListener {
            Log.d("Settings", "apply button")
            if(listNewsApiInfo.isEmpty()){
                Log.d("DB Create", "Create new key")
                var newItem: NewsApiInfo = NewsApiInfo(0,edit_key.text.toString(), selectedCountry)
                db.addNewsApiInfo(newItem)
            } else {
                Log.d("DB Update", "Update key")
                listNewsApiInfo[0].key = edit_key.text.toString()
                listNewsApiInfo[0].country = selectedCountry
                db.updateNewsApiInfo(listNewsApiInfo[0])
            }
            setResult(RESULT_OK)
            finish()
        }

        edit_key.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                fetchCountries()
            }
        })


    }
    override fun onDestroy() {
        super.onDestroy()
        //exitProcess(0)
    }

    fun fetchCountries(){
        val url: String = "https://newsapi.org/v2/sources?&apiKey=${edit_key.text}"
        Log.d("URL", url)
        val request: Request = Request.Builder()
            .url(url)
            .build()
        var results : JSONObject
        var totalResults = 0
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
                    val result: String = response.body!!.string()
                    results = JSONObject(result)
                    var listOfCountries : MutableList<String> = mutableListOf()
                    val listOfSources = results.get("sources") as JSONArray
                    for (i in 0 until listOfSources.length()) {
                        val jsonObject = listOfSources.getJSONObject(i)
                        val country = jsonObject.optString("country");
                        var isFound = false
                        for (s in listOfCountries) {
                            if (s == country) {
                                isFound = true
                                break
                            }
                        }
                        if(!isFound){
                            listOfCountries.add(country)
                        }
                    }
                    println(listOfCountries)
                    runOnUiThread {
                        val spinner = findViewById<Spinner>(R.id.spinner_country)
                        if (spinner != null) {
                            val adapter = ArrayAdapter(this@SettingsActivity, android.R.layout.simple_spinner_item, listOfCountries)
                            spinner.adapter = adapter

                            spinner.onItemSelectedListener = object :
                                AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(parent: AdapterView<*>,
                                                            view: View, position: Int, id: Long) {
                                    selectedCountry = listOfCountries[position]
                                    println(selectedCountry)
                                }

                                override fun onNothingSelected(parent: AdapterView<*>?) {
                                    TODO("Not yet implemented")
                                }
                            }
                        }
                        if(listNewsApiInfo.isNotEmpty()){
                            if(listNewsApiInfo[0].country != ""){
                                var pos: Int = 0
                                for(s in listOfCountries){
                                    if(s == listNewsApiInfo[0].country){
                                        break
                                    }
                                    pos += 1
                                }
                                spinner.setSelection(pos)
                            }
                        }

                    }
                }
            }
        })
    }
}