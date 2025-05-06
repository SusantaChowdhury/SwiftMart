package com.susanta.SwiftMart.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.susanta.SwiftMart.entities.Category;
import com.susanta.SwiftMart.entities.Product;
import com.susanta.SwiftMart.entities.User;
import com.susanta.SwiftMart.services.UserService;
import com.susanta.SwiftMart.services.impl.ProductServiceImpl;
import com.susanta.SwiftMart.services.OrderService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/seller")
public class SellerController {

    @Autowired
    private UserService userService; // Inject UserService dependency

    @Autowired
    private ProductServiceImpl productService;

    @Autowired
    private OrderService orderService;

    @GetMapping("/dashboard")
    public String dashboard(Principal principal, Model model, HttpSession session) {
        User seller = (User) session.getAttribute("user");
        if (seller == null) {
            String email = principal.getName();
            seller = userService.findByEmail(email);
            session.setAttribute("user", seller);
        }

        // Fetch analytics data
        long totalProductTypes = productService.countTotalProductTypesBySeller(seller);
        long totalProducts = productService.calculateTotalStockBySeller(seller);

        long totalOrders = orderService.countOrdersBySeller(seller);
        double totalSales = orderService.calculateTotalSalesBySeller(seller);
        long totalCustomers = orderService.countUniqueCustomersBySeller(seller);

        // Add data to the model
        model.addAttribute("totalProductTypes", totalProductTypes);
        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalSales", totalSales);
        model.addAttribute("totalCustomers", totalCustomers);

        return "seller/dashboard";
    }

    @GetMapping("/profile")
    public String sellerProfile() {
        return "seller/profile";
    }

    @GetMapping("/addProduct")
    public String showAddProductPage() {
        return "seller/addProduct";
    }

    @PostMapping("/addProduct")
    public String addProduct(Product product, HttpSession session) {
        // Get the logged-in seller from the session
        User seller = (User) session.getAttribute("user");
        if (seller == null) {
            return "redirect:/login"; // Redirect to login if session expired
        }

        // Set the seller for the product
        product.setSeller(seller);

        // Save the product
        productService.saveProduct(product);

        return "redirect:/seller/viewProducts"; // Redirect to seller's dashboard
    }

    // view products in tabular format
    // @GetMapping("/viewProducts")
    // public String viewProducts(HttpSession session, Model model) {
    // // Get the logged-in seller from the session
    // User seller = (User) session.getAttribute("user");
    // if (seller == null) {
    // return "redirect:/login"; // Redirect to login if session expired
    // }

    // // Fetch all products for the seller
    // List<Product> products = productService.getProductsBySeller(seller);

    // // Add products to the model
    // model.addAttribute("products", products);

    // return "seller/viewProducts"; // Render the viewProducts page
    // }

    // view products in card format
    @GetMapping("/viewProducts")
    public String viewProducts(
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "filter", required = false) String filter,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model, HttpSession session) {

        User seller = (User) session.getAttribute("user");
        if (seller == null) {
            return "redirect:/login";
        }

        // Fetch products with sorting, filtering, and pagination
        Page<Product> products = productService.getProductsBySeller(seller, sort, filter, page, size);

        model.addAttribute("products", products.getContent());
        model.addAttribute("categories", Category.values());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", products.getTotalPages());

        return "seller/viewProducts";
    }

    @GetMapping("/editProduct")
    public String showEditProductPage(@RequestParam("id") long id, Model model, HttpSession session) {
        // Get the logged-in seller from the session
        User seller = (User) session.getAttribute("user");
        if (seller == null) {
            return "redirect:/login"; // Redirect to login if session expired
        }

        // Fetch the product by ID
        Product product = productService.getProductById(id);

        // Ensure the product belongs to the logged-in seller
        if (!product.getSeller().getUserId().equals(seller.getUserId())) {
            return "redirect:/seller/viewProducts"; // Redirect if unauthorized
        }

        // Add product and categories to the model
        model.addAttribute("product", product);
        model.addAttribute("categories", Category.values());

        return "seller/editProduct"; // Render the editProduct page
    }

    // @PostMapping("/editProduct")
    // public String editProduct(Product product, HttpSession session) {
    // // Get the logged-in seller from the session
    // User seller = (User) session.getAttribute("user");
    // if (seller == null) {
    // return "redirect:/login"; // Redirect to login if session expired
    // }

    // // Ensure the product belongs to the logged-in seller
    // Product existingProduct = productService.getProductById(product.getId());
    // if (!existingProduct.getSeller().getUserId().equals(seller.getUserId())) {
    // return "redirect:/seller/viewProducts"; // Redirect if unauthorized
    // }

    // // Update the product details
    // product.setSeller(seller); // Ensure the seller remains the same
    // productService.saveProduct(product);

    // return "redirect:/seller/viewProducts"; // Redirect to the product list
    // }
    @PostMapping("/editProduct")
    public String editProduct(Product product, HttpSession session, Model model) {
        // Get the logged-in seller from the session
        User seller = (User) session.getAttribute("user");
        if (seller == null) {
            return "redirect:/login"; // Redirect to login if session expired
        }

        // Fetch the existing product from the database
        Product existingProduct = productService.getProductById(product.getId());

        // Ensure the product belongs to the logged-in seller
        if (!existingProduct.getSeller().getUserId().equals(seller.getUserId())) {
            return "redirect:/seller/viewProducts"; // Redirect if unauthorized
        }

        // Add the entered stock quantity to the current stock quantity
        int updatedStockQuantity = existingProduct.getStockQuantity() + product.getStockQuantity();
        existingProduct.setStockQuantity(updatedStockQuantity);

        // Update other product details
        existingProduct.setName(product.getName());
        existingProduct.setBrand(product.getBrand());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setCategory(product.getCategory());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setImageUrl(product.getImageUrl());

        // Save the updated product
        productService.saveProduct(existingProduct);

        return "redirect:/seller/viewProducts"; // Redirect to the product list
    }
}
