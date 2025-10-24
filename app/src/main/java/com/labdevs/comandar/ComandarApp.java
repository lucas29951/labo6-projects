package com.labdevs.comandar;

import android.app.Application;

import com.labdevs.comandar.data.database.AppDatabase;

public class ComandarApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Esta línea asegura que la instancia de la base de datos se cree
        // al iniciar la aplicación, lo que ejecutará el callback 'onCreate'
        // la primera vez para poblar los datos.
        AppDatabase.getDatabase(this);
    }
}
