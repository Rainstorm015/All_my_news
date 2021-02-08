package com.app.all_my_news

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import java.util.ArrayList

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sectionList: MutableList<Section> = ArrayList()

        sectionList.add(Section("title 1", "........", false))
        sectionList.add(Section("title 2", "content 2", false))
        sectionList.add(Section("title 3", "content 3", false))
        val adapter = RecAdapter(sectionList)
        val recyclerView: RecyclerView = findViewById(R.id.recview)
        (recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)
    }
}