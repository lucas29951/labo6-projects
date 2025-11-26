package com.labdevs.comandar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
    public static final String PREFS_NAME = "USER_PREFS";
    public static final String PREF_REMEMBER = "remember_session";
    public static final String PREF_USER_ID = "saved_user_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        checkSavedSession();

        binding.buttonLogin.setOnClickListener(v -> {
            String email = binding.inputEmail.getText().toString().trim();
            String password = binding.inputPassword.getText().toString();
            viewModel.iniciarSesion(email, password);
        });

        viewModel.getCamareroLogueado().observe(this, camarero -> {
            if (camarero != null) {
                Toast.makeText(this, "Bienvenido " + camarero.nombre, Toast.LENGTH_SHORT).show();
                
                saveUserSession(camarero.camareroId);

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

        binding.infoLocationLink.setOnClickListener(v -> {
            // Logica para mostrar el mapa y la direccion del local
            Toast.makeText(this, "Funcionalidad no implementada.", Toast.LENGTH_SHORT).show();
        });
    }

    private void saveUserSession(int idUser) {
        if (binding.checkboxRemember.isChecked()) {
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            editor.putBoolean(PREF_REMEMBER, true);
            editor.putInt(PREF_USER_ID, idUser);
            editor.apply();

            Log.d("LoginActivity", "Sesion guardada. USER_ID=" + idUser);
        }
    }

    private void checkSavedSession() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean remember = prefs.getBoolean(PREF_REMEMBER, false);

        if (remember) {
            int savedId = prefs.getInt(PREF_USER_ID, -1);

            if (savedId != -1) {
                Log.d("LoginActivity", "Sesion detectada. USER_ID=" + savedId);

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra(CAMARERO_ID_KEY, savedId);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        }
    }

    public static void clearSavedSession(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }
}