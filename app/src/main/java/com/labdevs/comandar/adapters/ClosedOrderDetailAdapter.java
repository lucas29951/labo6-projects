package com.labdevs.comandar.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.labdevs.comandar.data.model.ItemPedido;
import com.labdevs.comandar.databinding.ItemClosedOrderDetailBinding;

import java.util.Locale;

public class ClosedOrderDetailAdapter extends ListAdapter<ItemPedido, ClosedOrderDetailAdapter.ItemViewHolder> {

    public ClosedOrderDetailAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemClosedOrderDetailBinding binding = ItemClosedOrderDetailBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final ItemClosedOrderDetailBinding binding;

        ItemViewHolder(ItemClosedOrderDetailBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(ItemPedido item) {
            binding.textItemName.setText(String.format(Locale.getDefault(), "%d x %s", item.cantidad, item.nombreProducto));
            binding.textItemPrice.setText(String.format(Locale.US, "$%.2f", item.getSubtotal()));

            if (item.caracteristicasParticulares != null && !item.caracteristicasParticulares.isEmpty()) {
                binding.textItemNotes.setText(String.format(Locale.getDefault(), "(%s)", item.caracteristicasParticulares));
                binding.textItemNotes.setVisibility(View.VISIBLE);
            } else {
                binding.textItemNotes.setVisibility(View.GONE);
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
