package com.labdevs.comandar.viewmodels;

import android.app.Application;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.labdevs.comandar.RegisterActivity;
import com.labdevs.comandar.data.database.AppDatabase;
import com.labdevs.comandar.data.dto.RegisterRequest;
import com.labdevs.comandar.data.dto.UserResponse;
import com.labdevs.comandar.data.entity.Camarero;
import com.labdevs.comandar.data.repository.AppRepository;
import com.labdevs.comandar.service.RetrofitClient;
import com.labdevs.comandar.utils.PasswordUtils;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterViewModel extends AndroidViewModel {
    private final AppRepository repository;
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> registroExitoso = new MutableLiveData<>();

    public RegisterViewModel(@NonNull Application application) {
        super(application);
        repository = new AppRepository(application);
    }

    public LiveData<String> getError() { return error; }
    public LiveData<Boolean> getRegistroExitoso() { return registroExitoso; }

    public void registrarCamarero(String nombre, String apellido, String email, String password, String confirmPassword, String telefono, String imagen) {
        if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || imagen.isEmpty()) {
            error.setValue("Todos los campos son obligatorios.");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            error.setValue("El formato del email no es válido.");
            return;
        }
        if (!isPasswordValid(password)) {
            error.setValue("La contraseña debe tener al menos 8 caracteres, una letra, un numero y un caracter especial.");
            return;
        }

        if (!isPasswordValid(confirmPassword)) {
            error.setValue("La contraseña debe tener al menos 8 caracteres, una letra, un numero y un caracter especial.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            error.setValue("Las contraseñas deben ser iguales.");
            return;
        }

        String hashedPassword = PasswordUtils.hashPassword(password);
        if (hashedPassword == null) {
            error.setValue("Error de seguridad al procesar la contraseña.");
            return;
        }

        RegisterRequest request = new RegisterRequest(nombre, apellido, email, password);

        Camarero nuevoCamarero = new Camarero();
        nuevoCamarero.nombre = nombre;
        nuevoCamarero.apellido = apellido;
        nuevoCamarero.email = email;
        nuevoCamarero.passwordHash = hashedPassword;
        nuevoCamarero.numeroContacto = telefono;
        nuevoCamarero.fotoUrl = imagen;
        nuevoCamarero.createdAt = new Date();

        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                repository.registrarCamarero(nuevoCamarero);
                registroExitoso.postValue(true);

                registrarEnLaAPI(request);
            } catch (Exception e) {
                // Captura de error, por ejemplo, si el email ya existe (UNIQUE constraint)
                error.postValue("El email ya está registrado.");
            }
        });
    }

    private boolean isPasswordValid(String password) {
        if (password.length() < 8) {
            return false;
        }

        boolean hasLetter = password.matches(".*[A-Za-z].*");

        boolean hasDigit = password.matches(".*[0-9].*");

        boolean hasSpecial = password.matches(".*[^A-Za-z0-9].*");

        return hasLetter && hasDigit && hasSpecial;
    }

    private void registrarEnLaAPI(RegisterRequest req) {
        RetrofitClient.getApiService().register(req).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful()) {
                    Log.d("devtest", "Registro existoso en la API, ID: " + response.body().id);
                } else {
                    Log.d("devtest", "Error al registrar en la API");
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Log.d("devtest", "Error de conexion: " + t.getMessage());
            }
        });
    }
}
