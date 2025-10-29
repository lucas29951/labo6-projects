package com.labdevs.comandar.viewmodels;

import android.app.Application;

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
        camarero = repository.getPerfil(camareroId);
    }

    public void guardarCambios(String nombre, String apellido, String telefono) {
        if (nombre.trim().isEmpty() || apellido.trim().isEmpty()) {
            _error.setValue("El nombre y el apellido no pueden estar vacíos.");
            return;
        }

        Camarero camareroActual = camarero.getValue();
        if (camareroActual == null) {
            _error.setValue("No se pudieron cargar los datos del usuario.");
            return;
        }

        camareroActual.nombre = nombre.trim();
        camareroActual.apellido = apellido.trim();
        camareroActual.numeroContacto = telefono.trim();

        repository.actualizarPerfilCamarero(camareroActual);
        _updateSuccess.setValue(true);
    }

    public void onNavigationDone() {
        _updateSuccess.setValue(false);
        _error.setValue(null);
    }
}
