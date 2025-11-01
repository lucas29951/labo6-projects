package com.labdevs.comandar.data.database;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.labdevs.comandar.R;
import com.labdevs.comandar.data.dao.*;
import com.labdevs.comandar.data.entity.*;
import com.labdevs.comandar.data.entity.enums.EstadoMesa;
import com.labdevs.comandar.data.entity.enums.EstadoPedido;
import com.labdevs.comandar.utils.PasswordUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Camarero.class, Mesa.class, Pedido.class, CategoriaProducto.class, Producto.class, DetallePedido.class},
        version = 2,
        exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract CamareroDao camareroDao();
    public abstract MesaDao mesaDao();
    public abstract PedidoDao pedidoDao();
    public abstract CategoriaProductoDao categoriaProductoDao();
    public abstract ProductoDao productoDao();
    public abstract DetallePedidoDao detallePedidoDao();

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "gestion_camareros_db")
                            .fallbackToDestructiveMigration()
                            .addCallback(roomDatabaseCallback(context))
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // --- INICIO DE LA LÓGICA DE POBLACIÓN DE DATOS ---

    private static RoomDatabase.Callback roomDatabaseCallback(final Context context) {
        return new RoomDatabase.Callback() {
            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                super.onCreate(db);

                // Ejecutamos toda la lógica de población en un hilo de fondo
                databaseWriteExecutor.execute(() -> {
                    // Obtenemos una instancia de la BD y sus DAOs
                    AppDatabase database = AppDatabase.getDatabase(context);
                    CamareroDao camareroDao = database.camareroDao();
                    MesaDao mesaDao = database.mesaDao();
                    CategoriaProductoDao categoriaDao = database.categoriaProductoDao();
                    ProductoDao productoDao = database.productoDao();
                    PedidoDao pedidoDao = database.pedidoDao();
                    DetallePedidoDao detallePedidoDao = database.detallePedidoDao();

                    // --- 1. GESTIÓN DE FOTOS DE PERFIL ---
                    File profileImagesDir = new File(context.getFilesDir(), "profile_images");
                    if (!profileImagesDir.exists()) {
                        profileImagesDir.mkdirs();
                    }

                    // Copiamos la imagen por defecto para uso futuro
                    copyDrawableToFile(context, R.drawable.default_profile, new File(profileImagesDir, "default_profile.png"));

                    File fotoJuan = copyDrawableToFile(context, R.drawable.profile_juan, new File(profileImagesDir, "profile_juan.png"));
                    File fotoAna = copyDrawableToFile(context, R.drawable.profile_ana, new File(profileImagesDir, "profile_ana.png"));
                    File fotoCarlos = copyDrawableToFile(context, R.drawable.profile_carlos, new File(profileImagesDir, "profile_carlos.png"));

                    // --- 2. CREACIÓN Y GUARDADO DE CAMAREROS ---
                    Camarero juan = new Camarero();
                    juan.nombre = "Juan";
                    juan.apellido = "Pérez";
                    juan.email = "juan@comandar.com";
                    juan.passwordHash = PasswordUtils.hashPassword("password123");
                    juan.numeroContacto = "611222333";
                    juan.fotoUrl = fotoJuan != null ? fotoJuan.getAbsolutePath() : null;
                    juan.createdAt = new Date();

                    Camarero ana = new Camarero();
                    ana.nombre = "Ana";
                    ana.apellido = "García";
                    ana.email = "ana@comandar.com";
                    ana.passwordHash = PasswordUtils.hashPassword("password123");
                    ana.numeroContacto = "644555666";
                    ana.fotoUrl = fotoAna != null ? fotoAna.getAbsolutePath() : null;
                    ana.createdAt = new Date();

                    Camarero carlos = new Camarero();
                    carlos.nombre = "Carlos";
                    carlos.apellido = "Martínez";
                    carlos.email = "carlos@comandar.com";
                    carlos.passwordHash = PasswordUtils.hashPassword("password123");
                    carlos.numeroContacto = "677888999";
                    carlos.fotoUrl = fotoCarlos != null ? fotoCarlos.getAbsolutePath() : null;
                    carlos.createdAt = new Date();

                    // Insertamos y obtenemos sus IDs para poder asignar las mesas
                    long juanId = camareroDao.insertAndGetId(juan);
                    long anaId = camareroDao.insertAndGetId(ana);
                    long carlosId = camareroDao.insertAndGetId(carlos);


                    // --- 3. CREACIÓN Y GUARDADO DE MESAS ---
                    List<Mesa> mesas = new ArrayList<>();
                    int numeroMesa = 1;

                    // Mesas para Juan (IDs 1, 2, 3)
                    mesas.add(createMesa(numeroMesa++, 4, (int)juanId, EstadoMesa.ocupada));
                    mesas.add(createMesa(numeroMesa++, 2, (int)juanId, EstadoMesa.reservada));
                    mesas.add(createMesa(numeroMesa++, 6, (int)juanId, EstadoMesa.libre));

                    // Mesas para Ana (IDs 4, 5, 6)
                    mesas.add(createMesa(numeroMesa++, 2, (int)anaId, EstadoMesa.ocupada));
                    mesas.add(createMesa(numeroMesa++, 4, (int)anaId, EstadoMesa.reservada));
                    mesas.add(createMesa(numeroMesa++, 4, (int)anaId, EstadoMesa.libre));

                    // Mesas para Carlos (IDs 7, 8, 9)
                    mesas.add(createMesa(numeroMesa++, 6, (int)carlosId, EstadoMesa.ocupada));
                    mesas.add(createMesa(numeroMesa++, 2, (int)carlosId, EstadoMesa.reservada));
                    mesas.add(createMesa(numeroMesa++, 4, (int)carlosId, EstadoMesa.libre));

                    // Mesas restantes sin asignar (IDs 10, 11, 12)
                    mesas.add(createMesa(numeroMesa++, 4, null, EstadoMesa.libre));
                    mesas.add(createMesa(numeroMesa++, 8, null, EstadoMesa.libre));
                    mesas.add(createMesa(numeroMesa++, 2, null, EstadoMesa.libre));

                    mesaDao.insertAll(mesas);

                    // --- 4. CREACIÓN DE CATEGORÍAS Y PRODUCTOS ---
                    // Categorías
                    CategoriaProducto catEntradas = new CategoriaProducto(); catEntradas.nombreCategoria = "Entradas";
                    CategoriaProducto catPrincipales = new CategoriaProducto(); catPrincipales.nombreCategoria = "Platos Principales";
                    CategoriaProducto catBebidas = new CategoriaProducto(); catBebidas.nombreCategoria = "Bebidas";
                    CategoriaProducto catPostres = new CategoriaProducto(); catPostres.nombreCategoria = "Postres";

                    categoriaDao.insertAll(Arrays.asList(catEntradas, catPrincipales, catBebidas, catPostres));

                    // ESTIÓN DE IMÁGENES DE PRODUCTOS
                    File productImagesDir = new File(context.getFilesDir(), "product_images");
                    if (!productImagesDir.exists()) {
                        productImagesDir.mkdirs();
                    }

                    File imgNachos = copyDrawableToFile(context, R.drawable.producto_nachos, new File(productImagesDir, "producto_nachos.jpg"));
                    File imgQuesadillas = copyDrawableToFile(context, R.drawable.producto_quesadillas, new File(productImagesDir, "producto_quesadillas.jpg"));
                    File imgTacos = copyDrawableToFile(context, R.drawable.producto_tacos, new File(productImagesDir, "producto_tacos.jpg"));
                    File imgEnchiladas = copyDrawableToFile(context, R.drawable.producto_enchiladas, new File(productImagesDir, "producto_enchiladas.jpg"));
                    File imgHorchata = copyDrawableToFile(context, R.drawable.producto_horchata, new File(productImagesDir, "producto_horchata.jpg"));
                    File imgMargarita = copyDrawableToFile(context, R.drawable.producto_margarita, new File(productImagesDir, "producto_margarita.jpg"));
                    File imgCorona = copyDrawableToFile(context, R.drawable.producto_corona, new File(productImagesDir, "producto_corona.png"));
                    File imgTresLeches = copyDrawableToFile(context, R.drawable.producto_tres_leches, new File(productImagesDir, "producto_tres_leches.jpg"));
                    File imgChurros = copyDrawableToFile(context, R.drawable.producto_churros, new File(productImagesDir, "producto_churros.jpg"));
                    File imgFajitas = copyDrawableToFile(context, R.drawable.producto_fajitas, new File(productImagesDir, "producto_fajitas.jpg"));

                    // Productos (con la nueva URL de la imagen)
                    List<Producto> productos = new ArrayList<>();
                    productos.add(createProducto("Nachos con Guacamole", "Totopos de maíz con guacamole casero", 8.50, true, 1, imgNachos != null ? imgNachos.getAbsolutePath() : null));
                    productos.add(createProducto("Quesadillas de Champiñones", "Tortillas de trigo rellenas de queso y champiñones salteados", 7.00, true, 1, imgQuesadillas != null ? imgQuesadillas.getAbsolutePath() : null));
                    productos.add(createProducto("Tacos al Pastor", "Finas láminas de cerdo marinado con piña, cilantro y cebolla", 12.00, true, 2, imgTacos != null ? imgTacos.getAbsolutePath() : null));
                    productos.add(createProducto("Enchiladas Suizas", "Tortillas de maíz rellenas de pollo, bañadas en salsa verde cremosa y gratinadas con queso", 14.50, true, 2, imgEnchiladas != null ? imgEnchiladas.getAbsolutePath() : null));
                    productos.add(createProducto("Fajitas de Ternera", "Tiras de ternera salteadas con pimientos y cebolla. Se sirven con tortillas calientes", 16.00, false, 2, imgFajitas != null ? imgFajitas.getAbsolutePath() : null));
                    productos.add(createProducto("Agua de Horchata", "Bebida refrescante a base de arroz, canela y leche", 3.00, true, 3, imgHorchata != null ? imgHorchata.getAbsolutePath() : null));
                    productos.add(createProducto("Margarita Clásica", "Cóctel de tequila, triple seco y zumo de lima", 7.50, true, 3, imgMargarita != null ? imgMargarita.getAbsolutePath() : null));
                    productos.add(createProducto("Cerveza Corona", "Botella de 33cl", 3.50, true, 3, imgCorona != null ? imgCorona.getAbsolutePath() : null));
                    productos.add(createProducto("Pastel de Tres Leches", "Bizcocho bañado en tres tipos de leche con cobertura de merengue", 5.50, true, 4, imgTresLeches != null ? imgTresLeches.getAbsolutePath() : null));
                    productos.add(createProducto("Churros con Chocolate", "Porción de churros caseros con salsa de chocolate caliente", 4.50, true, 4, imgChurros != null ? imgChurros.getAbsolutePath() : null));

                    productoDao.insertAll(productos);

                    // --- 5. CREACIÓN DE PEDIDOS Y DETALLES PARA MESAS OCUPADAS ---
                    // Pedido para la mesa 1 (de Juan)
                    Pedido pedidoJuan = new Pedido();
                    pedidoJuan.mesaId = 1;
                    pedidoJuan.camareroId = (int) juanId;
                    long pedidoJuanId = pedidoDao.insert(pedidoJuan); // Obtenemos el ID del pedido
                    // Detalles para el pedido de Juan
                    detallePedidoDao.insertOrUpdate(createDetalle((int) pedidoJuanId, 1, 1, 8.50, "Extra picante"));
                    detallePedidoDao.insertOrUpdate(createDetalle((int) pedidoJuanId, 7, 2, 7.50, null));

                    // Pedido para la mesa 4 (de Ana)
                    Pedido pedidoAna = new Pedido();
                    pedidoAna.mesaId = 4;
                    pedidoAna.camareroId = (int) anaId;
                    long pedidoAnaId = pedidoDao.insert(pedidoAna);
                    // Detalles para el pedido de Ana
                    detallePedidoDao.insertOrUpdate(createDetalle((int) pedidoAnaId, 4, 1, 14.50, null));
                    detallePedidoDao.insertOrUpdate(createDetalle((int) pedidoAnaId, 6, 1, 3.00, "Con mucho hielo"));

                    // Pedido para la mesa 7 (de Carlos), este lo marcaremos como "enviado"
                    Pedido pedidoCarlos = new Pedido();
                    pedidoCarlos.mesaId = 7;
                    pedidoCarlos.camareroId = (int) carlosId;
                    pedidoCarlos.estado = EstadoPedido.enviado;
                    long pedidoCarlosId = pedidoDao.insert(pedidoCarlos);
                    // Detalles para el pedido de Carlos
                    detallePedidoDao.insertOrUpdate(createDetalle((int) pedidoCarlosId, 9, 2, 5.50, null));

                    // Este pedido está asociado a la mesa 3, que es la mesa libre de Juan.
                    // Simula un servicio que acaba de terminar.
                    Pedido pedidoJuanCerrado = new Pedido();
                    pedidoJuanCerrado.mesaId = 3; // La mesa ahora libre de Juan
                    pedidoJuanCerrado.camareroId = (int) juanId;
                    pedidoJuanCerrado.estado = EstadoPedido.cerrado; // Marcado como cerrado
                    long pedidoJuanCerradoId = pedidoDao.insert(pedidoJuanCerrado);
                    // Detalles del pedido que ya se cobró
                    detallePedidoDao.insertOrUpdate(createDetalle((int) pedidoJuanCerradoId, 2, 1, 7.00, null)); // Quesadillas
                    detallePedidoDao.insertOrUpdate(createDetalle((int) pedidoJuanCerradoId, 10, 1, 4.50, null)); // Churros

                    Log.d("AppDatabase", "Base de datos poblada con datos iniciales.");
                });
            }
        };
    }

    /**
     * Función de utilidad para crear una Mesa de forma rápida.
     */
    private static Mesa createMesa(int numero, int capacidad, Integer camareroId, EstadoMesa estado) {
        Mesa mesa = new Mesa();
        mesa.numeroMesa = numero;
        mesa.capacidad = capacidad;
        mesa.camareroId = camareroId;
        mesa.estado = estado;
        return mesa;
    }

    /**
     * Función de utilidad para crear un Producto de forma rápida.
     */
    private static Producto createProducto(String nombre, String desc, double precio, boolean disponible, int catId, String fotoUrl) {
        Producto p = new Producto();
        p.nombre = nombre;
        p.descripcion = desc;
        p.precio = precio;
        p.disponible = disponible;
        p.categoriaId = catId;
        p.fotoUrl = fotoUrl;
        return p;
    }

    /**
     * Copia un recurso drawable al almacenamiento interno de la app.
     * @return El archivo de destino si la copia fue exitosa, o null si hubo un error.
     */
    private static File copyDrawableToFile(Context context, int drawableId, File destinationFile) {
        try (InputStream in = context.getResources().openRawResource(drawableId);
             OutputStream out = new FileOutputStream(destinationFile)) {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            return destinationFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Función de utilidad para crear un DetallePedido de forma rápida.
     */
    private static DetallePedido createDetalle(int pedidoId, int productoId, int cantidad, double precio, String notas) {
        DetallePedido detalle = new DetallePedido();
        detalle.pedidoId = pedidoId;
        detalle.productoId = productoId;
        detalle.cantidad = cantidad;
        detalle.precioUnitario = precio;
        detalle.caracteristicasParticulares = notas;
        return detalle;
    }


}
