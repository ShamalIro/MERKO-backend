package com.merko.merko_backend.repository;

import com.merko.merko_backend.entity.CartItem;
import com.merko.merko_backend.entity.Cart;
import com.merko.merko_backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);

    void deleteByCartAndProduct(Cart cart, Product product);
}
