CREATE TABLE `user` (
                        `user_id`        BIGINT NOT NULL AUTO_INCREMENT,
                        `user_public_id` VARCHAR(36) NOT NULL,
                        `user_nickname`  VARCHAR(15)  NOT NULL,
                        `user_mbti`      VARCHAR(4)  NOT NULL,
                        PRIMARY KEY (`user_id`),
                        UNIQUE KEY `uk_user_public_id` (`user_public_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 여행(1)  : user(1) - travel(N)
CREATE TABLE `travel` (
                          `travel_id`       BIGINT NOT NULL AUTO_INCREMENT,
                          `user_id`         BIGINT NOT NULL,
                          `travel_dest`     VARCHAR(100) NOT NULL,
                          `travel_start`    DATE NOT NULL,
                          `travel_end`      DATE NOT NULL,
                          `travel_type`     VARCHAR(20) NOT NULL,
                          `companion_type`  VARCHAR(20) NOT NULL,
                          `travel_theme1`   VARCHAR(20) NOT NULL,
                          `travel_theme2`   VARCHAR(20) NOT NULL,
                          `travel_theme3`   VARCHAR(20) NOT NULL,
                          PRIMARY KEY (`travel_id`),
                          KEY `idx_travel_user_id` (`user_id`),
                          CONSTRAINT `fk_travel_user`
                              FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`)
                                  ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 여행일(여행의 i일차)
CREATE TABLE `travel_day` (
                              `travel_day_id`   BIGINT NOT NULL AUTO_INCREMENT,
                              `travel_id`       BIGINT NOT NULL,
                              `travel_day_index` INT    NOT NULL,  -- 1,2,3... 일차
                              PRIMARY KEY (`travel_day_id`),
                              UNIQUE KEY `uk_travel_day_unique` (`travel_id`, `travel_day_index`),
                              KEY `idx_travel_day_travel_id` (`travel_id`),
                              CONSTRAINT `fk_travel_day_travel`
                                  FOREIGN KEY (`travel_id`) REFERENCES `travel`(`travel_id`)
                                      ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 여행 코스(해당 일차 내 순서)
CREATE TABLE `travel_course` (
                                 `travel_course_id` BIGINT NOT NULL AUTO_INCREMENT,
                                 `travel_day_id`    BIGINT NOT NULL,
                                 `location_name`    VARCHAR(100) NOT NULL,
                                 `location_lat`     DOUBLE NULL,
                                 `location_lng`     DOUBLE NULL,
                                 `location_note`    VARCHAR(100) NULL,
                                 `location_theme`   VARCHAR(10) NULL,
                                 `location_time`    INT NULL,         -- 분 단위 등으로 사용
                                 `location_order`   INT NOT NULL,     -- 해당 일차 내 정렬
                                 PRIMARY KEY (`travel_course_id`),
                                 UNIQUE KEY `uk_course_order_in_day` (`travel_day_id`, `location_order`),
                                 KEY `idx_course_day_id` (`travel_day_id`),
                                 CONSTRAINT `fk_course_day`
                                     FOREIGN KEY (`travel_day_id`) REFERENCES `travel_day`(`travel_day_id`)
                                         ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
