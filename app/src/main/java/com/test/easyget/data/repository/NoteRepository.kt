package com.test.easyget.data.repository

import com.test.easyget.data.db.NoteDao
import com.test.easyget.data.model.Note
import kotlinx.coroutines.flow.Flow

class NoteRepository(private val noteDao: NoteDao) {

    val allNotes: Flow<List<Note>> = noteDao.getAll()

    suspend fun insert(note: Note): Long = noteDao.insert(note)

    suspend fun update(note: Note) = noteDao.update(note)

    suspend fun delete(note: Note) = noteDao.delete(note)

    suspend fun getById(id: Long): Note? = noteDao.getById(id)
}
