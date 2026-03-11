-- yiqipin mall database initialization script

-- Create database if not exists
CREATE DATABASE IF NOT EXISTS yiqipin CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE yiqipin;

-- User table
CREATE TABLE IF NOT EXISTS `users` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'User ID',
    `username` VARCHAR(50) NOT NULL COMMENT 'Username',
    `password` VARCHAR(255) NOT NULL COMMENT 'Password (hashed)',
    `phone` VARCHAR(20) COMMENT 'Phone number',
    `email` VARCHAR(100) COMMENT 'Email',
    `avatar` VARCHAR(500) COMMENT 'Avatar URL',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT 'Status: 0-disabled, 1-active',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='User table';

-- Address table
CREATE TABLE IF NOT EXISTS `addresses` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Address ID',
    `user_id` BIGINT NOT NULL COMMENT 'User ID',
    `receiver_name` VARCHAR(50) NOT NULL COMMENT 'Receiver name',
    `phone` VARCHAR(20) NOT NULL COMMENT 'Phone number',
    `province` VARCHAR(50) COMMENT 'Province',
    `city` VARCHAR(50) COMMENT 'City',
    `district` VARCHAR(50) COMMENT 'District',
    `detail_address` VARCHAR(255) NOT NULL COMMENT 'Detail address',
    `is_default` TINYINT NOT NULL DEFAULT 0 COMMENT 'Is default: 0-no, 1-yes',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Address table';

-- Category table
CREATE TABLE IF NOT EXISTS `categories` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Category ID',
    `name` VARCHAR(50) NOT NULL COMMENT 'Category name',
    `parent_id` BIGINT DEFAULT 0 COMMENT 'Parent category ID',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT 'Sort order',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT 'Status: 0-disabled, 1-active',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Category table';

-- Product table
CREATE TABLE IF NOT EXISTS `products` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Product ID',
    `name` VARCHAR(200) NOT NULL COMMENT 'Product name',
    `category_id` BIGINT COMMENT 'Category ID',
    `price` DECIMAL(10,2) NOT NULL COMMENT 'Price',
    `stock` INT NOT NULL DEFAULT 0 COMMENT 'Stock quantity',
    `sales` INT NOT NULL DEFAULT 0 COMMENT 'Sales count',
    `rating` DECIMAL(3,2) DEFAULT 0 COMMENT 'Rating score',
    `image` VARCHAR(500) COMMENT 'Main image URL',
    `images` TEXT COMMENT 'Image URLs (JSON array)',
    `description` TEXT COMMENT 'Product description',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT 'Status: 0-off-shelf, 1-on-shelf',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Product table';

-- Shopping cart table
CREATE TABLE IF NOT EXISTS `cart_items` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Cart item ID',
    `user_id` BIGINT NOT NULL COMMENT 'User ID',
    `product_id` BIGINT NOT NULL COMMENT 'Product ID',
    `quantity` INT NOT NULL DEFAULT 1 COMMENT 'Quantity',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_product` (`user_id`, `product_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Shopping cart table';

-- Order table
CREATE TABLE IF NOT EXISTS `orders` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Order ID',
    `order_no` VARCHAR(50) NOT NULL COMMENT 'Order number',
    `user_id` BIGINT NOT NULL COMMENT 'User ID',
    `total_price` DECIMAL(10,2) NOT NULL COMMENT 'Total price',
    `pay_price` DECIMAL(10,2) NOT NULL COMMENT 'Pay price',
    `freight` DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT 'Freight',
    `discount` DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT 'Discount',
    `status` VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT 'Order status: pending-paid-shipped-completed-cancelled',
    `pay_type` VARCHAR(20) COMMENT 'Payment type',
    `pay_time` DATETIME COMMENT 'Payment time',
    `ship_time` DATETIME COMMENT 'Shipping time',
    `receive_time` DATETIME COMMENT 'Receive time',
    `receiver_name` VARCHAR(50) NOT NULL COMMENT 'Receiver name',
    `receiver_phone` VARCHAR(20) NOT NULL COMMENT 'Receiver phone',
    `receiver_address` VARCHAR(255) NOT NULL COMMENT 'Receiver address',
    `remark` VARCHAR(500) COMMENT 'Order remark',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Order table';

-- Order item table
CREATE TABLE IF NOT EXISTS `order_items` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Order item ID',
    `order_id` BIGINT NOT NULL COMMENT 'Order ID',
    `product_id` BIGINT NOT NULL COMMENT 'Product ID',
    `product_name` VARCHAR(200) NOT NULL COMMENT 'Product name',
    `product_image` VARCHAR(500) COMMENT 'Product image',
    `price` DECIMAL(10,2) NOT NULL COMMENT 'Price',
    `quantity` INT NOT NULL COMMENT 'Quantity',
    `total_price` DECIMAL(10,2) NOT NULL COMMENT 'Total price',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Order item table';

-- Campaign table (for marketing)
CREATE TABLE IF NOT EXISTS `campaigns` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Campaign ID',
    `name` VARCHAR(100) NOT NULL COMMENT 'Campaign name',
    `type` VARCHAR(50) NOT NULL COMMENT 'Campaign type: coupon, flash_sale, group_buy',
    `status` VARCHAR(20) NOT NULL DEFAULT 'draft' COMMENT 'Status: draft-scheduled-active-ended',
    `discount` VARCHAR(50) COMMENT 'Discount content',
    `start_time` DATETIME COMMENT 'Start time',
    `end_time` DATETIME COMMENT 'End time',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Campaign table';

-- Insert sample data
INSERT INTO `categories` (`name`, `parent_id`, `sort_order`, `status`) VALUES
('Electronics', 0, 1, 1),
('Accessories', 0, 2, 1),
('Fitness', 0, 3, 1);

INSERT INTO `products` (`name`, `category_id`, `price`, `stock`, `sales`, `rating`, `image`, `images`, `description`, `status`) VALUES
('Wireless Earbuds', 1, 199.00, 120, 985, 4.80, '/static/logo.png', '["/static/logo.png"]', 'Noise-canceling earbuds with premium sound quality.', 1),
('Mechanical Keyboard', 2, 329.00, 80, 463, 4.70, '/static/logo.png', '["/static/logo.png"]', 'Compact 87-key mechanical keyboard with RGB.', 1),
('Fitness Band', 3, 149.00, 240, 1510, 4.60, '/static/logo.png', '["/static/logo.png"]', 'Daily health tracking band with heart rate monitor.', 1);

INSERT INTO `campaigns` (`name`, `type`, `status`, `discount`) VALUES
('New User Coupon', 'coupon', 'active', '10%'),
('Flash Sale', 'flash_sale', 'scheduled', '$20 off'),
('Group Buy', 'group_buy', 'active', '15%');

-- Admin users (password is 'admin123' encrypted with BCrypt)
INSERT INTO `users` (`username`, `password`, `phone`, `email`, `avatar`, `status`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '13800000000', 'admin@yiqipin.com', '/static/logo.png', 1),
('demo', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '13800000001', 'demo@yiqipin.com', '/static/logo.png', 1);
