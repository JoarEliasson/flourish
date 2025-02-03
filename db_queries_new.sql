USE db_mysql_g1_da489a;

DROP TABLE IF EXISTS `Plants`;
DROP TABLE IF EXISTS `Users`;
DROP TABLE IF EXISTS `Species`;

-- Species table
CREATE TABLE `Species` (
                           `id` INT NOT NULL,
                           `scientific_name` VARCHAR(255) NOT NULL,
                           `genus` VARCHAR(255) NOT NULL,
                           `family` VARCHAR(255) NOT NULL,
                           `common_name` VARCHAR(255) DEFAULT NULL,
                           `image_url` VARCHAR(500) DEFAULT NULL,
                           `light` VARCHAR(255) DEFAULT NULL,
                           `url_wikipedia_en` VARCHAR(500) DEFAULT NULL,
                           `water_frequency` INT DEFAULT NULL,
                           PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Users table
CREATE TABLE `Users` (
                         `id` INT NOT NULL AUTO_INCREMENT,
                         `username` VARCHAR(25) NOT NULL,
                         `email` VARCHAR(50) DEFAULT NULL,
                         `password` VARCHAR(100) NOT NULL,
                         `notification_activated` BIT NOT NULL DEFAULT b'0',
                         `fun_facts_activated` BIT NOT NULL DEFAULT b'0',
                         `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         PRIMARY KEY (`id`),
                         UNIQUE KEY `UQ_Users_username` (`username`),
                         UNIQUE KEY `UQ_Users_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Plants table
CREATE TABLE `Plants` (
                          `id` INT NOT NULL AUTO_INCREMENT,
                          `user_id` INT NOT NULL,
                          `species_id` INT NOT NULL,
                          `nickname` VARCHAR(255) NOT NULL,
                          `last_watered` DATE NOT NULL,
                          `image_url` VARCHAR(500) DEFAULT NULL,
                          `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          PRIMARY KEY (`id`),
                          UNIQUE KEY `UQ_Plants_user_nickname` (`user_id`, `nickname`),
                          KEY `IX_Plants_user_id` (`user_id`),
                          KEY `IX_Plants_species_id` (`species_id`),
                          CONSTRAINT `FK_Plants_Users` FOREIGN KEY (`user_id`) REFERENCES `Users` (`id`),
                          CONSTRAINT `FK_Plants_Species` FOREIGN KEY (`species_id`) REFERENCES `Species` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
