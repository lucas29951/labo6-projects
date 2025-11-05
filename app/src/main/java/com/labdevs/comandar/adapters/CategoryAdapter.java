package com.labdevs.comandar.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.labdevs.comandar.data.entity.CategoriaProducto;
import com.labdevs.comandar.databinding.ItemCategoryChipBinding;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends ListAdapter<CategoriaProducto, CategoryAdapter.CategoryViewHolder> {

    private final OnCategoryClickListener listener;
    private List<Integer> selectedIds = new ArrayList<>();

    public CategoryAdapter(OnCategoryClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCategoryChipBinding binding = ItemCategoryChipBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CategoryViewHolder(binding, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        CategoriaProducto category = getItem(position);
        holder.bind(category, selectedIds.contains(category.categoriaId));
    }

    public void setSelectedIds(List<Integer> ids) {
        this.selectedIds = ids;
        notifyDataSetChanged(); // Forma simple de actualizar todos los estados del chip
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final ItemCategoryChipBinding binding;
        private final OnCategoryClickListener listener;

        CategoryViewHolder(ItemCategoryChipBinding binding, OnCategoryClickListener listener) {
            super(binding.getRoot());
            this.binding = binding;
            this.listener = listener;
        }

        void bind(CategoriaProducto category, boolean isSelected) {
            binding.categoryChip.setText(category.nombreCategoria);
            binding.categoryChip.setChecked(isSelected);
            binding.categoryChip.setOnClickListener(v -> listener.onCategoryClick(category.categoriaId));
        }
    }

    public interface OnCategoryClickListener {
        void onCategoryClick(int categoryId);
    }

    private static final DiffUtil.ItemCallback<CategoriaProducto> DIFF_CALLBACK = new DiffUtil.ItemCallback<CategoriaProducto>() {
        @Override
        public boolean areItemsTheSame(@NonNull CategoriaProducto oldItem, @NonNull CategoriaProducto newItem) {
            return oldItem.categoriaId == newItem.categoriaId;
        }

        @Override
        public boolean areContentsTheSame(@NonNull CategoriaProducto oldItem, @NonNull CategoriaProducto newItem) {
            return oldItem.nombreCategoria.equals(newItem.nombreCategoria);
        }
    };
}
