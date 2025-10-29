package com.labdevs.comandar.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.labdevs.comandar.data.entity.Camarero;
import com.labdevs.comandar.data.repository.AppRepository;

public class ProfileViewModel extends AndroidViewModel {

    private final AppRepository repository;
    private LiveData<Camarero> camarero;
    private LiveData<Integer> conteoMesas;
    private int camareroId = -1;

    // LiveData para manejar el evento de logout como una acción única
    private final MutableLiveData<Boolean> _navigateToLogin = new MutableLiveData<>(false);
    public LiveData<Boolean> getNavigateToLogin() {
        return _navigateToLogin;
    }

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        repository = new AppRepository(application);
    }

    // Este método será llamado por el Fragment para inicializar la carga de datos
    public void loadCamarero(int id) {
        if (this.camareroId == id && camarero != null) return; // Ya está cargado
        this.camareroId = id;
        camarero = repository.getPerfil(camareroId);
        conteoMesas = repository.getConteoMesasAsignadas(camareroId);
    }

    public LiveData<Camarero> getCamarero() {
        return camarero;
    }

    public LiveData<Integer> getConteoMesas() {
        return conteoMesas;
    }

    public void logout() {
        if (camareroId != -1) {
            // Desasigna todas las mesas del camarero en un hilo de fondo
            repository.desasignarTodasLasMesas(camareroId);
        }
        // Dispara el evento para navegar a la pantalla de Login
        _navigateToLogin.postValue(true);
    }

    // Para resetear el evento después de la navegación
    public void onLoginNavigated() {
        _navigateToLogin.setValue(false);
    }
}
