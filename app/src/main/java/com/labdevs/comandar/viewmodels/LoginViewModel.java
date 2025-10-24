package com.labdevs.comandar.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.labdevs.comandar.data.database.AppDatabase;
import com.labdevs.comandar.data.entity.Camarero;
import com.labdevs.comandar.data.repository.AppRepository;
import com.labdevs.comandar.utils.PasswordUtils;

public class LoginViewModel extends AndroidViewModel {
    private final AppRepository repository;
    private final MutableLiveData<Camarero> camareroLogueado = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public LoginViewModel(@NonNull Application application) {
        super(application);
        repository = new AppRepository(application);
    }

    public LiveData<Camarero> getCamareroLogueado() { return camareroLogueado; }
    public LiveData<String> getError() { return error; }

    public void iniciarSesion(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            error.setValue("Email y contraseña son requeridos.");
            return;
        }

        AppDatabase.databaseWriteExecutor.execute(() -> {
            Camarero camarero = repository.iniciarSesion(email);
            if (camarero != null && PasswordUtils.verifyPassword(password, camarero.passwordHash)) {
                camareroLogueado.postValue(camarero);
            } else {
                error.postValue("Email o contraseña incorrectos.");
            }
        });
    }
}
