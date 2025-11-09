package com.labdevs.comandar.data.model;

import java.util.ArrayList;
import java.util.List;

public class BillItem {
    public ItemPedido itemPedido;
    public List<Integer> assignedToPersonIds;

    public BillItem(ItemPedido itemPedido) {
        this.itemPedido = itemPedido;
        this.assignedToPersonIds = new ArrayList<>();
    }

    public boolean isAssigned() {
        return !assignedToPersonIds.isEmpty();
    }
}
