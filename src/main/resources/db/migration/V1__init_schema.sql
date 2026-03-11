-- yiqipin database migration V1: Initial schema
-- Create tables for yiqipin e-commerce platform

-- User table
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'User ID',
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT 'Username',
    `password` VARCHAR(255) NOT NULL COMMENT 'Password (hashed)',
    `phone` VARCHAR(20) COMMENT 'Phone number',
    `email` VARCHAR(100) COMMENT 'Email',
    `avatar` VARCHAR(500) COMMENT 'Avatar URL',
    `status` TINYINT DEFAULT 1 COMMENT 'Status: 1=active, 0=disabled',
    `role` VARCHAR(20) DEFAULT 'user' COMMENT 'Role: user, admin',
    `growth_points` INT DEFAULT 0 COMMENT 'Growth points',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated time',
    PRIMARY KEY (`id`),
    INDEX `idx_username` (`username`),
    INDEX `idx_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='User table';

-- Address table
CREATE TABLE IF NOT EXISTS `address` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Address ID',
    `user_id` BIGINT NOT NULL COMMENT 'User ID',
    `receiver` VARCHAR(50) NOT NULL COMMENT 'Receiver name',
    `phone` VARCHAR(20) NOT NULL COMMENT 'Receiver phone',
    `province` VARCHAR(50) COMMENT 'Province',
    `city` VARCHAR(50) COMMENT 'City',
    `district` VARCHAR(50) COMMENT 'District',
    `detail` VARCHAR(255) NOT NULL COMMENT 'Detailed address',
    `is_default` TINYINT DEFAULT 0 COMMENT 'Is default address',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated time',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Address table';

-- Product category table
CREATE TABLE IF NOT EXISTS `category` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Category ID',
    `name` VARCHAR(50) NOT NULL COMMENT 'Category name',
    `parent_id` BIGINT DEFAULT 0 COMMENT 'Parent category ID',
    `sort` INT DEFAULT 0 COMMENT 'Sort order',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
    PRIMARY KEY (`id`),
    INDEX `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Product category table';

