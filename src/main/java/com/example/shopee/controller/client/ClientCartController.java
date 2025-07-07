package com.example.shopee.controller.client;


import com.example.shopee.entity.CartDetailEntity;
import com.example.shopee.entity.CartEntity;
import com.example.shopee.entity.ProductEntity;
import com.example.shopee.entity.UserEntity;
import com.example.shopee.repository.CartDetailRepository;
import com.example.shopee.repository.CartRepository;
import com.example.shopee.repository.ProductRepository;
import com.example.shopee.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@Controller
@RequestMapping("/client/cart")
public class ClientCartController {

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartDetailRepository cartDetailRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("")
    public String cart(Model model) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return "redirect:/login";
        }

        UserEntity userEntity = optionalUser.get();
        Optional<CartEntity> cartEntityOpt = cartRepository.findByUserEntity(userEntity);

        if (cartEntityOpt.isEmpty()) {
            CartEntity newCart = new CartEntity();
            newCart.setUserEntity(userEntity);
            newCart.setCartDetailEntities(Set.of());
            newCart = cartRepository.save(newCart);
            userEntity.setCartEntity(newCart);
            userRepository.save(userEntity);
            model.addAttribute("cartDetails", Set.of());
            model.addAttribute("totalPrice", 0);
            model.addAttribute("totalQuantity", 0);
        } else {
            CartEntity cart = cartEntityOpt.get();
            Set<CartDetailEntity> cartDetails = cart.getCartDetailEntities();

            int totalQuantity = cartDetails.stream().mapToInt(CartDetailEntity::getQuantity).sum();
            BigDecimal totalPrice = cartDetails.stream()
                    .map(detail -> detail.getPriceOfOne().multiply(BigDecimal.valueOf(detail.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            model.addAttribute("cartDetails", cartDetails);
            model.addAttribute("totalQuantity", totalQuantity);
            model.addAttribute("totalPrice", totalPrice);
        }

        return "cart";
    }

    @PostMapping("/update")
    public String updateQuantity(@RequestParam Long cartDetailId,
                                 @RequestParam Integer quantity,
                                 @RequestParam String action,
                                 @RequestParam(required = false) Integer quantityInput) {
        Optional<CartDetailEntity> optCartDetail = cartDetailRepository.findById(cartDetailId);
        if (optCartDetail.isPresent()) {
            CartDetailEntity cartDetail = optCartDetail.get();

            if ("increase".equals(action)) {
                quantity += 1;
            } else if ("decrease".equals(action) && quantity > 1) {
                quantity -= 1;
            } else if ("update".equals(action) && quantityInput != null) {
                quantity = quantityInput;
            }

            cartDetail.setQuantity(quantity);
            BigDecimal priceOfOne = cartDetail.getPriceOfOne();
            cartDetail.setTotalPrice(priceOfOne.multiply(BigDecimal.valueOf(quantity)));

            cartDetailRepository.save(cartDetail);
            updateCartTotalCost(cartDetail.getCartEntity());
        }
        return "redirect:/client/cart";
    }


    @GetMapping("/remove/{id}")
    public String removeItem(@PathVariable("id") Long cartDetailId) {
        Optional<CartDetailEntity> opt = cartDetailRepository.findById(cartDetailId);
        if (opt.isPresent()) {
            CartDetailEntity detail = opt.get();
            CartEntity cart = detail.getCartEntity();
            cartDetailRepository.deleteById(cartDetailId);
            updateCartTotalCost(cart);
        }
        return "redirect:/client/cart";
    }


    @PostMapping("/add")
    public String addToCart(@RequestParam("productId") Long productId,
                            @RequestParam("quantity") Integer quantity) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return "redirect:/login";
        }

        UserEntity user = optionalUser.get();

        CartEntity cart = cartRepository.findByUserEntity(user)
                .orElseGet(() -> {
                    CartEntity newCart = new CartEntity();
                    newCart.setUserEntity(user);
                    newCart.setCartDetailEntities(new HashSet<>());
                    return cartRepository.save(newCart);
                });
        user.setCartEntity(cart);
        userRepository.save(user);

        if (cart.getCartDetailEntities() == null) {
            cart.setCartDetailEntities(new HashSet<>());
        }


        Optional<CartDetailEntity> optionalCartDetail = cart.getCartDetailEntities().stream()
                .filter(cd -> cd.getProductEntity().getId().equals(productId))
                .findFirst();

        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (optionalCartDetail.isPresent()) {
            CartDetailEntity cartDetail = optionalCartDetail.get();
            cartDetail.setQuantity(cartDetail.getQuantity() + quantity);
        } else {
            CartDetailEntity cartDetail = new CartDetailEntity();
            cartDetail.setCartEntity(cart);
            cartDetail.setProductEntity(product);
            cartDetail.setQuantity(quantity);
            cartDetail.setPriceOfOne(product.getSalePrice());
            cartDetail.setTotalPrice(product.getSalePrice().multiply(new BigDecimal(quantity)));
            cart.getCartDetailEntities().add(cartDetail);
        }

        cartRepository.save(cart);
        updateCartTotalCost(cart);

        return "redirect:/client/cart";
    }

    private void updateCartTotalCost(CartEntity cart) {
        BigDecimal total = cart.getCartDetailEntities().stream()
                .map(CartDetailEntity::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setTotalCost(total);
        cartRepository.save(cart);
    }


}
