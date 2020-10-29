package com.word.search.puzzle.play.buildsrc

object Versioning {
    const val major = 1
    const val minor = 6
    const val path = 4

    @JvmStatic
    fun generateVersionName() = "$major.$minor.$path"
}