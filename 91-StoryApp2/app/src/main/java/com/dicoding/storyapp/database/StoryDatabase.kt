package com.dicoding.storyapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dicoding.storyapp.network.ListStoryItem

@Database(
    entities = [ListStoryItem::class, RemoteKeys::class],
    version = 2,
    exportSchema = false
)
abstract class StoryDatabase : RoomDatabase() {

    abstract fun storyDao(): StoryDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {
        @Volatile
        private var INSTANCE: StoryDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): StoryDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    StoryDatabase::class.java, DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }

        // @JvmStatic
        // fun getDatabase(context: Context, applicationScope: CoroutineScope): StoryDatabase {
        //     if (INSTANCE == null) {
        //         synchronized(StoryDatabase::class.java) {
        //             INSTANCE = Room.databaseBuilder(context.applicationContext,
        //                 StoryDatabase::class.java, DATABASE_NAME)
        //                 .fallbackToDestructiveMigration()
        //                 .createFromAsset(DATABASE_NAME + ".db")
        //                 .addCallback(object: Callback() {
        //                     override fun onCreate(db: SupportSQLiteDatabase) {
        //                         super.onCreate(db)
        //                         INSTANCE?.let { database ->
        //                             applicationScope.launch {
        //                                 val storyDao = database.storyDao()
        //                             }
        //                         }
        //                     }
        //                 })
        //                 .build()
        //         }
        //     }
        //     return INSTANCE as StoryDatabase
        // }

        private const val DATABASE_NAME = "story_database"
    }
}