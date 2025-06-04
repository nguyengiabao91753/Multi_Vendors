package com.example.shopee.controller;

import com.example.shopee.entity.ProductEntity;
import com.example.shopee.entity.UserEntity;
import com.example.shopee.repository.ProductRepository;
import com.example.shopee.repository.UserRepository;
import com.example.shopee.repository.WishlistRepository;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("")
public class HomeController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("")
    private String indexHome() {
        return "index";
    }

    @GetMapping("/home")
    private String homePage(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        if (authorities.stream().anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()))) {
            model.addAttribute("email", SecurityContextHolder.getContext().getAuthentication().getName());
            return "redirect:/admin/dashboard";
        } else if (authorities.stream().anyMatch(authority -> "ROLE_VENDOR".equals(authority.getAuthority()))) {
            model.addAttribute("email", SecurityContextHolder.getContext().getAuthentication().getName());
            return "redirect:/vendor/dashboard";
        } else {
            if (!Objects.equals(username, "")) {
                model.addAttribute("email", SecurityContextHolder.getContext().getAuthentication().getName());
                return "redirect:/client/home";
            } else {
                return "index";
            }
        }
    }

    @GetMapping("/index")
    private String index(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        if (authorities.stream().anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()))) {
            model.addAttribute("email", SecurityContextHolder.getContext().getAuthentication().getName());
            return "redirect:/admin/dashboard";
        } else if (authorities.stream().anyMatch(authority -> "ROLE_VENDOR".equals(authority.getAuthority()))) {
            model.addAttribute("email", SecurityContextHolder.getContext().getAuthentication().getName());
            return "redirect:/vendor/dashboard";
        } else {
            if (!Objects.equals(username, "")) {
                model.addAttribute("email", SecurityContextHolder.getContext().getAuthentication().getName());
                return "redirect:/client/home";
            } else {
                return "index";
            }
        }
    }

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/list")
    private String list(Model model,
                        @RequestParam(name = "keyword", required = false) String keyword,
                        @RequestParam(name = "page", defaultValue = "0") int page,
                        @RequestParam(name = "size", defaultValue = "6") int size,
                        @RequestParam(name = "sort", required = false) String sort,
                        @RequestParam(name = "order", required = false) String order) {
        Pageable pageable;
        Sort sortable = Sort.by("createdAt").descending();

        if (sort != null && !sort.isEmpty()) {
            if (sort.equals("createdAt")) {
                sortable = Sort.by("createdAt").descending();
            } else if (sort.equals("salePrice")) {
                if (order != null && order.equalsIgnoreCase("asc")) {
                    sortable = Sort.by("salePrice").ascending();
                } else if (order != null && order.equalsIgnoreCase("desc")) {
                    sortable = Sort.by("salePrice").descending();
                } else {
                    sortable = Sort.by("salePrice").ascending();
                }
            }
        }

        pageable = PageRequest.of(page, size, sortable);
        Page<ProductEntity> productPage;
        int status = 1;

        if (keyword != null && !keyword.isEmpty()) {
            productPage = productRepository.searchProducts(keyword.toLowerCase(), pageable);
            model.addAttribute("searchKeyword", keyword);
        } else {
            productPage = productRepository.findAllByStatus(status, pageable);
        }

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("sort", sort);
        model.addAttribute("order", order);

        return "list";
    }

    @Autowired
    private WishlistRepository wishlistRepository;

    @GetMapping("/detail/{id}")
    private String detail(@PathVariable("id") Long id, Model model) {
        Optional<ProductEntity> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()) {
            boolean inWishlist = false;

            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            Optional<UserEntity> optionalUser = userRepository.findByEmail(email);

            ProductEntity productEntity = productOptional.get();

            if (!optionalUser.isEmpty()) {
                UserEntity userEntity = optionalUser.get();
                inWishlist = wishlistRepository
                        .findByUserEntityAndProductEntity(userEntity, productEntity)
                        .isPresent();
            }

            model.addAttribute("inWishlist", inWishlist);
            model.addAttribute("product", productOptional.get());
            return "detail";
        } else {
            return "redirect:/list";
        }
    }

    private static Long id = 0L;

    @GetMapping("/view/vendor")
    private String viewVendor(@RequestParam(value = "id", required = false) Long id,
                              Model model,
                              @RequestParam(name = "keyword", required = false) String keyword,
                              @RequestParam(name = "page", defaultValue = "0") int page,
                              @RequestParam(name = "size", defaultValue = "6") int size,
                              @RequestParam(name = "sort", required = false) String sort,
                              @RequestParam(name = "order", required = false) String order) {
        System.out.println(id);
        if (id != null) {
            this.id = id;
        }
        Optional<UserEntity> optionalUserEntity = userRepository.findById(this.id);
        if (optionalUserEntity.isPresent()) {
            UserEntity vendor = optionalUserEntity.get();
            model.addAttribute("user", vendor);

            Sort sortable = Sort.by("createdAt").descending();
            if ("salePrice".equals(sort)) {
                if ("asc".equalsIgnoreCase(order)) {
                    sortable = Sort.by("salePrice").ascending();
                } else {
                    sortable = Sort.by("salePrice").descending();
                }
            }
            Pageable pageable = PageRequest.of(page, size, sortable);
            int status = 1;

            Page<ProductEntity> productPage;
            if (keyword != null && !keyword.isEmpty()) {
                productPage = productRepository.searchProducts(keyword.toLowerCase(), pageable);
                model.addAttribute("searchKeyword", keyword);
            } else {
                productPage = productRepository.findAllByUserIdAndStatus(id, status, pageable);
            }

            model.addAttribute("products", productPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", productPage.getTotalPages());
            model.addAttribute("sort", sort);
            model.addAttribute("order", order);

            return "viewVendor";
        } else {
            return "redirect:/";
        }
    }

}
