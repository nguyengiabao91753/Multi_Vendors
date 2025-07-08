package com.example.shopee.controller.admin;

import com.example.shopee.entity.CategoryEntity;
import com.example.shopee.repository.CategoryRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/category")
public class AdminCategoryController {

    @Autowired
    private CategoryRepository categoryRepository;


    @GetMapping("")
    public String categoryPage(Model model, HttpSession session) {
        String email = (String) session.getAttribute("email");

        if (email == null) {
            return "redirect:/admin/login";
        }
        List<CategoryEntity> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);
        return "admin/category/list";
    }

    @GetMapping("/insertCategoryPage")
    public String insertCategoryPage(Model model, HttpSession session) {
        String email = (String) session.getAttribute("email");

        if (email == null) {
            return "redirect:/admin/login";
        }
        CategoryEntity categoryEntity = new CategoryEntity();
        model.addAttribute("category", categoryEntity);
        return "admin/category/insert";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute(name = "category") CategoryEntity categoryEntity, Model model, HttpSession session) {
        String email = (String) session.getAttribute("email");

        if (email == null) {
            return "redirect:/admin/login";
        }
        Optional<CategoryEntity> existingCategory = categoryRepository.findByCategoryName(categoryEntity.getCategoryName());
        if (existingCategory.isPresent()) {
            model.addAttribute("error", "Danh mục này đã tồn tại.");
            return "admin/category/insert";
        }

        categoryRepository.save(categoryEntity);
        return "redirect:/admin/category";
    }

    @GetMapping("/updateCategory/{id}")
    public String getFormUpdateCategory(@PathVariable("id") Long id, Model model, HttpSession session) {
        String email = (String) session.getAttribute("email");

        if (email == null) {
            return "redirect:/admin/login";
        }
        CategoryEntity categoryEntity = categoryRepository.findById(id).get();
        model.addAttribute("category", categoryEntity);
        return "admin/category/update";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute(name = "category") CategoryEntity categoryEntity, Model model, HttpSession session) {
        String email = (String) session.getAttribute("email");

        if (email == null) {
            return "redirect:/admin/login";
        }
        Optional<CategoryEntity> existingCategory = categoryRepository.findByCategoryName(categoryEntity.getCategoryName());
        if (existingCategory.isPresent() && !existingCategory.get().getId().equals(categoryEntity.getId())) {
            model.addAttribute("error", "Danh mục này đã tồn tại.");
            model.addAttribute("category", categoryEntity);
            return "admin/category/update";
        }

        categoryRepository.save(categoryEntity);
        return "redirect:/admin/category?update=true";
    }

    @GetMapping("/deleteCategory/{id}")
    public String delete(@PathVariable("id") Long id) {
        Optional<CategoryEntity> categoryOpt = categoryRepository.findById(id);
        String msg = "";
        if (categoryOpt.isPresent()) {
            CategoryEntity category = categoryOpt.get();
            if (category.getProducts() != null && !category.getProducts().isEmpty()) {
                msg = "Cannot delete category because it has associated products";
            } else {
                categoryRepository.deleteById(id);
                return "redirect:/admin/category?delete=true";
            }
        } else {
            msg = "Category not found";
        }

        if (!msg.isEmpty()) {
            String encodedMsg = URLEncoder.encode(msg, StandardCharsets.UTF_8);
            return "redirect:/admin/category?msg=" + encodedMsg;
        }
        return "redirect:/admin/category";
    }

}
