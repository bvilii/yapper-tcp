-- Yapper database schema
-- Creates every table in dependency order: a table only references tables created above it.
-- Run it on an empty database (the MySQL container in Docker does this automatically on first start).

CREATE TABLE `users` (
                         `id` bigint NOT NULL AUTO_INCREMENT,
                         `username` varchar(32) NOT NULL,
                         `email` varchar(255) NOT NULL,
                         `password_hash` varchar(100) NOT NULL,
                         `created_at` datetime(3) NOT NULL,
                         PRIMARY KEY (`id`),
                         UNIQUE KEY `username` (`username`),
                         UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `rooms` (
                         `id` bigint NOT NULL AUTO_INCREMENT,
                         `name` varchar(64) DEFAULT NULL,
                         `created_by` bigint DEFAULT NULL,
                         `created_at` datetime(3) NOT NULL,
                         `type` varchar(16) NOT NULL DEFAULT 'GROUP',
                         `dm_key` varchar(41) DEFAULT NULL,
                         PRIMARY KEY (`id`),
                         UNIQUE KEY `uq_rooms_dm_key` (`dm_key`),
                         KEY `fk_rooms_created_by` (`created_by`),
                         CONSTRAINT `fk_rooms_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `room_members` (
                                `room_id` bigint NOT NULL,
                                `user_id` bigint NOT NULL,
                                `joined_at` datetime(3) NOT NULL,
                                PRIMARY KEY (`room_id`,`user_id`),
                                KEY `user_id` (`user_id`),
                                CONSTRAINT `room_members_ibfk_1` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`) ON DELETE CASCADE,
                                CONSTRAINT `room_members_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `messages` (
                            `id` bigint NOT NULL AUTO_INCREMENT,
                            `room_id` bigint NOT NULL,
                            `sender_id` bigint DEFAULT NULL,
                            `content` varchar(2000) NOT NULL,
                            `created_at` datetime(3) NOT NULL,
                            PRIMARY KEY (`id`),
                            KEY `idx_messages_room_id` (`room_id`,`id`),
                            KEY `fk_messages_sender` (`sender_id`),
                            CONSTRAINT `fk_messages_room` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`) ON DELETE CASCADE,
                            CONSTRAINT `fk_messages_sender` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;