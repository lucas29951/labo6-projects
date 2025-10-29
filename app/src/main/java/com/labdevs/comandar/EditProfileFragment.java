package com.labdevs.comandar;

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

import com.labdevs.comandar.databinding.FragmentEditProfileBinding;
import com.labdevs.comandar.viewmodels.EditProfileViewModel;

public class EditProfileFragment extends Fragment {

    private FragmentEditProfileBinding binding;
    private EditProfileViewModel viewModel;
    private int camareroId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            camareroId = getArguments().getInt("CAMARERO_ID", -1);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (camareroId == -1) return;

        viewModel = new ViewModelProvider(this).get(EditProfileViewModel.class);
        viewModel.loadCamarero(camareroId);

        observeViewModel();

        binding.buttonGuardarPerfil.setOnClickListener(v -> {
            String nombre = binding.editTextNombre.getText().toString();
            String apellido = binding.editTextApellido.getText().toString();
            String telefono = binding.editTextTelefono.getText().toString();
            viewModel.guardarCambios(nombre, apellido, telefono);
        });
    }

    private void observeViewModel() {
        viewModel.camarero.observe(getViewLifecycleOwner(), camarero -> {
            if (camarero != null) {
                binding.editTextNombre.setText(camarero.nombre);
                binding.editTextApellido.setText(camarero.apellido);
                binding.editTextTelefono.setText(camarero.numeroContacto);
            }
        });

        viewModel.getUpdateSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                Toast.makeText(getContext(), "Perfil actualizado", Toast.LENGTH_SHORT).show();
                NavHostFragment.findNavController(this).popBackStack();
                viewModel.onNavigationDone();
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                viewModel.onNavigationDone();
            }
        });
    }
}