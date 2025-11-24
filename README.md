# Conociendo el Dominio: App de Comandas

- Identificamos las funcionalidades principales de apps que ya existan
- Definimos los actores del sistema
- Definimos los servicios para la aplicacion

## Aplicaciones Consultadas

### 1. Comanda Electrónica
**Enlace:** https://play.google.com/store/apps/details?id=ricci.android.comandasocket&hl=es_419 

**Funcionalidades principales:**
- Toma de pedidos en mesa
- Comunicacion en tiempo real con la cocina
- Gestion de mesas y turnos
- Calculo de consumos

### 2. Comanda Electrónica - Pedidos
**Enlace:** https://play.google.com/store/apps/details?id=com.riccimobile.comandaonline 

**Funcionalidades principales:**
- Sistema de pedidos online
- Relacion con cocina y bar
- Reportes de ventas

### 3. Take It Easy
**Enlace:** https://play.google.com/store/apps/details?id=com.takeiteasy&hl=es_419

**Funcionalidades principales:**
- Codigo QR para pedidos
- Autogestion por parte del cliente
- Integracion con metodos de pago
- Gestion de inventario

### 4. Comandera
**Enlace:** https://play.google.com/store/apps/details?id=com.devramirez.bussoft.comandera&hl=es_419  

**Funcionalidades principales:**
- Interfaz para meseros
- Division de pagos
- Control de tiempo por mesa

### 5. RePOS
**Enlace:** https://play.google.com/store/apps/details?id=com.repos&hl=es_419 

**Funcionalidades principales:**
- Sistema POS completo
- Gestion de multiples locales
- Control de empleados
- Analisis de ventas

### 6. DOYO - 360 Restaurant App
**Enlace:** https://play.google.com/store/apps/details?id=com.app.doyourorder&hl=es_419  

**Funcionalidades principales:**
- Sistema completo para restaurantes
- Pedidos por codigo QR
- Reservas de mesas

### 7. Menula - Restaurant POS
**Enlace:** https://play.google.com/store/apps/details?id=app.menula.android&hl=es_419 

**Funcionalidades principales:**
- Menu digital interactivo
- Gestion de productos
- Relacion con cocina
- Control de stock

### 8. Restaurant Order-Taking App
**Enlace:** https://play.google.com/store/apps/details?id=com.globalfoodsoft.restaurantapp&hl=es_419  

**Funcionalidades principales:**
- Aplicacion especifica para toma de pedidos
- Categorias de productos
- Personalizacion de pedidos

### 9. Fudo
**Enlace:** https://play.google.com/store/apps/details?id=do.fu.app 

**Funcionalidades principales:**
- Gestion de ordenes en tiempo real
- Analisis de desempeño

---

## Actores

### 1. Mesero
- Toma pedidos en las mesa
- Gestiona varias mesas a la vez
- Cierra cuentas y procesa los pagos
- Comunica necesidades especiales a la cocina

### 2. Cocina
- Recibe ordenes de los meseros
- Marca pedidos como completados
- Comunica falta de productos
- Gestiona los tiempos de preparacion

### 3. Administrador
- Gestiona el menu y precios
- Genera reportes y analisis
- Analiza las ventas y rendimiento
- Gestiona turnos y empleados

---

## Otra Investigación

### GloriaFood / GlobalFood (Oracle)
- **Tipo:** SaaS propietario (suite online ordering + reservas + marketing).
- **Disponibilidad:** App ‘Restaurant Order-Taking App' (Android) para el restaurante; app 'FoodBooking' y white-label para clientes.
- **Módulos/funcionalidades clave:**
  - Recepción de pedidos (pickup/delivery/dine-in) con gestión de estados.
  - Reservas de mesa integradas; confirmación y tiempos estimados.
  - Impresión automática/bajo demanda en impresoras térmicas compatibles.
  - Pre-ordering (pedido por adelantado) junto con la reserva.
  - App white-label para clientes (iOS/Android) y captación vía FoodBooking.
