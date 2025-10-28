package com.labdevs.comandar;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.labdevs.comandar.adapters.MesaAdapter;
import com.labdevs.comandar.databinding.FragmentTablesBinding;
import com.labdevs.comandar.viewmodels.TablesViewModel;

public class TablesFragment extends Fragment {

    private FragmentTablesBinding binding;
    private TablesViewModel viewModel;
    private MesaAdapter adapter;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTablesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // La clave debe coincidir con la que se pasa desde LoginActivity
        final String CAMARERO_ID_KEY = "CAMARERO_ID";

        // Usamos requireActivity() para acceder al Intent que inició la actividad contenedora
        int camareroId = requireActivity().getIntent().getIntExtra(CAMARERO_ID_KEY, -1);

        // Es crucial verificar que el ID es válido antes de continuar
        if (camareroId == -1) {
            // Manejar el error, por ejemplo, mostrando un Toast y cerrando la app/fragmento
            Toast.makeText(getContext(), "Error: ID de camarero no encontrado.", Toast.LENGTH_LONG).show();
            // Podrías cerrar la actividad: requireActivity().finish();
            return; // Detiene la ejecución para evitar el crash
        }

        // El ViewModelProvider debe estar aquí para que el ViewModel se asocie al ciclo de vida del Fragment
        viewModel = new ViewModelProvider(this).get(TablesViewModel.class);
        // Pasamos el ID válido al ViewModel
        viewModel.setCamareroId(camareroId);

        setupRecyclerView();
        setupToolbar();
        observeViewModel();
    }

    private void setupRecyclerView() {
        adapter = new MesaAdapter(viewModel, requireContext());
        binding.recyclerViewMesas.setAdapter(adapter);
        updateLayoutManager(viewModel.getViewMode().getValue());
    }

    private void setupToolbar() {
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_toggle_view) {
                viewModel.toggleViewMode();
                return true;
            }
            return false;
        });
    }

    private void observeViewModel() {
        viewModel.getTodasLasMesas().observe(getViewLifecycleOwner(), mesas -> {
            if (mesas != null) {
                adapter.submitList(mesas);
            }
        });

        viewModel.getViewMode().observe(getViewLifecycleOwner(), viewMode -> {
            // ... lógica para cambiar layout y icono ...
            if (viewMode != null) {
                updateLayoutManager(viewMode);
                updateToolbarIcon(viewMode);
                // Forzamos al adaptador a redibujar todos los ítems con el nuevo layout.
                // Es importante para que los ítems visibles también cambien.
                adapter.notifyDataSetChanged();
            }
        });

        // --- NUEVO OBSERVADOR ---
        viewModel.getToastMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                viewModel.onToastMessageShown(); // Resetea el mensaje para que no se muestre de nuevo
            }
        });
    }


    private void updateLayoutManager(TablesViewModel.ViewMode viewMode) {
        if (viewMode == TablesViewModel.ViewMode.GRID) {
            binding.recyclerViewMesas.setLayoutManager(new GridLayoutManager(getContext(), 2));
        } else {
            binding.recyclerViewMesas.setLayoutManager(new LinearLayoutManager(getContext()));
        }
    }

    private void updateToolbarIcon(TablesViewModel.ViewMode viewMode) {
        MenuItem item = binding.toolbar.getMenu().findItem(R.id.action_toggle_view);
        if (viewMode == TablesViewModel.ViewMode.GRID) {
            item.setIcon(R.drawable.ic_view_list);
        } else {
            item.setIcon(R.drawable.ic_view_grid);
        }
    }
}