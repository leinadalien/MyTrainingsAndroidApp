package com.ldnprod.timer.Dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ldnprod.timer.Entities.Exercise

@Database(entities = [Exercise::class, TrainingDao::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract val trainingDao: TrainingDao
    abstract val exerciseDao: ExerciseDao
}