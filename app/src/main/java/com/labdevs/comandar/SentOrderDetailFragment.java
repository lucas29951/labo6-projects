package com.labdevs.comandar;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.labdevs.comandar.adapters.SentOrderItemAdapter;
import com.labdevs.comandar.databinding.FragmentSentOrderDetailBinding;
import com.labdevs.comandar.viewmodels.SentOrderDetailViewModel;

import java.util.Locale;

public class SentOrderDetailFragment extends Fragment {
    private FragmentSentOrderDetailBinding binding;
    private SentOrderDetailViewModel viewModel;
    private SentOrderItemAdapter adapter;
    private int pedidoId;
    private int mesaNumero;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pedidoId = getArguments().getInt("pedidoId");
            mesaNumero = getArguments().getInt("mesaNumero");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSentOrderDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(SentOrderDetailViewModel.class);
        viewModel.init(pedidoId);

        setupToolbar();
        setupRecyclerView();
        setupClickListeners();
        observeViewModel();
    }

    private void setupToolbar() {
        binding.toolbar.setTitle("Mesa " + mesaNumero);
        binding.toolbar.setNavigationOnClickListener(v -> NavHostFragment.findNavController(this).navigateUp());
    }

    private void setupRecyclerView() {
        adapter = new SentOrderItemAdapter();
        binding.recyclerViewItems.setAdapter(adapter);
        binding.recyclerViewItems.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void setupClickListeners() {
        binding.btnReopenOrder.setOnClickListener(v -> {
            viewModel.reopenOrder();
            Toast.makeText(getContext(), "Pedido reabierto para edición.", Toast.LENGTH_SHORT).show();
        });
        binding.btnCloseOrder.setOnClickListener(v -> {
            viewModel.closeOrder();
            Toast.makeText(getContext(), "Pedido cerrado y mesa liberada.", Toast.LENGTH_SHORT).show();
        });
    }

    private void observeViewModel() {
        viewModel.itemsDelPedido.observe(getViewLifecycleOwner(), items -> {
            adapter.submitList(items);
            if (items != null) {
                binding.summaryItemCount.setText(items.size() + " items");
            }
        });

        viewModel.getTotalPedido().observe(getViewLifecycleOwner(), total -> {
            binding.summaryTotal.setText(String.format(Locale.US, "$%.2f", total));
        });

        viewModel.getOrderActionFinished().observe(getViewLifecycleOwner(), finished -> {
            if (finished) {
                NavHostFragment.findNavController(this).popBackStack();
                viewModel.onNavigationDone();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}