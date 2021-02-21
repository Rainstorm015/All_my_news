package com.app.all_my_news.Model

class NewsApiInfo {
    var id: Int = 0
    var key: String = ""
    var country: String = ""

    constructor()

    constructor(id: Int, key: String){
        this.id = id
        this.key = key
    }

    constructor(id: Int, key: String, country: String){
        this.id = id
        this.key = key
        this.country = country
    }
}