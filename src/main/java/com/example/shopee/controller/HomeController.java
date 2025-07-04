package com.example.shopee.controller;

import com.example.shopee.entity.CategoryEntity;
import com.example.shopee.entity.FeedbackEntity;
import com.example.shopee.entity.ProductEntity;
import com.example.shopee.entity.UserEntity;
import com.example.shopee.repository.*;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("")
public class HomeController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;


    @GetMapping({"", "/home", "/index"})
    public String handleHomeAndIndex(Model model, RedirectAttributes redirectAttributes) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();


        if (authorities.stream().anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()))) {
            model.addAttribute("email", username);
            return "redirect:/admin/dashboard";
        } else {
            Pageable top6 = PageRequest.of(0, 6);

            List<ProductEntity> bestSellingProducts = productRepository.findBestSellingProducts(top6);
            List<ProductEntity> mostLikedProducts = wishlistRepository.findMostLikedProducts(top6);

            model.addAttribute("bestSellingProducts", bestSellingProducts);
            model.addAttribute("mostLikedProducts", mostLikedProducts);


            if (!username.isEmpty() && !"anonymousUser".equals(username)) {
                redirectAttributes.addFlashAttribute("email", username);
                redirectAttributes.addFlashAttribute("categories", categoryRepository.findAll());
                redirectAttributes.addFlashAttribute("bestSellingProducts", bestSellingProducts);
                redirectAttributes.addFlashAttribute("mostLikedProducts", mostLikedProducts);
                return "redirect:/client/home";
            } else {
                model.addAttribute("categories", categoryRepository.findAll());
                return "index";
            }
        }
    }




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

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {
        Optional<ProductEntity> productOptional = productRepository.findById(id);

        if (productOptional.isEmpty()) return "redirect:/list";

        ProductEntity product = productOptional.get();
        model.addAttribute("product", product);

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> userOptional = userRepository.findByEmail(email);

        boolean inWishlist = false;
        boolean canFeedback = false;
        boolean hasFeedback = false;

        if (userOptional.isPresent()) {
            UserEntity user = userOptional.get();

            inWishlist = wishlistRepository.findByUserEntityAndProductEntity(user, product).isPresent();
            model.addAttribute("user", user);

            canFeedback = orderRepository.hasUserBoughtProduct(product.getId(), user.getId());
            hasFeedback = feedbackRepository.existsByUserEntityAndProductEntity(user, product);
        }

        List<FeedbackEntity> feedbackList = feedbackRepository.findByProductEntityOrderByCreatedAtDesc(product);
        model.addAttribute("inWishlist", inWishlist);
        model.addAttribute("canFeedback", canFeedback);
        model.addAttribute("hasFeedback", hasFeedback);
        model.addAttribute("feedbackList", feedbackList);

        return "detail";
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



    @GetMapping("/list/cat/{categoryId}")
    public String listByCategory(@PathVariable("categoryId") Long categoryId,
                                 Model model,
                                 @RequestParam(name = "page", defaultValue = "0") int page,
                                 @RequestParam(name = "size", defaultValue = "6") int size) {

        Pageable pageable = PageRequest.of(page, size);
        int status = 1;

        CategoryEntity category = categoryRepository.findById(categoryId)
                .orElse(null);

        Page<ProductEntity> productPage = productRepository.findByCategoriesAndStatus(category, status, pageable);

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("categoryName", category.getCategoryName());

        model.addAttribute("categories", categoryRepository.findAll());

        return "list";
    }


}
