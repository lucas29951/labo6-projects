package com.labdevs.comandar.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.labdevs.comandar.data.dao.CamareroDao;
import com.labdevs.comandar.data.dao.CategoriaProductoDao;
import com.labdevs.comandar.data.dao.DetallePedidoDao;
import com.labdevs.comandar.data.dao.MesaDao;
import com.labdevs.comandar.data.dao.PedidoDao;
import com.labdevs.comandar.data.dao.ProductoDao;
import com.labdevs.comandar.data.database.AppDatabase;
import com.labdevs.comandar.data.entity.Camarero;
import com.labdevs.comandar.data.entity.CategoriaProducto;
import com.labdevs.comandar.data.entity.DetallePedido;
import com.labdevs.comandar.data.entity.Mesa;
import com.labdevs.comandar.data.entity.Pedido;
import com.labdevs.comandar.data.entity.Producto;
import com.labdevs.comandar.data.model.ItemPedido;

import java.util.List;

// data/repository/AppRepository.java
public class AppRepository {

    private final CamareroDao camareroDao;
    private final MesaDao mesaDao;
    private final PedidoDao pedidoDao;
    private final CategoriaProductoDao categoriaProductoDao;
    private final ProductoDao productoDao;
    private final DetallePedidoDao detallePedidoDao;

    // LiveData que pueden ser observados globalmente
    private final LiveData<List<Mesa>> allMesas;

    public AppRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        camareroDao = db.camareroDao();
        mesaDao = db.mesaDao();
        pedidoDao = db.pedidoDao();
        categoriaProductoDao = db.categoriaProductoDao();
        productoDao = db.productoDao();
        detallePedidoDao = db.detallePedidoDao();

        allMesas = mesaDao.getAllMesas();
    }

    // --- GESTIÓN DE PERFIL ---
    public void registrarCamarero(Camarero camarero) {
        AppDatabase.databaseWriteExecutor.execute(() -> camareroDao.insert(camarero));
    }

    public void actualizarPerfilCamarero(Camarero camarero) {
        AppDatabase.databaseWriteExecutor.execute(() -> camareroDao.update(camarero));
    }

    public Camarero iniciarSesion(String email) {
        // Esta es una operación de lectura, pero como no es LiveData,
        // la ejecutamos en un Callable para obtener el resultado de forma síncrona pero segura.
        // NOTA: En un ViewModel real, esto se manejaría de forma más asíncrona.
        try {
            return AppDatabase.databaseWriteExecutor.submit(() -> camareroDao.getCamareroByEmail(email)).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public LiveData<Camarero> getPerfil(int id) {
        return camareroDao.getCamareroById(id);
    }

    // --- GESTIÓN DE MESAS ---
    public LiveData<List<Mesa>> getMapaDeMesas() {
        return allMesas;
    }

    public void asignarMesa(Mesa mesa, int camareroId) {
        mesa.camareroId = camareroId;
        AppDatabase.databaseWriteExecutor.execute(() -> mesaDao.update(mesa));
    }

    public void desasignarMesa(Mesa mesa) {
        mesa.camareroId = null;
        AppDatabase.databaseWriteExecutor.execute(() -> mesaDao.update(mesa));
    }

    // --- GESTIÓN DE PEDIDOS Y MENÚ ---
    public LiveData<List<CategoriaProducto>> getCategoriasMenu() {
        return categoriaProductoDao.getAllCategorias();
    }

    public LiveData<List<Producto>> getProductosPorCategoria(int categoriaId) {
        return productoDao.getProductosByCategoria(categoriaId);
    }

    public LiveData<Pedido> getPedidoActivoDeMesa(int mesaId) {
        return pedidoDao.getPedidoActivoPorMesa(mesaId);
    }

    public void crearNuevoPedido(Pedido pedido) {
        AppDatabase.databaseWriteExecutor.execute(() -> pedidoDao.insert(pedido));
    }

    public void agregarItemAlPedido(DetallePedido detalle) {
        AppDatabase.databaseWriteExecutor.execute(() -> detallePedidoDao.insertOrUpdate(detalle));
    }

    public void eliminarItemDelPedido(DetallePedido detalle) {
        AppDatabase.databaseWriteExecutor.execute(() -> detallePedidoDao.delete(detalle));
    }

    public LiveData<List<ItemPedido>> getItemsDelPedido(int pedidoId) {
        return detallePedidoDao.getItemsPedidoConNombre(pedidoId);
    }

    public void actualizarEstadoPedido(Pedido pedido) {
        AppDatabase.databaseWriteExecutor.execute(() -> pedidoDao.update(pedido));
    }

    // --- Métodos para popular datos iniciales---
    public void poblarCategoriasYProductos(List<CategoriaProducto> categorias, List<Producto> productos) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            categoriaProductoDao.insertAll(categorias);
            productoDao.insertAll(productos);
        });
    }
}
