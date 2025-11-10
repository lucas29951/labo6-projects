package com.labdevs.comandar.data.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BillItem {
    public ItemPedido itemPedido;
    // La clave es el ID de la persona, el valor es la cantidad asignada a esa persona.
    public Map<Integer, Integer> assignments;

    public BillItem(ItemPedido itemPedido) {
        this.itemPedido = itemPedido;
        this.assignments = new HashMap<>();
    }

    public int getTotalAssignedQuantity() {
        return assignments.values().stream().mapToInt(Integer::intValue).sum();
    }

    public boolean isFullyAssigned() {
        return getTotalAssignedQuantity() >= itemPedido.cantidad;
    }

    public int getQuantityAssignedToPerson(int personId) {
        return assignments.getOrDefault(personId, 0);
    }

    public void setAssignment(int personId, int quantity) {
        if (quantity > 0) {
            assignments.put(personId, quantity);
        } else {
            assignments.remove(personId);
        }
    }
}
