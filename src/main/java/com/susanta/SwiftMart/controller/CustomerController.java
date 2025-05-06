package com.susanta.SwiftMart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.susanta.SwiftMart.entities.CartItem;
import com.susanta.SwiftMart.entities.Category;
import com.susanta.SwiftMart.entities.Product;
import com.susanta.SwiftMart.entities.User;
import com.susanta.SwiftMart.services.CartService;
import com.susanta.SwiftMart.services.ProductService;
import com.susanta.SwiftMart.services.UserService;

import jakarta.servlet.http.HttpSession;

import java.security.Principal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/customer")
@SessionAttributes("categories")
public class CustomerController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    @GetMapping("/dashboard")
    public String dashboard(Principal principal, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            String email = principal.getName();
            user = userService.findByEmail(email);
            session.setAttribute("user", user);
        }
        return "customer/dashboard";
    }

    @ModelAttribute("categories")
    public List<Category> addCategoriesToSession() {
        List<Category> categories = Arrays.asList(Category.values());
        return categories;
    }

    @GetMapping("/profile")
    public String customerProfile() {
        return "customer/profile";
    }

    @GetMapping("/products")
    public String viewProducts(@RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        Page<Product> products = productService.getAllProducts(PageRequest.of(page - 1, size));
        model.addAttribute("products", products.getContent());
        return "customer/products";
    }

    @GetMapping("/cart")
    public String viewCart(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        // Handle the case where the user is not found
        if (user == null) {
            throw new IllegalArgumentException("User not found with email: " + principal.getName());
        }

        model.addAttribute("cartItems", cartService.getCartItems(user));
        model.addAttribute("totalItems", cartService.getTotalItems(user));
        model.addAttribute("totalPrice", cartService.getTotalPrice(user));
        return "customer/cart";
    }

    @PostMapping("/addToCart/{productId}")
    public String addToCart(@PathVariable Long productId, @RequestParam(defaultValue = "1") int quantity,
            Principal principal) {
        User user = userService.findByEmail(principal.getName());
        Product product = productService.getProductById(productId);
        cartService.addToCart(user, product, quantity);
        return "redirect:/customer/cart";
    }

    @PostMapping("/updateCart/{productId}")
    @ResponseBody
    public Map<String, Object> updateCart(@PathVariable Long productId, @RequestParam int quantity,
            Principal principal) {
        String username = principal.getName(); // Get the logged-in user's username
        User user = userService.findByEmail(username);
        Product product = productService.getProductById(productId);

        cartService.updateCartItem(user, product, quantity);

        // Prepare the response with updated subtotal and total price
        Map<String, Object> response = new HashMap<>();
        response.put("subtotal", cartService.getCartItems(user).stream()
                .filter(item -> Long.valueOf(item.getProduct().getId()).equals(productId))
                .findFirst()
                .map(CartItem::getSubtotal)
                .orElse(0.0));
        response.put("totalPrice", cartService.getTotalPrice(user));
        response.put("totalItems", cartService.getTotalItems(user));
        return response;
    }

    @GetMapping("/removeFromCart/{productId}")
    public String removeFromCart(@PathVariable Long productId, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        cartService.removeCartItem(user, productService.getProductById(productId));
        return "redirect:/customer/cart";
    }
}
