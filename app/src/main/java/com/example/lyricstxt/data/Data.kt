package com.example.lyricstxt.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase

//Row
@Entity(tableName = "history_table")
data class HistoryEntry (
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val song: String?,
    val artist: String?,
    val img: String?
)

//Data Access Object
@Dao
interface HistoryDao {
    @Query("SELECT * FROM history_table")
    fun getAll() : List<HistoryEntry>

    @Query("SELECT * FROM history_table ORDER BY id DESC LIMIT 1")
    fun getMostRecent() : HistoryEntry?

    @Insert
    fun add(entry: HistoryEntry)
}

//Database
@Database(entities = [HistoryEntry::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao() : HistoryDao
}

//Singleton
object MyDatabase {
    fun getDatabase(context: Context) : AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java, name = "my_db"
        ).allowMainThreadQueries().build()
    }
}
