package com.labdevs.comandar.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.labdevs.comandar.data.dao.CamareroDao;
import com.labdevs.comandar.data.dao.CategoriaProductoDao;
import com.labdevs.comandar.data.dao.DetallePedidoDao;
import com.labdevs.comandar.data.dao.MesaDao;
import com.labdevs.comandar.data.dao.PedidoDao;
import com.labdevs.comandar.data.dao.ProductoDao;
import com.labdevs.comandar.data.entity.Camarero;
import com.labdevs.comandar.data.entity.CategoriaProducto;
import com.labdevs.comandar.data.entity.DetallePedido;
import com.labdevs.comandar.data.entity.Mesa;
import com.labdevs.comandar.data.entity.Pedido;
import com.labdevs.comandar.data.entity.Producto;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Camarero.class, Mesa.class, Pedido.class, CategoriaProducto.class, Producto.class, DetallePedido.class},
        version = 1,
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
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
