package com.word.search.puzzle.play.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plays")
data class PlaysEntity(

    @PrimaryKey(autoGenerate = true) var id: Int,

    @ColumnInfo(name = "clipboard_url") var clipboard_url: String,
    @ColumnInfo(name = "image_url") var image_url: String
)
