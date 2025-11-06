CREATE TABLE `travels` (
        `travel_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
        `user_id` BIGINT UNSIGNED NOT NULL,
        `travel_dest` VARCHAR(100) NOT NULL,
        `travel_start` DATE NOT NULL,
        `travel_end` DATE NOT NULL,
        `travel_type` VARCHAR(20) NOT NULL,
        `travel_companion` VARCHAR(20) NOT NULL,
        `travel_theme1` VARCHAR(20),
        `travel_theme2` VARCHAR(20),
        `travel_theme3` VARCHAR(20),
        PRIMARY KEY (`travel_id`),
        CONSTRAINT fk_travel_user FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;