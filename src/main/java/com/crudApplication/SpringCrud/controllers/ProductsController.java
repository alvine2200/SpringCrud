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

import java.io.File;
import java.io.IOException;
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

        if(bindingResult.hasErrors()){
            return "products/create";
        }
            Product product = new Product();
            product.setName(productRequest.getName());
            product.setBrand(productRequest.getBrand());
            product.setCategory(productRequest.getCategory());
            product.setDescription(productRequest.getDescription());
            product.setImageFileName(imageName);
            product.setPrice(productRequest.getPrice());
            product.setCreatedAt(createdAt);
            productRepository.save(product);

            httpSession.setAttribute("success", "Product successfully created!");

        return "redirect:/products";
    }

    @GetMapping("/edit")
    public String editProduct(Model model, @RequestParam Long id){
        try {
            Product product = productRepository.findById(id).get();
            model.addAttribute("product", product);

            ProductRequest productRequest = new ProductRequest();
            productRequest.setName(product.getName());
            productRequest.setBrand(product.getBrand());
            productRequest.setCategory(product.getCategory());
            productRequest.setPrice(product.getPrice());
            productRequest.setDescription(product.getDescription());

            model.addAttribute("productRequest",productRequest);

//            product.setCreatedAt(product.getCreatedAt());
        }catch (Exception ex) {
            System.out.println("Exception:" + ex.getMessage());
            return "redirect:/products";
        }
        return "products/edit";
    }



    @PostMapping("/edit")
    public String updateProduct(Model model, @RequestParam Long id,@Valid @ModelAttribute ProductRequest productRequest, BindingResult bindingResult)
    {
        try{
                Product product = productRepository.findById(id).get();
                model.addAttribute("product",product);

                if (bindingResult.hasErrors())
                {
                    return "redirect:/products/edit";
                }

                if (!productRequest.getImageFileName().isEmpty()){
                    String uploadDir = "public/images/";
                    Path oldImagePath = Paths.get(uploadDir + product.getImageFileName());

                    try{
                        Files.delete(oldImagePath);
                    }catch (Exception ex) {
                        System.out.println("Exception:" + ex.getMessage());
                    }

                    //new image after deleting the old image
                    try {
                        MultipartFile image = productRequest.getImageFileName();
                        Date createdAt = new Date();
                        String imageName = createdAt.getTime() + "_" + image.getOriginalFilename();

                        String uploadDi = "public/images/";
                        Path uploadPath = Paths.get(uploadDi);

                        if (!Files.exists(uploadPath)) {
                            Files.createDirectories(uploadPath);
                        }

                        InputStream inputStream = image.getInputStream();
                        Files.copy(inputStream,Paths.get(uploadDir + imageName), StandardCopyOption.REPLACE_EXISTING);

                        product.setImageFileName(imageName);
                    }catch (Exception ex)
                    {
                        System.out.println("Exception:" + ex.getMessage());
                    }
                }
                product.setName(productRequest.getName());
                product.setBrand(productRequest.getBrand());
                product.setCategory(productRequest.getCategory());
                product.setPrice(productRequest.getPrice());
                product.setDescription(productRequest.getDescription());
                productRepository.save(product);
        }catch (Exception ex)
        {
            System.out.println("Exception:" + ex.getMessage());
            return "redirect:/products/edit";
        }
        return "redirect:/products";
    }

    @GetMapping("/delete")
    public String deleteProduct(@RequestParam Long id) throws IOException {
        Product product = productRepository.findById(id).get();

        Path imagePath = Paths.get("public/images/" + product.getImageFileName());
        Files.delete(imagePath);
        productRepository.delete(product);
        return "redirect:/products";
    }
}
