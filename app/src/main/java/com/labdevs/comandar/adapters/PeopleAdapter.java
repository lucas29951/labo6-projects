package com.labdevs.comandar.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.labdevs.comandar.R;
import com.labdevs.comandar.data.model.Person;
import com.labdevs.comandar.databinding.ItemPersonChipBinding;

import java.util.Locale;

public class PeopleAdapter extends ListAdapter<Person, PeopleAdapter.PersonViewHolder> {
    private int selectedPersonId = 1;
    private final OnPersonClickListener listener;

    public PeopleAdapter(OnPersonClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public PersonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPersonChipBinding binding = ItemPersonChipBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new PersonViewHolder(binding, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonViewHolder holder, int position) {
        Person person = getItem(position);
        holder.bind(person, person.id == selectedPersonId);
    }

    public void setSelectedPersonId(int id) {
        int oldSelectedPosition = findPositionById(selectedPersonId);
        int newSelectedPosition = findPositionById(id);
        this.selectedPersonId = id;
        if (oldSelectedPosition != -1) notifyItemChanged(oldSelectedPosition);
        if (newSelectedPosition != -1) notifyItemChanged(newSelectedPosition);
    }

    private int findPositionById(int id) {
        for (int i = 0; i < getItemCount(); i++) {
            if (getItem(i).id == id) {
                return i;
            }
        }
        return -1;
    }

    static class PersonViewHolder extends RecyclerView.ViewHolder {
        private final ItemPersonChipBinding binding;
        private final OnPersonClickListener listener;
        private final Context context;

        PersonViewHolder(ItemPersonChipBinding binding, OnPersonClickListener listener) {
            super(binding.getRoot());
            this.binding = binding;
            this.listener = listener;
            this.context = itemView.getContext();
        }

        void bind(Person person, boolean isSelected) {
            binding.tvPersonId.setText(String.format(Locale.getDefault(), "P%d", person.id));
            binding.tvPersonTotal.setText(String.format(Locale.US, "$%.2f", person.totalAmount));
            itemView.setOnClickListener(v -> listener.onPersonClick(person.id));

            int strokeColor = isSelected ? ContextCompat.getColor(context, R.color.design_default_color_primary) : ContextCompat.getColor(context, android.R.color.transparent);
            int personIdBackgroundColor = isSelected ? ContextCompat.getColor(context, R.color.person_chip_selected_background) : ContextCompat.getColor(context, R.color.person_chip_unselected_background);
            int personIdTextColor = isSelected ? ContextCompat.getColor(context, R.color.person_chip_selected_text) : ContextCompat.getColor(context, R.color.person_chip_unselected_text);

            binding.cardPerson.setStrokeColor(strokeColor);
            binding.tvPersonId.setBackgroundColor(personIdBackgroundColor);
            binding.tvPersonId.setTextColor(personIdTextColor);
        }
    }

    public interface OnPersonClickListener {
        void onPersonClick(int personId);
    }

    private static final DiffUtil.ItemCallback<Person> DIFF_CALLBACK = new DiffUtil.ItemCallback<Person>() {
        @Override
        public boolean areItemsTheSame(@NonNull Person oldItem, @NonNull Person newItem) {
            return oldItem.id == newItem.id;
        }
        @Override
        public boolean areContentsTheSame(@NonNull Person oldItem, @NonNull Person newItem) {
            // Comparamos redondeado a centavos
            long a = Math.round(oldItem.totalAmount * 100);
            long b = Math.round(newItem.totalAmount * 100);
            return a == b;
        }
    };
}
