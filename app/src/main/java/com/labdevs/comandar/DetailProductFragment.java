package com.labdevs.comandar;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.labdevs.comandar.databinding.FragmentDetailProductBinding;
import com.labdevs.comandar.viewmodels.DetailProductViewModel;

import java.io.File;
import java.util.Locale;

public class DetailProductFragment extends Fragment {

    private FragmentDetailProductBinding binding;
    private DetailProductViewModel viewModel;
    private int productId = -1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Obtenemos el ID del producto desde los argumentos del fragmento
        if (getArguments() != null) {
            productId = getArguments().getInt("PRODUCT_ID", -1);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDetailProductBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(DetailProductViewModel.class);

        // Configurar la Toolbar para el botón de regreso
        binding.toolbarDetail.setNavigationOnClickListener(v ->
                NavHostFragment.findNavController(DetailProductFragment.this).navigateUp()
        );

        if (productId == -1) {
            Toast.makeText(getContext(), "Error: Producto no encontrado.", Toast.LENGTH_SHORT).show();
            NavHostFragment.findNavController(this).navigateUp();
            return;
        }

        observeViewModel();

        binding.buttonAddToOrder.setOnClickListener(v -> {
            int camareroId = ((MainActivity) requireActivity()).getCamareroId();
            AddProductDialogFragment dialogFragment = AddProductDialogFragment.newInstance(productId, camareroId);
            dialogFragment.show(getParentFragmentManager(), "AddProductDialog");
        });
    }

    private void observeViewModel() {
        viewModel.getProducto(productId).observe(getViewLifecycleOwner(), producto -> {
            if (producto != null) {
                binding.textProductNameDetail.setText(producto.nombre);
                binding.textProductPriceDetail.setText(String.format(Locale.US, "$%.2f", producto.precio));
                binding.textProductDescriptionDetail.setText(producto.descripcion);

                if (producto.disponible) {
                    binding.chipStatusDetail.setText("Disponible");
                    binding.chipStatusDetail.setChipBackgroundColorResource(R.color.status_disponible);
                    binding.buttonAddToOrder.setEnabled(true);
                } else {
                    binding.chipStatusDetail.setText("Agotado");
                    binding.chipStatusDetail.setChipBackgroundColorResource(R.color.status_agotado);
                    binding.buttonAddToOrder.setEnabled(false);
                }

                if (producto.fotoUrl != null && !producto.fotoUrl.isEmpty()) {
                    File imgFile = new File(producto.fotoUrl);
                    if (imgFile.exists()) {
                        binding.imageProductDetail.setImageURI(Uri.fromFile(imgFile));
                    }
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Prevenir fugas de memoria
    }
}