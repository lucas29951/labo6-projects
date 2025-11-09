package com.labdevs.comandar.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.labdevs.comandar.R;
import com.labdevs.comandar.data.model.PedidoConResumen;
import com.labdevs.comandar.databinding.ItemPedidoCardBinding;

import java.util.Locale;

public class PedidoAdapter extends ListAdapter<PedidoConResumen, PedidoAdapter.PedidoViewHolder> {

    private final OnPedidoActionsListener listener;

    public PedidoAdapter(OnPedidoActionsListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public PedidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPedidoCardBinding binding = ItemPedidoCardBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new PedidoViewHolder(binding, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull PedidoViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class PedidoViewHolder extends RecyclerView.ViewHolder {
        private final ItemPedidoCardBinding binding;
        private final OnPedidoActionsListener listener;
        private final Context context;

        PedidoViewHolder(ItemPedidoCardBinding binding, OnPedidoActionsListener listener) {
            super(binding.getRoot());
            this.binding = binding;
            this.listener = listener;
            this.context = itemView.getContext();
        }

        void bind(PedidoConResumen pedidoConResumen) {
            binding.textMesaNumero.setText(String.format("Mesa %d", pedidoConResumen.pedido.mesaId));
            binding.textPedidoInfo.setText(String.format(Locale.US, "%d Items - $%.2f", pedidoConResumen.totalItems, pedidoConResumen.totalPrecio));

            // Lógica para el tiempo (simplificada)
            long diff = new java.util.Date().getTime() - pedidoConResumen.pedido.fechaHoraCreacion.getTime();
            long diffMinutes = diff / (60 * 1000);
            binding.textPedidoTiempo.setText(String.format("Hace %d min", diffMinutes));

            // Lógica dinámica de UI basada en el estado
            switch (pedidoConResumen.pedido.estado) {
                case abierto:
                    configureView(R.color.pedido_abierto, "Editar Pedido", "Enviar a Cocina", true, true, pedidoConResumen);
                    break;
                case enviado:
                    configureView(R.color.pedido_enviado, "Volver a Abrir", "Cerrar Pedido", true, false, pedidoConResumen);
                    break;
                case cerrado:
                    configureView(R.color.pedido_cerrado, "Ver", null, false, false, pedidoConResumen);
                    break;
            }
        }

        private void configureView(int colorResId, String textBtn1, String textBtn2, boolean showBtn2, boolean showDelete, PedidoConResumen pcr) {
            binding.statusBar.setBackgroundColor(ContextCompat.getColor(context, colorResId));
            binding.buttonAccion1.setText(textBtn1);

            binding.buttonDelete.setVisibility(showDelete ? View.VISIBLE : View.GONE);

            if (showBtn2) {
                binding.buttonAccion2.setText(textBtn2);
                binding.buttonAccion2.setVisibility(View.VISIBLE);
            } else {
                binding.buttonAccion2.setVisibility(View.GONE);
            }

            // Set Listeners
            binding.buttonAccion1.setOnClickListener(v -> listener.onButton1Click(pcr));
            binding.buttonAccion2.setOnClickListener(v -> listener.onButton2Click(pcr));
            binding.buttonDelete.setOnClickListener(v -> listener.onDeleteClick(pcr));

            itemView.setOnClickListener(v -> listener.onCardClick(pcr));
        }
    }

    public interface OnPedidoActionsListener {
        void onButton1Click(PedidoConResumen pedido);
        void onButton2Click(PedidoConResumen pedido);
        void onDeleteClick(PedidoConResumen pedido);
        void onCardClick(PedidoConResumen pedido);
    }

    private static final DiffUtil.ItemCallback<PedidoConResumen> DIFF_CALLBACK = new DiffUtil.ItemCallback<PedidoConResumen>() {
        @Override
        public boolean areItemsTheSame(@NonNull PedidoConResumen oldItem, @NonNull PedidoConResumen newItem) {
            return oldItem.pedido.pedidoId == newItem.pedido.pedidoId;
        }

        @Override
        public boolean areContentsTheSame(@NonNull PedidoConResumen oldItem, @NonNull PedidoConResumen newItem) {
            return oldItem.pedido.estado.equals(newItem.pedido.estado)  &&
                    oldItem.totalItems == newItem.totalItems &&
                    oldItem.totalPrecio == newItem.totalPrecio;
        }
    };
}
