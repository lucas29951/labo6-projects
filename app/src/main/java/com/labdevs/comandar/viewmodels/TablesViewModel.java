package com.labdevs.comandar.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.labdevs.comandar.data.entity.Mesa;
import com.labdevs.comandar.data.entity.enums.EstadoMesa;
import com.labdevs.comandar.data.model.MesaConCamarero;
import com.labdevs.comandar.data.repository.AppRepository;

import java.util.List;

public class TablesViewModel extends AndroidViewModel {
    public enum ViewMode { GRID, LIST }

    private final AppRepository repository;
    private final LiveData<List<MesaConCamarero>> todasLasMesas;
    private final MutableLiveData<ViewMode> viewMode = new MutableLiveData<>(ViewMode.GRID);

    // LiveData para comunicar eventos únicos a la UI (ej. Toasts)
    private final MutableLiveData<String> _toastMessage = new MutableLiveData<>();
    public LiveData<String> getToastMessage() {
        return _toastMessage;
    }

    private int camareroId;

    public TablesViewModel(@NonNull Application application) {
        super(application);
        repository = new AppRepository(application);
        todasLasMesas = repository.getMapaDeMesasConCamarero();
    }

    public void setCamareroId(int id) { this.camareroId = id; }
    public int getCamareroId() { return this.camareroId; }
    public LiveData<List<MesaConCamarero>> getTodasLasMesas() {
        return todasLasMesas;
    }
    public LiveData<ViewMode> getViewMode() { return viewMode; }

    public void toggleViewMode() {
        viewMode.setValue(viewMode.getValue() == ViewMode.GRID ? ViewMode.LIST : ViewMode.GRID);
    }

    // --- LÓGICA DE NEGOCIO ACTUALIZADA ---

    public void asignarMesa(Mesa mesa) {
        Mesa mesaParaActualizar = mesa.copy();

        // Regla: Solo se puede asignar si no tiene dueño (camareroId es null).
        if (mesaParaActualizar.camareroId == null) {
            mesaParaActualizar.camareroId = this.camareroId;
            repository.actualizarMesa(mesaParaActualizar);
        }
    }

    public void desasignarMesa(Mesa mesa) {
        Mesa mesaParaActualizar = mesa.copy();

        // Regla: El camarero solo puede desasignar SUS PROPIAS mesas.
        if (mesaParaActualizar.camareroId != null && Integer.valueOf(this.camareroId).equals(mesaParaActualizar.camareroId)) {
            mesaParaActualizar.camareroId = null;
            repository.actualizarMesa(mesaParaActualizar);
        }
    }

    // Método para consumir el mensaje del Toast y evitar que se muestre de nuevo (ej. en rotación)
    public void onToastMessageShown() {
        _toastMessage.setValue(null);
    }
}
