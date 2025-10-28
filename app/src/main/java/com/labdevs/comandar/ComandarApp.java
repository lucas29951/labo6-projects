package com.labdevs.comandar;

import android.app.Application;

import com.labdevs.comandar.data.database.AppDatabase;

public class ComandarApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Forzamos la inicialización y el poblado de la base de datos al inicio
        AppDatabase.databaseWriteExecutor.execute(() -> {
            // Obtenemos la instancia, lo que prepara la BD
            AppDatabase db = AppDatabase.getDatabase(this);
            // Realizamos una operación de lectura inofensiva para forzar
            // la ejecución del callback onCreate si es la primera vez.
            db.camareroDao().getCamareroByEmail("");
        });
    }
}
