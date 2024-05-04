package com.crudApplication.SpringCrud.controllers;

import com.crudApplication.SpringCrud.dto.ProductRequest;
import com.crudApplication.SpringCrud.models.Product;
import com.crudApplication.SpringCrud.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/products")
public class ProductsController {

    @Autowired
    ProductRepository productRepository;

    @GetMapping({"","/"})
    public String ShowAllProducts(Model model)
    {
        List<Product> products = productRepository.findAll(Sort.by(Sort.Direction.DESC,"createdAt"));
        model.addAttribute("products",products);
        return "products/index";
    }

    @GetMapping("/create")
    public String createNewProduct(Model model)
    {
        ProductRequest request = new ProductRequest();
        model.addAttribute("product",request);
        return "product/create";
    }

    @GetMapping("/edit{id}")
    public Optional<Product> editProduct(@RequestBody Model model, Long id){
        Optional<Product> product = productRepository.findById(id);
        model.addAttribute("product",product);
        return product;
    }
}
