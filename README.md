# Gestión Limpia

**Sistema online de administración de lavanderías**

Gestión Limpia es una solución web para la gestión integral de lavanderías, digitalizando el flujo completo de pedidos, clientes, inventario y caja. Permite también generar estadísticas y reportes para la toma de decisiones basada en datos.

---

## Características principales

- **Gestión de usuarios y seguridad**
  - Inicio de sesión con usuario y clave.
  - Recuperación y cambio de contraseña mediante token por e‑mail.

- **Tablero Kanban**
  - La vista principal del sistema es un tablero Kanban donde se asignan los pedidos a las diferentes máquinas (lavado, secado).
  - Flujo de estados de pedidos: Pendiente → En lavado → En secado → Finalizado → Cobrado → Entregado.  

- **Módulo Pedidos**  
  - Alta, edición, baja lógica y restauración de pedidos.  
  - Marcar pedidos como cobrados y entregados.

- **Módulo Clientes / Proveedores / Inventario**  
  - CRUD completo con validaciones de datos de clientes y proveedores.
  - Gestión de inventario y reabastecimientos con control de costos.
  - Alertas de bajo stock configurables.

- **Módulo Caja**  
  - Registro de movimientos (ingresos, egresos) por tipo de caja.  
  - Generación de reporte Excel filtrable por período.

- **Estadísticas**  
  - Gráficos de ingresos, clientes, pedidos e inventario para el mes seleccionado.  
  - Exportación de gráficos estadísticos.

- **Administración y backup**  
  - Configuración de datos de la lavandería, usuario y clave, tipos de pedidos.
  - Creación y restauración de copias de seguridad en SQL.

---

## Tecnologías

- **Backend:** Java 17, Spring Boot, Spring Security, Hibernate (JPA)  
- **Frontend:** Thymeleaf, Bootstrap, DataTables, Morris.js, SweetAlert2, Notyf, jQuery  
- **BD:** MySQL  
- **Contenerización:** Docker, Docker Compose  
- **Control de versiones:** Git + GitHub  

---

## Requisitos

- Java 17 +  
- Maven  
- Docker & Docker Compose  
- MySQL  

---

## Manual de Usuario

- El sistema cuenta con un manual de usuario donde se explica como implementarlo en un VPS y utilizarlo.

