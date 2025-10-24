package com.labdevs.comandar;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.labdevs.comandar.databinding.ActivityRegisterBinding;
import com.labdevs.comandar.viewmodels.RegisterViewModel;

    public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private RegisterViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        binding.buttonRegister.setOnClickListener(v -> {
            String nombre = binding.inputRegisterName.getText().toString().trim();
            String apellido = binding.inputRegisterLastname.getText().toString().trim();
            String email = binding.inputRegisterEmail.getText().toString().trim();
            String password = binding.inputRegisterPassword.getText().toString();
            String telefono = binding.inputRegisterPhone.getText().toString().trim();
            viewModel.registrarCamarero(nombre, apellido, email, password, telefono);
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