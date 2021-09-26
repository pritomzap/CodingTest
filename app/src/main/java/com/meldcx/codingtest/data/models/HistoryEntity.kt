package com.meldcx.codingtest.data.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.meldcx.codingtest.service.appConstants.daoUrlColumnInfo
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
@Entity(tableName = "History")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val createdAt:String = SimpleDateFormat("dd-M-yyyy hh:mm:ss").format(Date()),//Using default current Date for input purpose only
    val imagePath:String,
    @ColumnInfo(name = daoUrlColumnInfo)var url:String
):Parcelable
