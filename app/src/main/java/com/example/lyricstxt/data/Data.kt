package com.example.lyricstxt.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlin.reflect.typeOf

//Row
@Entity(tableName = "history_table")
data class HistoryEntry (
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val song: String?,
    val artist: String?,
    val img: String?
) {
    override fun equals(other: Any?) : Boolean {
        if (other is HistoryEntry) {
            return song == other.song &&
                    artist == other.artist &&
                    img == other.img
        }
        else
            return false
    }
}

//Data Access Object
@Dao
interface HistoryDao {
    @Query("SELECT * FROM history_table ORDER BY id DESC")
    fun getAll() : List<HistoryEntry>

    @Query("SELECT * FROM history_table ORDER BY id DESC LIMIT 1")
    fun getMostRecent() : HistoryEntry?

    @Insert
    fun add(entry: HistoryEntry)

    @Query("DELETE FROM history_table")
    fun wipe()
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
