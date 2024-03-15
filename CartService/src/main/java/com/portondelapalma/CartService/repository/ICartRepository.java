package com.portondelapalma.CartService.repository;

import com.portondelapalma.CartService.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICartRepository extends JpaRepository<Cart, Long> {

}
