package com.labdevs.comandar.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.labdevs.comandar.data.model.ItemPedido;
import com.labdevs.comandar.databinding.ItemSentOrderItemBinding;

import java.io.File;
import java.util.Locale;

public class SentOrderItemAdapter extends ListAdapter<ItemPedido, SentOrderItemAdapter.ItemViewHolder> {

    public SentOrderItemAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSentOrderItemBinding binding = ItemSentOrderItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final ItemSentOrderItemBinding binding;

        ItemViewHolder(ItemSentOrderItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(ItemPedido item) {
            binding.textItemName.setText(item.nombreProducto);
            binding.textItemQuantity.setText(String.format(Locale.getDefault(), "Cantidad: %d", item.cantidad));
            binding.textItemSubtotal.setText(String.format(Locale.US, "$%.2f", item.getSubtotal()));

            if (item.fotoUrl != null && !item.fotoUrl.isEmpty()) {
                File imgFile = new File(item.fotoUrl);
                if (imgFile.exists()) {
                    binding.imageItem.setImageURI(Uri.fromFile(imgFile));
                }
            }
        }
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
