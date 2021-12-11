package com.cosmic.room.db;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.cosmic.room.dao.SearchDAO;
import com.cosmic.room.model.SearchTerm;

@Database(entities = {SearchTerm.class}, version = 1)
public abstract class SearchDatabase extends RoomDatabase {

    private static SearchDatabase instance;
    public abstract SearchDAO searchDAO();

    public static synchronized SearchDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context, SearchDatabase.class, "searches.db").fallbackToDestructiveMigration().build();
        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void>{

        private SearchDAO searchDAO;

        private PopulateDbAsyncTask(SearchDatabase database){
            searchDAO = database.searchDAO();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            searchDAO.insert(new SearchTerm());
            return null;
        }
    }

}
