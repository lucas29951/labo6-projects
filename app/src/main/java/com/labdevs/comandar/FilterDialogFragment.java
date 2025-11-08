package com.labdevs.comandar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.labdevs.comandar.databinding.DialogFilterBinding;

import java.util.Date;
import java.util.Locale;

public class FilterDialogFragment extends DialogFragment {

    public static final String REQUEST_KEY = "filter_request";
    public static final String KEY_FECHA_INICIO = "fecha_inicio";
    public static final String KEY_FECHA_FIN = "fecha_fin";
    private DialogFilterBinding binding;
    private final Calendar calInicio = Calendar.getInstance();
    private final Calendar calFin = Calendar.getInstance();

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        binding = DialogFilterBinding.inflate(LayoutInflater.from(getContext()));
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(binding.getRoot())
                .setTitle("Aplicar Filtros")
                .setPositiveButton("Aplicar", (dialog, id) -> applyFilter())
                .setNegativeButton("Cancelar", (dialog, id) -> FilterDialogFragment.this.getDialog().cancel());

        setupInitialState();
        setupClickListeners();

        return builder.create();
    }

    private void setupInitialState() {
        // Por defecto, las horas están al inicio y fin del día
        calInicio.set(Calendar.HOUR_OF_DAY, 0); calInicio.set(Calendar.MINUTE, 0);
        calFin.set(Calendar.HOUR_OF_DAY, 23); calFin.set(Calendar.MINUTE, 59);
        updateButtonText(binding.buttonFechaInicio, calInicio.getTime(), "dd/MM/yy");
        updateButtonText(binding.buttonFechaFin, calFin.getTime(), "dd/MM/yy");
        updateButtonText(binding.buttonHoraInicio, calInicio.getTime(), "HH:mm");
        updateButtonText(binding.buttonHoraFin, calFin.getTime(), "HH:mm");
    }

    private void setupClickListeners() {
        binding.buttonFechaInicio.setOnClickListener(v -> showDatePicker(calInicio, binding.buttonFechaInicio));
        binding.buttonFechaFin.setOnClickListener(v -> showDatePicker(calFin, binding.buttonFechaFin));
        binding.buttonHoraInicio.setOnClickListener(v -> showTimePicker(calInicio, binding.buttonHoraInicio));
        binding.buttonHoraFin.setOnClickListener(v -> showTimePicker(calFin, binding.buttonHoraFin));
    }

    private void showDatePicker(Calendar calendar, Button button) {
        new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            updateButtonText(button, calendar.getTime(), "dd/MM/yy");
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimePicker(Calendar calendar, Button button) {
        new TimePickerDialog(getContext(), (view, hourOfDay, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            updateButtonText(button, calendar.getTime(), "HH:mm");
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }

    private void updateButtonText(Button button, Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        button.setText(sdf.format(date));
    }

    private void applyFilter() {
        Bundle result = new Bundle();
        result.putLong(KEY_FECHA_INICIO, calInicio.getTimeInMillis());
        result.putLong(KEY_FECHA_FIN, calFin.getTimeInMillis());

        // Y lo enviamos al FragmentManager usando la clave de solicitud
        getParentFragmentManager().setFragmentResult(REQUEST_KEY, result);
    }
}