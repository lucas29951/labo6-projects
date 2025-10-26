package com.labdevs.comandar;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.labdevs.comandar.databinding.ActivityTablesBinding;

public class TablesActivity extends AppCompatActivity {

    private ActivityTablesBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTablesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}