package com.labdevs.comandar.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.labdevs.comandar.data.database.AppDatabase;
import com.labdevs.comandar.data.entity.Pedido;
import com.labdevs.comandar.data.model.ItemPedido;
import com.labdevs.comandar.data.repository.AppRepository;

import java.util.List;

public class ClosedOrderDetailViewModel extends AndroidViewModel {
    private final AppRepository repository;

    private final MutableLiveData<Pedido> pedido = new MutableLiveData<>();
    public LiveData<List<ItemPedido>> itemsDelPedido;

    public ClosedOrderDetailViewModel(@NonNull Application application) {
        super(application);
        repository = new AppRepository(application);
    }

    public void init(int pedidoId) {
        // Obtenemos los ítems del pedido, que es un LiveData
        this.itemsDelPedido = repository.getItemsDelPedido(pedidoId);

        // Obtenemos los detalles del Pedido (como la fecha y mesa) de forma síncrona
        // en un hilo de fondo, ya que solo se necesita una vez.
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Pedido pedidoData = repository.getPedidoByIdSync(pedidoId);
            pedido.postValue(pedidoData);
        });
    }

    public LiveData<Pedido> getPedido() {
        return pedido;
    }

    public LiveData<Double> getTotalPedido() {
        return Transformations.map(itemsDelPedido, items -> {
            double total = 0.0;
            if (items != null) {
                for (ItemPedido item : items) {
                    total += item.getSubtotal();
                }
            }
            return total;
        });
    }
}
