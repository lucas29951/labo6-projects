package com.labdevs.comandar.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.labdevs.comandar.data.entity.CategoriaProducto;
import com.labdevs.comandar.data.entity.Producto;
import com.labdevs.comandar.data.repository.AppRepository;

import java.util.ArrayList;
import java.util.List;

public class MenuViewModel extends AndroidViewModel {
    private final AppRepository repository;
    public final LiveData<List<CategoriaProducto>> allCategories;

    // Estado de los filtros y búsqueda
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");
    private final MutableLiveData<List<Integer>> selectedCategoryIds = new MutableLiveData<>(new ArrayList<>());

    // El LiveData final que la UI observará. Combina los filtros y la búsqueda.
    private final MediatorLiveData<List<Producto>> products = new MediatorLiveData<>();
    private LiveData<List<Producto>> currentProductsSource = null;

    public MenuViewModel(@NonNull Application application) {
        super(application);
        repository = new AppRepository(application);
        allCategories = repository.getCategoriasMenu();

        // El Mediator observa los cambios en la búsqueda y en las categorías seleccionadas
        products.addSource(searchQuery, query -> updateProductList());
        products.addSource(selectedCategoryIds, ids -> updateProductList());
    }

    public LiveData<List<Producto>> getProducts() {
        return products;
    }

    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
    }

    public void toggleCategorySelection(int categoryId) {
        List<Integer> currentSelection = selectedCategoryIds.getValue();
        if (currentSelection == null) {
            currentSelection = new ArrayList<>();
        }
        if (currentSelection.contains(categoryId)) {
            currentSelection.remove(Integer.valueOf(categoryId));
        } else {
            currentSelection.add(categoryId);
        }
        selectedCategoryIds.setValue(new ArrayList<>(currentSelection)); // Forzar actualización
    }

    private void updateProductList() {
        String query = searchQuery.getValue() != null ? searchQuery.getValue() : "";
        List<Integer> categoryIds = selectedCategoryIds.getValue() != null ? selectedCategoryIds.getValue() : new ArrayList<>();

        // Removemos la fuente anterior para evitar múltiples observers
        if (currentProductsSource != null) {
            products.removeSource(currentProductsSource);
        }
        // Obtenemos la nueva fuente de datos desde el repositorio
        currentProductsSource = repository.searchProductos(query, categoryIds);
        // Añadimos la nueva fuente al MediatorLiveData
        products.addSource(currentProductsSource, products::setValue);
    }
}
