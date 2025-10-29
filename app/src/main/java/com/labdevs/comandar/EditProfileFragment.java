package com.labdevs.comandar;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.labdevs.comandar.data.database.AppDatabase;
import com.labdevs.comandar.databinding.FragmentEditProfileBinding;
import com.labdevs.comandar.viewmodels.EditProfileViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class EditProfileFragment extends Fragment {

    private FragmentEditProfileBinding binding;
    private EditProfileViewModel viewModel;
    private int camareroId;

    // --- LANZADORES Y VARIABLES PARA LA GESTIÓN DE LA IMAGEN (traídos de RegisterActivity) ---
    private ActivityResultLauncher<PickVisualMediaRequest> pickMediaLauncher;
    private ActivityResultLauncher<Uri> takePictureLauncher;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private Uri tempImageUri;
    private String finalImagePath = null; // Ruta de la imagen, se inicializa con la actual
    private static final int MAX_IMAGE_SIZE = 800;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            camareroId = getArguments().getInt("CAMARERO_ID", -1);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (camareroId == -1) return;

        initializeLaunchers(); // Inicializar los lanzadores de selección de imagen

        viewModel = new ViewModelProvider(this).get(EditProfileViewModel.class);
        viewModel.loadCamarero(camareroId);

        observeViewModel();

        binding.imageProfileEdit.setOnClickListener(v -> showImagePickerDialog());

        binding.buttonGuardarPerfil.setOnClickListener(v -> {
            String nombre = binding.editTextNombre.getText().toString();
            String apellido = binding.editTextApellido.getText().toString();
            String email = binding.editTextEmail.getText().toString();
            String telefono = binding.editTextTelefono.getText().toString();

            viewModel.guardarCambios(nombre, apellido, email, telefono, finalImagePath);
        });
    }

    private void observeViewModel() {
        viewModel.camarero.observe(getViewLifecycleOwner(), camarero -> {
            if (camarero != null) {
                binding.editTextNombre.setText(camarero.nombre);
                binding.editTextApellido.setText(camarero.apellido);
                binding.editTextEmail.setText(camarero.email);
                binding.editTextTelefono.setText(camarero.numeroContacto);

                // Cargar la imagen actual y guardar su ruta
                finalImagePath = camarero.fotoUrl;
                if (finalImagePath != null && !finalImagePath.isEmpty()) {
                    File imgFile = new File(finalImagePath);
                    if (imgFile.exists()) {
                        binding.imageProfileEdit.setImageURI(Uri.fromFile(imgFile));
                    }
                }
            }
        });

        viewModel.getUpdateSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                Toast.makeText(getContext(), "Perfil actualizado", Toast.LENGTH_SHORT).show();
                NavHostFragment.findNavController(this).popBackStack();
                viewModel.onNavigationDone();
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                viewModel.onNavigationDone();
            }
        });
    }

    // --- MÉTODOS PARA SELECCIÓN DE IMAGEN ---

    private void initializeLaunchers() {
        pickMediaLauncher = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if (uri != null) {
                saveImageInternally(uri);
            }
        });

        takePictureLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
            if (success && tempImageUri != null) {
                saveImageInternally(tempImageUri);
            }
        });

        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                launchCamera();
            } else {
                Toast.makeText(getContext(), "Permiso de cámara denegado.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showImagePickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Cambiar foto de perfil");
        String[] options = {"Tomar Foto", "Elegir de Galería"};
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) { // Tomar Foto
                checkCameraPermissionAndLaunch();
            } else { // Elegir de Galería
                pickMediaLauncher.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            }
        });
        builder.show();
    }

    private void checkCameraPermissionAndLaunch() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            launchCamera();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void launchCamera() {
        tempImageUri = createImageFileUri();
        if (tempImageUri != null) {
            takePictureLauncher.launch(tempImageUri);
        }
    }

    private Uri createImageFileUri() {
        File imagesDir = new File(requireActivity().getCacheDir(), "temp_images");
        if (!imagesDir.exists()) {
            imagesDir.mkdirs();
        }
        File tempFile = new File(imagesDir, "temp_image_" + System.currentTimeMillis() + ".jpg");
        return FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".provider", tempFile);
    }

    private void saveImageInternally(Uri sourceUri) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                File profileImagesDir = new File(requireActivity().getFilesDir(), "profile_images");
                if (!profileImagesDir.exists()) profileImagesDir.mkdirs();

                String extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(requireActivity().getContentResolver().getType(sourceUri));
                if (extension == null) extension = "jpg";
                String fileName = "profile_" + System.currentTimeMillis() + "." + extension;
                File destinationFile = new File(profileImagesDir, fileName);

                try (InputStream in = requireActivity().getContentResolver().openInputStream(sourceUri)) {
                    Bitmap originalBitmap = BitmapFactory.decodeStream(in);
                    Bitmap scaledBitmap = scaleBitmap(originalBitmap, MAX_IMAGE_SIZE);
                    try (OutputStream out = new FileOutputStream(destinationFile)) {
                        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    }
                }

                finalImagePath = destinationFile.getAbsolutePath(); // Actualizamos la ruta de la nueva imagen
                requireActivity().runOnUiThread(() -> {
                    binding.imageProfileEdit.setImageURI(Uri.fromFile(destinationFile));
                });
            } catch (IOException e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Error al guardar la imagen.", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private Bitmap scaleBitmap(Bitmap bitmap, int maxSize) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (width <= maxSize && height <= maxSize) return bitmap;

        float ratio = (float) width / (float) height;
        if (ratio > 1) {
            width = maxSize;
            height = (int) (width / ratio);
        } else {
            height = maxSize;
            width = (int) (height * ratio);
        }
        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Limpiar la referencia al binding
    }
}