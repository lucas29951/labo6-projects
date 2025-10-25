package com.labdevs.comandar;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.labdevs.comandar.databinding.ActivityTablesBinding;
import com.labdevs.comandar.viewmodels.LoginViewModel;

public class TablesActivity extends AppCompatActivity {

    private ActivityTablesBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTablesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}