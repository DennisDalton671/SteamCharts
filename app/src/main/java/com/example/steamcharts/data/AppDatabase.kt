package com.example.steamcharts.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Game::class], version = 5)
abstract class AppDatabase : RoomDatabase() {

    abstract fun gameDao(): GameDao



    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {

            val MIGRATION_1_2: Migration = object : Migration(1, 2) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    // Implement the migration logic here, for example:
                    database.execSQL("ALTER TABLE games ADD COLUMN searchName TEXT NOT NULL DEFAULT ''")
                }
            }

            val MIGRATION_2_3: Migration = object : Migration(2, 3) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    // Implement the migration logic here, for example:
                    database.execSQL("ALTER TABLE games ADD COLUMN headerImage TEXT NOT NULL DEFAULT ''")
                }
            }

            val MIGRATION_3_4: Migration = object : Migration(3, 4) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    // Add the priceUS column as TEXT. Assuming prices are stored as text.
                    database.execSQL("ALTER TABLE games ADD COLUMN priceUS TEXT NOT NULL DEFAULT ''")

                    // Add the discount column as INTEGER. Assuming discounts are stored as integers.
                    database.execSQL("ALTER TABLE games ADD COLUMN discount INTEGER NOT NULL DEFAULT 0")

                    // Add the shortDescription column as TEXT.
                    database.execSQL("ALTER TABLE games ADD COLUMN shortDescription TEXT NOT NULL DEFAULT ''")
                }
            }

            val MIGRATION_4_5: Migration = object : Migration(4, 5) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    // Implement the migration logic here, for example:
                    database.execSQL("ALTER TABLE games ADD COLUMN reviewScore INTEGER NOT NULL DEFAULT 0")
                }
            }

            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "games"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }


}