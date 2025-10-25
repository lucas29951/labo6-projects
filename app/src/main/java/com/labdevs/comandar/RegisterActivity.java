package com.labdevs.comandar;

import static androidx.activity.result.contract.ActivityResultContracts.*;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.labdevs.comandar.databinding.ActivityRegisterBinding;
import com.labdevs.comandar.viewmodels.RegisterViewModel;

    public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private RegisterViewModel viewModel;

    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        pickMedia = registerForActivityResult(
                new PickVisualMedia(),
                uri -> {
                    if (uri != null) {
                        binding.inputRegisterImage.setImageURI(uri);
                        binding.inputRegisterUri.setText(uri.getPath());
                    } else {
                        Toast.makeText(this, "No se selecciono una imagen", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        binding.inputRegisterImage.setOnClickListener(v -> {
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });

        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        binding.buttonRegister.setOnClickListener(v -> {
            String nombre = binding.inputRegisterName.getText().toString().trim();
            String apellido = binding.inputRegisterLastname.getText().toString().trim();
            String email = binding.inputRegisterEmail.getText().toString().trim();
            String password = binding.inputRegisterPassword.getText().toString();
            String telefono = binding.inputRegisterPhone.getText().toString().trim();
            String imagen = binding.inputRegisterUri.getText().toString().trim();
            viewModel.registrarCamarero(nombre, apellido, email, password, telefono, imagen);
        });

        viewModel.getRegistroExitoso().observe(this, exitoso -> {
            if (exitoso) {
                Toast.makeText(this, "Registro exitoso. Ahora puedes iniciar sesión.", Toast.LENGTH_LONG).show();
                finish();
            }
        });

        viewModel.getError().observe(this, mensajeError -> {
            if (mensajeError != null && !mensajeError.isEmpty()) {
                Toast.makeText(this, mensajeError, Toast.LENGTH_SHORT).show();
            }
        });

        binding.iconBack.setOnClickListener(v -> finish());
    }
}