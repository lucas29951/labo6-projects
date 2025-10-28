package com.labdevs.comandar.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.labdevs.comandar.R;
import com.labdevs.comandar.data.entity.Mesa;
import com.labdevs.comandar.data.entity.enums.EstadoMesa;
import com.labdevs.comandar.data.model.MesaConCamarero;
import com.labdevs.comandar.databinding.ItemMesaGridBinding;
import com.labdevs.comandar.databinding.ItemMesaListBinding;
import com.labdevs.comandar.viewmodels.TablesViewModel;

import java.util.Objects;

public class MesaAdapter extends ListAdapter<MesaConCamarero, RecyclerView.ViewHolder> {

    private final TablesViewModel viewModel;
    private final Context context;

    private static final int VIEW_TYPE_GRID = 1;
    private static final int VIEW_TYPE_LIST = 2;

    public MesaAdapter(TablesViewModel viewModel, Context context) {
        super(DIFF_CALLBACK);
        this.viewModel = viewModel;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        return viewModel.getViewMode().getValue() == TablesViewModel.ViewMode.GRID ? VIEW_TYPE_GRID : VIEW_TYPE_LIST;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_GRID) {
            ItemMesaGridBinding binding = ItemMesaGridBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new GridViewHolder(binding);
        } else {
            ItemMesaListBinding binding = ItemMesaListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ListViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MesaConCamarero mesaActual = getItem(position);
        if (holder.getItemViewType() == VIEW_TYPE_GRID) {
            ((GridViewHolder) holder).bind(mesaActual.mesa);
        } else {
            ((ListViewHolder) holder).bind(mesaActual, viewModel.getCamareroId());
        }
    }

    // --- VIEW HOLDERS ---
    class GridViewHolder extends RecyclerView.ViewHolder {
        private final ItemMesaGridBinding binding;

        GridViewHolder(ItemMesaGridBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Mesa mesa) {
            binding.textMesaNumeroGrid.setText(String.format("%02d", mesa.numeroMesa));
            setCardColor(binding.cardViewMesa, mesa.estado);
        }
    }

    class ListViewHolder extends RecyclerView.ViewHolder {
        private final ItemMesaListBinding binding;

        ListViewHolder(ItemMesaListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(MesaConCamarero mesaConCamarero, int camareroLogueadoId) {
            Mesa mesa = mesaConCamarero.mesa;

            binding.textMesaNumeroList.setText("Mesa " + mesa.numeroMesa);
            binding.textMesaEstado.setText("Estado: " + mesa.estado.name());

            if (mesa.camareroId != null) {
                String nombreCompleto = mesaConCamarero.nombreCamarero + " " + mesaConCamarero.apellidoCamarero;
                if (mesa.camareroId == camareroLogueadoId) {
                    binding.textCamareroAsignado.setText("Asignada a ti (" + nombreCompleto.trim() + ")");
                    binding.textCamareroAsignado.setTypeface(null, Typeface.BOLD);
                } else {
                    binding.textCamareroAsignado.setText("Atendida por: " + nombreCompleto.trim());
                    binding.textCamareroAsignado.setTypeface(null, Typeface.NORMAL);
                }
            } else {
                binding.textCamareroAsignado.setText("Disponible para asignar");
                binding.textCamareroAsignado.setTypeface(null, Typeface.ITALIC);
            }

            setCardColor(binding.imageStatusIndicator, mesa.estado);

            // --- LÓGICA DE BOTONES DINÁMICA ---
            configureButton(mesa, camareroLogueadoId);
        }

        private void configureButton(Mesa mesa, int camareroLogueadoId) {
            // Caso 1: La mesa está libre (no tiene camarero asignado).
            if (mesa.camareroId == null) {
                binding.buttonAsignar.setText("Asignar");
                binding.buttonAsignar.setEnabled(true);
                binding.buttonAsignar.setOnClickListener(v -> viewModel.asignarMesa(mesa));
                return;
            }

            // Caso 2: La mesa está asignada AL CAMARERO ACTUAL.
            if (Integer.valueOf(camareroLogueadoId).equals(mesa.camareroId)) {
                binding.buttonAsignar.setText("Desasignar");
                binding.buttonAsignar.setEnabled(true);
                binding.buttonAsignar.setOnClickListener(v -> viewModel.desasignarMesa(mesa));
                return;
            }

            // Caso 3: La mesa está asignada a OTRO camarero.
            binding.buttonAsignar.setText("Asignar"); // Muestra "Asignar" pero deshabilitado.
            binding.buttonAsignar.setEnabled(false);
            binding.buttonAsignar.setOnClickListener(null);
        }
    }

    private void setCardColor(View view, EstadoMesa estado) {
        int colorResId;
        switch (estado) {
            case libre:
                colorResId = R.color.mesa_libre; // Verde
                break;
            case ocupada:
                colorResId = R.color.mesa_ocupada; // Naranja
                break;
            case reservada:
                colorResId = R.color.mesa_reservada; // Morado
                break;
            default:
                colorResId = R.color.mesa_default; // Gris
                break;
        }
        view.setBackgroundColor(ContextCompat.getColor(context, colorResId));
    }

    // --- DIFF_CALLBACK para ListAdapter ---
    private static final DiffUtil.ItemCallback<MesaConCamarero> DIFF_CALLBACK = new DiffUtil.ItemCallback<MesaConCamarero>() {
        @Override
        public boolean areItemsTheSame(@NonNull MesaConCamarero oldItem, @NonNull MesaConCamarero newItem) {
            return oldItem.mesa.mesaId == newItem.mesa.mesaId;
        }

        @Override
        public boolean areContentsTheSame(@NonNull MesaConCamarero oldItem, @NonNull MesaConCamarero newItem) {
            return oldItem.mesa.estado.equals(newItem.mesa.estado) &&
                    Objects.equals(oldItem.mesa.camareroId, newItem.mesa.camareroId);
        }
    };
}
