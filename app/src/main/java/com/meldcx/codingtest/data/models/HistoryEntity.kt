package com.meldcx.codingtest.data.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.meldcx.codingtest.service.appConstants.daoUrlColumnInfo
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.*

/*
* To pass throught activity i have added @Parcelize annotation and extended with Parcelable which is simple kotlin parcelize
* I have changed the 'url' column name to daoUrlColumnInfo
* */
@Parcelize
@Entity(tableName = "History")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    val createdAt:String = SimpleDateFormat("dd-M-yyyy hh:mm:ss").format(Date()),//Using default current Date for input purpose only
    val imagePath:String,
    @ColumnInfo(name = daoUrlColumnInfo)var url:String
):Parcelable
