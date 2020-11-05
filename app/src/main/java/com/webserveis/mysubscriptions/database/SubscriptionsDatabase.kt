package com.webserveis.mysubscriptions.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.webserveis.mysubscriptions.models.SubscriptionsDB

@Database(entities = [SubscriptionsDB::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class SubscriptionsDatabase : RoomDatabase() {

    abstract fun subscriptionsDao(): SubscriptionsDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: SubscriptionsDatabase? = null

        fun getDatabase(context: Context): SubscriptionsDatabase {
            val tempInstance = this.INSTANCE
            if (tempInstance != null) return tempInstance

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, SubscriptionsDatabase::class.java,
                    "subscriptions_database"
                ).fallbackToDestructiveMigration().build()
                this.INSTANCE = instance
                return instance
            }
        }

        fun destroyDataBase() {
            this.INSTANCE = null
        }
    }
}