- **Notas técnicas:** Flujo típico: web/app del local → botón ‘See Menu & Order' → pedido llega a la app del restaurante para aceptar/rechazar; posibilidad de pausar servicios y marcar items 'out-of-stock'.

### UpMenu (Mobile App + Ordering)
- **Tipo:** SaaS propietario (ordering + branded app + loyalty).
- **Disponibilidad:** Branded app iOS/Android para clientes.
- **Módulos/funcionalidades clave:**
  - Navegación de menú, pedidos (delivery/retirar), reservas de mesa.
  - Programa de fidelidad, cupones, ofertas y notificaciones push.
  - Agregación de pedidos multi-canal y gestión de entregas.
  - Integración con POS y pasarelas de pago; analíticas y reporting.
- **Notas técnicas:** Una 'buena app' debe incluir reservas y loyalty además de ordering; el stack incluye QR menu y website builder.

### Carbonara App (Waitlist + Reservations + Ordering)
- **Tipo:** SaaS propietario (gestión de turnos/espera y reservas; módulo de ordering).
- **Disponibilidad:** App móvil/tablet para el staff; enlaces a apps móviles del ecosistema.
- **Módulos/funcionalidades clave:**
  - Lista de espera con notificaciones por SMS.
  - Reservas por teléfono/email/web con autoservicio online sin comisión por reserva.
  - Gestión de mesas y colaboración multi-dispositivo.
  - Módulo de pedidos (ordering) en la suite.
- **Notas técnicas:** Adecuada para bares con alta rotación que requieren control de piso, asignación de mesas y reducción de tiempos de espera.

### Odoo PoS Restaurant
- **Tipo:** Suite ERP/PoS (open core) con módulo de restaurante.
- **Disponibilidad:** Apps móviles de Odoo + interfaz responsive; autoservicio desde smartphone o kiosco.
- **Módulos/funcionalidades clave:**
  - Pedidos móviles, reservas de mesa y pedidos online integrados con PoS.
  - Autoservicio 'self-ordering' desde smartphone o kiosko.
  - Pagos integrados, impresión de cocina/bar, KDS y control de salón (floor plan).
- **Notas técnicas:** Apto para integrar con contabilidad, inventario, CRM y marketing del ERP; fuerte para cadenas o bares con requerimientos administrativos.

### TastyIgniter (open source – Laravel)
- **Tipo:** Open source auto-hosteado (GPL/Marketplace de extensiones).
- **Disponibilidad:** Frontend responsive/PWA; apps móviles posibles vía wrappers o dev propio.
- **Módulos/funcionalidades clave:**
  - Sistema de pedidos online (takeaway/delivery).
  - 'Table booking system' integrado para reservas.
  - Pagos online, soporte multi-sucursal, extensible vía marketplace.
- **Notas técnicas:** Código extensible y comunidad activa; ideal como base para un desarrollo a medida sin fees por pedido.

### SkyeMobile – Online Ordering & Table Reservations
- **Tipo:** SaaS propietario (ordering + reservas, sin comisiones).
- **Disponibilidad:** Widget web responsive; app de toma de pedidos para el local.
- **Módulos/funcionalidades clave:**
  - Pedidos online desde web/Facebook/móvil.
  - Reservas de mesa (widget) sin comisión.
  - App de toma de pedidos para el restaurante; confirmación y tiempos.
- **Notas técnicas:** Enfocado en web + app de staff; útil para bares que quieren comenzar rápido sin desarrollo propio.

---

## Comparativa Rápida de Capacidades

