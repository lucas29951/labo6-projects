package com.labdevs.comandar;

import static androidx.activity.result.contract.ActivityResultContracts.*;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;
import com.labdevs.comandar.databinding.ActivityRegisterBinding;
import com.labdevs.comandar.viewmodels.RegisterViewModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private RegisterViewModel viewModel;

    // --- LANZADORES DE ACTIVIDADES ---
    private ActivityResultLauncher<PickVisualMediaRequest> pickMediaLauncher;
    private ActivityResultLauncher<Uri> takePictureLauncher;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    // --- VARIABLES PARA MANEJAR LA FOTO ---
    private Uri tempImageUri; // Uri temporal para la foto de la cámara
    private String finalImagePath = null; // Ruta final de la imagen guardada internamente

    private static final int MAX_IMAGE_SIZE = 800; // Tamaño máximo (ancho o alto) para la foto de perfil en píxeles

    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        // Inicializar los lanzadores de resultados
        initializeLaunchers();

        // --- LISTENERS DE LA UI ---
        binding.inputRegisterImage.setOnClickListener(v -> showImagePickerDialog());

        binding.buttonRegister.setOnClickListener(v -> attemptRegistration());

        binding.iconBack.setOnClickListener(v -> finish());

        // --- OBSERVADORES DEL VIEWMODEL ---
        viewModel.getRegistroExitoso().observe(this, exitoso -> {
            if (exitoso) {
                Toast.makeText(this, "Registro exitoso. Ahora puedes iniciar sesión.", Toast.LENGTH_LONG).show();
                finish(); // Cierra esta actividad y vuelve a Login
            }
        });

        viewModel.getError().observe(this, mensajeError -> {
            if (mensajeError != null && !mensajeError.isEmpty()) {
                Toast.makeText(this, mensajeError, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initializeLaunchers() {
        // Lanzador para seleccionar imagen de la galería
        pickMediaLauncher = registerForActivityResult(new PickVisualMedia(), uri -> {
            if (uri != null) {
                saveImageInternally(uri);
            } else {
                Toast.makeText(this, "No se seleccionó ninguna imagen.", Toast.LENGTH_SHORT).show();
            }
        });

        // Lanzador para tomar una foto
        takePictureLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
            if (success) {
                if (tempImageUri != null) {
                    saveImageInternally(tempImageUri);
                }
            } else {
                Toast.makeText(this, "No se tomó ninguna foto.", Toast.LENGTH_SHORT).show();
            }
        });

        // Lanzador para solicitar permisos
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                launchCamera(); // Si el permiso se concede, lanzamos la cámara
            } else {
                Toast.makeText(this, "Permiso de cámara denegado.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showImagePickerDialog() {
        // Creamos un diálogo para que el usuario elija la fuente de la imagen
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccionar foto de perfil");
        String[] options = {"Tomar Foto", "Elegir de Galería"};
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) { // Tomar Foto
                checkCameraPermissionAndLaunch();
            } else { // Elegir de Galería
                pickMediaLauncher.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            }
        });
        builder.show();
    }

    private void checkCameraPermissionAndLaunch() {
        // Verificamos si ya tenemos el permiso
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            launchCamera();
        } else {
            // Si no lo tenemos, lo solicitamos
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void launchCamera() {
        // Creamos un archivo temporal para guardar la foto de la cámara
        tempImageUri = createImageFileUri();
        if (tempImageUri != null) {
            takePictureLauncher.launch(tempImageUri);
        } else {
            Toast.makeText(this, "No se pudo crear el archivo para la foto.", Toast.LENGTH_SHORT).show();
        }
    }

    private Uri createImageFileUri() {
        File imagesDir = new File(getCacheDir(), "temp_images");
        if (!imagesDir.exists()) {
            imagesDir.mkdirs();
        }
        File tempFile = new File(imagesDir, "temp_image_" + System.currentTimeMillis() + ".jpg");
        // Usamos FileProvider para compartir el archivo de forma segura con la app de cámara
        return FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", tempFile);
    }

    private void saveImageInternally(Uri sourceUri) {
        try {
            File profileImagesDir = new File(getFilesDir(), "profile_images");
            if (!profileImagesDir.exists()) {
                profileImagesDir.mkdirs();
            }
            String extension = getExtensionFromUri(this, sourceUri);
            if (extension == null) extension = "jpg";
            String fileName = "profile_" + System.currentTimeMillis() + "." + extension;
            File destinationFile = new File(profileImagesDir, fileName);

            // --- AÑADIMOS EL ESCALADO ---
            try (InputStream in = getContentResolver().openInputStream(sourceUri)) {
                // 1. Decodificar el stream a un Bitmap
                Bitmap originalBitmap = BitmapFactory.decodeStream(in);

                // 2. Escalar el Bitmap a un tamaño manejable
                Bitmap scaledBitmap = scaleBitmap(originalBitmap, MAX_IMAGE_SIZE);

                // 3. Comprimir y guardar el Bitmap escalado
                try (OutputStream out = new FileOutputStream(destinationFile)) {
                    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out); // Calidad 90 es un buen balance
                }
            }

            finalImagePath = destinationFile.getAbsolutePath();
            binding.inputRegisterImage.setImageURI(Uri.fromFile(destinationFile)); // Muestra la imagen seleccionada
            Log.d("RegisterActivity", "Imagen guardada en: " + finalImagePath);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al guardar la imagen.", Toast.LENGTH_SHORT).show();
            finalImagePath = null;
        }
    }

    private Bitmap scaleBitmap(Bitmap bitmap, int maxSize) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if (width <= maxSize && height <= maxSize) {
            return bitmap; // No necesita ser escalado
        }

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) { // Imagen apaisada (más ancha que alta)
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else { // Imagen vertical o cuadrada
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

    private void attemptRegistration() {
        String nombre = binding.inputRegisterName.getText().toString().trim();
        String apellido = binding.inputRegisterLastname.getText().toString().trim();
        String email = binding.inputRegisterEmail.getText().toString().trim();
        String password = binding.inputRegisterPassword.getText().toString();
        String telefono = binding.inputRegisterPhone.getText().toString().trim();

        // --- LÓGICA PARA LA IMAGEN POR DEFECTO ---
        if (finalImagePath == null || finalImagePath.isEmpty()) {
            File defaultImage = new File(getFilesDir(), "profile_images/default_profile.png");
            if (defaultImage.exists()) {
                finalImagePath = defaultImage.getAbsolutePath();
                Log.d("RegisterActivity", "Usando imagen por defecto: " + finalImagePath);
            } else {
                // Este caso es poco probable si el poblado de BD funciona, pero es un buen fallback.
                Toast.makeText(this, "Error: no se encontró la imagen por defecto.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        viewModel.registrarCamarero(nombre, apellido, email, password, telefono, finalImagePath);
    }

    // Método de utilidad para obtener la extensión del archivo desde un Uri
    public String getExtensionFromUri(@NonNull Context context, @NonNull Uri uri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(context.getContentResolver().getType(uri));
    }
}