package com.example.android.braindump;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Task.class}, version = 2)
@TypeConverters(DateConverter.class)
public abstract class AppDatabase extends RoomDatabase {
    public abstract TaskDao taskDao();

    private static AppDatabase INSTANCE;


    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE task"
                    + " ADD COLUMN audiodesc TEXT");
        }
    };



    public static com.example.android.braindump.AppDatabase getINSTANCE(Context context) {
        if(INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "task")
                    .addMigrations(MIGRATION_1_2)
                    .build();
        }
        return INSTANCE;

    }


}
