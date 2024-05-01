INSERT INTO db_burbujas.proveedores(nombre) VALUES ('Jabones Premium'), ('Todo Limpieza');
INSERT INTO db_burbujas.tipos_pedido(minutos_duracion_lavado, minutos_duracion_secado, descripcion) VALUES (45.0,30.0,'Ropa'),(60.0,45.0,'Acolchados');
INSERT INTO db_burbujas.productos(cantidad_actual, cantidad_por_unidad, tipo) VALUES (10.00, 10.00,'Jabón Liquido'),(8.00, 8.00,'Suavizante');
INSERT INTO db_burbujas.tipo_pedido_producto_mapping(cantidad_usada, producto_id, tipo_pedido_id)  VALUES (0.5, 1,1),(0.85, 1,2),(0.3, 2,1),(0.5, 2,2);
INSERT INTO db_burbujas.reabastecimientos(cantidad_producto, precio, unidades, fecha, producto_id, proveedor_id) VALUES (10,12500,1,CURRENT_TIMESTAMP,1,1),(8,8900,1,CURRENT_TIMESTAMP,2,1);
INSERT INTO db_burbujas.maquinas(numero, tipo) VALUES (0,'NINGUNO'),(1,'LAVADORA'),(2,'LAVADORA'),(3,'LAVADORA'),(4,'SECADORA'),(5,'SECADORA'),(6,'SECADORA'),(7,'SECADORA'),(8,'SECADORA'),(9,'SECADORA');
INSERT INTO db_burbujas.clientes(direccion, dni, nombre_apellido, telefono) VALUES ('Paraguay 14', '41980093','Hernán Frank', '3435196243');
INSERT INTO db_burbujas.pedidos(precio, cliente_id, fecha_hora_entrega, fecha_hora_ingreso, maquina_actual_id, estado_actual, prioridad, tipo_id) VALUES (4750, 1, NULL, CURRENT_TIMESTAMP, NULL, 'PENDIENTE', 'PRIORITARIO', 2),(1750, 1, NULL, CURRENT_TIMESTAMP, NULL, 'PENDIENTE', 'NORMAL', 1);