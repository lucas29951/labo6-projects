package com.labdevs.comandar.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.labdevs.comandar.R;
import com.labdevs.comandar.data.model.BillItem;
import com.labdevs.comandar.databinding.ItemPersonSummaryBinding;

import java.util.Locale;

public class PersonSummaryAdapter extends ListAdapter<BillItem, PersonSummaryAdapter.SummaryViewHolder> {

    public PersonSummaryAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public SummaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPersonSummaryBinding binding = ItemPersonSummaryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SummaryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SummaryViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class SummaryViewHolder extends RecyclerView.ViewHolder {
        private final ItemPersonSummaryBinding binding;

        SummaryViewHolder(ItemPersonSummaryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(BillItem billItem) {
            String name = String.format(Locale.getDefault(), "%dx %s", billItem.itemPedido.cantidad, billItem.itemPedido.nombreProducto);
            double price = billItem.itemPedido.getSubtotal();

            if (billItem.assignedToPersonIds.size() > 1) {
                name += " " + itemView.getContext().getString(R.string.item_divided);
                price /= billItem.assignedToPersonIds.size();
            }

            binding.tvSummaryItemName.setText(name);
            binding.tvSummaryItemPrice.setText(String.format(Locale.US, "$%.2f", price));
        }
    }

    private static final DiffUtil.ItemCallback<BillItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<BillItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull BillItem oldItem, @NonNull BillItem newItem) {
            return oldItem.itemPedido.productoId == newItem.itemPedido.productoId;
        }
        @Override
        public boolean areContentsTheSame(@NonNull BillItem oldItem, @NonNull BillItem newItem) {
            return oldItem.assignedToPersonIds.size() == newItem.assignedToPersonIds.size();
        }
    };
}