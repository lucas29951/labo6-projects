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
import java.util.Date;
import java.util.List;

public class CuentaViewModel extends AndroidViewModel {
    private final AppRepository repository;
    private int camareroId = -1;

    // LiveData para gestionar los filtros (estado y fecha)
    private final MutableLiveData<Filtros> filtros = new MutableLiveData<>();

    // LiveData final que la UI observará. Es un Mediator para reaccionar a múltiples fuentes.
    public final MediatorLiveData<List<PedidoConResumen>> pedidos = new MediatorLiveData<>();
    private LiveData<List<PedidoConResumen>> currentSource = null;

    // Clase interna para agrupar los parámetros del filtro.
    private static class Filtros {
        EstadoPedido estado;
        Date fechaInicio;
        Date fechaFin;

        Filtros(EstadoPedido estado, Date fechaInicio, Date fechaFin) {
            this.estado = estado;
            this.fechaInicio = fechaInicio;
            this.fechaFin = fechaFin;
        }
    }

    public CuentaViewModel(@NonNull Application application) {
        super(application);
        repository = new AppRepository(application);

        // El Mediator escucha a DOS fuentes:
        // 1. Cambios en el objeto 'filtros' (cuando el usuario cambia de pestaña o aplica un filtro de fecha).
        pedidos.addSource(filtros, f -> reloadData());

        // 2. Notificaciones globales desde el Repositorio que indican que los datos de pedidos han cambiado.
        pedidos.addSource(AppRepository.getPedidosChangedNotifier(), invalidated -> {
            if (invalidated != null && invalidated) {
                Filtros curr = filtros.getValue();
                if (curr != null) {
                    filtros.setValue(new Filtros(curr.estado, curr.fechaInicio, new Date()));
                }
            }
        });
    }

    /**
     * Método central que se encarga de obtener los datos actualizados del repositorio.
     * Es llamado por cualquiera de las fuentes del MediatorLiveData.
     */
    private void reloadData() {
        Filtros f = filtros.getValue();
        // No hacemos nada si no tenemos los filtros o el ID del camarero.
        if (f == null || camareroId == -1) return;

        // Quitamos la fuente de datos anterior para evitar que el Mediator escuche a LiveDatas viejos.
        if (currentSource != null) {
            pedidos.removeSource(currentSource);
        }

        // Obtenemos un NUEVO LiveData del repositorio con los filtros actuales.
        currentSource = repository.getPedidos(camareroId, f.estado, f.fechaInicio, f.fechaFin);

        // Le decimos al Mediator que empiece a escuchar a esta nueva fuente y propague sus valores.
        pedidos.addSource(currentSource, pedidos::setValue);
    }

    /**
     * Inicializa el ViewModel con el ID del camarero.
     * Este método es crucial y debe ser llamado desde el Fragment una vez.
     */
    public void setCamareroId(int id) {
        if (this.camareroId == id) return; // Evitar reinicializaciones innecesarias.
        this.camareroId = id;

        // Establecemos los filtros iniciales. Esto disparará la primera llamada a reloadData().
        Calendar start = Calendar.getInstance();
        start.set(1970, 0, 1, 0, 0, 0);

        Calendar end = Calendar.getInstance();
        end.set(2099, 11, 31, 23, 59, 59);

        filtros.setValue(new Filtros(EstadoPedido.abierto, start.getTime(), end.getTime()));
    }

    // Métodos llamados por la UI para cambiar los filtros.
    // Solo actualizan el LiveData `filtros`, y el Mediator se encarga del resto.

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

    public void enviarPedidoACocina(PedidoConResumen pcr) {
        repository.enviarPedidoACocina(pcr.pedido);
    }

    public void solicitarReabrirPedido(PedidoConResumen pcr) {
        repository.reabrirPedido(pcr.pedido);
    }

    public void cerrarPedido(int pedidoId) {
        // Lógica futura para cambiar el estado del pedido a 'cerrado'
    }

    public void verDetallePedido(int pedidoId) {
        // Lógica futura para navegar a una pantalla de detalle de solo lectura
    }

    // Lógica para eliminar pedido
    public void solicitarEliminacionPedido(PedidoConResumen pcr) {
        repository.eliminarPedidoYLiberarMesa(pcr.pedido);
    }
}
