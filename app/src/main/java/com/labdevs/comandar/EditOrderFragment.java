package com.labdevs.comandar;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.labdevs.comandar.adapters.EditOrderAdapter;
import com.labdevs.comandar.data.model.ItemPedido;
import com.labdevs.comandar.databinding.FragmentEditOrderBinding;
import com.labdevs.comandar.viewmodels.EditOrderViewModel;

import java.util.Locale;

public class EditOrderFragment extends Fragment {
    private FragmentEditOrderBinding binding;
    private EditOrderViewModel viewModel;
    private EditOrderAdapter adapter;
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
        binding = FragmentEditOrderBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(EditOrderViewModel.class);
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
        adapter = new EditOrderAdapter(new EditOrderAdapter.OnItemInteractionListener() {
            @Override
            public void onIncreaseClick(ItemPedido item) { viewModel.aumentarCantidad(item); }
            @Override
            public void onDecreaseClick(ItemPedido item) { viewModel.disminuirCantidad(item); }
            @Override
            public void onDeleteClick(ItemPedido item) {
                // Lógica para el último ítem
                if (adapter.getItemCount() <= 1) {
                    mostrarDialogoConfirmacionUltimoItem(item);
                } else {
                    viewModel.eliminarItem(item);
                }
            }
        });
        binding.recyclerViewItems.setAdapter(adapter);
        binding.recyclerViewItems.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void setupClickListeners() {
        binding.fabAddItem.setOnClickListener(v -> {
            // Preparamos el argumento para indicar el origen
            Bundle args = new Bundle();
            args.putString("origin", "EditOrderFragment");

            // Usamos el NavController para navegar con los argumentos
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_editOrderFragment_to_menuFragment, args);
        });
        binding.btnEnviarPedido.setOnClickListener(v -> viewModel.enviarPedido());
    }

    private void observeViewModel() {
        viewModel.itemsDelPedido.observe(getViewLifecycleOwner(), items -> {
            adapter.submitList(items);
            binding.summaryItemCount.setText(items.size() + " items");
        });

        viewModel.getTotalPedido().observe(getViewLifecycleOwner(), total -> {
            binding.summaryTotal.setText(String.format(Locale.US, "$%.2f", total));
        });

        // Observadores para navegación
        viewModel.getPedidoEnviado().observe(getViewLifecycleOwner(), enviado -> {
            if (enviado) {
                Toast.makeText(getContext(), "Pedido enviado a cocina", Toast.LENGTH_SHORT).show();
                NavHostFragment.findNavController(this).popBackStack();
                viewModel.onNavigationDone();
            }
        });

        viewModel.getPedidoEliminado().observe(getViewLifecycleOwner(), eliminado -> {
            if(eliminado) {
                Toast.makeText(getContext(), "El pedido ha sido eliminado", Toast.LENGTH_SHORT).show();
                NavHostFragment.findNavController(this).popBackStack(R.id.accountFragment, false);
                viewModel.onNavigationDone();
            }
        });
    }

    private void mostrarDialogoConfirmacionUltimoItem(ItemPedido item) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar Último Ítem")
                .setMessage("Si eliminas este ítem, el pedido completo se cancelará. ¿Deseas continuar?")
                .setPositiveButton("Sí, eliminar pedido", (dialog, which) -> viewModel.eliminarItem(item))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}