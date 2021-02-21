package com.app.all_my_news.DBHelper

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.app.all_my_news.Model.NewsApiInfo

class DBHelper(context: Context): SQLiteOpenHelper(context, DATABESE_NAME, null, DATABASE_VER) {
    companion object {
        private val DATABASE_VER = 1
        private val DATABESE_NAME = "AllMyNews.db"

        //Table
        private val TABLE_NAME = "NewsApiInfo"
        private val COL_ID = "id"
        private val COL_KEY = "key"
        private val COL_COUNTRY = "country"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE_QUERY: String = ("CREATE TABLE $TABLE_NAME (" +
                "$COL_ID INTEGER PRIMARY KEY," +
                "$COL_KEY TEXT," +
                "$COL_COUNTRY TEXT)")
        db!!.execSQL(CREATE_TABLE_QUERY)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db!!)
    }

    //CRUD
    val allNewsApiInfo:List<NewsApiInfo>
        get(){
            val listNewsApiInfo = ArrayList<NewsApiInfo>()
            val selectQuery = "SELECT * FROM $TABLE_NAME"
            val db: SQLiteDatabase = this.writableDatabase
            val cursor: Cursor = db.rawQuery(selectQuery, null)
            if(cursor.moveToFirst()){
                do{
                    val newsApiInfo = NewsApiInfo()
                    newsApiInfo.id = cursor.getInt(cursor.getColumnIndex(COL_ID))
                    newsApiInfo.key = cursor.getString(cursor.getColumnIndex(COL_KEY))
                    newsApiInfo.country = cursor.getString(cursor.getColumnIndex(COL_COUNTRY))
                    listNewsApiInfo.add(newsApiInfo)
                } while(cursor.moveToNext())
            }
            db.close()
            return listNewsApiInfo
        }

    fun addNewsApiInfo(newsApiInfo: NewsApiInfo){
        val db: SQLiteDatabase = this.writableDatabase
        val values = ContentValues()
        values.put(COL_ID, newsApiInfo.id)
        values.put(COL_KEY, newsApiInfo.key)
        values.put(COL_COUNTRY, newsApiInfo.country)

        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun updateNewsApiInfo(newsApiInfo: NewsApiInfo): Int{
        val db: SQLiteDatabase = this.writableDatabase
        val values = ContentValues()
        values.put(COL_ID, newsApiInfo.id)
        values.put(COL_KEY, newsApiInfo.key)
        values.put(COL_COUNTRY, newsApiInfo.country)

        return db.update(TABLE_NAME, values, "$COL_ID=?", arrayOf(newsApiInfo.id.toString()))
    }

    fun deleteNewsApiInfo(newsApiInfo: NewsApiInfo){
        val db: SQLiteDatabase = this.writableDatabase
        db.delete(TABLE_NAME, "$COL_ID=?", arrayOf(newsApiInfo.id.toString()))
        db.close()
    }
}