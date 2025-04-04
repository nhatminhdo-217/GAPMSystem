-- MySQL dump 10.13  Distrib 8.0.34, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: gapms
-- ------------------------------------------------------
-- Server version	8.0.34

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `brand`
--

DROP TABLE IF EXISTS `brand`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `brand` (
                         `id` bigint NOT NULL AUTO_INCREMENT,
                         `create_at` datetime(6) DEFAULT NULL,
                         `update_at` datetime(6) DEFAULT NULL,
                         `description` tinytext,
                         `name` varchar(100) DEFAULT NULL,
                         `production_id` bigint NOT NULL,
                         PRIMARY KEY (`id`),
                         KEY `FK4velypig79aflmlw6jrpui5m0` (`production_id`),
                         CONSTRAINT `FK4velypig79aflmlw6jrpui5m0` FOREIGN KEY (`production_id`) REFERENCES `product` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `brand`
--

LOCK TABLES `brand` WRITE;
/*!40000 ALTER TABLE `brand` DISABLE KEYS */;
INSERT INTO `brand` VALUES (1,'2025-02-28 16:05:47.000000','2025-02-28 16:05:45.000000','Day la brand A','NORATEX',1),(2,'2025-03-03 15:46:09.000000','2025-03-03 15:46:10.000000','Day la brand B','DELYCA',3),(3,'2025-03-03 15:46:47.000000','2025-03-03 15:46:48.000000','Day la brand C','FILCO',2);
/*!40000 ALTER TABLE `brand` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cate_brand_price`
--

DROP TABLE IF EXISTS `cate_brand_price`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cate_brand_price` (
                                    `price` decimal(10,2) NOT NULL,
                                    `brand_id` bigint NOT NULL,
                                    `cate_id` bigint NOT NULL,
                                    `is_color` bit(1) NOT NULL,
                                    PRIMARY KEY (`brand_id`,`cate_id`,`is_color`),
                                    KEY `FKhgbrbeu17eo4mjuo365eyqdx7` (`cate_id`),
                                    CONSTRAINT `FKamheyf9mrtg5uk149bp4ogw2u` FOREIGN KEY (`brand_id`) REFERENCES `brand` (`id`),
                                    CONSTRAINT `FKhgbrbeu17eo4mjuo365eyqdx7` FOREIGN KEY (`cate_id`) REFERENCES `category` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cate_brand_price`
--

LOCK TABLES `cate_brand_price` WRITE;
/*!40000 ALTER TABLE `cate_brand_price` DISABLE KEYS */;
INSERT INTO `cate_brand_price` VALUES (20000.00,1,1,_binary '\0'),(22500.00,1,1,_binary ''),(21000.00,1,2,_binary '\0'),(23000.00,1,2,_binary ''),(12000.00,2,3,_binary '\0'),(13000.00,2,3,_binary ''),(20000.00,2,5,_binary '\0'),(21000.00,2,5,_binary ''),(38000.00,3,4,_binary '\0'),(39000.00,3,4,_binary '');
/*!40000 ALTER TABLE `cate_brand_price` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category` (
                            `id` bigint NOT NULL AUTO_INCREMENT,
                            `create_at` datetime(6) DEFAULT NULL,
                            `update_at` datetime(6) DEFAULT NULL,
                            `description` tinytext,
                            `name` varchar(100) DEFAULT NULL,
                            PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category`
--

LOCK TABLES `category` WRITE;
/*!40000 ALTER TABLE `category` DISABLE KEYS */;
INSERT INTO `category` VALUES (1,'2025-02-28 16:06:55.000000','2025-02-28 16:06:55.000000','Day la danh muc A','40/2-5000M'),(2,'2025-02-28 16:07:29.000000','2025-02-28 16:07:30.000000','Day la danh muc B','30/2-5000M'),(3,'2025-03-03 15:47:59.000000','2025-03-03 15:48:01.000000','Day la danh muc C','Tơ 150D-5000M'),(4,'2025-03-03 15:48:25.000000','2025-03-03 15:48:26.000000','Day la danh mục D','70D/2-5000M'),(5,'2025-03-03 15:48:52.000000','2025-03-03 15:48:53.000000',NULL,'Tơ 300D-5000M');
/*!40000 ALTER TABLE `category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `company`
--

DROP TABLE IF EXISTS `company`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `company` (
                           `id` bigint NOT NULL AUTO_INCREMENT,
                           `create_at` datetime(6) DEFAULT NULL,
                           `update_at` datetime(6) DEFAULT NULL,
                           `address` varchar(255) NOT NULL,
                           `email` varchar(255) NOT NULL,
                           `name` varchar(255) NOT NULL,
                           `phone_number` varchar(20) NOT NULL,
                           `tax_number` varchar(15) NOT NULL,
                           PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `company`
--

LOCK TABLES `company` WRITE;
/*!40000 ALTER TABLE `company` DISABLE KEYS */;
INSERT INTO `company` VALUES (1,'2025-02-28 15:59:01.000000','2025-02-28 15:59:03.000000','So 123 Duong 456','abc@gmail.com','Cong Ty A','0912345678','1234567890'),(2,'2025-03-05 11:08:00.971615','2025-03-05 11:08:00.971615','Đất Thổ Cư Hòa Lạc, Km29 Đường Cao Tốc 08, Thạch Hoà, Thạch Thất, Hà Nội','aabbcc@gmail.com','cong ty A','0912345678','1234567890'),(3,'2025-03-05 11:52:31.389078','2025-03-05 11:52:31.389078','Đất Thổ Cư Hòa Lạc, Km29 Đường Cao Tốc 08, Thạch Hoà, Thạch Thất, Hà Nội','aabbcc@gmail.com','cong ty A','0912345678','1234567890');
/*!40000 ALTER TABLE `company` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `company_user`
--

DROP TABLE IF EXISTS `company_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `company_user` (
                                `company_id` bigint NOT NULL,
                                `user_id` bigint NOT NULL,
                                PRIMARY KEY (`company_id`,`user_id`),
                                KEY `FK80v9xde08qif57hi17uwg7a3w` (`user_id`),
                                CONSTRAINT `FK80v9xde08qif57hi17uwg7a3w` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
                                CONSTRAINT `FKby7i4fqhd2jvje6rvc5vc5837` FOREIGN KEY (`company_id`) REFERENCES `company` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `company_user`
--

LOCK TABLES `company_user` WRITE;
/*!40000 ALTER TABLE `company_user` DISABLE KEYS */;
INSERT INTO `company_user` VALUES (2,2),(1,3),(3,5);
/*!40000 ALTER TABLE `company_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `contract`
--

DROP TABLE IF EXISTS `contract`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `contract` (
                            `id` varchar(15) NOT NULL,
                            `create_at` datetime(6) DEFAULT NULL,
                            `update_at` datetime(6) DEFAULT NULL,
                            `status` enum('APPROVED','CANCELED','NOT_APPROVED') NOT NULL,
                            `approved_by` bigint DEFAULT NULL,
                            `name` varchar(255) DEFAULT NULL,
                            `path` varchar(255) DEFAULT NULL,
                            `create_by` bigint DEFAULT NULL,
                            PRIMARY KEY (`id`),
                            KEY `FK4y7jvgiurmfygak81m3qi6rs2` (`approved_by`),
                            KEY `FK77hnlpg1lmimlhyeqain2pons` (`create_by`),
                            CONSTRAINT `FK4y7jvgiurmfygak81m3qi6rs2` FOREIGN KEY (`approved_by`) REFERENCES `user` (`id`),
                            CONSTRAINT `FK77hnlpg1lmimlhyeqain2pons` FOREIGN KEY (`create_by`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `contract`
--

LOCK TABLES `contract` WRITE;
/*!40000 ALTER TABLE `contract` DISABLE KEYS */;
INSERT INTO `contract` VALUES ('HD0001','2025-03-03 16:49:07.000000','2025-03-03 16:49:43.000000','NOT_APPROVED',NULL,NULL,'IMG_0407.jpg',NULL),('HD0002','2025-03-15 21:37:47.534772','2025-03-15 21:37:47.534772','NOT_APPROVED',2,'Hợp đồng khách hàng','effb9fa8-1241-485b-b046-b602cfab64f5_po2.jpg',NULL),('HD0003','2025-03-19 15:58:22.161765','2025-03-30 22:08:56.247054','APPROVED',2,'cong ty A','f580eabb-c71b-4c48-baaf-112428ff75dd_224dedbb-6261-4e5a-b82b-9c79f7856325.jpg',NULL),('HD0004','2025-03-27 13:38:48.813431','2025-03-29 14:12:37.264640','APPROVED',2,'123 123','8b30aae8-ce46-4b29-a038-e1d4a83001f8_bom.jpg',NULL),('HD0005','2025-03-27 15:49:09.211769','2025-03-27 15:49:09.211769','NOT_APPROVED',2,'Nguyen Van A','c2e42284-981e-4ab2-989e-bdaa1d31c3b2_po2.jpg',NULL),('HD0006','2025-03-29 13:32:44.947228','2025-03-29 14:12:09.024234','APPROVED',2,'Do Nhat Minh','29ed19f4-b26a-4267-82c7-397c19138cb9_po2.jpg',2);
/*!40000 ALTER TABLE `contract` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dye_machine`
--

DROP TABLE IF EXISTS `dye_machine`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dye_machine` (
                               `id` bigint NOT NULL AUTO_INCREMENT,
                               `create_at` datetime(6) DEFAULT NULL,
                               `update_at` datetime(6) DEFAULT NULL,
                               `capacity` int NOT NULL,
                               `cone_per_pile` int NOT NULL,
                               `description` tinytext,
                               `diameter` int NOT NULL,
                               `max_weight` int NOT NULL,
                               `pile` int NOT NULL,
                               PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dye_machine`
--

LOCK TABLES `dye_machine` WRITE;
/*!40000 ALTER TABLE `dye_machine` DISABLE KEYS */;
/*!40000 ALTER TABLE `dye_machine` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dye_stage`
--

DROP TABLE IF EXISTS `dye_stage`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dye_stage` (
                             `id` bigint NOT NULL AUTO_INCREMENT,
                             `create_at` datetime(6) DEFAULT NULL,
                             `update_at` datetime(6) DEFAULT NULL,
                             `actual_output` decimal(10,2) DEFAULT NULL,
                             `complete_at` datetime(6) DEFAULT NULL,
                             `cone_quantity` decimal(10,2) NOT NULL,
                             `cone_weight` decimal(10,2) NOT NULL,
                             `deadline` date NOT NULL,
                             `liters` decimal(10,2) NOT NULL,
                             `liters_min` decimal(10,2) NOT NULL,
                             `start_at` datetime(6) DEFAULT NULL,
                             `test_status` enum('NOT_STARTED','TESTED','TESTING') NOT NULL,
                             `work_status` enum('FINISHED','IN_PROGRESS','NOT_STARTED') NOT NULL,
                             `checked_by` bigint DEFAULT NULL,
                             `dye_machine_id` bigint DEFAULT NULL,
                             `leader_id` bigint NOT NULL,
                             `work_order_detail_id` bigint NOT NULL,
                             `dye_photo` varchar(255) DEFAULT NULL,
                             PRIMARY KEY (`id`),
                             UNIQUE KEY `UKpc2gu6mgi7kpcis6vfdm5l096` (`work_order_detail_id`),
                             UNIQUE KEY `UKkpfm7fhckc68ocruxgndyga6c` (`dye_machine_id`),
                             KEY `FKmtshbxkw9j5gs13xd0gp68kn3` (`checked_by`),
                             KEY `FKren32fqmlhdj0r944kfcshq5a` (`leader_id`),
                             CONSTRAINT `FK8koe9i1on9c0kbt5n86r3aodu` FOREIGN KEY (`work_order_detail_id`) REFERENCES `work_order_detail` (`id`),
                             CONSTRAINT `FKdlrio7owjqu28y9nl9wrx6yc9` FOREIGN KEY (`dye_machine_id`) REFERENCES `dye_machine` (`id`),
                             CONSTRAINT `FKmtshbxkw9j5gs13xd0gp68kn3` FOREIGN KEY (`checked_by`) REFERENCES `user` (`id`),
                             CONSTRAINT `FKren32fqmlhdj0r944kfcshq5a` FOREIGN KEY (`leader_id`) REFERENCES `user` (`id`),
                             CONSTRAINT `dye_stage_chk_1` CHECK ((`test_status` between 0 and 2)),
                             CONSTRAINT `dye_stage_chk_2` CHECK ((`work_status` between 0 and 2))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dye_stage`
--

LOCK TABLES `dye_stage` WRITE;
/*!40000 ALTER TABLE `dye_stage` DISABLE KEYS */;
/*!40000 ALTER TABLE `dye_stage` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `packaging_stage`
--

DROP TABLE IF EXISTS `packaging_stage`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `packaging_stage` (
                                   `id` bigint NOT NULL AUTO_INCREMENT,
                                   `create_at` datetime(6) DEFAULT NULL,
                                   `update_at` datetime(6) DEFAULT NULL,
                                   `actual_output` int DEFAULT NULL,
                                   `complete_at` datetime(6) DEFAULT NULL,
                                   `deadline` date NOT NULL,
                                   `received_product_at` datetime(6) NOT NULL,
                                   `start_at` datetime(6) DEFAULT NULL,
                                   `test_status` enum('NOT_STARTED','TESTED','TESTING') NOT NULL,
                                   `work_status` enum('FINISHED','IN_PROGRESS','NOT_STARTED') NOT NULL,
                                   `checked_by` bigint DEFAULT NULL,
                                   `leader_id` bigint NOT NULL,
                                   `winding_stage_id` bigint NOT NULL,
                                   `work_order_detail_id` bigint NOT NULL,
                                   `packaging_photo` varchar(255) DEFAULT NULL,
                                   PRIMARY KEY (`id`),
                                   UNIQUE KEY `UKsfnrw0wp99c35duv48c3ia6wb` (`winding_stage_id`),
                                   UNIQUE KEY `UKickl87df96ro0ecdg5j1gkdld` (`work_order_detail_id`),
                                   KEY `FKooclgopa86jr5eq4jv3wfsh3i` (`checked_by`),
                                   KEY `FKmmgkl2w3tfonjw31ntdp1cets` (`leader_id`),
                                   CONSTRAINT `FK3kghktnntrc5xke7mpcy27r3d` FOREIGN KEY (`work_order_detail_id`) REFERENCES `work_order_detail` (`id`),
                                   CONSTRAINT `FK4w53bqie62trn9sg8dv2ilb4f` FOREIGN KEY (`winding_stage_id`) REFERENCES `winding_stage` (`id`),
                                   CONSTRAINT `FKmmgkl2w3tfonjw31ntdp1cets` FOREIGN KEY (`leader_id`) REFERENCES `user` (`id`),
                                   CONSTRAINT `FKooclgopa86jr5eq4jv3wfsh3i` FOREIGN KEY (`checked_by`) REFERENCES `user` (`id`),
                                   CONSTRAINT `packaging_stage_chk_1` CHECK ((`test_status` between 0 and 2)),
                                   CONSTRAINT `packaging_stage_chk_2` CHECK ((`work_status` between 0 and 2))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `packaging_stage`
--

LOCK TABLES `packaging_stage` WRITE;
/*!40000 ALTER TABLE `packaging_stage` DISABLE KEYS */;
/*!40000 ALTER TABLE `packaging_stage` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product`
--

DROP TABLE IF EXISTS `product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product` (
                           `id` bigint NOT NULL AUTO_INCREMENT,
                           `create_at` datetime(6) DEFAULT NULL,
                           `update_at` datetime(6) DEFAULT NULL,
                           `description` tinytext,
                           `name` varchar(100) NOT NULL,
                           `thread_id` bigint DEFAULT NULL,
                           PRIMARY KEY (`id`),
                           KEY `product_thread_id_fk` (`thread_id`),
                           CONSTRAINT `FK12vgwiylafavkx3p8jeb76nhq` FOREIGN KEY (`thread_id`) REFERENCES `thread` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product`
--

LOCK TABLES `product` WRITE;
/*!40000 ALTER TABLE `product` DISABLE KEYS */;
INSERT INTO `product` VALUES (1,'2025-02-28 16:06:18.000000','2025-02-28 16:06:19.000000','San Pham A','100% Spun Polyester',1),(2,'2025-03-03 15:44:14.000000','2025-03-03 15:44:15.000000','San Pham B','Filament Polyester',1),(3,'2025-03-03 15:45:21.000000','2025-03-03 15:45:22.000000','Chỉ tơ vắt số','Texturised Polyester',1);
/*!40000 ALTER TABLE `product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `production_order`
--

DROP TABLE IF EXISTS `production_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `production_order` (
                                    `id` bigint NOT NULL AUTO_INCREMENT,
                                    `create_at` datetime(6) DEFAULT NULL,
                                    `update_at` datetime(6) DEFAULT NULL,
                                    `status` enum('APPROVED','CANCELED','NOT_APPROVED','WAIT_FOR_APPROVAL','DRAFT') NOT NULL,
                                    `purchase_order_id` bigint NOT NULL,
                                    `approved_by` bigint DEFAULT NULL,
                                    `created_by` bigint DEFAULT NULL,
                                    PRIMARY KEY (`id`),
                                    KEY `FKs997690fj1o626jteobs6359i` (`purchase_order_id`),
                                    KEY `FKollevvjatqr527p6d4efgcw1g` (`approved_by`),
                                    KEY `FKh7bevi6hhdqo6bax3rmc5cp1v` (`created_by`),
                                    CONSTRAINT `FKh7bevi6hhdqo6bax3rmc5cp1v` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`),
                                    CONSTRAINT `FKollevvjatqr527p6d4efgcw1g` FOREIGN KEY (`approved_by`) REFERENCES `user` (`id`),
                                    CONSTRAINT `FKs997690fj1o626jteobs6359i` FOREIGN KEY (`purchase_order_id`) REFERENCES `purchase_order` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `production_order`
--

LOCK TABLES `production_order` WRITE;
/*!40000 ALTER TABLE `production_order` DISABLE KEYS */;
INSERT INTO `production_order` VALUES (1,'2025-03-23 10:25:12.000000','2025-03-23 22:42:41.204073','NOT_APPROVED',5,2,2),(3,'2025-03-29 22:08:57.315367','2025-03-29 22:46:25.603654','WAIT_FOR_APPROVAL',6,2,2),(5,'2025-03-30 22:08:56.289472','2025-03-30 22:14:17.659265','CANCELED',3,NULL,2);
/*!40000 ALTER TABLE `production_order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `production_order_detail`
--

DROP TABLE IF EXISTS `production_order_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `production_order_detail` (
                                           `id` bigint NOT NULL AUTO_INCREMENT,
                                           `create_at` datetime(6) DEFAULT NULL,
                                           `update_at` datetime(6) DEFAULT NULL,
                                           `description` tinytext,
                                           `light_env` bit(1) DEFAULT NULL,
                                           `thread_mass` decimal(10,4) DEFAULT NULL,
                                           `production_order_id` bigint NOT NULL,
                                           `purchase_order_detail_id` bigint NOT NULL,
                                           PRIMARY KEY (`id`),
                                           UNIQUE KEY `UKehnn157s2wn0a3f0ekq7f6sav` (`purchase_order_detail_id`),
                                           KEY `FK2qgig4k8iaajgdm3t0w3nwr9y` (`production_order_id`),
                                           CONSTRAINT `FK2qgig4k8iaajgdm3t0w3nwr9y` FOREIGN KEY (`production_order_id`) REFERENCES `production_order` (`id`),
                                           CONSTRAINT `FKl92n1v1sh2vsruqf2s1twgfux` FOREIGN KEY (`purchase_order_detail_id`) REFERENCES `purchase_order_detail` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `production_order_detail`
--

LOCK TABLES `production_order_detail` WRITE;
/*!40000 ALTER TABLE `production_order_detail` DISABLE KEYS */;
INSERT INTO `production_order_detail` VALUES (1,'2025-03-23 10:29:59.000000','2025-03-23 22:20:31.232076',NULL,_binary '',19.1420,1,2),(2,'2025-03-23 10:31:19.000000','2025-03-23 10:31:19.000000',NULL,NULL,NULL,1,3),(3,'2025-03-29 22:08:57.334935','2025-03-29 22:08:57.334935',NULL,NULL,15.4000,3,5),(4,'2025-03-29 22:08:57.342840','2025-03-29 22:08:57.342840',NULL,NULL,18.6340,3,6),(6,'2025-03-30 22:08:56.320217','2025-03-30 22:08:56.320217',NULL,NULL,18.7880,5,4);
/*!40000 ALTER TABLE `production_order_detail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `purchase_order`
--

DROP TABLE IF EXISTS `purchase_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `purchase_order` (
                                  `id` bigint NOT NULL AUTO_INCREMENT,
                                  `create_at` datetime(6) DEFAULT NULL,
                                  `update_at` datetime(6) DEFAULT NULL,
                                  `status` enum('APPROVED','CANCELED','NOT_APPROVED','DRAFT','WAIT_FOR_APPROVAL') NOT NULL,
                                  `quotation_id` bigint NOT NULL,
                                  `approved_by` bigint DEFAULT NULL,
                                  `customer` bigint DEFAULT NULL,
                                  `manage_by` bigint DEFAULT NULL,
                                  `solution` bigint DEFAULT NULL,
                                  `contract_id` varchar(15) DEFAULT NULL,
                                  PRIMARY KEY (`id`),
                                  UNIQUE KEY `UKc56k5e79nx2xhxrafh7vvng7r` (`solution`),
                                  KEY `FK264mq9l1u99cn67a1dypssv6d` (`quotation_id`),
                                  KEY `purchase_order_user_id_fk` (`approved_by`),
                                  KEY `FK4uevhho17af6tperwr6tdh1x9` (`customer`),
                                  KEY `FKc2mndcwtfolvu2tu5a9g9k8rc` (`manage_by`),
                                  KEY `purchase_order_contract_id_fk` (`contract_id`),
                                  CONSTRAINT `FK264mq9l1u99cn67a1dypssv6d` FOREIGN KEY (`quotation_id`) REFERENCES `quotation` (`id`),
                                  CONSTRAINT `FK27nndvuml99qivo7igtqvutrh` FOREIGN KEY (`approved_by`) REFERENCES `user` (`id`),
                                  CONSTRAINT `FK4uevhho17af6tperwr6tdh1x9` FOREIGN KEY (`customer`) REFERENCES `user` (`id`),
                                  CONSTRAINT `FKc2mndcwtfolvu2tu5a9g9k8rc` FOREIGN KEY (`manage_by`) REFERENCES `user` (`id`),
                                  CONSTRAINT `FKotls6dyde2woaf35rmskleo1y` FOREIGN KEY (`solution`) REFERENCES `solution` (`id`),
                                  CONSTRAINT `purchase_order_contract_id_fk` FOREIGN KEY (`contract_id`) REFERENCES `contract` (`id`),
                                  CONSTRAINT `purchase_order_user_id_fk` FOREIGN KEY (`approved_by`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `purchase_order`
--

LOCK TABLES `purchase_order` WRITE;
/*!40000 ALTER TABLE `purchase_order` DISABLE KEYS */;
INSERT INTO `purchase_order` VALUES (1,'2025-03-02 17:38:36.000000','2025-03-16 15:36:34.766043','APPROVED',1,3,NULL,NULL,NULL,'HD0001'),(2,'2025-03-06 12:55:24.000000','2025-03-19 15:43:08.393002','APPROVED',5,2,NULL,NULL,NULL,'HD0002'),(3,'2025-03-12 16:53:20.000000','2025-03-30 22:08:56.333078','APPROVED',7,2,5,2,6,'HD0003'),(4,'2025-03-18 16:01:25.435987','2025-03-18 16:01:25.435987','NOT_APPROVED',4,NULL,NULL,NULL,NULL,'HD0005'),(5,'2025-03-18 16:17:43.149853','2025-03-29 14:12:37.273161','APPROVED',2,2,5,2,NULL,'HD0004'),(6,'2025-03-29 12:56:41.030747','2025-03-29 22:08:57.354301','WAIT_FOR_APPROVAL',8,2,2,2,NULL,'HD0006');
/*!40000 ALTER TABLE `purchase_order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `purchase_order_detail`
--

DROP TABLE IF EXISTS `purchase_order_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `purchase_order_detail` (
                                         `id` bigint NOT NULL AUTO_INCREMENT,
                                         `create_at` datetime(6) DEFAULT NULL,
                                         `update_at` datetime(6) DEFAULT NULL,
                                         `quantity` int DEFAULT NULL,
                                         `unit_price` decimal(38,2) DEFAULT NULL,
                                         `brand_id` bigint DEFAULT NULL,
                                         `category_id` bigint DEFAULT NULL,
                                         `product_id` bigint DEFAULT NULL,
                                         `purchase_order_id` bigint NOT NULL,
                                         `note_color` varchar(255) DEFAULT NULL,
                                         `total_price` decimal(38,2) DEFAULT NULL,
                                         PRIMARY KEY (`id`),
                                         KEY `FK4u0noqd93evlla1n1hoaeknlf` (`brand_id`),
                                         KEY `FKh5lry2ti0ki18u8big0aqvjqg` (`category_id`),
                                         KEY `FKcpv7slf2i15y8e9utfwd7e1vk` (`product_id`),
                                         KEY `FKi6xlnsg9o9ght6xcwl51ooa4k` (`purchase_order_id`),
                                         CONSTRAINT `FK4u0noqd93evlla1n1hoaeknlf` FOREIGN KEY (`brand_id`) REFERENCES `brand` (`id`),
                                         CONSTRAINT `FKcpv7slf2i15y8e9utfwd7e1vk` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`),
                                         CONSTRAINT `FKh5lry2ti0ki18u8big0aqvjqg` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`),
                                         CONSTRAINT `FKi6xlnsg9o9ght6xcwl51ooa4k` FOREIGN KEY (`purchase_order_id`) REFERENCES `purchase_order` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `purchase_order_detail`
--

LOCK TABLES `purchase_order_detail` WRITE;
/*!40000 ALTER TABLE `purchase_order_detail` DISABLE KEYS */;
INSERT INTO `purchase_order_detail` VALUES (1,'2025-03-18 16:01:25.464366','2025-03-18 16:01:25.464366',122,21000.00,1,2,1,4,'abc',2562000.00),(2,'2025-03-18 16:17:43.160934','2025-03-18 16:17:43.160934',123,20000.00,1,1,1,5,'Red',2460000.00),(3,'2025-03-18 16:17:43.166446','2025-03-18 16:17:43.166446',111,38000.00,3,4,2,5,'Blue',4218000.00),(4,'2025-03-19 15:59:12.000000','2025-03-19 15:59:13.000000',122,23000.00,1,2,1,3,'abc',2806000.00),(5,'2025-03-29 12:56:41.053386','2025-03-29 12:56:41.053386',100,21000.00,1,2,1,6,'#df706f',2100000.00),(6,'2025-03-29 12:56:41.061102','2025-03-29 12:56:41.061102',121,12000.00,2,3,3,6,'#7a7a7a',1452000.00);
/*!40000 ALTER TABLE `purchase_order_detail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `purchase_order_price`
--

DROP TABLE IF EXISTS `purchase_order_price`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `purchase_order_price` (
                                        `id` bigint NOT NULL AUTO_INCREMENT,
                                        `detail_price` decimal(10,2) NOT NULL,
                                        `quotation_id` bigint NOT NULL,
                                        PRIMARY KEY (`id`),
                                        KEY `FKdny9sbtrn96khb2tl72cyikst` (`quotation_id`),
                                        CONSTRAINT `FKdny9sbtrn96khb2tl72cyikst` FOREIGN KEY (`quotation_id`) REFERENCES `quotation` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `purchase_order_price`
--

LOCK TABLES `purchase_order_price` WRITE;
/*!40000 ALTER TABLE `purchase_order_price` DISABLE KEYS */;
/*!40000 ALTER TABLE `purchase_order_price` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `quotation`
--

DROP TABLE IF EXISTS `quotation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `quotation` (
                             `id` bigint NOT NULL AUTO_INCREMENT,
                             `create_at` datetime(6) DEFAULT NULL,
                             `update_at` datetime(6) DEFAULT NULL,
                             `is_canceled` bit(1) DEFAULT b'0',
                             `rfq_id` bigint NOT NULL,
                             `is_accepted` enum('APPROVED','CANCELED','DRAFT','NOT_APPROVED','WAIT_FOR_APPROVAL') DEFAULT NULL,
                             `created_by` bigint DEFAULT NULL,
                             PRIMARY KEY (`id`),
                             KEY `FK4dm0ceydduelrbvdsr59qkami` (`rfq_id`),
                             KEY `FKh8bnkcd7bplti5a007ffudfew` (`created_by`),
                             CONSTRAINT `FK4dm0ceydduelrbvdsr59qkami` FOREIGN KEY (`rfq_id`) REFERENCES `rfq` (`id`),
                             CONSTRAINT `FKh8bnkcd7bplti5a007ffudfew` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `quotation`
--

LOCK TABLES `quotation` WRITE;
/*!40000 ALTER TABLE `quotation` DISABLE KEYS */;
INSERT INTO `quotation` VALUES (1,'2025-02-28 16:24:31.000000','2025-02-28 16:24:32.000000',_binary '\0',1,'NOT_APPROVED',NULL),(2,'2025-03-05 12:01:59.199267','2025-03-18 16:17:43.143303',_binary '\0',2,'APPROVED',NULL),(3,'2025-03-05 12:03:12.382587','2025-03-05 12:03:12.382587',_binary '\0',3,'NOT_APPROVED',NULL),(4,'2025-03-05 15:01:30.827963','2025-03-05 15:01:30.827963',_binary '\0',5,'NOT_APPROVED',NULL),(5,'2025-03-05 15:16:56.398550','2025-03-05 15:16:56.398550',_binary '\0',6,'NOT_APPROVED',NULL),(7,'2025-03-09 16:43:37.410436','2025-03-18 16:01:25.414572',_binary '\0',7,'APPROVED',NULL),(8,'2025-03-29 12:26:38.140116','2025-03-29 12:56:41.022240',_binary '\0',8,'APPROVED',2);
/*!40000 ALTER TABLE `quotation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `quotation_detail`
--

DROP TABLE IF EXISTS `quotation_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `quotation_detail` (
                                    `id` bigint NOT NULL AUTO_INCREMENT,
                                    `note_color` varchar(255) DEFAULT NULL,
                                    `price` decimal(10,2) NOT NULL,
                                    `brand_id` bigint NOT NULL,
                                    `category_id` bigint NOT NULL,
                                    `product_id` bigint NOT NULL,
                                    `quotation_id` bigint NOT NULL,
                                    `quantity` int NOT NULL,
                                    `unit_price` decimal(38,2) DEFAULT NULL,
                                    PRIMARY KEY (`id`),
                                    KEY `FK7388q9ghj5vbhlcklyj99be4g` (`brand_id`),
                                    KEY `FKgi67vobcj3ysstoxltq2sc3fi` (`category_id`),
                                    KEY `FK9savr9mtq0o95el3sugqjoatm` (`product_id`),
                                    KEY `FK5uhuhl4i0dvr1lqwn3cv35wl` (`quotation_id`),
                                    CONSTRAINT `FK5uhuhl4i0dvr1lqwn3cv35wl` FOREIGN KEY (`quotation_id`) REFERENCES `quotation` (`id`),
                                    CONSTRAINT `FK7388q9ghj5vbhlcklyj99be4g` FOREIGN KEY (`brand_id`) REFERENCES `brand` (`id`),
                                    CONSTRAINT `FK9savr9mtq0o95el3sugqjoatm` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`),
                                    CONSTRAINT `FKgi67vobcj3ysstoxltq2sc3fi` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `quotation_detail`
--

LOCK TABLES `quotation_detail` WRITE;
/*!40000 ALTER TABLE `quotation_detail` DISABLE KEYS */;
/*!40000 ALTER TABLE `quotation_detail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rfq`
--

DROP TABLE IF EXISTS `rfq`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rfq` (
                       `id` bigint NOT NULL AUTO_INCREMENT,
                       `create_at` datetime(6) DEFAULT NULL,
                       `update_at` datetime(6) DEFAULT NULL,
                       `expect_delivery_date` date NOT NULL,
                       `is_approved` enum('NOT_SENT','SENT') DEFAULT NULL,
                       `approved_by` bigint DEFAULT NULL,
                       `create_by` bigint NOT NULL,
                       `is_sent` enum('APPROVED','CANCELED','NOT_APPROVED') DEFAULT NULL,
                       PRIMARY KEY (`id`),
                       KEY `FKs2ir7v8lqbrcljtobq0vt4934` (`approved_by`),
                       KEY `FK99hljyxkynwwiewjcs5bywq7n` (`create_by`),
                       CONSTRAINT `FK99hljyxkynwwiewjcs5bywq7n` FOREIGN KEY (`create_by`) REFERENCES `user` (`id`),
                       CONSTRAINT `FKs2ir7v8lqbrcljtobq0vt4934` FOREIGN KEY (`approved_by`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rfq`
--

LOCK TABLES `rfq` WRITE;
/*!40000 ALTER TABLE `rfq` DISABLE KEYS */;
INSERT INTO `rfq` VALUES (1,'2025-02-28 16:02:17.000000','2025-03-05 11:19:45.404573','2025-03-01','SENT',2,3,'APPROVED'),(2,'2025-03-05 11:09:00.366747','2025-03-05 11:10:16.616170','2025-03-15','SENT',2,2,'APPROVED'),(3,'2025-03-05 11:52:55.052471','2025-03-05 12:00:07.764154','2025-03-15','SENT',2,5,'APPROVED'),(4,'2025-03-05 14:27:38.760116','2025-03-05 14:28:19.802694','2025-03-15','SENT',2,5,'APPROVED'),(5,'2025-03-05 14:59:13.445241','2025-03-05 14:59:53.812790','2025-03-15','SENT',2,5,'APPROVED'),(6,'2025-03-05 15:15:59.670342','2025-03-05 15:16:24.299824','2025-03-15','SENT',2,5,'APPROVED'),(7,'2025-03-09 16:41:49.213082','2025-03-09 16:42:30.260001','2025-03-13','SENT',2,2,'APPROVED'),(8,'2025-03-29 12:21:46.697594','2025-03-29 12:22:40.820596','2025-04-10','SENT',2,2,'APPROVED');
/*!40000 ALTER TABLE `rfq` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rfq_detail`
--

DROP TABLE IF EXISTS `rfq_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rfq_detail` (
                              `id` bigint NOT NULL AUTO_INCREMENT,
                              `create_at` datetime(6) DEFAULT NULL,
                              `update_at` datetime(6) DEFAULT NULL,
                              `note_color` varchar(100) DEFAULT NULL,
                              `quantity` int NOT NULL,
                              `brand_id` bigint NOT NULL,
                              `cate_id` bigint NOT NULL,
                              `product_id` bigint NOT NULL,
                              `rfq_id` bigint NOT NULL,
                              PRIMARY KEY (`id`),
                              KEY `FKdmv8nxky45qrett1bvuksopnu` (`brand_id`),
                              KEY `FK7qsucvvdg64chh1djhe4vhrko` (`cate_id`),
                              KEY `FKdptipdipa729pexpsy815qnnf` (`product_id`),
                              KEY `FKdoq6vx8ekmc2girnjdr1jgnja` (`rfq_id`),
                              CONSTRAINT `FK7qsucvvdg64chh1djhe4vhrko` FOREIGN KEY (`cate_id`) REFERENCES `category` (`id`),
                              CONSTRAINT `FKdmv8nxky45qrett1bvuksopnu` FOREIGN KEY (`brand_id`) REFERENCES `brand` (`id`),
                              CONSTRAINT `FKdoq6vx8ekmc2girnjdr1jgnja` FOREIGN KEY (`rfq_id`) REFERENCES `rfq` (`id`),
                              CONSTRAINT `FKdptipdipa729pexpsy815qnnf` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rfq_detail`
--

LOCK TABLES `rfq_detail` WRITE;
/*!40000 ALTER TABLE `rfq_detail` DISABLE KEYS */;
INSERT INTO `rfq_detail` VALUES (1,'2025-02-28 16:22:55.000000','2025-02-28 16:22:56.000000','Viva Violet',100,1,1,1,1),(2,'2025-02-28 16:23:45.000000','2025-02-28 16:23:46.000000','Fuzzy',150,1,2,1,1),(3,'2025-03-03 15:53:32.000000','2025-03-03 15:53:33.000000','Blue',100,3,4,2,1),(4,'2025-03-03 15:56:22.000000','2025-03-03 15:56:23.000000','tê',120,2,5,3,1),(5,'2025-03-05 11:09:00.375611','2025-03-05 11:09:00.375611','Red',123,1,1,1,2),(6,'2025-03-05 11:09:00.379134','2025-03-05 11:09:00.379134','Blue',111,3,4,2,2),(7,'2025-03-05 11:52:55.058934','2025-03-05 11:52:55.058934','Red',123,1,1,1,3),(8,'2025-03-05 11:52:55.063782','2025-03-05 11:52:55.063782','Blue',111,2,3,3,3),(9,'2025-03-05 14:27:38.784917','2025-03-05 14:27:38.784917','abc',122,1,1,1,4),(10,'2025-03-05 14:27:38.787771','2025-03-05 14:27:38.787771','amu',111,3,4,2,4),(11,'2025-03-05 14:59:13.477428','2025-03-05 14:59:13.477428','abc',122,1,1,1,5),(12,'2025-03-05 14:59:13.481099','2025-03-05 14:59:13.481099','amu',111,3,4,2,5),(13,'2025-03-05 15:15:59.697813','2025-03-05 15:15:59.697813','abc',122,1,1,1,6),(14,'2025-03-05 15:15:59.700946','2025-03-05 15:15:59.700946','amu',111,3,4,2,6),(15,'2025-03-09 16:41:49.236124','2025-03-09 16:41:49.236124','abc',122,1,2,1,7),(16,'2025-03-29 12:21:46.733306','2025-03-29 12:21:46.733306','#df706f',100,1,2,1,8),(17,'2025-03-29 12:21:46.737302','2025-03-29 12:21:46.737302','#7a7a7a',121,2,3,3,8);
/*!40000 ALTER TABLE `rfq_detail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role` (
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `create_at` datetime(6) DEFAULT NULL,
                        `update_at` datetime(6) DEFAULT NULL,
                        `description` varchar(255) DEFAULT NULL,
                        `name` varchar(50) NOT NULL,
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `UK8sewwnpamngi6b1dwaa88askk` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` VALUES (1,'2025-02-24 11:49:07.989725','2025-02-24 11:49:07.989725','Admin role','ADMIN'),(2,'2025-02-24 11:49:08.043546','2025-02-24 11:49:08.043546','User role','CUSTOMER'),(3,'2025-02-28 15:53:46.000000','2025-02-28 15:53:48.000000','Sale staff role','SALE_STAFF'),(4,'2025-02-28 23:20:18.000000','2025-02-28 23:20:19.000000','Technical','TECHNICAL'),(5,'2025-03-05 22:24:50.000000','2025-03-05 22:24:51.000000','Sale Manager','SALE_MANAGER');
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `solution`
--

DROP TABLE IF EXISTS `solution`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `solution` (
                            `id` bigint NOT NULL AUTO_INCREMENT,
                            `create_at` datetime(6) DEFAULT NULL,
                            `update_at` datetime(6) DEFAULT NULL,
                            `actual_delivery_date` date NOT NULL,
                            `description` tinytext,
                            `reason` varchar(255) DEFAULT NULL,
                            `create_by` bigint NOT NULL,
                            `rfq_id` bigint NOT NULL,
                            `is_sent` enum('NOT_SENT','SENT') DEFAULT NULL,
                            PRIMARY KEY (`id`),
                            KEY `FK75cxtcljre4s0wg1756smcay5` (`create_by`),
                            KEY `FK3fludxudjgawngmahkeigyj8f` (`rfq_id`),
                            CONSTRAINT `FK3fludxudjgawngmahkeigyj8f` FOREIGN KEY (`rfq_id`) REFERENCES `rfq` (`id`),
                            CONSTRAINT `FK75cxtcljre4s0wg1756smcay5` FOREIGN KEY (`create_by`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `solution`
--

LOCK TABLES `solution` WRITE;
/*!40000 ALTER TABLE `solution` DISABLE KEYS */;
INSERT INTO `solution` VALUES (1,'2025-02-28 23:18:41.000000','2025-03-09 15:06:22.994955','2025-03-03','Loi roi','Dang bi loi may',4,1,'SENT'),(2,'2025-03-05 12:01:17.832739','2025-03-05 12:01:59.187706','2025-03-15','asdf','ok',2,2,'SENT'),(3,'2025-03-05 12:03:07.810587','2025-03-05 12:03:12.371999','2025-03-18','Có thể lùi lại 3 ngày','Không kịp nhập chỉ',2,3,'SENT'),(4,'2025-03-05 15:01:09.416723','2025-03-05 15:01:30.813808','2025-03-22','asdf','Không kịp nhập chỉ',2,5,'SENT'),(5,'2025-03-05 15:16:52.624976','2025-03-05 15:16:56.383060','2025-03-22','aaaa','Không kịp nhập chỉ',2,6,'SENT'),(6,'2025-03-09 16:43:03.938052','2025-03-09 16:43:37.397376','2025-03-15','sssss','Không kịp nhập chỉ',2,7,'SENT'),(7,'2025-03-29 12:26:33.811567','2025-03-29 12:26:38.128419','2025-04-12','abc','Không kịp nhập chỉ',2,8,'SENT');
/*!40000 ALTER TABLE `solution` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `thread`
--

DROP TABLE IF EXISTS `thread`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `thread` (
                          `id` bigint NOT NULL AUTO_INCREMENT,
                          `create_at` datetime(6) DEFAULT NULL,
                          `update_at` datetime(6) DEFAULT NULL,
                          `convert_rate` decimal(10,4) NOT NULL,
                          `description` tinytext,
                          `name` varchar(100) NOT NULL,
                          `process_id` bigint NOT NULL,
                          PRIMARY KEY (`id`),
                          UNIQUE KEY `UK9x1v43vsxx9fm96q8u8wxgm18` (`process_id`),
                          CONSTRAINT `FKprd1gta209jr8hmmae3ou3m32` FOREIGN KEY (`process_id`) REFERENCES `winding_process` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `thread`
--

LOCK TABLES `thread` WRITE;
/*!40000 ALTER TABLE `thread` DISABLE KEYS */;
INSERT INTO `thread` VALUES (1,'2025-03-23 10:49:33.000000','2025-03-23 10:49:34.000000',0.1540,NULL,'40/2BGK',1);
/*!40000 ALTER TABLE `thread` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `create_at` datetime(6) DEFAULT NULL,
                        `update_at` datetime(6) DEFAULT NULL,
                        `avatar` varchar(255) NOT NULL,
                        `email` varchar(255) NOT NULL,
                        `is_active` tinyint(1) DEFAULT '1',
                        `is_verified` tinyint(1) DEFAULT '0',
                        `password` varchar(128) DEFAULT NULL,
                        `phone_number` varchar(20) DEFAULT NULL,
                        `name` varchar(100) NOT NULL,
                        `role_id` bigint NOT NULL,
                        PRIMARY KEY (`id`),
                        KEY `FKn82ha3ccdebhokx3a8fgdqeyy` (`role_id`),
                        CONSTRAINT `FKn82ha3ccdebhokx3a8fgdqeyy` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'2025-02-24 11:49:08.141797','2025-02-24 15:36:32.854737','default-avatar.png','admin@example.com',1,1,'$2a$10$s43uCJZj3x5t5v7Z1paVluK0w5hCyd97Uo6aBX0eMptQKvzJFk/S.','+84123456789','admin',1),(2,'2025-02-24 11:50:20.581891','2025-02-24 11:50:20.581891','ff2d3f52-73d2-42d7-baf9-13cfaea52866.jpg','nhatminh21072003@gmail.com',1,0,NULL,'0912123435','Nhật Minh Đỗ',5),(3,'2025-02-24 15:22:58.009394','2025-02-24 15:23:29.518252','default_avatar.png','longaaccbb1605@gmail.com',1,1,'$2a$10$itRYlCEtGkxuZsC28RSjEecBI1PWDNq9M1Ki16qFNNpOV19XVyOU.','0912345678','minh',5),(4,'2025-02-28 23:20:54.000000','2025-02-28 23:20:55.000000','default_avatar.png','abc@gmail.com',1,1,'Abcd@1234','0912345677','Nguyen Van Tech',4),(5,'2025-03-05 11:52:21.497697','2025-03-05 11:52:21.497697','007bcb1b-9e0e-43b2-a868-74b7989e31db.jpg','adudarkwa2107@gmail.com',1,1,NULL,NULL,'Darkwa Adu',2);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `winding_machine`
--

DROP TABLE IF EXISTS `winding_machine`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `winding_machine` (
                                   `id` bigint NOT NULL AUTO_INCREMENT,
                                   `create_at` datetime(6) DEFAULT NULL,
                                   `update_at` datetime(6) DEFAULT NULL,
                                   `capacity` int NOT NULL,
                                   `description` longtext,
                                   `motor_speed` int NOT NULL,
                                   `spindle` int NOT NULL,
                                   PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `winding_machine`
--

LOCK TABLES `winding_machine` WRITE;
/*!40000 ALTER TABLE `winding_machine` DISABLE KEYS */;
/*!40000 ALTER TABLE `winding_machine` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `winding_process`
--

DROP TABLE IF EXISTS `winding_process`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `winding_process` (
                                   `id` bigint NOT NULL AUTO_INCREMENT,
                                   `create_at` datetime(6) DEFAULT NULL,
                                   `update_at` datetime(6) DEFAULT NULL,
                                   `glue_level` bit(1) NOT NULL,
                                   `machine_speed` int NOT NULL,
                                   `tocdo_banhvot` decimal(10,1) NOT NULL,
                                   PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `winding_process`
--

LOCK TABLES `winding_process` WRITE;
/*!40000 ALTER TABLE `winding_process` DISABLE KEYS */;
INSERT INTO `winding_process` VALUES (1,'2025-03-23 10:50:24.000000','2025-03-23 10:50:25.000000',_binary '',600,4.5);
/*!40000 ALTER TABLE `winding_process` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `winding_stage`
--

DROP TABLE IF EXISTS `winding_stage`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `winding_stage` (
                                 `id` bigint NOT NULL AUTO_INCREMENT,
                                 `create_at` datetime(6) DEFAULT NULL,
                                 `update_at` datetime(6) DEFAULT NULL,
                                 `actual_output` int DEFAULT NULL,
                                 `complete_at` datetime(6) DEFAULT NULL,
                                 `deadline` date NOT NULL,
                                 `received_cone_at` datetime(6) NOT NULL,
                                 `start_at` datetime(6) DEFAULT NULL,
                                 `test_status` enum('NOT_STARTED','TESTED','TESTING') NOT NULL,
                                 `work_status` enum('FINISHED','IN_PROGRESS','NOT_STARTED') NOT NULL,
                                 `checked_by` bigint DEFAULT NULL,
                                 `dye_stage_id` bigint NOT NULL,
                                 `leader_id` bigint NOT NULL,
                                 `winding_machine_id` bigint NOT NULL,
                                 `work_order_detail_id` bigint NOT NULL,
                                 `winding_photo` varchar(255) DEFAULT NULL,
                                 PRIMARY KEY (`id`),
                                 UNIQUE KEY `UKaeh8bve5ogy1md8bhpawf0fy2` (`dye_stage_id`),
                                 UNIQUE KEY `UKc3xuytqgneesyb1i6mlmlecnt` (`winding_machine_id`),
                                 UNIQUE KEY `UKgbdpxb5kmn5bmo17mv6e5kmje` (`work_order_detail_id`),
                                 KEY `FK3ovvq82clhmc3iro3xhm1jha0` (`checked_by`),
                                 KEY `FKagpwwbxj5k4u6apd783aoiveu` (`leader_id`),
                                 CONSTRAINT `FK3ovvq82clhmc3iro3xhm1jha0` FOREIGN KEY (`checked_by`) REFERENCES `user` (`id`),
                                 CONSTRAINT `FK7opeay1nsrqsr9td1gpimo0w8` FOREIGN KEY (`winding_machine_id`) REFERENCES `winding_machine` (`id`),
                                 CONSTRAINT `FKagpwwbxj5k4u6apd783aoiveu` FOREIGN KEY (`leader_id`) REFERENCES `user` (`id`),
                                 CONSTRAINT `FKina693xr9xcj7uph8ujrm15kp` FOREIGN KEY (`work_order_detail_id`) REFERENCES `work_order_detail` (`id`),
                                 CONSTRAINT `FKlhca55b31pofyuarfuy43r5kx` FOREIGN KEY (`dye_stage_id`) REFERENCES `dye_stage` (`id`),
                                 CONSTRAINT `winding_stage_chk_1` CHECK ((`test_status` between 0 and 2)),
                                 CONSTRAINT `winding_stage_chk_2` CHECK ((`work_status` between 0 and 2))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `winding_stage`
--

LOCK TABLES `winding_stage` WRITE;
/*!40000 ALTER TABLE `winding_stage` DISABLE KEYS */;
/*!40000 ALTER TABLE `winding_stage` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `work_order`
--

DROP TABLE IF EXISTS `work_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `work_order` (
                              `id` bigint NOT NULL AUTO_INCREMENT,
                              `create_at` datetime(6) DEFAULT NULL,
                              `update_at` datetime(6) DEFAULT NULL,
                              `deadline` date NOT NULL,
                              `send_status` enum('NOT_SENT','SENT') NOT NULL,
                              `status` enum('APPROVED','CANCELED','DRAFT','NOT_APPROVED','WAIT_FOR_APPROVAL') NOT NULL,
                              `approved_by` bigint DEFAULT NULL,
                              `created_by` bigint NOT NULL,
                              `production_order_id` bigint NOT NULL,
                              `is_production` enum('FINISHED','IN_PROGRESS','NOT_STARTED') DEFAULT NULL,
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `UKawtcfscyku3ckmmy5d6lfu6ji` (`production_order_id`),
                              KEY `FK8kpmqktc6jeh6fi5xltfcvbj7` (`approved_by`),
                              KEY `FKeo96hy4s49hcxisr6ra04hmi2` (`created_by`),
                              CONSTRAINT `FK26g0d1vug3mav2jx6nq1otn0r` FOREIGN KEY (`production_order_id`) REFERENCES `production_order` (`id`),
                              CONSTRAINT `FK8kpmqktc6jeh6fi5xltfcvbj7` FOREIGN KEY (`approved_by`) REFERENCES `user` (`id`),
                              CONSTRAINT `FKeo96hy4s49hcxisr6ra04hmi2` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`),
                              CONSTRAINT `work_order_chk_1` CHECK ((`send_status` between 0 and 1)),
                              CONSTRAINT `work_order_chk_2` CHECK ((`status` between 0 and 3))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `work_order`
--

LOCK TABLES `work_order` WRITE;
/*!40000 ALTER TABLE `work_order` DISABLE KEYS */;
/*!40000 ALTER TABLE `work_order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `work_order_detail`
--

DROP TABLE IF EXISTS `work_order_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `work_order_detail` (
                                     `id` bigint NOT NULL AUTO_INCREMENT,
                                     `create_at` datetime(6) DEFAULT NULL,
                                     `update_at` datetime(6) DEFAULT NULL,
                                     `complete_at` datetime(6) DEFAULT NULL,
                                     `start_at` datetime(6) DEFAULT NULL,
                                     `work_order_id` bigint NOT NULL,
                                     `work_status` enum('FINISHED','IN_PROGRESS','NOT_STARTED') NOT NULL,
                                     `production_order_detail_id` bigint DEFAULT NULL,
                                     `purchase_order_detail_id` bigint DEFAULT NULL,
                                     PRIMARY KEY (`id`),
                                     UNIQUE KEY `UK1kkjbamh3tjae33wqijlri93i` (`production_order_detail_id`),
                                     UNIQUE KEY `UKsr6obpsuybgryit0c018iqobx` (`purchase_order_detail_id`),
                                     KEY `FKm0ia1imutoolp7eoxcek1rub5` (`work_order_id`),
                                     CONSTRAINT `FKir66dge9dt13tbgti110t4t7o` FOREIGN KEY (`purchase_order_detail_id`) REFERENCES `purchase_order_detail` (`id`),
                                     CONSTRAINT `FKm0ia1imutoolp7eoxcek1rub5` FOREIGN KEY (`work_order_id`) REFERENCES `work_order` (`id`),
                                     CONSTRAINT `FKxjtw3nefq9wab1dovu8tby7n` FOREIGN KEY (`production_order_detail_id`) REFERENCES `production_order_detail` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `work_order_detail`
--

LOCK TABLES `work_order_detail` WRITE;
/*!40000 ALTER TABLE `work_order_detail` DISABLE KEYS */;
/*!40000 ALTER TABLE `work_order_detail` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-04-04 11:11:00
