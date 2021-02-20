package com.app.all_my_news.Model

class NewsApiInfo {
    var id: Int = 0
    var key: String = ""

    constructor()

    constructor(id: Int, key: String){
        this.id = id
        this.key = key
    }
}