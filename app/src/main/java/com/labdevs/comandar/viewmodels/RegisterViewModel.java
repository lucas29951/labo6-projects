package com.labdevs.comandar.viewmodels;

import android.app.Application;
import android.util.Patterns;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.labdevs.comandar.data.database.AppDatabase;
import com.labdevs.comandar.data.entity.Camarero;
import com.labdevs.comandar.data.repository.AppRepository;
import com.labdevs.comandar.utils.PasswordUtils;

import java.util.Date;

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
        if (password.length() < 6) {
            error.setValue("La contraseña debe tener al menos 6 caracteres.");
            return;
        }

        if (confirmPassword.length() < 6) {
            error.setValue("La contraseña debe tener al menos 6 caracteres.");
            return;
        }

        if (password.compareTo(confirmPassword) != 0) {
            error.setValue("Las contraseñas deben ser iguales.");
            return;
        }

        String hashedPassword = PasswordUtils.hashPassword(password);
        if (hashedPassword == null) {
            error.setValue("Error de seguridad al procesar la contraseña.");
            return;
        }

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
            } catch (Exception e) {
                // Captura de error, por ejemplo, si el email ya existe (UNIQUE constraint)
                error.postValue("El email ya está registrado.");
            }
        });
    }
}
