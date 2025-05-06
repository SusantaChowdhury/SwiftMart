package com.susanta.SwiftMart.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.susanta.SwiftMart.entities.Product;
import com.susanta.SwiftMart.entities.User;
import com.susanta.SwiftMart.repositories.ProductRepository;
import com.susanta.SwiftMart.services.ProductService;
import com.susanta.SwiftMart.services.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    // @Autowired
    // private ProductRepository productRepository;

    @Autowired
    private SessionRegistry sessionRegistry;

    @GetMapping("/dashboard")
    public String dashboard(Principal principal, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            String email = principal.getName();
            user = userService.findByEmail(email);
            session.setAttribute("user", user);
        }
        return "admin/dashboard";
    }

    @GetMapping("/profile")
    public String adminProfile() {
        return "admin/profile";
    }

    @GetMapping("/viewProducts")
    public String viewAllProducts(Model model) {
        List<Product> products = productService.getAllProductsWithSellers();
        model.addAttribute("totalProducts", products.size());
        model.addAttribute("products", products);
        return "admin/viewProducts";
    }

    @GetMapping("/productDetails/{id}")
    public String viewProductDetails(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        if (product == null) {
            model.addAttribute("error", "Product not found.");
            return "redirect:/admin/viewProducts";
        }
        model.addAttribute("product", product);
        return "admin/productDetails";
    }

    @GetMapping("/sellerDetails/{id}")
    public String viewSellerDetails(@PathVariable String id, Model model) {
        User seller = userService.getSellerById(id);
        if (seller == null) {
            model.addAttribute("error", "Seller not found.");
            return "redirect:/admin/viewProducts";
        }
        model.addAttribute("seller", seller);
        return "admin/sellerDetails";
    }

    @GetMapping("/activeSessions")
    public List<String> getActiveSessions() {
        return sessionRegistry.getAllPrincipals().stream()
                .map(principal -> principal.toString())
                .collect(Collectors.toList());
    }
}
