package com.example.agenius_back.service;

import com.example.agenius_back.entity.AppRole;
import com.example.agenius_back.entity.AppUser;
import com.example.agenius_back.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AppUserService {

    @Autowired
    private AppUserRepository appUserRepository;

    public List<AppUser> getAllUsers() {
        return appUserRepository.findAll();
    }

    public void deleteUserById(String id) {
        appUserRepository.deleteById(id);
    }
    public AppUser updateUser(String id, boolean actived, String roleName) {
        Optional<AppUser> optionalUser = appUserRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("Utilisateur introuvable");
        }

        AppUser user = optionalUser.get();
        user.setActived(actived);

        // On remplace les rôles existants (si tu veux seulement en ajouter, adapte cette partie)
        AppRole role = new AppRole();
        role.setRoleName(roleName);
        user.setRoles(List.of(role));

        return appUserRepository.save(user);
    }
}