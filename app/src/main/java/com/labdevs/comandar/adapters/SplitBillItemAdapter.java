package com.labdevs.comandar.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexboxLayout;
import com.labdevs.comandar.R;
import com.labdevs.comandar.data.model.BillItem;
import com.labdevs.comandar.databinding.ItemSplitBillBinding;

import java.util.Locale;

public class SplitBillItemAdapter extends ListAdapter<BillItem, SplitBillItemAdapter.BillItemViewHolder> {
    private int selectedPersonId = 1;
    private final OnBillItemClickListener listener;

    public SplitBillItemAdapter(OnBillItemClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public BillItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSplitBillBinding binding = ItemSplitBillBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new BillItemViewHolder(binding, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull BillItemViewHolder holder, int position) {
        BillItem item = getItem(position);
        // Ahora pasamos el mapa completo de asignaciones
        holder.bind(item, item.assignments.containsKey(selectedPersonId));
    }

    public void setSelectedPersonId(int id) {
        this.selectedPersonId = id;
        notifyDataSetChanged(); // Simple way to re-render all items with new selection context
    }

    static class BillItemViewHolder extends RecyclerView.ViewHolder {
        private final ItemSplitBillBinding binding;
        private final OnBillItemClickListener listener;
        private final Context context;

        BillItemViewHolder(ItemSplitBillBinding binding, OnBillItemClickListener listener) {
            super(binding.getRoot());
            this.binding = binding;
            this.listener = listener;
            this.context = itemView.getContext();
        }

        void bind(BillItem billItem, boolean isAssignedToSelectedPerson) {
            binding.tvItemName.setText(String.format(Locale.getDefault(), "%dx %s", billItem.itemPedido.cantidad, billItem.itemPedido.nombreProducto));
            binding.tvItemPrice.setText(String.format(Locale.US, "$%.2f", billItem.itemPedido.getSubtotal()));
            itemView.setOnClickListener(v -> listener.onBillItemClick(billItem));

            // Lógica de resaltado
            int strokeColor = isAssignedToSelectedPerson ? ContextCompat.getColor(context, R.color.design_default_color_primary) : ContextCompat.getColor(context, android.R.color.transparent);
            binding.cardItem.setStrokeColor(strokeColor);

            if (billItem.assignments.isEmpty()) {
                binding.chipUnassigned.setVisibility(View.VISIBLE);
                binding.flexboxAssignedPeople.setVisibility(View.GONE);
                binding.flexboxAssignedPeople.removeAllViews();
            } else {
                binding.chipUnassigned.setVisibility(View.GONE);
                binding.flexboxAssignedPeople.setVisibility(View.VISIBLE);
                updateAssignedPeopleIndicators(binding.flexboxAssignedPeople, billItem.assignments);
            }
        }

        private void updateAssignedPeopleIndicators(FlexboxLayout flexbox, java.util.Map<Integer, Integer> assignments) {
            flexbox.removeAllViews();
            LayoutInflater inflater = LayoutInflater.from(context);
            for (Integer personId : assignments.keySet()) {
                TextView indicator = (TextView) inflater.inflate(R.layout.item_assigned_person_indicator, flexbox, false);
                indicator.setText(String.format(Locale.getDefault(), "P%d", personId));
                flexbox.addView(indicator);
            }
        }
    }

    public interface OnBillItemClickListener {
        void onBillItemClick(BillItem item);
    }

    private static final DiffUtil.ItemCallback<BillItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<BillItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull BillItem oldItem, @NonNull BillItem newItem) {
            return oldItem.itemPedido.productoId == newItem.itemPedido.productoId;
        }
        @Override
        public boolean areContentsTheSame(@NonNull BillItem oldItem, @NonNull BillItem newItem) {
            return oldItem.assignments.equals(newItem.assignments);
        }
    };
}
