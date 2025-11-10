package com.labdevs.comandar.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.labdevs.comandar.data.database.AppDatabase;
import com.labdevs.comandar.data.entity.Pedido;
import com.labdevs.comandar.data.model.BillItem;
import com.labdevs.comandar.data.model.ItemPedido;
import com.labdevs.comandar.data.model.Person;
import com.labdevs.comandar.data.repository.AppRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SplitBillViewModel extends AndroidViewModel {
    public enum SplitMode { SINGLE_PAYMENT, ASSIGN_ITEMS, SPLIT_EQUALLY }

    private final AppRepository repository;
    private int pedidoId;
    private LiveData<List<ItemPedido>> originalItems;
    private Observer<List<ItemPedido>> originalItemsObserver;

    private final MutableLiveData<SplitMode> _splitMode = new MutableLiveData<>(SplitMode.SINGLE_PAYMENT);
    public LiveData<SplitMode> splitMode = _splitMode;

    private final MutableLiveData<Integer> _numberOfPeople = new MutableLiveData<>(1);
    public LiveData<Integer> numberOfPeople = _numberOfPeople;

    private final MutableLiveData<Integer> _selectedPersonId = new MutableLiveData<>(1);
    public LiveData<Integer> selectedPersonId = _selectedPersonId;

    private final MutableLiveData<List<Person>> _people = new MutableLiveData<>();
    public LiveData<List<Person>> people = _people;

    private final MutableLiveData<List<BillItem>> _billItems = new MutableLiveData<>();
    public LiveData<List<BillItem>> billItems = _billItems;

    private final MutableLiveData<List<BillItem>> _personSummaryItems = new MutableLiveData<>();
    public LiveData<List<BillItem>> personSummaryItems = _personSummaryItems;

    private final MutableLiveData<Double> _orderTotal = new MutableLiveData<>(0.0);
    public LiveData<Double> orderTotal = _orderTotal;

    private final MutableLiveData<Boolean> _closeOrderSuccess = new MutableLiveData<>(false);
    public LiveData<Boolean> closeOrderSuccess = _closeOrderSuccess;

    public SplitBillViewModel(@NonNull Application application) {
        super(application);
        repository = new AppRepository(application);
    }

    public void init(int pedidoId) {
        this.pedidoId = pedidoId;
        originalItems = repository.getItemsDelPedido(pedidoId);

        originalItemsObserver = items -> {
            if (items != null) {
                List<BillItem> newBillItems = items.stream().map(BillItem::new).collect(Collectors.toList());
                _billItems.setValue(newBillItems);
                recalculateAllTotals();
            }
        };
        originalItems.observeForever(originalItemsObserver);

        setNumberOfPeople(1);
    }

    public void confirmAndCloseOrder() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Pedido pedido = repository.getPedidoByIdSync(pedidoId);
            if(pedido != null) {
                repository.cerrarPedidoYLiberarMesa(pedido);
                _closeOrderSuccess.postValue(true);
            }
        });

    }

    public void setSplitMode(SplitMode mode) {
        if (_splitMode.getValue() == mode) return;

        _splitMode.setValue(mode);

        if (mode == SplitMode.SINGLE_PAYMENT) {
            setNumberOfPeople(1);
        }

        List<BillItem> items = _billItems.getValue();
        if (items != null) {
            List<BillItem> rebuilt = new ArrayList<>(items.size());
            for (BillItem it : items) {
                BillItem fresh = new BillItem(it.itemPedido); // assignments vacíos
                rebuilt.add(fresh);
            }
            _billItems.setValue(rebuilt);
        }

        recalculateAllTotals();
    }

    public void setNumberOfPeople(int count) {
        // No permitimos cambiar el número de personas en modo Pago Único
        if (_splitMode.getValue() == SplitMode.SINGLE_PAYMENT && count > 1) {
            return;
        }

        if (count < 1) count = 1;
        final int finalCount = count;
        _numberOfPeople.setValue(finalCount);

        List<Person> newPeople = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            newPeople.add(new Person(i));
        }
        _people.setValue(newPeople);

        if (_selectedPersonId.getValue() > count) {
            _selectedPersonId.setValue(count);
        }

        recalculateAllTotals();
    }

    public void incrementPeople() {
        setNumberOfPeople(_numberOfPeople.getValue() + 1);
    }

    public void decrementPeople() {
        setNumberOfPeople(_numberOfPeople.getValue() - 1);
    }

    public void selectPerson(int personId) {
        _selectedPersonId.setValue(personId);
        recalculateAllTotals();
    }

    public void toggleItemAssignment(BillItem billItem) {
        Integer currentPersonIdObj = _selectedPersonId.getValue();
        if (currentPersonIdObj == null) return;
        final int currentPersonId = currentPersonIdObj;

        List<BillItem> current = _billItems.getValue();
        if (current == null) return;

        List<BillItem> updated = new ArrayList<>(current.size());
        for (BillItem it : current) {
            if (it.itemPedido.productoId == billItem.itemPedido.productoId) {
                // Clono el item y su mapa de asignaciones
                BillItem copy = new BillItem(it.itemPedido);
                copy.assignments = new java.util.HashMap<>(it.assignments);

                if (copy.assignments.containsKey(currentPersonId)) {
                    copy.assignments.remove(currentPersonId);
                } else {
                    copy.assignments.put(currentPersonId, 1);
                }
                updated.add(copy);
            } else {
                updated.add(it);
            }
        }

        _billItems.setValue(updated);
        recalculateAllTotals();
    }

    private void recalculateAllTotals() {
        List<BillItem> currentBillItems = _billItems.getValue();
        if (currentBillItems == null) return;

        double total = currentBillItems.stream()
                .mapToDouble(bi -> bi.itemPedido.getSubtotal())
                .sum();
        _orderTotal.setValue(total);

        SplitMode mode = _splitMode.getValue();
        Integer numPeopleObj = _numberOfPeople.getValue();
        if (numPeopleObj == null) numPeopleObj = 1;
        int numPeople = Math.max(1, numPeopleObj);

        // Construyo SIEMPRE una NUEVA lista de Person (ids 1..N), sin mutar las previas
        List<Person> recomputed = new ArrayList<>(numPeople);
        for (int i = 1; i <= numPeople; i++) {
            recomputed.add(new Person(i));
        }

        if (mode == SplitMode.ASSIGN_ITEMS) {
            for (BillItem item : currentBillItems) {
                if (!item.assignments.isEmpty()) {
                    double dividedPrice = item.itemPedido.getSubtotal() / item.assignments.size();
                    for (Integer personId : item.assignments.keySet()) {
                        int idx = personId - 1;
                        if (idx >= 0 && idx < recomputed.size()) {
                            recomputed.get(idx).totalAmount += dividedPrice;
                        }
                    }
                }
            }
        } else if (mode == SplitMode.SPLIT_EQUALLY) {
            double amountPerPerson = total / numPeople;
            for (Person p : recomputed) p.totalAmount = amountPerPerson;
        } else { // SINGLE_PAYMENT
            if (!recomputed.isEmpty()) {
                recomputed.get(0).totalAmount = total;
            }
        }

        // Publico NUEVA lista (objetos nuevos) => DiffUtil re-bindea chips y observers disparan
        _people.setValue(recomputed);

        // Summary de la persona seleccionada (no necesita clonado)
        Integer selectedPerson = _selectedPersonId.getValue();
        if (selectedPerson == null) selectedPerson = 1;
        Integer finalSelectedPerson = selectedPerson;
        List<BillItem> summaryList = currentBillItems.stream()
                .filter(item -> item.assignments.containsKey(finalSelectedPerson))
                .collect(java.util.stream.Collectors.toList());
        _personSummaryItems.setValue(summaryList);
    }

    public void onNavigationDone() {
        _closeOrderSuccess.setValue(false);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (originalItems != null && originalItemsObserver != null) {
            originalItems.removeObserver(originalItemsObserver);
        }
    }
}
