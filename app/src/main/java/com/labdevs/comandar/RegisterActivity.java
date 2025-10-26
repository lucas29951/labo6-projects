package com.labdevs.comandar;

import static androidx.activity.result.contract.ActivityResultContracts.*;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.labdevs.comandar.databinding.ActivityRegisterBinding;
import com.labdevs.comandar.viewmodels.RegisterViewModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private RegisterViewModel viewModel;

    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        pickMedia = registerForActivityResult(
                new PickVisualMedia(),
                uri -> {
                    if (uri != null) {
                        handleImageSelected(uri);
                    } else {
                        Toast.makeText(this, "No se selecciono una imagen", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        binding.inputRegisterImage.setOnClickListener(v -> {
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });

        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        binding.buttonRegister.setOnClickListener(v -> {
            String nombre = binding.inputRegisterName.getText().toString().trim();
            String apellido = binding.inputRegisterLastname.getText().toString().trim();
            String email = binding.inputRegisterEmail.getText().toString().trim();
            String password = binding.inputRegisterPassword.getText().toString();
            String telefono = binding.inputRegisterPhone.getText().toString().trim();
            String imagen = binding.inputRegisterUri.getText().toString().trim();
            viewModel.registrarCamarero(nombre, apellido, email, password, telefono, imagen);
        });

        viewModel.getRegistroExitoso().observe(this, exitoso -> {
            if (exitoso) {
                Toast.makeText(this, "Registro exitoso. Ahora puedes iniciar sesión.", Toast.LENGTH_LONG).show();
                finish();
            }
        });

        viewModel.getError().observe(this, mensajeError -> {
            if (mensajeError != null && !mensajeError.isEmpty()) {
                Toast.makeText(this, mensajeError, Toast.LENGTH_SHORT).show();
            }
        });

        binding.iconBack.setOnClickListener(v -> finish());
    }

    private void handleImageSelected(Uri uri) {
        try {
            String rutaLocal = saveImageToInternalStorage(getApplicationContext(), uri);

            binding.inputRegisterImage.setImageURI(Uri.fromFile(new File(rutaLocal)));
            binding.inputRegisterUri.setText(rutaLocal);
            Toast.makeText(this, "Guardado localmente", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error guardando imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public String saveImageToInternalStorage(Context context, Uri uri) throws IOException {
        File imagesDir = new File(context.getFilesDir(), "profile_images");

        if (!imagesDir.exists()) {
            imagesDir.mkdir();
        }

        String extension = getExtensionFromUri(context, uri);

        if (extension == null) {
            extension = "jpg";
        }

        String fileName = "img_" + System.currentTimeMillis() + "." + extension;
        File outFile = new File(imagesDir, fileName);

        InputStream in = null;
        FileOutputStream out = null;

        try {
            in = context.getContentResolver().openInputStream(uri);

            if (in == null) {
                throw new FileNotFoundException("InputStream es null para la URI");
            }

            out = new FileOutputStream(outFile);

            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("devtest", "Archivo no encontrado: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("devtest", "Error al copiar la imagen: " + e.getMessage());
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return outFile.getAbsolutePath();
    }

    public String getExtensionFromUri(Context context, Uri uri) {
        String extension = null;

        String type = context.getContentResolver().getType(uri);
        if (type != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(type);
        }

        if (extension == null) {
            String path = uri.getPath();
            if (path != null) {
                int lastDot = path.lastIndexOf('.');

                if (lastDot != -1) {
                    extension = path.substring(lastDot + 1);
                }
            }
        }

        return extension;
    }
}