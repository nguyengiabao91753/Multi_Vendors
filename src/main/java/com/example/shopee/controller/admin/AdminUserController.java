package com.example.shopee.controller.admin;

import com.example.shopee.entity.RoleEntity;
import com.example.shopee.entity.UserEntity;
import com.example.shopee.enums.RoleEnum;
import com.example.shopee.repository.RoleRepository;
import com.example.shopee.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/user")
public class AdminUserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @GetMapping("")
    public String userListPage(Model model,
                               @RequestParam(value = "email", required = false) String emailParam,
                               @RequestParam(value = "role", required = false) String roleParam,
                               @RequestParam(value = "page", defaultValue = "0") int page,
                               @RequestParam(value = "size", defaultValue = "5") int size) {

        List<UserEntity> allUsers = userRepository.findAll();

        if (emailParam != null && !emailParam.isEmpty()) {
            allUsers = allUsers.stream()
                    .filter(u -> u.getEmail() != null && u.getEmail().toLowerCase().contains(emailParam.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (roleParam != null && !roleParam.isEmpty()) {
            allUsers = allUsers.stream()
                    .filter(u -> u.getRoleEntities().stream()
                            .anyMatch(role -> role.getName().name().equalsIgnoreCase(roleParam)))
                    .collect(Collectors.toList());
        }

        int totalItems = allUsers.size();
        int totalPages = (int) Math.ceil((double) totalItems / size);

        int start = page * size;
        int end = Math.min(start + size, totalItems);

        List<UserEntity> users = allUsers.subList(start, end);

        model.addAttribute("users", users);
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("selectedRole", roleParam);
        model.addAttribute("email", emailParam);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        return "admin/user/list";
    }


    @GetMapping("/add")
    public String addUserForm(Model model) {
        model.addAttribute("user", new UserEntity());
        model.addAttribute("roles", roleRepository.findAll());
        return "admin/user/insert";
    }

    @PostMapping("/save")
    public String saveUser(@ModelAttribute("user") UserEntity user,
                           @RequestParam("role") String role) {
        RoleEntity selectedRole = roleRepository.findByName(RoleEnum.valueOf(role));
        user.setRoleEntities(Collections.singleton(selectedRole));
        userRepository.save(user);
        return "redirect:/admin/user?save=true";
    }

    @PostMapping("/update-status/{id}")
    public String updateUserStatus(@PathVariable Long id,
                                   @RequestParam("status") Integer status) {
        Optional<UserEntity> optional = userRepository.findById(id);
        if (optional.isPresent()) {
            UserEntity user = optional.get();
            user.setStatus(status);
            userRepository.save(user);
        }
        return "redirect:/admin/user?update=true";
    }
}
