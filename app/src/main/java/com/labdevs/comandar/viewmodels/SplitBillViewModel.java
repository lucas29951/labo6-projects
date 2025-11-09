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

    private final MutableLiveData<SplitMode> _splitMode = new MutableLiveData<>(SplitMode.ASSIGN_ITEMS);
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

    public void setSplitMode(SplitMode mode) {
        _splitMode.setValue(mode);
        recalculateAllTotals();
    }

    public void setNumberOfPeople(int count) {
        if (count < 1) count = 1;
        _numberOfPeople.setValue(count);

        List<Person> newPeople = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            newPeople.add(new Person(i));
        }
        _people.setValue(newPeople);

        if (_selectedPersonId.getValue() > count) {
            _selectedPersonId.setValue(count);
        }

        // Desasignar ítems si se reduce el número de personas
        List<BillItem> currentItems = _billItems.getValue();
        if (currentItems != null) {
            for (BillItem item : currentItems) {
                int finalCount = count;
                item.assignedToPersonIds.removeIf(personId -> personId > finalCount);
            }
            _billItems.setValue(new ArrayList<>(currentItems));
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

    public void toggleItemAssignment(int productoId) {
        List<BillItem> currentItems = _billItems.getValue();
        int currentPersonId = _selectedPersonId.getValue();

        if (currentItems != null) {
            for (BillItem item : currentItems) {
                if (item.itemPedido.productoId == productoId) {
                    if (item.assignedToPersonIds.contains(currentPersonId)) {
                        item.assignedToPersonIds.remove(Integer.valueOf(currentPersonId));
                    } else {
                        item.assignedToPersonIds.add(currentPersonId);
                    }
                    break;
                }
            }
            _billItems.setValue(new ArrayList<>(currentItems));
            recalculateAllTotals();
        }
    }

    private void recalculateAllTotals() {
        List<BillItem> currentBillItems = _billItems.getValue();
        if (currentBillItems == null) return;

        double total = currentBillItems.stream().mapToDouble(bi -> bi.itemPedido.getSubtotal()).sum();
        _orderTotal.setValue(total);

        SplitMode mode = _splitMode.getValue();
        int numPeople = _numberOfPeople.getValue();
        List<Person> currentPeople = _people.getValue();

        if(currentPeople == null) return;

        // Reset totals
        for(Person p : currentPeople) p.totalAmount = 0.0;

        if (mode == SplitMode.ASSIGN_ITEMS) {
            for (BillItem item : currentBillItems) {
                if (item.isAssigned()) {
                    double dividedPrice = item.itemPedido.getSubtotal() / item.assignedToPersonIds.size();
                    for (Integer personId : item.assignedToPersonIds) {
                        currentPeople.get(personId - 1).totalAmount += dividedPrice;
                    }
                }
            }
        } else if (mode == SplitMode.SPLIT_EQUALLY) {
            double amountPerPerson = total / numPeople;
            for(Person p : currentPeople) p.totalAmount = amountPerPerson;
        } else { // SINGLE_PAYMENT
            if(!currentPeople.isEmpty()){
                currentPeople.get(0).totalAmount = total;
            }
        }

        _people.setValue(new ArrayList<>(currentPeople));

        // Update person summary list
        int selectedPerson = _selectedPersonId.getValue();
        List<BillItem> summaryList = currentBillItems.stream()
                .filter(item -> item.assignedToPersonIds.contains(selectedPerson))
                .collect(Collectors.toList());
        _personSummaryItems.setValue(summaryList);
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
