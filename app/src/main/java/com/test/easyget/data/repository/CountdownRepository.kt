package com.test.easyget.data.repository

import com.test.easyget.data.db.CountdownDao
import com.test.easyget.data.model.Countdown
import kotlinx.coroutines.flow.Flow

class CountdownRepository(private val countdownDao: CountdownDao) {

    val allCountdowns: Flow<List<Countdown>> = countdownDao.getAll()

    suspend fun insert(countdown: Countdown): Long = countdownDao.insert(countdown)

    suspend fun update(countdown: Countdown) = countdownDao.update(countdown)

    suspend fun delete(countdown: Countdown) = countdownDao.delete(countdown)

    suspend fun getById(id: Long): Countdown? = countdownDao.getById(id)
}
