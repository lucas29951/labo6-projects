package com.labdevs.comandar;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.labdevs.comandar.data.entity.Mesa;
import com.labdevs.comandar.databinding.FragmentAddProductDialogBinding;
import com.labdevs.comandar.viewmodels.AddProductViewModel;

import java.util.List;
import java.util.stream.Collectors;

public class AddProductDialogFragment extends DialogFragment {
    private FragmentAddProductDialogBinding binding;
    private AddProductViewModel viewModel;
    private int cantidad = 1;
    public static final String PRODUCT_ADDED_REQUEST_KEY = "productAddedRequest";

    static AddProductDialogFragment newInstance(int productoId, int camareroId) {
        AddProductDialogFragment fragment = new AddProductDialogFragment();
        Bundle args = new Bundle();
        args.putInt("PRODUCT_ID", productoId);
        args.putInt("CAMARERO_ID", camareroId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            // Establecer el ancho para que coincida con el 90% del ancho del padre
            int width = (int)(getResources().getDisplayMetrics().widthPixels * 0.90);
            // La altura se ajustará al contenido
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            getDialog().getWindow().setLayout(width, height);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddProductDialogBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AddProductViewModel.class);

        int productoId = getArguments().getInt("PRODUCT_ID");
        int camareroId = getArguments().getInt("CAMARERO_ID");

        setupListeners(productoId, camareroId);
        observeViewModel();
        viewModel.cargarMesasAsignadas(camareroId);
    }

    private void setupListeners(int productoId, int camareroId) {
        binding.buttonIncrease.setOnClickListener(v -> {
            cantidad++;
            binding.textQuantity.setText(String.valueOf(cantidad));
        });
        binding.buttonDecrease.setOnClickListener(v -> {
            if (cantidad > 1) {
                cantidad--;
                binding.textQuantity.setText(String.valueOf(cantidad));
            }
        });
        binding.buttonConfirmAdd.setOnClickListener(v -> {
            Mesa mesaSeleccionada = (Mesa) binding.spinnerMesas.getSelectedItem();
            String notasInput = binding.editTextNotes.getText().toString().trim();
            String notas = notasInput.isEmpty() ? null : notasInput;
            viewModel.confirmarAñadirProducto(productoId, mesaSeleccionada, camareroId, cantidad, notas);
        });

        binding.buttonCancel.setOnClickListener(v -> dismiss());
    }

    private void observeViewModel() {
        viewModel.getMesasAsignadas().observe(getViewLifecycleOwner(), mesas -> {
            if (mesas != null && !mesas.isEmpty()) {
                ArrayAdapter<Mesa> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, mesas);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.spinnerMesas.setAdapter(adapter);
                binding.spinnerMesas.setEnabled(true);
                binding.buttonConfirmAdd.setEnabled(true);
            } else {
                binding.spinnerMesas.setEnabled(false);
                binding.buttonConfirmAdd.setEnabled(false);
                Toast.makeText(getContext(), "No tienes mesas asignadas para añadir un pedido.", Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getToastMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                viewModel.onToastShown();
                if (!message.contains("Por favor")) { // Si no es un error de validación
                    // Envía un resultado al FragmentManager padre para notificar que se añadió un producto.
                    getParentFragmentManager().setFragmentResult(PRODUCT_ADDED_REQUEST_KEY, new Bundle());
                    dismiss(); // Cerrar el diálogo después de añadir
                }
            }
        });
    }
}