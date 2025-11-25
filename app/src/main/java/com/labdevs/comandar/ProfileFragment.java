package com.labdevs.comandar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.labdevs.comandar.databinding.FragmentProfileBinding;
import com.labdevs.comandar.viewmodels.ProfileViewModel;

import java.io.File;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private ProfileViewModel viewModel;
    private int camareroId = -1;
    private Integer mesasAsignadasCount = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() instanceof MainActivity) {
            this.camareroId = ((MainActivity) getActivity()).getCamareroId();
            Log.d("ProfileFragment", "ID de camarero obtenido de MainActivity: " + this.camareroId);
        }

        if (camareroId == -1) {
            Toast.makeText(getContext(), "Error de sesión. Volviendo al login.", Toast.LENGTH_LONG).show();
            navigateToLogin();
            return;
        }

        try {
            viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
            viewModel.loadCamarero(camareroId);
        } catch (Exception e) {
            Log.e("ProfileFragment", "CRASH al inicializar ViewModel", e);
            Toast.makeText(getContext(), "Error fatal al cargar perfil.", Toast.LENGTH_LONG).show();
            navigateToLogin();
            return;
        }

        observeViewModel();
        setupClickListeners();
    }

    private void observeViewModel() {
        // Observador de Camarero
        if (viewModel.getCamarero() == null) {
            Log.e("ProfileFragment", "ViewModel.getCamarero() es nulo. No se puede observar.");
            return;
        }
        viewModel.getCamarero().observe(getViewLifecycleOwner(), camarero -> {
            if (camarero != null) {
                binding.textFullName.setText(String.format("%s %s", camarero.nombre, camarero.apellido));
                binding.textEmail.setText(camarero.email);
                binding.textContactPhone.setText(camarero.numeroContacto != null && !camarero.numeroContacto.isEmpty() ? camarero.numeroContacto : "No disponible");
                if (camarero.fotoUrl != null && !camarero.fotoUrl.isEmpty()) {
                    File imgFile = new File(camarero.fotoUrl);
                    if (imgFile.exists()) {
                        binding.profileImage.setImageURI(Uri.fromFile(imgFile));
                    }
                }
            } else {
                Log.w("ProfileFragment", "El objeto Camarero observado es nulo.");
            }
        });

        // Observador de Conteo de Mesas
        if (viewModel.getConteoMesas() == null) {
            Log.e("ProfileFragment", "ViewModel.getConteoMesas() es nulo. No se puede observar.");
            return;
        }
        viewModel.getConteoMesas().observe(getViewLifecycleOwner(), count -> {
            mesasAsignadasCount = (count != null) ? count : 0;
            binding.textTablesCount.setText(String.valueOf(mesasAsignadasCount));
        });

        // Observador de Logout
        viewModel.getNavigateToLogin().observe(getViewLifecycleOwner(), navigate -> {
            if (navigate != null && navigate) {
                navigateToLogin();
                viewModel.onLoginNavigated();
            }
        });
    }

    private void setupClickListeners() {
        binding.buttonLogout.setOnClickListener(v -> showLogoutConfirmationDialog());

        binding.buttonEditProfile.setOnClickListener(v -> {
            try {
                NavController navController = Navigation.findNavController(v);
                Bundle args = new Bundle();
                args.putInt(LoginActivity.CAMARERO_ID_KEY, camareroId);
                navController.navigate(R.id.action_profileFragment_to_editProfileFragment, args);
            } catch (IllegalStateException e) {
                Log.e("ProfileFragment", "Error al navegar a EditProfile (IllegalStateException): ", e);
                Toast.makeText(getContext(), "No se puede navegar ahora, intente de nuevo.", Toast.LENGTH_SHORT).show();
            }
        });

        binding.buttonChangePassword.setOnClickListener(v -> {
            try {
                NavController navController = Navigation.findNavController(v);
                Bundle args = new Bundle();
                args.putInt(LoginActivity.CAMARERO_ID_KEY, camareroId);
                navController.navigate(R.id.action_profileFragment_to_changePasswordFragment, args);
            } catch (IllegalStateException e) {
                Log.e("ProfileFragment", "Error al navegar a ChangePassword (IllegalStateException): ", e);
                Toast.makeText(getContext(), "No se puede navegar ahora, intente de nuevo.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLogoutConfirmationDialog() {
        if (getContext() == null) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Cerrar Sesión");
        if (mesasAsignadasCount > 0) {
            builder.setMessage("Tienes " + mesasAsignadasCount + " mesas asignadas. Si cierras sesión, se desasignarán automáticamente. ¿Deseas continuar?");
        } else {
            builder.setMessage("¿Estás seguro de que quieres cerrar sesión?");
        }
        builder.setPositiveButton("Confirmar", (dialog, which) -> {
            if (viewModel != null) {
                viewModel.logout();
                LoginActivity.clearSavedSession(getContext());
            }
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void navigateToLogin() {
        if (getActivity() == null) return;
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}