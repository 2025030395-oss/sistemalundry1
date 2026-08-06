-- MySQL dump 10.13  Distrib 8.0.36, for Win64 (x86_64)
--
-- Host: localhost    Database: su_laundry
-- ------------------------------------------------------
-- Server version	8.4.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `clientes`
--

DROP TABLE IF EXISTS `clientes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `clientes` (
  `id_cliente` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(150) NOT NULL,
  `telefono` varchar(20) NOT NULL,
  `correo` varchar(100) NOT NULL,
  `calle_numero` varchar(255) NOT NULL,
  `estado` varchar(20) DEFAULT 'Activo',
  PRIMARY KEY (`id_cliente`),
  UNIQUE KEY `correo` (`correo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `clientes`
--

LOCK TABLES `clientes` WRITE;
/*!40000 ALTER TABLE `clientes` DISABLE KEYS */;
/*!40000 ALTER TABLE `clientes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cortes_de_caja`
--

DROP TABLE IF EXISTS `cortes_de_caja`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cortes_de_caja` (
  `id_corte` int NOT NULL AUTO_INCREMENT,
  `fecha_apertura` date NOT NULL,
  `turno` varchar(30) NOT NULL,
  `hora_inicio` time NOT NULL,
  `hora_fin_estimado` time DEFAULT NULL,
  `saldo_inicial` decimal(10,2) DEFAULT '2000.00',
  `estado` varchar(30) DEFAULT 'Pendiente',
  `id_responsable` int DEFAULT NULL,
  `monto_real` double DEFAULT '0',
  `ingresos` double DEFAULT '0',
  `gastos` double DEFAULT '0',
  `diferencia` double DEFAULT '0',
  PRIMARY KEY (`id_corte`),
  KEY `id_responsable` (`id_responsable`),
  CONSTRAINT `cortes_de_caja_ibfk_1` FOREIGN KEY (`id_responsable`) REFERENCES `responsables` (`id_responsable`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cortes_de_caja`
--

LOCK TABLES `cortes_de_caja` WRITE;
/*!40000 ALTER TABLE `cortes_de_caja` DISABLE KEYS */;
/*!40000 ALTER TABLE `cortes_de_caja` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `detalles_pedido`
--

DROP TABLE IF EXISTS `detalles_pedido`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `detalles_pedido` (
  `id_detalle` int NOT NULL AUTO_INCREMENT,
  `id_pedido` int DEFAULT NULL,
  `id_servicio_config` int DEFAULT NULL,
  `descripcion_detallada` text,
  `monto_servicio` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id_detalle`),
  KEY `id_pedido` (`id_pedido`),
  KEY `id_servicio_config` (`id_servicio_config`),
  CONSTRAINT `detalles_pedido_ibfk_1` FOREIGN KEY (`id_pedido`) REFERENCES `pedidos` (`id_pedido`) ON DELETE CASCADE,
  CONSTRAINT `detalles_pedido_ibfk_2` FOREIGN KEY (`id_servicio_config`) REFERENCES `servicios_config` (`id_servicio_config`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `detalles_pedido`
--

LOCK TABLES `detalles_pedido` WRITE;
/*!40000 ALTER TABLE `detalles_pedido` DISABLE KEYS */;
/*!40000 ALTER TABLE `detalles_pedido` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `maquinaria_equipos`
--

DROP TABLE IF EXISTS `maquinaria_equipos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `maquinaria_equipos` (
  `id_equipo` int NOT NULL AUTO_INCREMENT,
  `nombre_equipo` varchar(150) NOT NULL,
  `tipo` varchar(50) NOT NULL,
  `capacidad` varchar(30) NOT NULL,
  `estado_operacion` enum('Disponible','Ocupada','Mantenimiento') DEFAULT 'Disponible',
  `id_pedido_actual` int DEFAULT NULL,
  PRIMARY KEY (`id_equipo`),
  KEY `id_pedido_actual` (`id_pedido_actual`),
  CONSTRAINT `maquinaria_equipos_ibfk_1` FOREIGN KEY (`id_pedido_actual`) REFERENCES `pedidos` (`id_pedido`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `maquinaria_equipos`
--

LOCK TABLES `maquinaria_equipos` WRITE;
/*!40000 ALTER TABLE `maquinaria_equipos` DISABLE KEYS */;
/*!40000 ALTER TABLE `maquinaria_equipos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `movimientos_caja`
--

DROP TABLE IF EXISTS `movimientos_caja`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `movimientos_caja` (
  `id_movimiento` int NOT NULL AUTO_INCREMENT,
  `fecha_hora` datetime NOT NULL,
  `tipo` enum('Ingreso','Egreso') NOT NULL,
  `concepto` varchar(150) NOT NULL,
  `categoria` varchar(50) NOT NULL,
  `metodo_pago` varchar(50) NOT NULL,
  `monto` decimal(10,2) NOT NULL,
  `id_corte` int DEFAULT NULL,
  PRIMARY KEY (`id_movimiento`),
  KEY `id_corte` (`id_corte`),
  CONSTRAINT `movimientos_caja_ibfk_1` FOREIGN KEY (`id_corte`) REFERENCES `cortes_de_caja` (`id_corte`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `movimientos_caja`
--

LOCK TABLES `movimientos_caja` WRITE;
/*!40000 ALTER TABLE `movimientos_caja` DISABLE KEYS */;
/*!40000 ALTER TABLE `movimientos_caja` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pedidos`
--

DROP TABLE IF EXISTS `pedidos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pedidos` (
  `id_pedido` int NOT NULL AUTO_INCREMENT,
  `id_cliente` int DEFAULT NULL,
  `fecha_recepcion` datetime NOT NULL,
  `fecha_entrega` datetime NOT NULL,
  `total_prendas` int NOT NULL,
  `tipo_lavado` varchar(50) NOT NULL,
  `estado` enum('Pendiente','En proceso','Listo','Listo para entregar') DEFAULT 'Pendiente',
  `subtotal` decimal(10,2) NOT NULL,
  `iva` decimal(10,2) NOT NULL,
  `total_estimado` decimal(10,2) NOT NULL,
  `gasto_agua_estimado_litros` int DEFAULT '0',
  `id_corte_perteneciente` int DEFAULT NULL,
  `id_responsable` int DEFAULT '1',
  `tipo_servicio` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id_pedido`),
  KEY `id_cliente` (`id_cliente`),
  KEY `id_corte_perteneciente` (`id_corte_perteneciente`),
  CONSTRAINT `pedidos_ibfk_1` FOREIGN KEY (`id_cliente`) REFERENCES `clientes` (`id_cliente`) ON DELETE SET NULL,
  CONSTRAINT `pedidos_ibfk_2` FOREIGN KEY (`id_corte_perteneciente`) REFERENCES `cortes_de_caja` (`id_corte`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pedidos`
--

LOCK TABLES `pedidos` WRITE;
/*!40000 ALTER TABLE `pedidos` DISABLE KEYS */;
/*!40000 ALTER TABLE `pedidos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `productos_insumos`
--

DROP TABLE IF EXISTS `productos_insumos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `productos_insumos` (
  `id_producto` int NOT NULL AUTO_INCREMENT,
  `nombre_producto` varchar(150) NOT NULL,
  `categoria` varchar(50) NOT NULL,
  `unidad_medida` varchar(30) NOT NULL,
  `presentacion` varchar(100) DEFAULT NULL,
  `stock_actual` decimal(10,2) NOT NULL,
  `stock_minimo` decimal(10,2) NOT NULL,
  `stock_maximo` decimal(10,2) NOT NULL,
  `proveedor` varchar(150) DEFAULT NULL,
  `fecha_compra` date DEFAULT NULL,
  `costo_uso_servicio` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id_producto`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `productos_insumos`
--

LOCK TABLES `productos_insumos` WRITE;
/*!40000 ALTER TABLE `productos_insumos` DISABLE KEYS */;
/*!40000 ALTER TABLE `productos_insumos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `registro_consumo_agua`
--

DROP TABLE IF EXISTS `registro_consumo_agua`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `registro_consumo_agua` (
  `id` int NOT NULL AUTO_INCREMENT,
  `fecha` date NOT NULL,
  `litros_consumidos` decimal(10,2) NOT NULL,
  `kilos_lavados` decimal(10,2) DEFAULT '0.00',
  `costo_estimado` decimal(10,2) DEFAULT '0.00',
  `origen` enum('automatico_pedido','manual_medidor') DEFAULT 'automatico_pedido',
  `id_pedido` int DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `id_pedido` (`id_pedido`),
  CONSTRAINT `registro_consumo_agua_ibfk_1` FOREIGN KEY (`id_pedido`) REFERENCES `pedidos` (`id_pedido`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `registro_consumo_agua`
--

LOCK TABLES `registro_consumo_agua` WRITE;
/*!40000 ALTER TABLE `registro_consumo_agua` DISABLE KEYS */;
/*!40000 ALTER TABLE `registro_consumo_agua` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `responsables`
--

DROP TABLE IF EXISTS `responsables`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `responsables` (
  `id_responsable` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `correo` varchar(100) NOT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `sucursal` varchar(100) DEFAULT 'SU Laundry Centro',
  `rol` varchar(50) DEFAULT 'Administrador',
  `password` varchar(255) DEFAULT '12345',
  PRIMARY KEY (`id_responsable`),
  UNIQUE KEY `correo` (`correo`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `responsables`
--

LOCK TABLES `responsables` WRITE;
/*!40000 ALTER TABLE `responsables` DISABLE KEYS */;
INSERT INTO `responsables` VALUES (1,'Paulina Martínez','paulina.m@sulaundry.com','+52 669 123 4567','SU Laundry Centro','Administrador','12345');
/*!40000 ALTER TABLE `responsables` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `servicio_insumos_vinculados`
--

DROP TABLE IF EXISTS `servicio_insumos_vinculados`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `servicio_insumos_vinculados` (
  `id_servicio_config` int NOT NULL,
  `id_producto` int NOT NULL,
  PRIMARY KEY (`id_servicio_config`,`id_producto`),
  KEY `id_producto` (`id_producto`),
  CONSTRAINT `servicio_insumos_vinculados_ibfk_1` FOREIGN KEY (`id_servicio_config`) REFERENCES `servicios_config` (`id_servicio_config`) ON DELETE CASCADE,
  CONSTRAINT `servicio_insumos_vinculados_ibfk_2` FOREIGN KEY (`id_producto`) REFERENCES `productos_insumos` (`id_producto`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `servicio_insumos_vinculados`
--

LOCK TABLES `servicio_insumos_vinculados` WRITE;
/*!40000 ALTER TABLE `servicio_insumos_vinculados` DISABLE KEYS */;
/*!40000 ALTER TABLE `servicio_insumos_vinculados` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `servicios_config`
--

DROP TABLE IF EXISTS `servicios_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `servicios_config` (
  `id_servicio_config` int NOT NULL AUTO_INCREMENT,
  `nombre_servicio` varchar(150) NOT NULL,
  `precio_mano_obra_base` decimal(10,2) NOT NULL,
  `precio_sugerido_final` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id_servicio_config`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `servicios_config`
--

LOCK TABLES `servicios_config` WRITE;
/*!40000 ALTER TABLE `servicios_config` DISABLE KEYS */;
INSERT INTO `servicios_config` VALUES (1,'Lavado Convencional por Kilo',20.00,30.00),(2,'Lavado compartido',400.00,450.00),(3,'Lavado Exclusivo',1100.00,1183.33);
/*!40000 ALTER TABLE `servicios_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `vista_movimientos_del_dia`
--

DROP TABLE IF EXISTS `vista_movimientos_del_dia`;
/*!50001 DROP VIEW IF EXISTS `vista_movimientos_del_dia`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `vista_movimientos_del_dia` AS SELECT 
 1 AS `ID_MOVIMIENTO`,
 1 AS `HORA`,
 1 AS `TIPO`,
 1 AS `CONCEPTO`,
 1 AS `RESPONSABLE`,
 1 AS `METODO`,
 1 AS `MONTO`,
 1 AS `ID_CORTE`*/;
SET character_set_client = @saved_cs_client;

--
-- Final view structure for view `vista_movimientos_del_dia`
--

/*!50001 DROP VIEW IF EXISTS `vista_movimientos_del_dia`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `vista_movimientos_del_dia` AS select `m`.`id_movimiento` AS `ID_MOVIMIENTO`,time_format(`m`.`fecha_hora`,'%h:%i %p') AS `HORA`,`m`.`tipo` AS `TIPO`,`m`.`concepto` AS `CONCEPTO`,'Paulina Martínez' AS `RESPONSABLE`,`m`.`metodo_pago` AS `METODO`,`m`.`monto` AS `MONTO`,`m`.`id_corte` AS `ID_CORTE` from `movimientos_caja` `m` where (cast(`m`.`fecha_hora` as date) = curdate()) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-08-06 17:50:47
