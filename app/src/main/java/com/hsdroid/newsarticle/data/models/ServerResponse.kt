package com.hsdroid.newsarticle.data.models

data class ServerResponse(
    val articles: List<Article>,
    val status: String
)