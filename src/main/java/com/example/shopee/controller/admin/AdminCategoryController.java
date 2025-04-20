package com.example.shopee.controller.admin;

import com.example.shopee.entity.CategoryEntity;
import com.example.shopee.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/category")
public class AdminCategoryController {

    @Autowired
    private CategoryRepository categoryRepository;


    // List all categories
    @GetMapping("")
    public String categoryPage(Model model) {
        List<CategoryEntity> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);
        return "admin/category/list";
    }

    // Show form to insert a new category
    @GetMapping("/insertCategoryPage")
    public String insertCategoryPage(Model model) {
        CategoryEntity categoryEntity = new CategoryEntity();
        model.addAttribute("category", categoryEntity);
        return "admin/category/insert";
    }

    // Save a new category
    @PostMapping("/save")
    public String save(@ModelAttribute(name = "category") CategoryEntity categoryEntity) {
        categoryRepository.save(categoryEntity);
        return "redirect:/admin/category";
    }

    // Show form to update an existing category
    @GetMapping("/updateCategory/{id}")
    public String getFormUpdateCategory(@PathVariable("id") Long id, Model model) {
        CategoryEntity categoryEntity = categoryRepository.findById(id).get();
        model.addAttribute("category", categoryEntity);
        return "admin/category/update";
    }

    // Update an existing category
    @PostMapping("/update")
    public String update(@ModelAttribute(name = "category") CategoryEntity categoryEntity) {
        categoryRepository.save(categoryEntity);
        return "redirect:/admin/category/updateCategory/" + categoryEntity.getId();
    }

    // Delete a category
    @GetMapping("/deleteCategory/{id}")
    public String delete(@PathVariable("id") Long id) {
        categoryRepository.deleteById(id);
        return "redirect:/admin/category";
    }
}
