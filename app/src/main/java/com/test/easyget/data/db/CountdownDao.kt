package com.test.easyget.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.test.easyget.data.model.Countdown
import kotlinx.coroutines.flow.Flow

@Dao
interface CountdownDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(countdown: Countdown): Long

    @Update
    suspend fun update(countdown: Countdown)

    @Delete
    suspend fun delete(countdown: Countdown)

    @Query("SELECT * FROM countdowns ORDER BY endTime ASC")
    fun getAll(): Flow<List<Countdown>>

    @Query("SELECT * FROM countdowns WHERE id = :id")
    suspend fun getById(id: Long): Countdown?
}
