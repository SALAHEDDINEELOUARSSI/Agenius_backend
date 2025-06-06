package com.example.agenius_back.web;

import com.example.agenius_back.entity.AppUser;
import com.example.agenius_back.entity.UpdateUserDTO;
import com.example.agenius_back.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/api/users")
public class AppUserController {
    @Autowired
    private AppUserService appUserService;

    @GetMapping
    public ResponseEntity<List<AppUser>> getAllUsers() {
        List<AppUser> users = appUserService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        appUserService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{id}")
    public AppUser updateUser(@PathVariable String id, @RequestBody UpdateUserDTO updateUserDTO) {
        return appUserService.updateUser(id, updateUserDTO.isActived(), updateUserDTO.getRole());
    }

}