-- Product table
CREATE TABLE IF NOT EXISTS `product` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Product ID',
    `name` VARCHAR(200) NOT NULL COMMENT 'Product name',
    `category_id` BIGINT COMMENT 'Category ID',
    `price` DECIMAL(10,2) NOT NULL COMMENT 'Price',
    `original_price` DECIMAL(10,2) COMMENT 'Original price',
    `stock` INT DEFAULT 0 COMMENT 'Stock quantity',
    `sales` INT DEFAULT 0 COMMENT 'Sales count',
    `rating` DECIMAL(3,2) DEFAULT 5.00 COMMENT 'Rating score',
    `image` VARCHAR(500) COMMENT 'Main image URL',
    `images` TEXT COMMENT 'Image URLs (JSON array)',
    `description` TEXT COMMENT 'Product description',
    `status` TINYINT DEFAULT 1 COMMENT 'Status: 1=onsale, 0=offsale',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated time',
    PRIMARY KEY (`id`),
    INDEX `idx_category_id` (`category_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Product table';

-- Shopping cart table
CREATE TABLE IF NOT EXISTS `cart_item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Cart item ID',
    `user_id` BIGINT NOT NULL COMMENT 'User ID',
    `product_id` BIGINT NOT NULL COMMENT 'Product ID',
    `quantity` INT NOT NULL DEFAULT 1 COMMENT 'Quantity',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated time',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_product` (`user_id`, `product_id`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Shopping cart table';

-- Order table
CREATE TABLE IF NOT EXISTS `orders` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Order ID',
    `order_no` VARCHAR(50) NOT NULL UNIQUE COMMENT 'Order number',
    `user_id` BIGINT NOT NULL COMMENT 'User ID',
    `total_price` DECIMAL(10,2) NOT NULL COMMENT 'Total price',
    `pay_price` DECIMAL(10,2) COMMENT 'Actual paid price',
    `status` VARCHAR(20) DEFAULT 'pending' COMMENT 'Order status: pending, paid, shipped, completed, cancelled',
    `pay_method` VARCHAR(20) COMMENT 'Payment method',
    `pay_time` DATETIME COMMENT 'Payment time',
    `ship_time` DATETIME COMMENT 'Shipping time',
    `receive_time` DATETIME COMMENT 'Receive time',
    `receiver` VARCHAR(50) COMMENT 'Receiver name',
    `phone` VARCHAR(20) COMMENT 'Receiver phone',
    `address` VARCHAR(255) COMMENT 'Shipping address',
    `remark` VARCHAR(500) COMMENT 'Order remark',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated time',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Order table';

-- Order item table
CREATE TABLE IF NOT EXISTS `order_item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Order item ID',
    `order_id` BIGINT NOT NULL COMMENT 'Order ID',
    `product_id` BIGINT NOT NULL COMMENT 'Product ID',
    `product_name` VARCHAR(200) NOT NULL COMMENT 'Product name (snapshot)',
    `product_image` VARCHAR(500) COMMENT 'Product image (snapshot)',
    `price` DECIMAL(10,2) NOT NULL COMMENT 'Price',
    `quantity` INT NOT NULL COMMENT 'Quantity',
    `total_price` DECIMAL(10,2) NOT NULL COMMENT 'Total price',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
    PRIMARY KEY (`id`),
    INDEX `idx_order_id` (`order_id`),
    INDEX `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Order item table';

-- Marketing campaign table
CREATE TABLE IF NOT EXISTS `campaign` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Campaign ID',
    `name` VARCHAR(100) NOT NULL COMMENT 'Campaign name',
    `type` VARCHAR(50) NOT NULL COMMENT 'Campaign type: coupon, flash_sale, group_buy',
    `status` VARCHAR(20) DEFAULT 'active' COMMENT 'Status: active, scheduled, ended',
    `discount` VARCHAR(50) COMMENT 'Discount description',
    `start_time` DATETIME COMMENT 'Start time',
    `end_time` DATETIME COMMENT 'End time',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
    PRIMARY KEY (`id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Marketing campaign table';

-- Growth record table
CREATE TABLE IF NOT EXISTS `growth_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Record ID',
    `user_id` BIGINT NOT NULL COMMENT 'User ID',
    `type` VARCHAR(50) NOT NULL COMMENT 'Type: order, login, review',
    `points` INT NOT NULL COMMENT 'Points change',
    `description` VARCHAR(255) COMMENT 'Description',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Growth record table';

-- Insert default admin user (password: admin123)
INSERT INTO `user` (`username`, `password`, `phone`, `email`, `role`, `status`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '13800000000', 'admin@yiqipin.com', 'admin', 1);

-- Insert default categories
INSERT INTO `category` (`name`, `parent_id`, `sort`) VALUES
('Electronics', 0, 1),
('Accessories', 0, 2),
('Health & Fitness', 0, 3);

-- Insert sample products
INSERT INTO `product` (`name`, `category_id`, `price`, `original_price`, `stock`, `sales`, `rating`, `image`, `images`, `description`, `status`) VALUES
('Wireless Earbuds', 1, 199.00, 299.00, 120, 985, 4.80, '/static/products/earbuds.jpg', '["/static/products/earbuds.jpg","/static/products/earbuds-2.jpg"]', 'Noise-canceling earbuds with premium sound quality.', 1),
('Mechanical Keyboard', 1, 329.00, 399.00, 80, 463, 4.70, '/static/products/keyboard.jpg', '["/static/products/keyboard.jpg"]', 'Compact 87-key mechanical keyboard with RGB lighting.', 1),
('Fitness Band', 3, 149.00, 199.00, 240, 1510, 4.60, '/static/products/fitness-band.jpg', '["/static/products/fitness-band.jpg"]', 'Daily health tracking band with heart rate monitor.', 1),
('Smart Watch', 1, 499.00, 599.00, 60, 320, 4.90, '/static/products/watch.jpg', '["/static/products/watch.jpg"]', 'Smart watch with multiple health tracking features.', 1),
('Bluetooth Speaker', 2, 159.00, 199.00, 150, 680, 4.50, '/static/products/speaker.jpg', '["/static/products/speaker.jpg"]', 'Portable Bluetooth speaker with 360° sound.', 1);

-- Insert sample campaigns
INSERT INTO `campaign` (`name`, `type`, `status`, `discount`, `start_time`, `end_time`) VALUES
('New User Coupon', 'coupon', 'active', '10% OFF', '2026-01-01 00:00:00', '2026-12-31 23:59:59'),
('Flash Sale', 'flash_sale', 'scheduled', '$20 OFF', '2026-03-15 00:00:00', '2026-03-15 23:59:59'),
('Group Buy', 'group_buy', 'active', '15% OFF', '2026-01-01 00:00:00', '2026-06-30 23:59:59');
