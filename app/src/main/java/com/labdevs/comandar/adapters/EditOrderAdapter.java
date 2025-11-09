package com.labdevs.comandar.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.labdevs.comandar.data.model.ItemPedido;
import com.labdevs.comandar.databinding.ItemEditOrderBinding;

import java.io.File;
import java.util.Locale;

public class EditOrderAdapter extends ListAdapter<ItemPedido, EditOrderAdapter.ItemViewHolder> {

    private final OnItemInteractionListener listener;

    public EditOrderAdapter(OnItemInteractionListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemEditOrderBinding binding = ItemEditOrderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ItemViewHolder(binding, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final ItemEditOrderBinding binding;
        private final OnItemInteractionListener listener;

        ItemViewHolder(ItemEditOrderBinding binding, OnItemInteractionListener listener) {
            super(binding.getRoot());
            this.binding = binding;
            this.listener = listener;
        }

        void bind(ItemPedido item) {
            binding.textItemName.setText(item.nombreProducto);
            binding.textItemPrice.setText(String.format(Locale.US, "$%.2f", item.precioUnitario));
            binding.textQuantity.setText(String.valueOf(item.cantidad));

            if (item.fotoUrl != null && !item.fotoUrl.isEmpty()) {
                File imgFile = new File(item.fotoUrl);
                if (imgFile.exists()) {
                    binding.imageItem.setImageURI(Uri.fromFile(imgFile));
                } else {
                    // Opcional: poner una imagen por defecto si el archivo no se encuentra
                    // binding.imageItem.setImageResource(R.drawable.placeholder);
                }
            } else {
                // Opcional: poner una imagen por defecto si la URL es nula
                // binding.imageItem.setImageResource(R.drawable.placeholder);
            }

            // Listeners
            binding.buttonIncrease.setOnClickListener(v -> listener.onIncreaseClick(item));
            binding.buttonDecrease.setOnClickListener(v -> listener.onDecreaseClick(item));
            binding.buttonDeleteItem.setOnClickListener(v -> listener.onDeleteClick(item));
        }
    }

    public interface OnItemInteractionListener {
        void onIncreaseClick(ItemPedido item);
        void onDecreaseClick(ItemPedido item);
        void onDeleteClick(ItemPedido item);
    }

    private static final DiffUtil.ItemCallback<ItemPedido> DIFF_CALLBACK = new DiffUtil.ItemCallback<ItemPedido>() {
        @Override
        public boolean areItemsTheSame(@NonNull ItemPedido oldItem, @NonNull ItemPedido newItem) {
            return oldItem.productoId == newItem.productoId;
        }

        @Override
        public boolean areContentsTheSame(@NonNull ItemPedido oldItem, @NonNull ItemPedido newItem) {
            return oldItem.cantidad == newItem.cantidad && oldItem.nombreProducto.equals(newItem.nombreProducto);
        }
    };
}
