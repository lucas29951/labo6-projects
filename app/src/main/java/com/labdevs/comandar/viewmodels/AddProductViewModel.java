package com.labdevs.comandar.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.labdevs.comandar.data.entity.Mesa;
import com.labdevs.comandar.data.repository.AppRepository;

import java.util.List;

public class AddProductViewModel extends AndroidViewModel {
    private final AppRepository repository;
    private final MutableLiveData<List<Mesa>> mesasAsignadas = new MutableLiveData<>();
    private final MutableLiveData<String> toastMessage = new MutableLiveData<>();

    public AddProductViewModel(@NonNull Application application) {
        super(application);
        repository = new AppRepository(application);
    }

    public LiveData<List<Mesa>> getMesasAsignadas() { return mesasAsignadas; }
    public LiveData<String> getToastMessage() { return toastMessage; }

    public void cargarMesasAsignadas(int camareroId) {
        new Thread(() -> {
            List<Mesa> mesas = repository.getMesasAsignadasSync(camareroId);
            mesasAsignadas.postValue(mesas);
        }).start();
    }

    public void confirmarAñadirProducto(int productoId, Mesa mesaSeleccionada, int camareroId, int cantidad, String notas) {
        if (mesaSeleccionada == null) {
            toastMessage.setValue("Por favor, selecciona una mesa.");
            return;
        }
        repository.agregarProductoAPedido(productoId, mesaSeleccionada.mesaId, camareroId, cantidad, notas);
        toastMessage.setValue("Producto añadido correctamente.");
    }

    public void onToastShown() {
        toastMessage.setValue(null);
    }
}
