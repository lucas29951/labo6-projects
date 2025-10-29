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

public class ChangePasswordViewModel extends AndroidViewModel {
    private final AppRepository repository;
    private int camareroId;

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public LiveData<String> getError() { return _error; }

    private final MutableLiveData<Boolean> _updateSuccess = new MutableLiveData<>(false);
    public LiveData<Boolean> getUpdateSuccess() { return _updateSuccess; }

    public ChangePasswordViewModel(@NonNull Application application) {
        super(application);
        repository = new AppRepository(application);
    }

    public void setCamareroId(int id) {
        this.camareroId = id;
    }

    public void cambiarContraseña(String actual, String nueva, String confirmacion) {
        if (actual.isEmpty() || nueva.isEmpty() || confirmacion.isEmpty()) {
            _error.setValue("Todos los campos son obligatorios.");
            return;
        }
        if (!nueva.equals(confirmacion)) {
            _error.setValue("Las contraseñas nuevas no coinciden.");
            return;
        }
        if (nueva.length() < 6) {
            _error.setValue("La nueva contraseña debe tener al menos 6 caracteres.");
            return;
        }

        AppDatabase.databaseWriteExecutor.execute(() -> {
            // Obtenemos el camarero de forma síncrona dentro del hilo de fondo.
            // Necesitamos un método síncrono en el DAO para esto.
            Camarero camarero = repository.getCamareroByIdSync(camareroId);

            if (camarero != null) {
                if (PasswordUtils.verifyPassword(actual, camarero.passwordHash)) {
                    camarero.passwordHash = PasswordUtils.hashPassword(nueva);
                    repository.actualizarPerfilCamarero(camarero);
                    _updateSuccess.postValue(true);
                } else {
                    _error.postValue("La contraseña actual es incorrecta.");
                }
            } else {
                _error.postValue("Error al recuperar los datos del usuario.");
            }
        });
    }

    public void onNavigationDone() {
        _updateSuccess.setValue(false);
        _error.setValue(null);
    }
}
