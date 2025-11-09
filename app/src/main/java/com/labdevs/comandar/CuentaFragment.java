package com.labdevs.comandar;

import android.icu.text.SimpleDateFormat;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.labdevs.comandar.adapters.PedidoAdapter;
import com.labdevs.comandar.data.entity.enums.EstadoPedido;
import com.labdevs.comandar.data.model.PedidoConResumen;
import com.labdevs.comandar.databinding.FragmentCuentaBinding;
import com.labdevs.comandar.viewmodels.CuentaViewModel;

import java.util.Date;
import java.util.Locale;

public class CuentaFragment extends Fragment {

    private FragmentCuentaBinding binding;
    private CuentaViewModel viewModel;
    private PedidoAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // CONFIGURAMOS EL OYENTE DE RESULTADOS AQUÍ, EN onCreate.
        // Esto asegura que el listener esté registrado antes de que la vista se cree
        // y pueda sobrevivir a cambios de configuración.
        getParentFragmentManager().setFragmentResultListener(FilterDialogFragment.REQUEST_KEY, this, (requestKey, bundle) -> {
            // Este código se ejecutará cuando el FilterDialogFragment envíe un resultado.
            long fechaInicioMillis = bundle.getLong(FilterDialogFragment.KEY_FECHA_INICIO);
            long fechaFinMillis = bundle.getLong(FilterDialogFragment.KEY_FECHA_FIN);

            // Convertimos los milisegundos de vuelta a objetos Date
            Date fechaInicio = new Date(fechaInicioMillis);
            Date fechaFin = new Date(fechaFinMillis);

            // Llamamos a nuestro método onFilterApplied con los datos
            onFilterApplied(fechaInicio, fechaFin);
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCuentaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(CuentaViewModel.class);

        // Obtenemos el ID del camarero desde la MainActivity
        int camareroId = ((MainActivity) requireActivity()).getCamareroId();
        if (camareroId != -1) {
            // Pasamos el ID al ViewModel para que pueda empezar a cargar los datos
            viewModel.setCamareroId(camareroId);
        } else {
            Toast.makeText(getContext(), "Error de sesión.", Toast.LENGTH_LONG).show();
            // Aquí podrías navegar de vuelta al login si fuera necesario
        }

        setupRecyclerView();
        setupChipGroupListener();
        setupToolbar();
        setupFilterChip();
        observeViewModel();
    }

    private void setupRecyclerView() {
        adapter = new PedidoAdapter(new PedidoAdapter.OnPedidoActionsListener() {
            @Override
            public void onButton1Click(PedidoConResumen pedido) {
                handleAction(pedido, true);
            }

            @Override
            public void onButton2Click(PedidoConResumen pedido) {
                handleAction(pedido, false);
            }

            @Override
            public void onDeleteClick(PedidoConResumen pedido) {
                // Nueva llamada específica para la eliminación
                mostrarDialogoConfirmacionEliminar(pedido);
            }

            @Override
            public void onCardClick(PedidoConResumen pedido) {
                switch (pedido.pedido.estado) {
                    case abierto:
                        // Si el pedido está abierto, el click en la tarjeta hace lo mismo que el botón "Editar"
                        Bundle args = new Bundle();
                        args.putInt("pedidoId", pedido.pedido.pedidoId);
                        args.putInt("mesaNumero", pedido.pedido.mesaId);
                        NavHostFragment.findNavController(CuentaFragment.this)
                                .navigate(R.id.action_accountFragment_to_editOrderFragment, args);
                        break;
                    case enviado:
                        // Para pedidos enviados, mostramos un Toast como placeholder
                        Toast.makeText(getContext(), "Funcionalidad para ver pedido enviado no implementada.", Toast.LENGTH_SHORT).show();
                        break;
                    case cerrado:
                        // Para pedidos cerrados, lo mismo que el botón "Ver"
                        Toast.makeText(getContext(), "FUNCIONALIDAD: Ver detalle del pedido " + pedido.pedido.pedidoId, Toast.LENGTH_SHORT).show();
                        break;
                }
            }

        });
        binding.recyclerViewPedidos.setAdapter(adapter);
    }

    private void setupToolbar() {
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_filter) {
                // Lanzamos el nuevo DialogFragment
                FilterDialogFragment dialog = new FilterDialogFragment();
                dialog.show(getParentFragmentManager(), "FilterDialog");
                return true;
            }
            return false;
        });
    }

    private void setupFilterChip() {
        binding.chipFiltroFecha.setOnCloseIconClickListener(v -> {
            viewModel.resetFiltroFecha();
            binding.chipFiltroFecha.setVisibility(View.GONE);
        });
    }
    private void setupChipGroupListener() {
        binding.toggleButtonGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            // Solo reaccionamos cuando un botón es marcado, no cuando es desmarcado
            if (isChecked) {
                if (checkedId == R.id.button_abiertos) {
                    viewModel.setFiltroEstado(EstadoPedido.abierto);
                } else if (checkedId == R.id.button_enviados) {
                    viewModel.setFiltroEstado(EstadoPedido.enviado);
                } else if (checkedId == R.id.button_cerrados) {
                    viewModel.setFiltroEstado(EstadoPedido.cerrado);
                }
            }
        });
    }

    private void observeViewModel() {
        viewModel.pedidos.observe(getViewLifecycleOwner(), pedidos -> {
            if (pedidos != null) {
                adapter.submitList(pedidos);
            }

            // LÓGICA DE ESTADO VACÍO
            if (pedidos.isEmpty()) {
                binding.recyclerViewPedidos.setVisibility(View.GONE);
                binding.textEmptyState.setVisibility(View.VISIBLE);
                // Actualizar el texto del estado vacío
                updateEmptyStateText();
            } else {
                binding.recyclerViewPedidos.setVisibility(View.VISIBLE);
                binding.textEmptyState.setVisibility(View.GONE);
            }
        });
    }

    private void updateEmptyStateText() {
        boolean filtroAplicado = binding.chipFiltroFecha.getVisibility() == View.VISIBLE;
        int checkedId = binding.toggleButtonGroup.getCheckedButtonId();
        String estado = "pedidos";
        if (checkedId == R.id.button_abiertos) estado = "abiertos";
        else if (checkedId == R.id.button_enviados) estado = "enviados";
        else if (checkedId == R.id.button_cerrados) estado = "cerrados";

        if (filtroAplicado) {
            binding.textEmptyState.setText("No tienes pedidos " + estado + " con el filtro aplicado.");
        } else {
            binding.textEmptyState.setText("No tienes pedidos " + estado + ".");
        }
    }

    private void handleAction(PedidoConResumen pcr, boolean isButton1) {
        String message = "";
        switch(pcr.pedido.estado) {
            case abierto:
                if (isButton1) {
                    // Preparamos los argumentos para la nueva pantalla
                    Bundle args = new Bundle();
                    args.putInt("pedidoId", pcr.pedido.pedidoId);

                    // Buscamos el número de la mesa. Necesitaremos un método síncrono para esto.
                    // Lo añadiremos en el siguiente paso. Por ahora, asumimos que lo tenemos.
                    // Para simplificar, lo pasaremos desde el Pedido, que ya tiene mesaId.
                    args.putInt("mesaNumero", pcr.pedido.mesaId); // Pasamos el ID, que es el número en este caso

                    // Navegamos
                    NavHostFragment.findNavController(CuentaFragment.this)
                            .navigate(R.id.action_accountFragment_to_editOrderFragment, args);
                    return;
                } else {
                    viewModel.enviarPedidoACocina(pcr);
                    message = "Pedido de la Mesa " + pcr.pedido.mesaId + " enviado a cocina.";
                }
                break;
            case enviado:
                if (isButton1) {
                    viewModel.solicitarReabrirPedido(pcr);

                    // Navegar a la pantalla de edición
                    Bundle args = new Bundle();
                    args.putInt("pedidoId", pcr.pedido.pedidoId);
                    args.putInt("mesaNumero", pcr.pedido.mesaId);
                    NavHostFragment.findNavController(this)
                            .navigate(R.id.action_accountFragment_to_editOrderFragment, args);
                    return; // Salir para no mostrar toast genérico
                } else {
                    viewModel.cerrarPedido(pcr.pedido.pedidoId);
                    message = "FUNCIONALIDAD: Cerrar pedido " + pcr.pedido.pedidoId;
                }
                break;
            case cerrado:
                viewModel.verDetallePedido(pcr.pedido.pedidoId);
                message = "FUNCIONALIDAD: Ver detalle del pedido " + pcr.pedido.pedidoId;
                break;
        }
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void mostrarDialogoConfirmacionEliminar(PedidoConResumen pcr) {
        if (getContext() == null) return;

        new AlertDialog.Builder(getContext())
                .setTitle("Confirmar Eliminación")
                .setMessage("¿Estás seguro de que quieres eliminar el pedido de la Mesa " + pcr.pedido.mesaId + "? Esta acción no se puede deshacer.")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    // Si el usuario confirma, llamamos al ViewModel
                    viewModel.solicitarEliminacionPedido(pcr);
                    Toast.makeText(getContext(), "Pedido eliminado.", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null) // No hace nada, solo cierra el diálogo
                .setIcon(R.drawable.ic_delete_red)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Prevenir memory leaks
    }

    private void onFilterApplied(Date fechaInicio, Date fechaFin) {
        viewModel.setFiltroFecha(fechaInicio, fechaFin);

        // Actualizar el Chip para mostrar el filtro aplicado
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault());
        String filtroTexto = sdf.format(fechaInicio) + " - " + sdf.format(fechaFin);
        binding.chipFiltroFecha.setText(filtroTexto);
        binding.chipFiltroFecha.setVisibility(View.VISIBLE);
    }
}