package com.labdevs.comandar.viewmodels;

import android.app.Application;
import android.icu.util.Calendar;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.labdevs.comandar.data.entity.enums.EstadoPedido;
import com.labdevs.comandar.data.model.PedidoConResumen;
import com.labdevs.comandar.data.repository.AppRepository;
import com.labdevs.comandar.utils.DataInvalidationNotifier;

import java.util.Date;
import java.util.List;

public class CuentaViewModel extends AndroidViewModel {
    private final AppRepository repository;
    private int camareroId = -1; // Almacenamos el ID del camarero

    // LiveData para gestionar los filtros (estado y fecha)
    private final MutableLiveData<Filtros> filtros = new MutableLiveData<>();

    // LiveData final que la UI observará.
    public final MediatorLiveData<List<PedidoConResumen>> pedidos = new MediatorLiveData<>();
    private LiveData<List<PedidoConResumen>> currentSource = null;

    private static class Filtros {
        EstadoPedido estado;
        Date fechaInicio;
        Date fechaFin;

        public Filtros(EstadoPedido estado, Date fechaInicio, Date fechaFin) {
            this.estado = estado;
            this.fechaInicio = fechaInicio;
            this.fechaFin = fechaFin;
        }
    }

    public CuentaViewModel(@NonNull Application application) {
        super(application);
        repository = new AppRepository(application);

        // El Mediator escucha a DOS fuentes:
        // 1. Cambios en los filtros del usuario.
        pedidos.addSource(filtros, f -> reloadData());

        // 2. Notificaciones globales de que los datos de pedidos han cambiado.
        pedidos.addSource(DataInvalidationNotifier.getInstance().getPedidosInvalidated(), invalidated -> {
            if (invalidated != null && invalidated) {
                reloadData();
            }
        });
    }

    private void reloadData() {
        Filtros f = filtros.getValue();
        if (f == null || camareroId == -1) return;

        if (currentSource != null) {
            pedidos.removeSource(currentSource);
        }

        currentSource = repository.getPedidos(camareroId, f.estado, f.fechaInicio, f.fechaFin);

        pedidos.addSource(currentSource, pedidos::setValue);
    }

    // Método para inicializar el ViewModel con el ID del camarero
    public void setCamareroId(int id) {
        if (this.camareroId == id) return; // Evitar recargas innecesarias
        this.camareroId = id;

        // Inicializa los filtros para disparar la primera carga
        Calendar cal = Calendar.getInstance();
        cal.set(1970, 0, 1);
        filtros.setValue(new Filtros(EstadoPedido.abierto, cal.getTime(), new Date()));
    }

    // Los métodos para cambiar filtros ahora solo actualizan el LiveData `filtros`
    public void setFiltroEstado(EstadoPedido estado) {
        Filtros current = filtros.getValue();
        if (current != null && current.estado != estado) {
            filtros.setValue(new Filtros(estado, current.fechaInicio, current.fechaFin));
        }
    }

    public void setFiltroFecha(Date inicio, Date fin) {
        Filtros current = filtros.getValue();
        if (current != null) {
            filtros.setValue(new Filtros(current.estado, inicio, fin));
        }
    }

    public void resetFiltroFecha() {
        Filtros current = filtros.getValue();
        if (current != null) {
            Calendar cal = Calendar.getInstance();
            cal.set(1970, 0, 1);
            filtros.setValue(new Filtros(current.estado, cal.getTime(), new Date()));
        }
    }

    // --- Métodos para las acciones de los botones (placeholders) ---
    public void editarPedido(int pedidoId) {
        // Lógica futura para navegar a la pantalla de edición
    }

    public void enviarACocina(int pedidoId) {
        // Lógica futura para cambiar el estado del pedido a 'enviado'
    }

    public void volverAabrirPedido(int pedidoId) {
        // Lógica futura para cambiar el estado del pedido a 'abierto'
    }

    public void cerrarPedido(int pedidoId) {
        // Lógica futura para cambiar el estado del pedido a 'cerrado'
    }

    public void verDetallePedido(int pedidoId) {
        // Lógica futura para navegar a una pantalla de detalle de solo lectura
    }

    // Lógica para eliminar pedido
    public void eliminarPedido(int pedidoId) {
        // Lógica futura para eliminar el pedido de la base de datos
    }
}
