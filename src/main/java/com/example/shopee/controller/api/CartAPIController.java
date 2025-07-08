package com.example.shopee.controller.api;

import com.example.shopee.entity.CartDetailEntity;
import com.example.shopee.entity.CartEntity;
import com.example.shopee.entity.ProductEntity;
import com.example.shopee.entity.UserEntity;
import com.example.shopee.repository.CartDetailRepository;
import com.example.shopee.repository.CartRepository;
import com.example.shopee.repository.ProductRepository;
import com.example.shopee.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/cart")
public class CartAPIController {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartDetailRepository cartDetailRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    // Get cart of current user
    @GetMapping("")
    public ResponseEntity<?> getCart() {
        Optional<UserEntity> userOpt = getCurrentUser();
        if (userOpt.isEmpty()) return ResponseEntity.status(401).body("Unauthorized");

        UserEntity user = userOpt.get();
        CartEntity cart = cartRepository.findByUserEntity(user).orElse(null);

        if (cart == null || cart.getCartDetailEntities().isEmpty()) {
            return ResponseEntity.ok(Map.of(
                "items", List.of(),
                "totalPrice", BigDecimal.ZERO,
                "totalQuantity", 0
            ));
        }

        Set<CartDetailEntity> items = cart.getCartDetailEntities();
        int totalQty = items.stream().mapToInt(CartDetailEntity::getQuantity).sum();
        BigDecimal totalPrice = items.stream()
                .map(d -> d.getPriceOfOne().multiply(BigDecimal.valueOf(d.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return ResponseEntity.ok(Map.of(
            "items", items,
            "totalPrice", totalPrice,
            "totalQuantity", totalQty
        ));
    }

    // Add to cart
    @PostMapping("/add")
    @Transactional
    public ResponseEntity<?> addToCart(@RequestParam Long productId, @RequestParam Integer quantity) {
        Optional<UserEntity> userOpt = getCurrentUser();
        if (userOpt.isEmpty()) return ResponseEntity.status(401).body("Unauthorized");

        UserEntity user = userOpt.get();
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        CartEntity cart = cartRepository.findByUserEntity(user)
                .orElseGet(() -> {
                    CartEntity newCart = new CartEntity();
                    newCart.setUserEntity(user);
                    newCart.setCartDetailEntities(new HashSet<>());
                    return cartRepository.save(newCart);
                });

        user.setCartEntity(cart);
        userRepository.save(user);

        if (cart.getCartDetailEntities() == null)
            cart.setCartDetailEntities(new HashSet<>());

        Optional<CartDetailEntity> existing = cart.getCartDetailEntities().stream()
                .filter(d -> d.getProductEntity().getId().equals(productId))
                .findFirst();

        if (existing.isPresent()) {
            CartDetailEntity detail = existing.get();
            detail.setQuantity(detail.getQuantity() + quantity);
            detail.setTotalPrice(detail.getPriceOfOne().multiply(BigDecimal.valueOf(detail.getQuantity())));
            cartDetailRepository.save(detail);
        } else {
            CartDetailEntity detail = new CartDetailEntity();
            detail.setCartEntity(cart);
            detail.setProductEntity(product);
            detail.setQuantity(quantity);
            detail.setPriceOfOne(product.getSalePrice());
            detail.setTotalPrice(product.getSalePrice().multiply(BigDecimal.valueOf(quantity)));
            cartDetailRepository.save(detail);
            cart.getCartDetailEntities().add(detail);
        }

        updateCartTotal(cart);
        return ResponseEntity.ok("Added to cart");
    }

    // Update quantity
    @PostMapping("/update")
    public ResponseEntity<?> updateItem(@RequestParam Long cartDetailId, @RequestParam Integer quantity) {
        if (quantity <= 0) return ResponseEntity.badRequest().body("Quantity must be > 0");

        CartDetailEntity detail = cartDetailRepository.findById(cartDetailId)
                .orElseThrow(() -> new RuntimeException("Cart detail not found"));

        detail.setQuantity(quantity);
        detail.setTotalPrice(detail.getPriceOfOne().multiply(BigDecimal.valueOf(quantity)));
        cartDetailRepository.save(detail);

        updateCartTotal(detail.getCartEntity());
        return ResponseEntity.ok("Updated");
    }

    // Remove item from cart
    @DeleteMapping("/remove/{id}")
    public ResponseEntity<?> removeItem(@PathVariable Long id) {
        Optional<CartDetailEntity> opt = cartDetailRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.status(404).body("Cart item not found");

        CartDetailEntity detail = opt.get();
        CartEntity cart = detail.getCartEntity();

        cartDetailRepository.delete(detail);
        updateCartTotal(cart);

        return ResponseEntity.ok("Removed");
    }

    private void updateCartTotal(CartEntity cart) {
        BigDecimal total = cart.getCartDetailEntities().stream()
                .map(CartDetailEntity::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalCost(total);
        cartRepository.save(cart);
    }

    private Optional<UserEntity> getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email);
    }
}
