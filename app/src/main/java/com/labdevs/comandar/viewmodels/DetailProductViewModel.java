package com.labdevs.comandar.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.labdevs.comandar.data.entity.Producto;
import com.labdevs.comandar.data.repository.AppRepository;

public class DetailProductViewModel extends AndroidViewModel {
    private final AppRepository repository;

    public DetailProductViewModel(@NonNull Application application) {
        super(application);
        repository = new AppRepository(application);
    }

    public LiveData<Producto> getProducto(int productoId) {
        return repository.getProductoById(productoId);
    }
}
