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
import com.labdevs.comandar.data.entity.enums.EstadoMesa;
import com.labdevs.comandar.data.entity.enums.EstadoPedido;
import com.labdevs.comandar.data.model.ItemPedido;
import com.labdevs.comandar.data.model.MesaConCamarero;
import com.labdevs.comandar.data.model.PedidoConResumen;
import com.labdevs.comandar.utils.DataInvalidationNotifier;

import java.util.Date;
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

    private final LiveData<List<MesaConCamarero>> allMesasConCamarero;

    public AppRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        camareroDao = db.camareroDao();
        mesaDao = db.mesaDao();
        pedidoDao = db.pedidoDao();
        categoriaProductoDao = db.categoriaProductoDao();
        productoDao = db.productoDao();
        detallePedidoDao = db.detallePedidoDao();

        allMesas = mesaDao.getAllMesas();
        allMesasConCamarero = mesaDao.getMesasConCamarero();
    }

    // --- GESTIÓN DE PERFIL ---
    public void registrarCamarero(Camarero camarero) {
        camareroDao.insert(camarero);
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

    public LiveData<Integer> getConteoMesasAsignadas(int camareroId) {
        return mesaDao.countMesasAsignadas(camareroId);
    }

    public void desasignarTodasLasMesas(int camareroId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mesaDao.desasignarTodasLasMesasDeCamarero(camareroId);
        });
    }

    // ----- NUEVO MÉTODO SÍNCRONO -----
    public Camarero getCamareroByIdSync(int id) {
        // No necesita Executor porque ya será llamado desde uno en el ViewModel
        return camareroDao.getCamareroByIdSync(id);
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

    public LiveData<List<MesaConCamarero>> getMapaDeMesasConCamarero() {
        return allMesasConCamarero;
    }


    // Nuevo método para actualizar una mesa genéricamente
    public void actualizarMesa(Mesa mesa) {
        AppDatabase.databaseWriteExecutor.execute(() -> mesaDao.update(mesa));
    }

    // --- GESTIÓN DE PEDIDOS Y MENÚ ---
    public List<Mesa> getMesasAsignadasSync(int camareroId) {
        return mesaDao.getMesasByCamareroSync(camareroId);
    }

    public void agregarProductoAPedido(int productoId, int mesaId, int camareroId, int cantidad, String notas) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            boolean isNewPedidoCreated = false; // Flag para saber si creamos un pedido nuevo

            // 1. Buscar si ya existe un pedido activo para esa mesa
            Pedido pedidoActivo = pedidoDao.getPedidoActivoPorMesaSync(mesaId);
            long pedidoId;

            if (pedidoActivo == null) {
                // 2a. Si no existe, crear uno nuevo
                Pedido nuevoPedido = new Pedido();
                nuevoPedido.mesaId = mesaId;
                nuevoPedido.camareroId = camareroId;
                pedidoId = pedidoDao.insert(nuevoPedido); // 'insert' devuelve el nuevo ID
                isNewPedidoCreated = true; // Marcamos que se creó uno nuevo
            } else {
                // 2b. Si ya existe, usamos su ID
                pedidoId = pedidoActivo.pedidoId;
            }

            // 3. Obtener el producto para saber su precio
            Producto producto = productoDao.getProductoByIdSync(productoId);
            if (producto == null) return; // Salir si el producto no existe

            // 4. Crear el detalle del pedido
            DetallePedido detalle = new DetallePedido();
            detalle.pedidoId = (int) pedidoId;
            detalle.productoId = productoId;
            detalle.cantidad = cantidad;
            detalle.precioUnitario = producto.precio;
            detalle.caracteristicasParticulares = notas;

            detallePedidoDao.insertOrUpdate(detalle); // Usar insertOrUpdate para manejar si el producto ya estaba

            // 5. Lógica de negocio: Cambiar estado de la mesa a 'ocupada'
            Mesa mesa = mesaDao.getMesaByIdSync(mesaId);
            if (mesa != null && (mesa.estado == EstadoMesa.libre || mesa.estado == EstadoMesa.reservada)) {
                mesa.estado = EstadoMesa.ocupada;
                mesaDao.update(mesa);
            }
            if (isNewPedidoCreated) {
                DataInvalidationNotifier.getInstance().notifyPedidosChanged();
            }
        });
    }

    public LiveData<List<CategoriaProducto>> getCategoriasMenu() {
        return categoriaProductoDao.getAllCategorias();
    }

    public LiveData<List<Producto>> getAllProductos() {
        return productoDao.getAllProductos();
    }

    public LiveData<List<Producto>> getProductosByCategorias(List<Integer> categoriaIds) {
        return productoDao.getProductosByCategorias(categoriaIds);
    }

    public LiveData<Producto> getProductoById(int productoId) {
        return productoDao.getProductoById(productoId);
    }

    public LiveData<List<Producto>> searchProductos(String query, List<Integer> categoryIds) {
        if (query.isEmpty() && categoryIds.isEmpty()) {
            return getAllProductos();
        } else if (query.isEmpty()) {
            return getProductosByCategorias(categoryIds);
        } else if (categoryIds.isEmpty()) {
            return productoDao.searchProductosByName(query);
        } else {
            return productoDao.searchProductosByNameAndCategory(query, categoryIds);
        }
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

    public LiveData<List<PedidoConResumen>> getPedidos(int camareroId, EstadoPedido estado, Date fechaInicio, Date fechaFin) {
        return pedidoDao.getPedidosFiltrados(camareroId, estado, fechaInicio, fechaFin);
    }

    // --- Métodos para popular datos iniciales---
    public void poblarCategoriasYProductos(List<CategoriaProducto> categorias, List<Producto> productos) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            categoriaProductoDao.insertAll(categorias);
            productoDao.insertAll(productos);
        });
    }
}
