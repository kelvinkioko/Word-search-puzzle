package com.word.search.puzzle.play.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper

class WordSearchReader@JvmOverloads constructor(
    context: Context
) : SQLiteAssetHelper(context, "wordsearch.db", null, 1) {

    private var database: SQLiteDatabase? = null

    init {
        setForcedUpgrade()
    }

    fun open() {
        database = readableDatabase
    }

    fun getRandomWords(count: Int, selectedLangTable: String): Array<String?> {
        val cursor = database!!.query(
            "$selectedLangTable Order BY RANDOM() LIMIT $count", arrayOf("*"),
            null,
            null,
            null,
            null,
            null
        )
        cursor.moveToFirst()
        val words = arrayOfNulls<String>(cursor.count)
        var i = 0
        while (!cursor.isAfterLast) {
            println("cursor ${cursor.getString(0)}")
            words[i++] = cursor.getString(0)
            cursor.moveToNext()
        }
        cursor.close()
        return words
    }
}
