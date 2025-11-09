package com.labdevs.comandar;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.labdevs.comandar.adapters.PeopleAdapter;
import com.labdevs.comandar.adapters.PersonSummaryAdapter;
import com.labdevs.comandar.adapters.SplitBillItemAdapter;
import com.labdevs.comandar.databinding.FragmentSplitBillBinding;
import com.labdevs.comandar.viewmodels.SplitBillViewModel;

import java.util.Locale;

public class SplitBillFragment extends Fragment {

    private FragmentSplitBillBinding binding;
    private SplitBillViewModel viewModel;
    private int pedidoId;
    private int mesaNumero;
    private PeopleAdapter peopleAdapter;
    private SplitBillItemAdapter billItemAdapter;
    private PersonSummaryAdapter personSummaryAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pedidoId = getArguments().getInt("pedidoId");
            mesaNumero = getArguments().getInt("mesaNumero");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSplitBillBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(SplitBillViewModel.class);
        viewModel.init(pedidoId);

        setupToolbar();
        setupAdapters();
        setupListeners();
        observeViewModel();
    }

    private void setupToolbar() {
        binding.toolbar.setTitle(getString(R.string.title_split_bill, mesaNumero));
        binding.toolbar.setNavigationOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());
    }

    private void setupAdapters() {
        peopleAdapter = new PeopleAdapter(personId -> viewModel.selectPerson(personId));
        binding.rvPeople.setAdapter(peopleAdapter);

        billItemAdapter = new SplitBillItemAdapter(productoId -> viewModel.toggleItemAssignment(productoId));
        binding.rvBillItems.setAdapter(billItemAdapter);

        personSummaryAdapter = new PersonSummaryAdapter();
        binding.rvPersonSummary.setAdapter(personSummaryAdapter);
    }

    private void setupListeners() {
        binding.btnIncrementPeople.setOnClickListener(v -> viewModel.incrementPeople());
        binding.btnDecrementPeople.setOnClickListener(v -> viewModel.decrementPeople());
        binding.btnConfirmSplit.setOnClickListener(v -> viewModel.confirmAndCloseOrder());

        binding.toggleButtonGroupMode.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if(isChecked) {
                if(checkedId == R.id.button_mode_single) viewModel.setSplitMode(SplitBillViewModel.SplitMode.SINGLE_PAYMENT);
                else if(checkedId == R.id.button_mode_assign) viewModel.setSplitMode(SplitBillViewModel.SplitMode.ASSIGN_ITEMS);
                else if(checkedId == R.id.button_mode_equal) viewModel.setSplitMode(SplitBillViewModel.SplitMode.SPLIT_EQUALLY);
            }
        });
    }

    private void observeViewModel() {
        viewModel.splitMode.observe(getViewLifecycleOwner(), this::updateUiForSplitMode);
        viewModel.numberOfPeople.observe(getViewLifecycleOwner(), count -> binding.tvPeopleCount.setText(String.valueOf(count)));
        viewModel.selectedPersonId.observe(getViewLifecycleOwner(), selectedId -> {
            peopleAdapter.setSelectedPersonId(selectedId);
            billItemAdapter.setSelectedPersonId(selectedId);
            binding.tvItemsSubheader.setText(getString(R.string.select_person_to_assign, selectedId));
            binding.tvSummaryHeader.setText(getString(R.string.summary_for_person, selectedId));
            binding.tvPersonTotalLabel.setText(getString(R.string.total_for_person, selectedId));
        });

        viewModel.people.observe(getViewLifecycleOwner(), people -> {
            peopleAdapter.submitList(new java.util.ArrayList<>(people));
            Integer selectedPersonId = viewModel.selectedPersonId.getValue();
            if (selectedPersonId != null && selectedPersonId > 0 && selectedPersonId <= people.size()) {
                double total = people.get(selectedPersonId - 1).totalAmount;
                binding.tvPersonTotalValue.setText(String.format(Locale.US, "R$ %.2f", total));
            }
        });

        viewModel.billItems.observe(getViewLifecycleOwner(), billItems -> billItemAdapter.submitList(new java.util.ArrayList<>(billItems)));
        viewModel.personSummaryItems.observe(getViewLifecycleOwner(), summaryItems -> personSummaryAdapter.submitList(new java.util.ArrayList<>(summaryItems)));
        viewModel.orderTotal.observe(getViewLifecycleOwner(), total -> binding.tvOrderTotal.setText(String.format(Locale.US, "R$ %.2f", total)));

        viewModel.closeOrderSuccess.observe(getViewLifecycleOwner(), success -> {
            if(success) {
                Toast.makeText(getContext(), "Pedido cerrado y pagado.", Toast.LENGTH_SHORT).show();
                // Vuelve a la pantalla de Cuentas, limpiando la pila intermedia
                NavHostFragment.findNavController(this).popBackStack(R.id.accountFragment, false);
                viewModel.onNavigationDone();
            }
        });
    }

    private void updateUiForSplitMode(SplitBillViewModel.SplitMode mode) {
        boolean isSingle = mode == SplitBillViewModel.SplitMode.SINGLE_PAYMENT;
        boolean isAssign = mode == SplitBillViewModel.SplitMode.ASSIGN_ITEMS;

        binding.btnDecrementPeople.setEnabled(!isSingle);
        binding.btnIncrementPeople.setEnabled(!isSingle);

        binding.rvPeople.setVisibility(isSingle ? View.GONE : View.VISIBLE);
        binding.tvItemsHeader.setVisibility(isAssign ? View.VISIBLE : View.GONE);
        binding.tvItemsSubheader.setVisibility(isAssign ? View.VISIBLE : View.GONE);
        binding.rvBillItems.setVisibility(isAssign ? View.VISIBLE : View.GONE);
        binding.tvSummaryHeader.setVisibility(isSingle ? View.GONE : View.VISIBLE);
        binding.rvPersonSummary.setVisibility(isSingle ? View.GONE : View.VISIBLE);
        binding.dividerSummary.setVisibility(isSingle ? View.GONE : View.VISIBLE);
        binding.layoutPersonTotal.setVisibility(isSingle ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}