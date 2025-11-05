package com.labdevs.comandar;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.labdevs.comandar.adapters.CategoryAdapter;
import com.labdevs.comandar.adapters.ProductAdapter;
import com.labdevs.comandar.databinding.FragmentMenuBinding;
import com.labdevs.comandar.viewmodels.MenuViewModel;

public class MenuFragment extends Fragment {

    private FragmentMenuBinding binding;
    private MenuViewModel viewModel;
    private CategoryAdapter categoryAdapter;
    private ProductAdapter productAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMenuBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MenuViewModel.class);

        setupCategoryRecyclerView();
        setupProductRecyclerView();
        setupSearchView();
        observeViewModel();
    }

    private void setupCategoryRecyclerView() {
        categoryAdapter = new CategoryAdapter(categoryId -> viewModel.toggleCategorySelection(categoryId));
        binding.recyclerViewCategories.setAdapter(categoryAdapter);
        // LayoutManager ya está definido en el XML, pero es buena práctica confirmarlo.
        binding.recyclerViewCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    private void setupProductRecyclerView() {
        productAdapter = new ProductAdapter(getContext(), new ProductAdapter.OnProductClickListener() {
            @Override
            public void onProductItemClick(int productId) {
                // Acción para el ítem: navegar al detalle
                Bundle args = new Bundle();
                args.putInt("PRODUCT_ID", productId);
                NavHostFragment.findNavController(MenuFragment.this)
                        .navigate(R.id.action_menuFragment_to_detailProductFragment, args);
            }

            @Override
            public void onAddButtonClick(String productName) {
                // Acción para el botón: mostrar Toast
                Toast.makeText(getContext(), productName + " añadido al pedido.", Toast.LENGTH_SHORT).show();
            }
        });
        binding.recyclerViewProducts.setAdapter(productAdapter);
        binding.recyclerViewProducts.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void setupSearchView() {
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                viewModel.setSearchQuery(newText);
                return true;
            }
        });
    }

    private void observeViewModel() {
        viewModel.allCategories.observe(getViewLifecycleOwner(), categories -> {
            categoryAdapter.submitList(categories);
        });

        viewModel.getProducts().observe(getViewLifecycleOwner(), products -> {
            productAdapter.submitList(products);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Evitar fugas de memoria
    }
}