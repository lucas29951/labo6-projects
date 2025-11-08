package com.labdevs.comandar;

import android.icu.text.SimpleDateFormat;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

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
                viewModel.eliminarPedido(pedido.pedido.pedidoId);
                Toast.makeText(getContext(), "FUNCIONALIDAD: Eliminar pedido " + pedido.pedido.pedidoId, Toast.LENGTH_SHORT).show();
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
                    viewModel.editarPedido(pcr.pedido.pedidoId);
                    message = "FUNCIONALIDAD: Editar pedido " + pcr.pedido.pedidoId;
                } else {
                    viewModel.enviarACocina(pcr.pedido.pedidoId);
                    message = "FUNCIONALIDAD: Enviar a cocina pedido " + pcr.pedido.pedidoId;
                }
                break;
            case enviado:
                if (isButton1) {
                    viewModel.volverAabrirPedido(pcr.pedido.pedidoId);
                    message = "FUNCIONALIDAD: Volver a abrir pedido " + pcr.pedido.pedidoId;
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