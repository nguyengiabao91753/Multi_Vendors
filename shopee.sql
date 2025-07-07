CREATE DATABASE  IF NOT EXISTS `shopee` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `shopee`;
-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: localhost    Database: shopee
-- ------------------------------------------------------
-- Server version	8.0.42

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
-- Table structure for table `cart`
--

DROP TABLE IF EXISTS `cart`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart` (
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `created_date_time` datetime(6) DEFAULT NULL,
                        `created_by` varchar(255) DEFAULT NULL,
                        `status` int DEFAULT NULL,
                        `modified_date_time` datetime(6) DEFAULT NULL,
                        `updated_by` varchar(255) DEFAULT NULL,
                        `total_cost` decimal(38,2) DEFAULT NULL,
                        PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart`
--

LOCK TABLES `cart` WRITE;
/*!40000 ALTER TABLE `cart` DISABLE KEYS */;
INSERT INTO `cart` VALUES (1,NULL,NULL,NULL,NULL,NULL,NULL),(2,NULL,NULL,NULL,NULL,NULL,NULL),(3,NULL,NULL,NULL,NULL,NULL,NULL),(4,NULL,NULL,NULL,NULL,NULL,NULL),(5,NULL,NULL,NULL,NULL,NULL,NULL),(6,NULL,NULL,NULL,NULL,NULL,NULL),(7,NULL,NULL,NULL,NULL,NULL,NULL),(20,NULL,NULL,NULL,NULL,NULL,400000.00),(21,NULL,NULL,NULL,NULL,NULL,5000000.00);
/*!40000 ALTER TABLE `cart` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cart_details`
--

DROP TABLE IF EXISTS `cart_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart_details` (
                                `id` bigint NOT NULL AUTO_INCREMENT,
                                `created_date_time` datetime(6) DEFAULT NULL,
                                `created_by` varchar(255) DEFAULT NULL,
                                `status` int DEFAULT NULL,
                                `modified_date_time` datetime(6) DEFAULT NULL,
                                `updated_by` varchar(255) DEFAULT NULL,
                                `price_of_one` decimal(38,2) DEFAULT NULL,
                                `quantity` int DEFAULT NULL,
                                `total_price` decimal(38,2) DEFAULT NULL,
                                `cart_id` bigint DEFAULT NULL,
                                `product_id` bigint DEFAULT NULL,
                                PRIMARY KEY (`id`),
                                KEY `FKhq1m50l0ke2fkqxxd6ubo3x4b` (`cart_id`),
                                KEY `FK9rlic3aynl3g75jvedkx84lhv` (`product_id`),
                                CONSTRAINT `FK9rlic3aynl3g75jvedkx84lhv` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
                                CONSTRAINT `FKhq1m50l0ke2fkqxxd6ubo3x4b` FOREIGN KEY (`cart_id`) REFERENCES `cart` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart_details`
--

LOCK TABLES `cart_details` WRITE;
/*!40000 ALTER TABLE `cart_details` DISABLE KEYS */;
INSERT INTO `cart_details` VALUES (1,NULL,NULL,NULL,NULL,NULL,5000000.00,1,5000000.00,4,3),(2,NULL,NULL,NULL,NULL,NULL,5000000.00,1,5000000.00,6,3),(23,NULL,NULL,NULL,NULL,NULL,300000.00,1,300000.00,20,5),(25,NULL,NULL,NULL,NULL,NULL,5000000.00,1,5000000.00,21,3),(26,NULL,NULL,NULL,NULL,NULL,100000.00,1,100000.00,20,7);
/*!40000 ALTER TABLE `cart_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categories` (
                              `id` bigint NOT NULL AUTO_INCREMENT,
                              `created_date_time` datetime(6) DEFAULT NULL,
                              `created_by` varchar(255) DEFAULT NULL,
                              `status` int DEFAULT NULL,
                              `modified_date_time` datetime(6) DEFAULT NULL,
                              `updated_by` varchar(255) DEFAULT NULL,
                              `category_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
                              `avatar` varchar(255) DEFAULT NULL,
                              PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categories`
--

LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
INSERT INTO `categories` VALUES (1,NULL,NULL,NULL,NULL,NULL,'Điện thoại',NULL),(2,NULL,NULL,NULL,NULL,NULL,'Quần','https://cdn.vuahanghieu.com/unsafe/0x900/left/top/smart/filters:quality(90)/https://admin.vuahanghieu.com/upload/product/2022/05/quan-bo-dsquared2-icon-print-skater-fit-jeans-s79la0017-mau-den-628739d2a2695-20052022134850.jpg'),(3,NULL,NULL,NULL,NULL,NULL,'Áo','https://bizweb.dktcdn.net/thumb/1024x1024/100/448/697/products/3-ded5f9d7-ce8b-4dcf-9fd1-a4f3a183baa5.png?v=1688556462967'),(4,NULL,NULL,NULL,NULL,NULL,'Giày','https://media.istockphoto.com/id/665062786/vi/vec-to/qu%E1%BA%A7n-%C3%A1o-m%C3%A0u-icon-gi%C3%A0y-nam-gi%C3%A0y-th%E1%BB%83-thao.jpg?s=612x612&w=0&k=20&c=2ci6qbkCmDqOfG-NuCYocMadksYOQQr5IIjHzPa5VaI='),(5,NULL,NULL,NULL,NULL,NULL,'Điện tử','https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR_kKsnNY8dYR710Q73uOHqwNexMJwEaQ49lw&s');
/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `feedback`
--

DROP TABLE IF EXISTS `feedback`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `feedback` (
                            `id` bigint NOT NULL AUTO_INCREMENT,
                            `created_date_time` datetime(6) DEFAULT NULL,
                            `created_by` varchar(255) DEFAULT NULL,
                            `status` int DEFAULT NULL,
                            `modified_date_time` datetime(6) DEFAULT NULL,
                            `updated_by` varchar(255) DEFAULT NULL,
                            `comment` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
                            `feedback_date_time` int DEFAULT NULL,
                            `rated_star` int DEFAULT NULL,
                            `product_id` bigint DEFAULT NULL,
                            `user_id` bigint DEFAULT NULL,
                            PRIMARY KEY (`id`),
                            KEY `FKc3p4lovbwrtqqkd3ci5t0g84u` (`product_id`),
                            KEY `FKpwwmhguqianghvi1wohmtsm8l` (`user_id`),
                            CONSTRAINT `FKc3p4lovbwrtqqkd3ci5t0g84u` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
                            CONSTRAINT `FKpwwmhguqianghvi1wohmtsm8l` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `feedback`
--

LOCK TABLES `feedback` WRITE;
/*!40000 ALTER TABLE `feedback` DISABLE KEYS */;
INSERT INTO `feedback` VALUES (1,'2025-06-23 13:19:54.568711',NULL,NULL,NULL,NULL,'Sản phẩm ok',NULL,3,3,1),(3,'2025-06-23 15:34:03.182482',NULL,NULL,NULL,NULL,'Sản phẩm ok',NULL,3,5,1);
/*!40000 ALTER TABLE `feedback` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_details`
--

DROP TABLE IF EXISTS `order_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_details` (
                                 `id` bigint NOT NULL AUTO_INCREMENT,
                                 `created_date_time` datetime(6) DEFAULT NULL,
                                 `created_by` varchar(255) DEFAULT NULL,
                                 `status` int DEFAULT NULL,
                                 `modified_date_time` datetime(6) DEFAULT NULL,
                                 `updated_by` varchar(255) DEFAULT NULL,
                                 `price_of_one` decimal(38,2) DEFAULT NULL,
                                 `quantity` int DEFAULT NULL,
                                 `total_price` decimal(38,2) DEFAULT NULL,
                                 `order_id` bigint DEFAULT NULL,
                                 `product_id` bigint DEFAULT NULL,
                                 `voucher_id` bigint DEFAULT NULL,
                                 PRIMARY KEY (`id`),
                                 KEY `FKjyu2qbqt8gnvno9oe9j2s2ldk` (`order_id`),
                                 KEY `FK4q98utpd73imf4yhttm3w0eax` (`product_id`),
                                 KEY `FKs5pemrd6ksp3k38hti17i1jx7` (`voucher_id`),
                                 CONSTRAINT `FK4q98utpd73imf4yhttm3w0eax` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
                                 CONSTRAINT `FKjyu2qbqt8gnvno9oe9j2s2ldk` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
                                 CONSTRAINT `FKs5pemrd6ksp3k38hti17i1jx7` FOREIGN KEY (`voucher_id`) REFERENCES `vouchers` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_details`
--

LOCK TABLES `order_details` WRITE;
/*!40000 ALTER TABLE `order_details` DISABLE KEYS */;
INSERT INTO `order_details` VALUES (1,'2025-07-07 15:05:36.370534',NULL,1,'2025-07-07 15:05:36.370534',NULL,100000.00,2,197989.00,16,7,3),(2,'2025-07-07 15:05:36.370534',NULL,3,'2025-07-07 15:05:36.370534',NULL,300000.00,1,296998.00,16,5,1),(3,'2025-07-07 16:40:27.708229',NULL,NULL,NULL,NULL,300000.00,1,300000.00,17,5,NULL),(4,'2025-07-07 16:40:27.730689',NULL,3,NULL,NULL,100000.00,1,100000.00,17,7,NULL);
/*!40000 ALTER TABLE `order_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
                          `id` bigint NOT NULL AUTO_INCREMENT,
                          `created_date_time` datetime(6) DEFAULT NULL,
                          `created_by` varchar(255) DEFAULT NULL,
                          `status` int DEFAULT NULL,
                          `modified_date_time` datetime(6) DEFAULT NULL,
                          `updated_by` varchar(255) DEFAULT NULL,
                          `full_cost` decimal(38,2) DEFAULT NULL,
                          `order_time` datetime(6) DEFAULT NULL,
                          `shipping_fee` decimal(38,2) DEFAULT NULL,
                          `total_cost` decimal(38,2) DEFAULT NULL,
                          `user_id` bigint DEFAULT NULL,
                          `address` varchar(255) DEFAULT NULL,
                          `email` varchar(255) DEFAULT NULL,
                          `full_name` varchar(255) DEFAULT NULL,
                          `method` varchar(255) DEFAULT NULL,
                          `phone` varchar(255) DEFAULT NULL,
                          `voucher_id` bigint DEFAULT NULL,
                          `payment_status` int DEFAULT NULL,
                          `price_of_one` decimal(38,2) DEFAULT NULL,
                          `quantity` int DEFAULT NULL,
                          `product_id` bigint DEFAULT NULL,
                          `vnp_txn_ref` varchar(255) DEFAULT NULL,
                          PRIMARY KEY (`id`),
                          KEY `FK32ql8ubntj5uh44ph9659tiih` (`user_id`),
                          KEY `FKdimvsocblb17f45ikjr6xn1wj` (`voucher_id`),
                          KEY `FKkp5k52qtiygd8jkag4hayd0qg` (`product_id`),
                          CONSTRAINT `FK32ql8ubntj5uh44ph9659tiih` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
                          CONSTRAINT `FKdimvsocblb17f45ikjr6xn1wj` FOREIGN KEY (`voucher_id`) REFERENCES `vouchers` (`id`),
                          CONSTRAINT `FKkp5k52qtiygd8jkag4hayd0qg` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (12,'2025-07-03 21:26:20.279502',NULL,3,'2025-07-03 21:26:20.279502',NULL,NULL,NULL,NULL,300000.00,1,'3602 Gaylord Dr','hvv@gmail.com','Hoàng Văn Vinh','COD','6308348000',1,0,300000.00,1,5,NULL),(13,'2025-07-03 22:37:34.946920',NULL,1,'2025-07-03 22:37:34.946920',NULL,NULL,NULL,NULL,300000.00,1,'3602 Gaylord Dr','hvv@gmail.com','Hoàng Văn Vinh','VNPay','6308348000',NULL,1,300000.00,1,5,NULL),(14,'2025-07-04 15:05:37.960889',NULL,3,'2025-07-04 15:05:37.960889',NULL,NULL,NULL,NULL,300000.00,1,'3602 Gaylord Dr','ntt@gmail.com','Nguyễn Thuỳ Trang','COD','6308348000',1,1,300000.00,1,5,NULL),(15,'2025-07-06 09:23:13.088545',NULL,3,'2025-07-06 09:23:13.088545',NULL,NULL,NULL,NULL,600000.00,1,'3602 Gaylord Dr','ntt@gmail.com','Nguyễn Thuỳ Trang','COD','6308348000',1,1,300000.00,2,5,NULL),(16,'2025-07-07 15:05:36.370534',NULL,1,'2025-07-07 15:05:36.370534',NULL,NULL,NULL,NULL,494987.00,1,'3602 Gaylord Dr','ntt@gmail.com','Nguyễn Thuỳ Trang','COD','6308348000',NULL,1,NULL,NULL,NULL,NULL),(17,'2025-07-07 16:40:27.708229',NULL,1,'2025-07-07 16:40:27.708229',NULL,NULL,NULL,NULL,400000.00,1,'3602 Gaylord Dr','ntt@gmail.com','Nguyễn Thuỳ Trang','VNPay','6308348000',NULL,1,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_category`
--

DROP TABLE IF EXISTS `product_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_category` (
                                    `product_id` bigint NOT NULL,
                                    `category_id` bigint NOT NULL,
                                    KEY `FKdswxvx2nl2032yjv609r29sdr` (`category_id`),
                                    KEY `FK5w81wp3eyugvi2lii94iao3fm` (`product_id`),
                                    CONSTRAINT `FK5w81wp3eyugvi2lii94iao3fm` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
                                    CONSTRAINT `FKdswxvx2nl2032yjv609r29sdr` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_category`
--

LOCK TABLES `product_category` WRITE;
/*!40000 ALTER TABLE `product_category` DISABLE KEYS */;
INSERT INTO `product_category` VALUES (6,1),(6,5),(7,1),(2,1),(2,5),(5,2),(5,3);
/*!40000 ALTER TABLE `product_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_image`
--

DROP TABLE IF EXISTS `product_image`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_image` (
                                 `id` bigint NOT NULL AUTO_INCREMENT,
                                 `url_image` varchar(1111) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
                                 `product_id` bigint DEFAULT NULL,
                                 PRIMARY KEY (`id`),
                                 KEY `FK1n91c4vdhw8pa4frngs4qbbvs` (`product_id`),
                                 CONSTRAINT `FK1n91c4vdhw8pa4frngs4qbbvs` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_image`
--

LOCK TABLES `product_image` WRITE;
/*!40000 ALTER TABLE `product_image` DISABLE KEYS */;
INSERT INTO `product_image` VALUES (3,'http://res.cloudinary.com/djyw3ytjd/image/upload/v1746262566/otecdyad6ldloajhvztn.jpg',6),(4,'http://res.cloudinary.com/djyw3ytjd/image/upload/v1746262567/u4frja7j1brkfz9yi7dw.jpg',6),(5,'http://res.cloudinary.com/djyw3ytjd/image/upload/v1746262838/bj31whuz2zbp0kwpfn3y.jpg',6),(6,'http://res.cloudinary.com/djyw3ytjd/image/upload/v1746262998/odeaqaaqmzlrzv2cjvxx.jpg',2),(7,'http://res.cloudinary.com/djyw3ytjd/image/upload/v1746262999/pfnezxyliy5ty3aihtt7.jpg',2),(8,'http://res.cloudinary.com/djyw3ytjd/image/upload/v1746263059/ifrvrq6vztoyd33fpwd7.jpg',5),(12,'http://res.cloudinary.com/djyw3ytjd/image/upload/v1747197722/nsfivtt7thpkzf3k3z12.png',7),(13,'http://res.cloudinary.com/djyw3ytjd/image/upload/v1747800830/pfne2nf7x0qe4t8zlqz4.jpg',1),(14,'http://res.cloudinary.com/djyw3ytjd/image/upload/v1747800831/bl7nszlqhsaodn9fte02.jpg',1),(15,'http://res.cloudinary.com/djyw3ytjd/image/upload/v1747800832/k6gm0w7ucemnico0bhxg.jpg',1),(16,'http://res.cloudinary.com/djyw3ytjd/image/upload/v1747801069/xyznagnaxaehwn0dbwwj.jpg',3),(17,'http://res.cloudinary.com/djyw3ytjd/image/upload/v1747801071/w3dzu7rihxocstuoktno.jpg',3),(18,'http://res.cloudinary.com/djyw3ytjd/image/upload/v1747801072/vsq6sxpcm91ithdqtqcx.jpg',3),(20,'http://res.cloudinary.com/djyw3ytjd/image/upload/v1751725316/h8rciq9zmdtyu7jvjis8.png',5);
/*!40000 ALTER TABLE `product_image` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_vouchers`
--

DROP TABLE IF EXISTS `product_vouchers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_vouchers` (
                                    `id` bigint NOT NULL AUTO_INCREMENT,
                                    `created_date_time` datetime(6) DEFAULT NULL,
                                    `created_by` varchar(255) DEFAULT NULL,
                                    `status` int DEFAULT NULL,
                                    `modified_date_time` datetime(6) DEFAULT NULL,
                                    `updated_by` varchar(255) DEFAULT NULL,
                                    `product_id` bigint DEFAULT NULL,
                                    `voucher_id` bigint DEFAULT NULL,
                                    PRIMARY KEY (`id`),
                                    KEY `FKi8gw58lovcetficdvt8dq1447` (`product_id`),
                                    KEY `FKfw55tc05vugt9umeie6rqx003` (`voucher_id`),
                                    CONSTRAINT `FKfw55tc05vugt9umeie6rqx003` FOREIGN KEY (`voucher_id`) REFERENCES `vouchers` (`id`),
                                    CONSTRAINT `FKi8gw58lovcetficdvt8dq1447` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_vouchers`
--

LOCK TABLES `product_vouchers` WRITE;
/*!40000 ALTER TABLE `product_vouchers` DISABLE KEYS */;
/*!40000 ALTER TABLE `product_vouchers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `products` (
                            `id` bigint NOT NULL AUTO_INCREMENT,
                            `created_date_time` datetime(6) DEFAULT NULL,
                            `created_by` varchar(255) DEFAULT NULL,
                            `status` int DEFAULT NULL,
                            `modified_date_time` datetime(6) DEFAULT NULL,
                            `updated_by` varchar(255) DEFAULT NULL,
                            `amount` int DEFAULT NULL,
                            `description` text,
                            `price` decimal(38,2) DEFAULT NULL,
                            `product_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
                            `sale_price` decimal(38,2) DEFAULT NULL,
                            `thumbnail` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
                            `user_id` bigint DEFAULT NULL,
                            `min` int DEFAULT NULL,
                            PRIMARY KEY (`id`),
                            KEY `FKdb050tk37qryv15hd932626th` (`user_id`),
                            CONSTRAINT `FKdb050tk37qryv15hd932626th` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (1,'2025-05-21 00:00:00.000000',NULL,1,NULL,'12',12,'RAM:	6GB\r\nBộ nhớ trong:	128-256-512GB\r\nThẻ SIM:	2 SIM\r\nDung lượng pin:	Li-Ion 3687 mAh\r\nSạc nhanh PD2.0, 50% trong 30ph (quảng cáo)\r\nSạc không dây Qi2 15W (iOS 17.4)\r\nMagSafe không dây 15W',11000000.00,'Điện thoại iPhone 12 Pro Max cũ',10000000.00,'http://res.cloudinary.com/djyw3ytjd/image/upload/v1745159238/iwzsrjhebbndeha6xy2b.png',3,5),(2,'2025-05-21 00:00:00.000000',NULL,NULL,NULL,'12',12,'CPU: 	Qualcomm SM8550 Snapdragon 8 Gen 2 (4 nm)\r\n8 nhân (1x3.2 GHz & 2x2.8 GHz & 2x2.8 GHz & 3x2.0 GHz)\r\nGPU: Adreno 740\r\nRAM: 	8-12GB, LPDDR5X\r\nBộ nhớ trong: 	128GB (UFS 3.1 - 2.2GB/s)\r\n256GB/512GB (UFS 4.0 - 3.5GB/s)',10000000.00,'Xiaomi 13 Pro',9000000.00,'http://res.cloudinary.com/djyw3ytjd/image/upload/v1745160190/yewmfio5sf91ofi9u50n.png',4,5),(3,'2025-05-21 00:00:00.000000',NULL,1,NULL,'5',6,'Điện thoại iPhone 11 Pro Max cũ',6000000.00,'Điện thoại iPhone 11 Pro Max cũ',5000000.00,'http://res.cloudinary.com/djyw3ytjd/image/upload/v1745160491/k968ibp2ws1rxjyulprb.png',3,5),(4,'2025-05-19 00:00:00.000000',NULL,0,NULL,'12',12,'Giày nam size 42',500000.00,'Giày nam đen',400000.00,'http://res.cloudinary.com/djyw3ytjd/image/upload/v1745160705/vizezir2vhfqxgx9clpr.jpg',3,5),(5,'2025-05-19 00:00:00.000000',NULL,1,NULL,'6',1,'Áo Polo Vải Dry Pique Ngắn Tay 1',500000.00,'Áo Polo Vải Dry Pique Ngắn Tay 1',300000.00,'http://res.cloudinary.com/djyw3ytjd/image/upload/v1745160787/risgbcqfogw9bl4ccohd.jpg',4,5),(6,'2025-05-19 00:00:00.000000',NULL,0,NULL,'12',12,'\r\n    Hệ điều hành:\r\n    iOS 14\r\n    Chip xử lý (CPU):\r\n    Apple A12 Bionic\r\n    Tốc độ CPU:\r\n    2 nhân 2.5 GHz & 4 nhân 1.6 GHz\r\n    Chip đồ họa (GPU):\r\n    Apple GPU 4 nhân\r\n    RAM:\r\n    4 GB\r\n    Dung lượng lưu trữ:\r\n    64 GB\r\n    Dung lượng còn lại (khả dụng) khoảng:\r\n    Khoảng 57 GB\r\n    Danh bạ:\r\n    Không giới hạn\r\n',6000000.00,'XS Max',5500000.00,NULL,4,5),(7,'2025-05-19 00:00:00.000000',NULL,1,NULL,'12',9,'Bánh ngọt mousse',123000.00,'Bánh ngọt mousse',100000.00,NULL,4,5);
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `receipt_details`
--

DROP TABLE IF EXISTS `receipt_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `receipt_details` (
                                   `id` bigint NOT NULL AUTO_INCREMENT,
                                   `created_date_time` datetime(6) DEFAULT NULL,
                                   `created_by` varchar(255) DEFAULT NULL,
                                   `status` int DEFAULT NULL,
                                   `modified_date_time` datetime(6) DEFAULT NULL,
                                   `updated_by` varchar(255) DEFAULT NULL,
                                   `quantity` int DEFAULT NULL,
                                   `product_id` bigint DEFAULT NULL,
                                   `receipt_id` bigint DEFAULT NULL,
                                   PRIMARY KEY (`id`),
                                   KEY `FK5mjbgdjnq1ynx0g9tktny2ot` (`product_id`),
                                   KEY `FKgg9qo0w1vjcu9ridx36dyrhn2` (`receipt_id`),
                                   CONSTRAINT `FK5mjbgdjnq1ynx0g9tktny2ot` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
                                   CONSTRAINT `FKgg9qo0w1vjcu9ridx36dyrhn2` FOREIGN KEY (`receipt_id`) REFERENCES `receipts` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `receipt_details`
--

LOCK TABLES `receipt_details` WRITE;
/*!40000 ALTER TABLE `receipt_details` DISABLE KEYS */;
INSERT INTO `receipt_details` VALUES (1,NULL,NULL,NULL,NULL,NULL,1,2,4),(2,NULL,NULL,NULL,NULL,NULL,2,6,4),(3,NULL,NULL,NULL,NULL,NULL,2,5,5),(4,NULL,NULL,NULL,NULL,NULL,3,2,5),(5,NULL,NULL,NULL,NULL,NULL,1,2,6),(6,NULL,NULL,NULL,NULL,NULL,2,5,6);
/*!40000 ALTER TABLE `receipt_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `receipts`
--

DROP TABLE IF EXISTS `receipts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `receipts` (
                            `id` bigint NOT NULL AUTO_INCREMENT,
                            `created_date_time` datetime(6) DEFAULT NULL,
                            `created_by` varchar(255) DEFAULT NULL,
                            `status` int DEFAULT NULL,
                            `modified_date_time` datetime(6) DEFAULT NULL,
                            `updated_by` varchar(255) DEFAULT NULL,
                            `order_time` datetime(6) DEFAULT NULL,
                            `supplier` varchar(255) DEFAULT NULL,
                            `total_cost` decimal(38,2) DEFAULT NULL,
                            `user_id` bigint DEFAULT NULL,
                            PRIMARY KEY (`id`),
                            KEY `FK7t0uo7yxjck29e967rny84ky4` (`user_id`),
                            CONSTRAINT `FK7t0uo7yxjck29e967rny84ky4` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `receipts`
--

LOCK TABLES `receipts` WRITE;
/*!40000 ALTER TABLE `receipts` DISABLE KEYS */;
INSERT INTO `receipts` VALUES (4,'2025-05-05 07:05:30.582874',NULL,NULL,NULL,NULL,NULL,'TGDD',22000000.00,4),(5,'2025-05-05 07:13:36.588129',NULL,NULL,NULL,NULL,NULL,'TGDD',31000000.00,4),(6,'2025-07-05 21:33:11.686615',NULL,NULL,NULL,NULL,NULL,'TGDD',11000000.00,4);
/*!40000 ALTER TABLE `receipts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `returns`
--

DROP TABLE IF EXISTS `returns`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `returns` (
                           `id` bigint NOT NULL AUTO_INCREMENT,
                           `created_date_time` datetime(6) DEFAULT NULL,
                           `created_by` varchar(255) DEFAULT NULL,
                           `status` int DEFAULT NULL,
                           `modified_date_time` datetime(6) DEFAULT NULL,
                           `updated_by` varchar(255) DEFAULT NULL,
                           `img_back` varchar(255) DEFAULT NULL,
                           `img_return` varchar(255) DEFAULT NULL,
                           `note` varchar(255) DEFAULT NULL,
                           `reason` varchar(255) DEFAULT NULL,
                           `return_status` enum('APPROVED','COMPLETED','PENDING','REJECTED') NOT NULL,
                           `order_id` bigint DEFAULT NULL,
                           `user_id` bigint DEFAULT NULL,
                           `order_detail_id` bigint DEFAULT NULL,
                           PRIMARY KEY (`id`),
                           UNIQUE KEY `UK57n87uqssdjl6f5nn96ob9g04` (`order_detail_id`),
                           KEY `FKtge2tys80xohjn8v3wtiy21yi` (`order_id`),
                           KEY `FKof2cd2g96d3xgt0lnqbrydgvx` (`user_id`),
                           CONSTRAINT `FKmhecnqlmslat5w9abbywvxsg2` FOREIGN KEY (`order_detail_id`) REFERENCES `order_details` (`id`),
                           CONSTRAINT `FKof2cd2g96d3xgt0lnqbrydgvx` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
                           CONSTRAINT `FKtge2tys80xohjn8v3wtiy21yi` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `returns`
--

LOCK TABLES `returns` WRITE;
/*!40000 ALTER TABLE `returns` DISABLE KEYS */;
INSERT INTO `returns` VALUES (2,'2025-07-03 22:47:13.612221',NULL,NULL,NULL,NULL,'http://res.cloudinary.com/djyw3ytjd/image/upload/v1751601407/g68wyglz5qkapv7qrxk0.png','http://res.cloudinary.com/djyw3ytjd/image/upload/v1751557624/htar71cyjmiq4p8e1yls.png','Sản phẩm quay đầu bị rách','Sản phẩm không đúng như mô tả, bị rách','REJECTED',12,1,NULL),(3,'2025-07-04 15:06:39.132833',NULL,NULL,NULL,NULL,'http://res.cloudinary.com/djyw3ytjd/image/upload/v1751616549/iv8vxvx4kgtyzsnm6i5d.png','http://res.cloudinary.com/djyw3ytjd/image/upload/v1751616394/egaer88jm3qfxzo6q76p.png','Hàng bị rách','Hàng không đúng mô tả','REJECTED',14,1,NULL),(4,'2025-07-06 09:24:31.049412',NULL,NULL,NULL,NULL,NULL,'http://res.cloudinary.com/djyw3ytjd/image/upload/v1751768665/xhnis8imrmxu8yrzxi7r.png',NULL,'Wrong','PENDING',15,1,NULL),(5,'2025-07-07 16:34:19.982965',NULL,NULL,NULL,NULL,NULL,'http://res.cloudinary.com/djyw3ytjd/image/upload/v1751880850/yxiwx0v8ntrbjeh5php5.jpg',NULL,'Bị rách ở tay áo','PENDING',NULL,1,2),(6,'2025-07-07 16:41:17.911949',NULL,NULL,NULL,NULL,'http://res.cloudinary.com/djyw3ytjd/image/upload/v1751886714/qrhnbgws5a3lynmd8ptb.jpg','http://res.cloudinary.com/djyw3ytjd/image/upload/v1751881270/irvbsumgj96wf1kspybp.png','Bị rách','Bánh bị chảy kem','REJECTED',NULL,1,4);
/*!40000 ALTER TABLE `returns` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
                         `id` bigint NOT NULL AUTO_INCREMENT,
                         `created_date_time` datetime(6) DEFAULT NULL,
                         `created_by` varchar(255) DEFAULT NULL,
                         `status` int DEFAULT NULL,
                         `modified_date_time` datetime(6) DEFAULT NULL,
                         `updated_by` varchar(255) DEFAULT NULL,
                         `name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
                         PRIMARY KEY (`id`),
                         UNIQUE KEY `UKofx66keruapi6vyqpv6f2or37` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (1,NULL,NULL,1,NULL,NULL,'ADMIN'),(2,NULL,NULL,NULL,NULL,NULL,'VENDOR'),(3,NULL,NULL,NULL,NULL,NULL,'CLIENT');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_role`
--

DROP TABLE IF EXISTS `user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_role` (
                             `user_id` bigint NOT NULL,
                             `role_id` bigint NOT NULL,
                             PRIMARY KEY (`user_id`,`role_id`),
                             KEY `FKt7e7djp752sqn6w22i6ocqy6q` (`role_id`),
                             CONSTRAINT `FKj345gk1bovqvfame88rcx7yyx` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
                             CONSTRAINT `FKt7e7djp752sqn6w22i6ocqy6q` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_role`
--

LOCK TABLES `user_role` WRITE;
/*!40000 ALTER TABLE `user_role` DISABLE KEYS */;
INSERT INTO `user_role` VALUES (3,1),(5,1),(6,1),(8,1),(4,2),(7,2),(1,3),(2,3),(4,3),(7,3);
/*!40000 ALTER TABLE `user_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
                         `id` bigint NOT NULL AUTO_INCREMENT,
                         `created_date_time` datetime(6) DEFAULT NULL,
                         `created_by` varchar(255) DEFAULT NULL,
                         `status` int DEFAULT NULL,
                         `modified_date_time` datetime(6) DEFAULT NULL,
                         `updated_by` varchar(255) DEFAULT NULL,
                         `address` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
                         `avatar` varchar(255) DEFAULT NULL,
                         `dob` date DEFAULT NULL,
                         `email` varchar(50) NOT NULL,
                         `full_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
                         `password` varchar(120) NOT NULL,
                         `phone` varchar(20) DEFAULT NULL,
                         `cart_id` bigint DEFAULT NULL,
                         PRIMARY KEY (`id`),
                         UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`),
                         UNIQUE KEY `UKpnp1baae4enifkkuq2cd01r9l` (`cart_id`),
                         CONSTRAINT `FKqmifheg6lnigfifvlmpjnuny8` FOREIGN KEY (`cart_id`) REFERENCES `cart` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,NULL,'ADMIN',1,NULL,'ADMIN','Dong Da','http://res.cloudinary.com/djyw3ytjd/image/upload/v1751514484/qdibbjg1de26sytunrig.png',NULL,'chuyendizz@gmail.com','Chuyen Dizz 1','$2a$10$95T8Z5H/beHpIpQtSJatM.XucOs7oSfctzsLscYg.hRvLuhga6dbC','6308348000',20),(2,NULL,'ADMIN',1,NULL,'ADMIN',NULL,NULL,NULL,'phamminhhiep0402@gmail.com','Pham Huy','$2a$10$KWtPamXHqnTueHNTXZLgtuZHUKTbASsUWlCK3xta6./uDz/kD4zZq','0912345566',NULL),(3,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'admin@gmail.com','Shopee','$2a$12$ChoekAKeHq6SQpkZjN2nc.F6MYO96osDdDcgjxLNU5HUBlE9pkYba',NULL,NULL),(4,NULL,NULL,1,NULL,NULL,'Dong Da','http://res.cloudinary.com/djyw3ytjd/image/upload/v1751617034/fmz5bvms5pbfxhgeredx.png','2000-12-19','vendor@gmail.com','Apple Store','$2a$10$AcgQnoRB/nEpOos4zOgcouS1o9xY8Mj.1PpGwx/y5XKuezbaW7wF6','0947134196',21),(5,NULL,NULL,NULL,NULL,NULL,'3602 Gaylord Dr',NULL,'2025-05-14','lta@gmail.com','Nguyễn Thuỳ Trang','123','6308348000',NULL),(6,NULL,NULL,NULL,NULL,NULL,'3602 Gaylord Dr',NULL,'2000-12-11','hvv@gmail.com','Hoàng Văn Vinh','123','6308348000',NULL),(7,NULL,'ADMIN',1,NULL,'ADMIN',NULL,NULL,NULL,'phamminhhiep301@gmail.com',NULL,'$2a$10$M.qo6V7gcM5Mf2esazrhgOtPu9ktXa9fnagJ7zdiJgqjO0coh3J/u','6308348000',NULL),(8,NULL,NULL,NULL,NULL,NULL,'3602 Gaylord Dr',NULL,NULL,'admin123@gmail.com','Nguyễn Trang','123456a@A','0947134111',NULL);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `vouchers`
--

DROP TABLE IF EXISTS `vouchers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `vouchers` (
                            `id` bigint NOT NULL AUTO_INCREMENT,
                            `created_date_time` datetime(6) DEFAULT NULL,
                            `created_by` varchar(255) DEFAULT NULL,
                            `status` int DEFAULT NULL,
                            `modified_date_time` datetime(6) DEFAULT NULL,
                            `updated_by` varchar(255) DEFAULT NULL,
                            `amount` int DEFAULT NULL,
                            `code` varchar(255) DEFAULT NULL,
                            `end_time` datetime(6) DEFAULT NULL,
                            `name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
                            `percent_decrease` int DEFAULT NULL,
                            `start_time` datetime(6) DEFAULT NULL,
                            `user_id` bigint DEFAULT NULL,
                            PRIMARY KEY (`id`),
                            KEY `FK3sgwux4uor7og45vdilosjha8` (`user_id`),
                            CONSTRAINT `FK3sgwux4uor7og45vdilosjha8` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vouchers`
--

LOCK TABLES `vouchers` WRITE;
/*!40000 ALTER TABLE `vouchers` DISABLE KEYS */;
INSERT INTO `vouchers` VALUES (1,NULL,NULL,1,NULL,NULL,2,'HE2025001','2025-10-10 04:34:00.000000','Chào hè 2025',1,'2025-05-14 04:34:00.000000',3),(2,NULL,NULL,1,NULL,NULL,12,'304105','2025-05-17 05:41:00.000000','30/4 - 1/5',1,'2025-04-30 05:41:00.000000',3),(3,NULL,NULL,1,NULL,NULL,11,'KT001','2025-07-24 05:42:00.000000','Khai trương',1,'2025-05-14 05:42:00.000000',4);
/*!40000 ALTER TABLE `vouchers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wishlist`
--

DROP TABLE IF EXISTS `wishlist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wishlist` (
                            `id` bigint NOT NULL AUTO_INCREMENT,
                            `created_date_time` datetime(6) DEFAULT NULL,
                            `created_by` varchar(255) DEFAULT NULL,
                            `status` int DEFAULT NULL,
                            `modified_date_time` datetime(6) DEFAULT NULL,
                            `updated_by` varchar(255) DEFAULT NULL,
                            `product_id` bigint DEFAULT NULL,
                            `user_id` bigint DEFAULT NULL,
                            PRIMARY KEY (`id`),
                            KEY `FK6p7qhvy1bfkri13u29x6pu8au` (`product_id`),
                            KEY `FKtrd6335blsefl2gxpb8lr0gr7` (`user_id`),
                            CONSTRAINT `FK6p7qhvy1bfkri13u29x6pu8au` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
                            CONSTRAINT `FKtrd6335blsefl2gxpb8lr0gr7` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wishlist`
--

LOCK TABLES `wishlist` WRITE;
/*!40000 ALTER TABLE `wishlist` DISABLE KEYS */;
INSERT INTO `wishlist` VALUES (7,NULL,NULL,NULL,NULL,NULL,5,1);
/*!40000 ALTER TABLE `wishlist` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'shopee'
--

--
-- Dumping routines for database 'shopee'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-07-07 19:04:07
