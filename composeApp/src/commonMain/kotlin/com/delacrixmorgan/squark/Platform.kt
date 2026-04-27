package com.delacrixmorgan.squark

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform