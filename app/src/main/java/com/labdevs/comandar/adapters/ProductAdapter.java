package com.labdevs.comandar.adapters;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.labdevs.comandar.R;
import com.labdevs.comandar.data.entity.Producto;
import com.labdevs.comandar.databinding.ItemProductBinding;

import java.io.File;
import java.util.Locale;

public class ProductAdapter extends ListAdapter<Producto, ProductAdapter.ProductViewHolder> {

    private final OnProductClickListener listener;
    private final Context context;



    public ProductAdapter(Context context, OnProductClickListener listener) {
        super(DIFF_CALLBACK);
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProductBinding binding = ItemProductBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ProductViewHolder(binding, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Producto product = getItem(position);
        holder.bind(product);
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        private final ItemProductBinding binding;
        private final OnProductClickListener listener;

        ProductViewHolder(ItemProductBinding binding, OnProductClickListener listener) {
            super(binding.getRoot());
            this.binding = binding;
            this.listener = listener;
        }

        void bind(Producto product) {
            binding.textProductName.setText(product.nombre);
            binding.textProductPrice.setText(String.format(Locale.US, "$%.2f", product.precio));

            if (product.fotoUrl != null && !product.fotoUrl.isEmpty()) {
                File imgFile = new File(product.fotoUrl);
                if (imgFile.exists()) {
                    binding.imageProduct.setImageURI(Uri.fromFile(imgFile));
                }
            }

            if (product.disponible) {
                binding.chipStatus.setText("Disponible");
                binding.chipStatus.setChipBackgroundColorResource(R.color.status_disponible);
                binding.buttonAddProduct.setEnabled(true);

                // Poner imagen a color
                binding.imageProduct.clearColorFilter();

                // Asignar listeners
                itemView.setOnClickListener(v -> listener.onProductItemClick(product.productoId));
                binding.buttonAddProduct.setOnClickListener(v -> listener.onAddButtonClick(product.productoId));
            } else {
                binding.chipStatus.setText("Agotado");
                binding.chipStatus.setChipBackgroundColorResource(R.color.status_agotado);
                binding.buttonAddProduct.setEnabled(false);

                // Poner imagen en escala de grises
                ColorMatrix matrix = new ColorMatrix();
                matrix.setSaturation(0); // 0 = escala de grises, 1 = color normal
                ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
                binding.imageProduct.setColorFilter(filter);

                // Deshabilitar clics
                itemView.setOnClickListener(null);
                binding.buttonAddProduct.setOnClickListener(null);
            }
        }
    }

    public interface OnProductClickListener {
        void onProductItemClick(int productId); // Para el ítem completo
        void onAddButtonClick(int productId); // Para el botón "Añadir"
    }

    private static final DiffUtil.ItemCallback<Producto> DIFF_CALLBACK = new DiffUtil.ItemCallback<Producto>() {
        @Override
        public boolean areItemsTheSame(@NonNull Producto oldItem, @NonNull Producto newItem) {
            return oldItem.productoId == newItem.productoId;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Producto oldItem, @NonNull Producto newItem) {
            return oldItem.nombre.equals(newItem.nombre) &&
                    oldItem.precio == newItem.precio &&
                    oldItem.disponible == newItem.disponible;
        }
    };
}
