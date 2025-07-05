package com.example.shopee.controller.admin;

import com.example.shopee.entity.CategoryEntity;
import com.example.shopee.entity.UserEntity;
import com.example.shopee.entity.VoucherEntity;
import com.example.shopee.repository.UserRepository;
import com.example.shopee.repository.VoucherRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/voucher")
public class AdminVoucherController {

    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("")
    public String listVouchers(Model model,
                               @RequestParam(value = "keyword", required = false) String keyword,
                               @RequestParam(value = "page", defaultValue = "0") int page,
                               @RequestParam(value = "size", defaultValue = "5") int size) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        List<VoucherEntity> vouchers = voucherRepository.findAllByUserEntity_Id(user.getId());

        if (keyword != null && !keyword.isEmpty()) {
            vouchers = vouchers.stream()
                    .filter(v -> v.getName() != null && v.getName().toLowerCase().contains(keyword.toLowerCase()))
                    .toList();
        }

        int totalItems = vouchers.size();
        int totalPages = (int) Math.ceil((double) totalItems / size);
        int start = page * size;
        int end = Math.min(start + size, totalItems);
        List<VoucherEntity> pagedVouchers = vouchers.subList(start, end);

        model.addAttribute("vouchers", pagedVouchers);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        return "admin/voucher/list";
    }

    @GetMapping("/insert")
    public String addVoucherForm(Model model) {
        model.addAttribute("voucher", new VoucherEntity());
        return "admin/voucher/insert";
    }

    @PostMapping("/save")
    public String saveVoucher(@ModelAttribute("voucher") VoucherEntity voucher, Model model) {

        Optional<VoucherEntity> byName = voucherRepository.findByName(voucher.getName());
        if (byName.isPresent()) {
            model.addAttribute("error", "Voucher này đã tồn tại.");
            return "admin/voucher/insert";
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(email).orElseThrow();
        voucher.setUserEntity(user);
        voucherRepository.save(voucher);
        return "redirect:/admin/voucher?save=true";
    }


    @GetMapping("/update/{id}")
    public String updateVoucherPage(@PathVariable("id") Long id, Model model) {
        Optional<VoucherEntity> voucherOpt = voucherRepository.findById(id);
        if (voucherOpt.isPresent()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            voucherOpt.ifPresent(voucher -> {
                model.addAttribute("voucher", voucher);
                model.addAttribute("startTimeFormatted", voucher.getStartTime().format(formatter));
                model.addAttribute("endTimeFormatted", voucher.getEndTime().format(formatter));
            });

            model.addAttribute("voucher", voucherOpt.get());

            return "admin/voucher/update";
        }
        return "redirect:/admin/voucher";
    }

    @PostMapping("/update")
    public String updateVoucher(@ModelAttribute VoucherEntity voucher, Model model) {
        Optional<VoucherEntity> byName = voucherRepository.findByName(voucher.getName());
        if (byName.isPresent() && !byName.get().getId().equals(voucher.getId())) {
            model.addAttribute("error", "Voucher này đã tồn tại.");
            model.addAttribute("voucher", voucher);
            return "admin/voucher/update";
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(email).orElse(null);
        voucher.setUserEntity(user);
        voucherRepository.save(voucher);
        return "redirect:/admin/voucher?update=true";
    }

    @GetMapping("/delete/{id}")
    public String deleteVoucher(@PathVariable("id") Long id) {
        voucherRepository.deleteById(id);
        return "redirect:/admin/voucher?delete=true";
    }
}
