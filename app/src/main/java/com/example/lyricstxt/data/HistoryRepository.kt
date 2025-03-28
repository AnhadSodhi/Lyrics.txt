package com.example.lyricstxt.data

class HistoryRepository(private val dao: HistoryDao) {

    fun getAll() : List<HistoryEntry> {
        return dao.getAll()
    }

    fun getMostRecent() : HistoryEntry? {
        return dao.getMostRecent()
    }

    fun insertEntity(entry: HistoryEntry) {
        dao.add(entry)
    }

}
