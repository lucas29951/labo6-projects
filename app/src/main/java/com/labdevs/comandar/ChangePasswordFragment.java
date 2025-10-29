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

import com.labdevs.comandar.databinding.FragmentChangePasswordBinding;
import com.labdevs.comandar.viewmodels.ChangePasswordViewModel;

public class ChangePasswordFragment extends Fragment {

    private FragmentChangePasswordBinding binding;
    private ChangePasswordViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChangePasswordBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int camareroId = getArguments() != null ? getArguments().getInt("CAMARERO_ID", -1) : -1;
        if (camareroId == -1) return;

        viewModel = new ViewModelProvider(this).get(ChangePasswordViewModel.class);
        viewModel.setCamareroId(camareroId);

        observeViewModel();

        binding.buttonGuardarPass.setOnClickListener(v -> {
            String actual = binding.editTextPassActual.getText().toString();
            String nueva = binding.editTextPassNueva.getText().toString();
            String confirm = binding.editTextPassConfirm.getText().toString();
            viewModel.cambiarContraseña(actual, nueva, confirm);
        });
    }

    private void observeViewModel() {
        viewModel.getUpdateSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                Toast.makeText(getContext(), "Contraseña actualizada", Toast.LENGTH_SHORT).show();
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