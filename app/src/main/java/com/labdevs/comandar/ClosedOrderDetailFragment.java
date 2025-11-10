package com.labdevs.comandar;

import android.icu.text.SimpleDateFormat;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.labdevs.comandar.adapters.ClosedOrderDetailAdapter;
import com.labdevs.comandar.databinding.FragmentClosedOrderDetailBinding;
import com.labdevs.comandar.viewmodels.ClosedOrderDetailViewModel;

import java.util.Locale;

public class ClosedOrderDetailFragment extends Fragment {

    private FragmentClosedOrderDetailBinding binding;
    private ClosedOrderDetailViewModel viewModel;
    private ClosedOrderDetailAdapter adapter;
    private int pedidoId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pedidoId = getArguments().getInt("pedidoId");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentClosedOrderDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ClosedOrderDetailViewModel.class);
        viewModel.init(pedidoId);

        setupToolbar();
        setupRecyclerView();
        observeViewModel();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> NavHostFragment.findNavController(this).navigateUp());
    }

    private void setupRecyclerView() {
        adapter = new ClosedOrderDetailAdapter();
        binding.recyclerViewClosedItems.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.getPedido().observe(getViewLifecycleOwner(), pedido -> {
            if (pedido != null) {
                binding.toolbar.setTitle(String.format(Locale.getDefault(), "Pedido - #%d", pedido.pedidoId));

                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                String hora = sdf.format(pedido.fechaHoraCreacion);

                String statusHeader = String.format(Locale.getDefault(), "Pedido Cerrado     Mesa: %d     Hora: %s", pedido.mesaId, hora);
                binding.textStatusHeader.setText(statusHeader);
            }
        });

        viewModel.itemsDelPedido.observe(getViewLifecycleOwner(), items -> {
            if (items != null) {
                adapter.submitList(items);
            }
        });

        viewModel.getTotalPedido().observe(getViewLifecycleOwner(), total -> {
            binding.textTotalAmount.setText(String.format(Locale.US, "$%.2f", total));
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}