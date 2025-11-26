package com.labdevs.comandar.viewmodels;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.labdevs.comandar.data.database.AppDatabase;
import com.labdevs.comandar.data.dto.LoginRequest;
import com.labdevs.comandar.data.dto.UserResponse;
import com.labdevs.comandar.data.entity.Camarero;
import com.labdevs.comandar.data.repository.AppRepository;
import com.labdevs.comandar.service.RetrofitClient;
import com.labdevs.comandar.utils.PasswordUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends AndroidViewModel {
    private final AppRepository repository;
    private final MutableLiveData<Camarero> camareroLogueado = new MutableLiveData<>();
    private final MutableLiveData<UserResponse> userLogueado = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public LoginViewModel(@NonNull Application application) {
        super(application);
        repository = new AppRepository(application);
    }

    public LiveData<Camarero> getCamareroLogueado() { return camareroLogueado; }
    public LiveData<UserResponse> getUserLogueado() { return userLogueado; }
    public LiveData<String> getError() { return error; }

    public void iniciarSesion(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            error.setValue("Email y contraseña son requeridos.");
            return;
        }

        AppDatabase.databaseWriteExecutor.execute(() -> {
            Camarero camarero = repository.iniciarSesion(email);
            if (camarero != null && PasswordUtils.verifyPassword(password, camarero.passwordHash)) {
                camareroLogueado.postValue(camarero);
            } else {
                error.postValue("Email o contraseña incorrectos.");
            }
        });
    }

    public void iniciarSesionConAPI(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            error.setValue("Email y contraseña son requeridos.");
            return;
        }

        LoginRequest request = new LoginRequest(email, password);

        RetrofitClient.getApiService().login(request).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getApplication(), "Logueado desde la API: " + response.body().firstName, Toast.LENGTH_SHORT).show();
                    UserResponse user = response.body();
                    userLogueado.postValue(user);
                } else {
                    error.postValue("Credenciales incorrectas");
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                error.postValue("Error de conexion: " + t.getMessage());
            }
        });
    }
}
