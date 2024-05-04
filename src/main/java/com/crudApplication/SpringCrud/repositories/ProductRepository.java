package com.crudApplication.SpringCrud.repositories;

import com.crudApplication.SpringCrud.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository  extends JpaRepository<Product,Long> {

}
