package com.ldnprod.timer.DI

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.ldnprod.timer.Dao.AppDatabase
import com.ldnprod.timer.Implementations.ExerciseRepository
import com.ldnprod.timer.Implementations.TrainingRepository
import com.ldnprod.timer.Interfaces.IExerciseRepository
import com.ldnprod.timer.Interfaces.ITrainingRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MainModule {
    @Provides
    @Singleton
    fun provideAppDatabase(app: Application): AppDatabase {
        return Room.databaseBuilder(
            app,
            AppDatabase::class.java,
            "database"
        ).build()
    }
    @Provides
    @Singleton
    fun provideExerciseRepository(db: AppDatabase): IExerciseRepository {
        return ExerciseRepository(db.exerciseDao)
    }
    @Provides
    @Singleton
    fun provideTrainingRepository(db: AppDatabase): ITrainingRepository {
        return TrainingRepository(db.trainingDao)
    }
}