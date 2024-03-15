package com.portondelapalma.productsv.repository;

import com.portondelapalma.productsv.model.Category;
import com.portondelapalma.productsv.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IProductRepository extends JpaRepository<Product, Long> {

    @Query("select p from Product p where p.category = ?1")
    List<Product> getAllByCategory(Category category);

    @Query("select p from Product p where p.nameProduct = ?1")
    Product getByName(String nameProduct);
}
