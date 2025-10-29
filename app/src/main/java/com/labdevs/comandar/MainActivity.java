package com.labdevs.comandar;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.labdevs.comandar.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private NavController navController;
    private int camareroId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 1. Obtenemos y guardamos el ID del camarero al crear la Activity
        if (getIntent().getExtras() != null) {
            camareroId = getIntent().getExtras().getInt(LoginActivity.CAMARERO_ID_KEY, -1);
        }

        // 2. Volvemos a la configuración estándar del NavController
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        Bundle startArgs = new Bundle();
        startArgs.putInt(LoginActivity.CAMARERO_ID_KEY, camareroId);
        navController.setGraph(R.navigation.nav_graph, startArgs);

        // 3. Conectamos la barra de navegación
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController);
    }

    /**
     * Permite que los fragmentos alojados en esta Activity obtengan el ID del camarero logueado.
     * @return el ID del camarero, o -1 si no se encontró.
     */
    public int getCamareroId() {
        return camareroId;
    }
}