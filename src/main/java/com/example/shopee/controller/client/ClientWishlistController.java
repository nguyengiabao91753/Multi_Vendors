package com.example.shopee.controller.client;

import com.example.shopee.entity.ProductEntity;
import com.example.shopee.entity.UserEntity;
import com.example.shopee.entity.WishlistEntity;
import com.example.shopee.repository.ProductRepository;
import com.example.shopee.repository.UserRepository;
import com.example.shopee.repository.WishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/client/wishlist")
public class ClientWishlistController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private WishlistRepository wishlistRepository;

    @GetMapping("")
    public String viewWishlist(Model model) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return "redirect:/login";
        }

        UserEntity user = optionalUser.get();
        var wishlistItems = wishlistRepository.findAllByUserEntity(user);
        model.addAttribute("wishlistItems", wishlistItems);

        return "client/wishlist";
    }


    @PostMapping("/toggle")
    public String toggleWishlist(@RequestParam("productId") Long productId, RedirectAttributes redirectAttributes) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> optionalUser = userRepository.findByEmail(email);

        if(optionalUser.isEmpty()) {
            redirectAttributes.addAttribute("mess", "Bạn phải đăng nhập thì mới thực hiện được chức năng này");
            return "redirect:/login";
        }

        UserEntity user = optionalUser.get();
        ProductEntity product = productRepository.findById(productId).get();

        Optional<WishlistEntity> wishlistItem = wishlistRepository.findByUserEntityAndProductEntity(user, product);

        if (wishlistItem.isPresent()) {
            wishlistRepository.delete(wishlistItem.get());
        } else {
            WishlistEntity newItem = new WishlistEntity();
            newItem.setUserEntity(user);
            newItem.setProductEntity(product);
            wishlistRepository.save(newItem);
        }

        return "redirect:/client/wishlist";
    }

}
