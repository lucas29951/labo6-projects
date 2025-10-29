package com.labdevs.comandar.viewmodels;

import android.app.Application;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.labdevs.comandar.data.entity.Camarero;
import com.labdevs.comandar.data.repository.AppRepository;

public class EditProfileViewModel extends AndroidViewModel {
    private final AppRepository repository;
    public LiveData<Camarero> camarero;

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public LiveData<String> getError() { return _error; }

    private final MutableLiveData<Boolean> _updateSuccess = new MutableLiveData<>(false);
    public LiveData<Boolean> getUpdateSuccess() { return _updateSuccess; }

    public EditProfileViewModel(@NonNull Application application) {
        super(application);
        repository = new AppRepository(application);
    }

    public void loadCamarero(int camareroId) {
        if (camarero == null) { // Cargar solo si no está ya cargado
            camarero = repository.getPerfil(camareroId);
        }
    }

    public void guardarCambios(String nombre, String apellido, String email, String telefono, String fotoUrl) {
        if (nombre.trim().isEmpty() || apellido.trim().isEmpty() || email.trim().isEmpty()) {
            _error.setValue("Nombre, apellido y email no pueden estar vacíos.");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            _error.setValue("El formato del correo electrónico no es válido.");
            return;
        }

        Camarero camareroActual = camarero.getValue();
        if (camareroActual == null) {
            _error.setValue("No se pudieron cargar los datos del usuario.");
            return;
        }

        camareroActual.nombre = nombre.trim();
        camareroActual.apellido = apellido.trim();
        camareroActual.email = email.trim(); // Nuevo
        camareroActual.numeroContacto = telefono.trim();
        camareroActual.fotoUrl = fotoUrl; // Nuevo

        repository.actualizarPerfilCamarero(camareroActual);
        _updateSuccess.setValue(true);
    }

    public void onNavigationDone() {
        _updateSuccess.setValue(false);
        _error.setValue(null);
    }
}
