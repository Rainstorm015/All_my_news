package com.app.all_my_news

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.app.all_my_news.DBHelper.DBHelper
import com.app.all_my_news.Model.NewsApiInfo

class SettingsActivity : AppCompatActivity() {
    internal lateinit var db: DBHelper
    internal var listNewsApiInfo: List<NewsApiInfo> = ArrayList<NewsApiInfo>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        db = DBHelper(this)
        listNewsApiInfo = db.allNewsApiInfo
        Log.d("List from db in Intent", "size = " + listNewsApiInfo.size)
        val edit_key: EditText = findViewById(R.id.newsapikey_edit)
        val apply_button: Button = findViewById(R.id.apply)
        if(listNewsApiInfo.isEmpty()){
            edit_key.setText("")
        } else {
            edit_key.setText(listNewsApiInfo[0].key)
        }
        apply_button.setOnClickListener {
            Log.d("Settings", "apply button")
            if(listNewsApiInfo.isEmpty()){
                Log.d("DB Create", "Create new key")
                var newItem: NewsApiInfo = NewsApiInfo(0,edit_key.text.toString())
                db.addNewsApiInfo(newItem)
            } else {
                Log.d("DB Update", "Update key")
                listNewsApiInfo[0].key = edit_key.text.toString()
                db.updateNewsApiInfo(listNewsApiInfo[0])
            }
            setResult(RESULT_OK)
            finish()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        //exitProcess(0)
    }
}