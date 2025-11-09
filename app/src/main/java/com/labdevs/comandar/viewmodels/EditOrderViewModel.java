package com.labdevs.comandar.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.labdevs.comandar.data.database.AppDatabase;
import com.labdevs.comandar.data.entity.DetallePedido;
import com.labdevs.comandar.data.entity.Pedido;
import com.labdevs.comandar.data.model.ItemPedido;
import com.labdevs.comandar.data.repository.AppRepository;

import java.util.List;

public class EditOrderViewModel extends AndroidViewModel {
    private final AppRepository repository;
    private int pedidoId;
    private LiveData<Pedido> pedidoActivo;
    public LiveData<List<ItemPedido>> itemsDelPedido;

    // LiveData para eventos de navegación
    private final MutableLiveData<Boolean> _pedidoEnviado = new MutableLiveData<>(false);
    public LiveData<Boolean> getPedidoEnviado() { return _pedidoEnviado; }

    private final MutableLiveData<Boolean> _pedidoEliminado = new MutableLiveData<>(false);
    public LiveData<Boolean> getPedidoEliminado() { return _pedidoEliminado; }

    public EditOrderViewModel(@NonNull Application application) {
        super(application);
        repository = new AppRepository(application);
    }

    public void init(int pedidoId) {
        this.pedidoId = pedidoId;
        this.itemsDelPedido = repository.getItemsDelPedido(pedidoId);
        this.pedidoActivo = repository.getPedidoActivoDeMesa(this.pedidoId); // Asumiendo que mesaId y pedidoId son lo mismo por simplicidad, se puede mejorar
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

    private void modificarCantidad(int productoId, int cambio) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            DetallePedido detalle = repository.getDetalleSync(pedidoId, productoId);
            if (detalle != null) {
                int nuevaCantidad = detalle.cantidad + cambio;
                if (nuevaCantidad >= 1) { // Lógica de cantidad mínima
                    detalle.cantidad = nuevaCantidad;
                    repository.actualizarDetallePedido(detalle);
                }
            }
        });
    }

    public void aumentarCantidad(ItemPedido item) {
        modificarCantidad(item.productoId, 1);
    }

    public void disminuirCantidad(ItemPedido item) {
        modificarCantidad(item.productoId, -1);
    }

    public void eliminarItem(ItemPedido item) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            DetallePedido detalle = repository.getDetalleSync(pedidoId, item.productoId);
            if (detalle != null) {
                // Callback para cuando se elimine el último item
                Runnable onEmptyOrderCallback = () -> {
                    // Obtenemos el pedido de forma síncrona aquí
                    Pedido pedidoParaEliminar = repository.getPedidoByIdSync(pedidoId);
                    if(pedidoParaEliminar != null) {
                        repository.eliminarPedidoYLiberarMesa(pedidoParaEliminar);
                        _pedidoEliminado.postValue(true);
                    }
                };
                repository.eliminarDetallePedido(detalle, onEmptyOrderCallback);
            }
        });
    }

    public void enviarPedido() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            // Obtenemos el pedido de forma síncrona aquí DENTRO del Executor
            Pedido pedidoParaEnviar = repository.getPedidoByIdSync(pedidoId);
            if (pedidoParaEnviar != null) {
                repository.enviarPedidoACocina(pedidoParaEnviar);
                _pedidoEnviado.postValue(true);
            }
        });
    }

    public void onNavigationDone() {
        _pedidoEnviado.setValue(false);
        _pedidoEliminado.setValue(false);
    }
}
