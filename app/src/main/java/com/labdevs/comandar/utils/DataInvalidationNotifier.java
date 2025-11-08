package com.labdevs.comandar.utils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

/**
 * Singleton que actúa como un bus de eventos simple para notificar
 * a diferentes partes de la app que los datos han cambiado y necesitan recargarse.
 */
public class DataInvalidationNotifier {

    private static volatile DataInvalidationNotifier INSTANCE;

    // Un LiveData que emitirá un evento (el valor no importa, solo el cambio).
    private final MutableLiveData<Boolean> pedidosInvalidated = new MutableLiveData<>();

    private DataInvalidationNotifier() {}

    public static DataInvalidationNotifier getInstance() {
        if (INSTANCE == null) {
            synchronized (DataInvalidationNotifier.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DataInvalidationNotifier();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Llama a este método cuando una acción (ej. añadir un nuevo pedido)
     * debería provocar que las listas de pedidos se recarguen.
     */
    public void notifyPedidosChanged() {
        // Usamos postValue para asegurar que se ejecute en el hilo principal.
        pedidosInvalidated.postValue(true);
    }

    /**
     * El ViewModel observará este LiveData para saber cuándo recargar.
     */
    public LiveData<Boolean> getPedidosInvalidated() {
        return pedidosInvalidated;
    }
}