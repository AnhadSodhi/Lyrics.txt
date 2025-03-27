package com.example.lyricstxt.data

class HistoryRepository(private val dao: HistoryDao) {

    fun insertEntity(entry: HistoryEntry) {
        dao.add(entry)
    }

    fun getAll() : List<HistoryEntry> {
        return dao.getAll()
    }

    fun getMostRecent() : HistoryEntry? {
        return dao.getMostRecent()
    }
}
