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

public class SentOrderDetailViewModel extends AndroidViewModel {
    private final AppRepository repository;
    private int pedidoId;

    public LiveData<List<ItemPedido>> itemsDelPedido;
    private LiveData<Pedido> pedido;

    private final MutableLiveData<Boolean> _orderActionFinished = new MutableLiveData<>(false);
    public LiveData<Boolean> getOrderActionFinished() { return _orderActionFinished; }

    public SentOrderDetailViewModel(@NonNull Application application) {
        super(application);
        repository = new AppRepository(application);
    }

    public void init(int pedidoId) {
        this.pedidoId = pedidoId;
        this.itemsDelPedido = repository.getItemsDelPedido(pedidoId);
        this.pedido = repository.getPedidoActivoDeMesa(this.pedidoId); // Asumiendo que se reabrirá
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

    public void reopenOrder() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Pedido pedido = repository.getPedidoByIdSync(pedidoId);
            if (pedido != null) {
                repository.reabrirPedido(pedido);
                _orderActionFinished.postValue(true);
            }
        });
    }

    public void closeOrder() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Pedido pedido = repository.getPedidoByIdSync(pedidoId);
            if (pedido != null) {
                repository.cerrarPedidoYLiberarMesa(pedido);
                _orderActionFinished.postValue(true);
            }
        });
    }

    public void onNavigationDone() {
        _orderActionFinished.setValue(false);
    }
}
