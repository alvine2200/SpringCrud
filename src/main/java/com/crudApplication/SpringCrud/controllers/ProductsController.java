package com.crudApplication.SpringCrud.controllers;

import com.crudApplication.SpringCrud.dto.CreateProductRequest;
import com.crudApplication.SpringCrud.dto.ProductRequest;
import com.crudApplication.SpringCrud.models.Product;
import com.crudApplication.SpringCrud.repositories.ProductRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
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
        ProductRequest productRequest = new ProductRequest();
        model.addAttribute("productRequest",productRequest);
        return "products/create";
    }


    @PostMapping("/create")
    public String saveNewProduct(@Valid @ModelAttribute ProductRequest productRequest, BindingResult bindingResult, HttpSession httpSession)
    {
        if(productRequest.getImageFileName().isEmpty()){
            bindingResult.addError(new FieldError("productRequest","imageFileName","Product must have an image"));
        }

        if(bindingResult.hasErrors()){
            return "products/create";
        }

        MultipartFile image = productRequest.getImageFileName();
        Date createdAt = new Date();
        String imageName = createdAt.getTime() + "_" + image.getOriginalFilename();

        try {
            String uploadDir = "public/images/";
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            InputStream inputStream = image.getInputStream();
            Files.copy(inputStream,Paths.get(uploadDir + imageName), StandardCopyOption.REPLACE_EXISTING);

        }catch (Exception ex)
        {
            System.out.println("Exception:" + ex.getMessage());
        }
            Product product = new Product();
            product.setName(productRequest.getName());
            product.setBrand(productRequest.getBrand());
            product.setCategory(productRequest.getCategory());
            product.setImageFileName(imageName);
            product.setPrice(productRequest.getPrice());
            product.setCreatedAt(createdAt);
            productRepository.save(product);

            httpSession.setAttribute("success", "Product successfully created!");

        return "redirect:/products";
    }

    @GetMapping("/edit{id}")
    public Optional<Product> editProduct(@RequestBody Model model, Long id){
        Optional<Product> product = productRepository.findById(id);
        model.addAttribute("product",product);
        return product;
    }
}