| Plataforma | Tipo | App Android (Cliente) | App Android (Restaurante) | Reservas | Order Ahead | Pedido en mesa (QR/App) | Impresión/KDS | Loyalty/Marketing | POS/API |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **GloriaFood** | SaaS | Sí (white-label/FoodBooking) | Sí | Sí | Sí | Sí (App/Web) | Sí (impresora) | Marketing | API/Integraciones |
| **UpMenu** | SaaS | Sí (branded) | — | Sí | — | Sí (QR/App) | — | Sí (loyalty/cupons) | Sí |
| **Carbonara** | SaaS | — (enfoque staff/gestión) | Sí (gestión) | Sí | — | — | — | — | — |
| **Odoo POS** | ERP/POS | Sí (autoservicio web/app) | Sí (POS) | Sí | Sí | Sí (self-ordering) | Sí (KDS/impresoras) | Sí (ERP) | Sí |
| **TastyIgniter**| Open Source | Responsive/PWA | Panel web | Sí | — | Sí (QR vía web) | Depende extensión | Depende extensión | Sí (PHP/Laravel) |
| **SkyeMobile** | SaaS | Web móvil | Sí | Sí | — | Sí (web) | — | — | — |

---

## Modelo de Dominio Sugerido (Alto Nivel)

**Entidades principales:** Restaurante, Sucursal/Ubicación, Mesa, Turno/Horario, Reserva, ListaEspera, Cliente, Menú, Ítem, Modificador/Opción, Pedido, LíneaPedido, Pago, Comprobante, Dispositivo, TicketKDS, Campaña, ReglaLealtad, UsuarioStaff, Rol/Permisos.

**Reglas clave:**
1.  Reserva puede incluir 'order ahead' con pre-pago parcial.
2.  Pedido en mesa vincula Mesa + Cliente anónimo (token QR) o identificable (login).
3.  Sincronización en tiempo real con KDS y estados (NEW → IN_PREP → READY → SERVED).
4.  Inventario sincronizado para items/variantes.
5.  Fallback offline para toma de pedidos en el dispositivo del mozo/encargado.

---

## Arquitectura de Referencia (Implementación Propia)

- **Cliente móvil:** Android (Kotlin) + opcional iOS (Swift) o Flutter/React Native.
- **Backend:** Java/Spring Boot (hexagonal), JWT/OAuth2, PostgreSQL/MySQL, Redis (sesiones/colas), WebSockets (STOMP) o SSE.
- **Integraciones:** Pasarelas de pago (Mercado Pago/Stripe), impresoras térmicas (ESC/POS), KDS (web tablet), notificaciones (FCM).
- **Observabilidad y NFR:** métricas, trazas, rate-limits, idempotencia, control de concurrencia por mesa y por reserva.

---

## Recomendaciones para un MVP

- Reserva + order ahead con pago/garantía; QR por mesa para reordenar sin mozo.
- Catálogo con variantes/modificadores, impuestos y reglas de disponibilidad (kitchen hours).
- Estados de pedido con KDS y estimaciones de tiempo; impresión térmica opcional.
- Notificaciones push/SMS y recordatorios de reserva; política de cancelación/no-show.
- Panel del local con control de salón y asignación automática de mesas por capacidad.

---

## Riesgos y Consideraciones

- **Sincronización de estados:** en tiempo real entre múltiples dispositivos (condiciones de carrera).
- **Gestión de picos de tráfico:** (horarios pico) y degradación controlada del servicio.
- **Regulatorio:** protección de datos (PII), medios de pago y facturación local.
- **Conectividad intermitente en el salón:** diseño offline-first para el staff.


## 🗄️ Esquema de Base de Datos - App Camarero

- **Diagrama interactivo:** [dbdiagram.io](https://dbdiagram.io/d/DB-App-Camarero-68e12497d2b621e42241baa3)

<img width="1303" height="1051" alt="DB - App Camarero" src="https://github.com/user-attachments/assets/e9bc95a8-b2b8-480b-8225-5d71834ad096" />


## Figma

Enlace: https://www.figma.com/design/cSS9EGbwuacVvSXPpHTIaC/ComandAR-App?m=auto&t=vxTHqgZowMDZlVue-6
