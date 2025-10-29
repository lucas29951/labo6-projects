package com.labdevs.comandar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.labdevs.comandar.databinding.ActivityLoginBinding;
import com.labdevs.comandar.viewmodels.LoginViewModel;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private LoginViewModel viewModel;
    public static final String CAMARERO_ID_KEY = "CAMARERO_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        binding.buttonLogin.setOnClickListener(v -> {
            String email = binding.inputEmail.getText().toString().trim();
            String password = binding.inputPassword.getText().toString();
            viewModel.iniciarSesion(email, password);
        });

        viewModel.getCamareroLogueado().observe(this, camarero -> {
            if (camarero != null) {
                Toast.makeText(this, "Bienvenido " + camarero.nombre, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra(CAMARERO_ID_KEY, camarero.camareroId);
                Log.d("LoginActivity", "Enviando CAMARERO_ID: " + camarero.camareroId); // DEBUG
                // Estas flags limpian el historial de navegación para que el usuario no pueda
                // volver a la pantalla de login con el botón de "atrás".
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        viewModel.getError().observe(this, mensajeError -> {
            if (mensajeError != null && !mensajeError.isEmpty()) {
                Toast.makeText(this, mensajeError, Toast.LENGTH_SHORT).show();
            }
        });

        binding.registerAccountLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        binding.forgotPasswordLink.setOnClickListener(v -> {
            // Lógica para "Olvidé mi contraseña"
            Toast.makeText(this, "Funcionalidad no implementada.", Toast.LENGTH_SHORT).show();
        });
    }
}