package com.example.agenius_back.service;

import com.example.agenius_back.repository.AppRoleRepository;  // Changement ici
import com.example.agenius_back.repository.AppUserRepository;
import com.example.agenius_back.entity.AppRole;
import com.example.agenius_back.entity.AppUser;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {
    private AppUserRepository appUserRepository;
    private AppRoleRepository appRoleRepository;  // Changement ici
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public AccountServiceImpl(AppUserRepository appUserRepository,
                              AppRoleRepository appRoleRepositor,
                              BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.appUserRepository = appUserRepository;
        this.appRoleRepository = appRoleRepositor;  // Changement ici
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public AppUser saveUser(String username, String password, String confirmedPassword) {
        AppUser user = appUserRepository.findByUsername(username);
        if (user != null) throw new RuntimeException("User already exists");
        if (!password.equals(confirmedPassword)) throw new RuntimeException("Please confirm your password");
        AppUser appUser = new AppUser();
        appUser.setUsername(username);
        appUser.setActived(true);
        Collection<AppRole> approlesc = new ArrayList<>();
         AppRole approle=appRoleRepository.getAppRoleByRoleName("USER") ;
        approlesc.add( approle);
        appUser.setRoles(approlesc) ;

        appUser.setPassword(bCryptPasswordEncoder.encode(password));
        appUserRepository.save(appUser);
        addRoleToUser(username, "USER");
        return appUser;
    }

    @Override
    public AppRole save(AppRole role) {
        return appRoleRepository.save(role);  // Changement ici
    }

    @Override
    public AppUser loadUserByUsername(String username) {
        return appUserRepository.findByUsername(username);
    }

    @Override
    public void addRoleToUser(String username, String rolename) {
        AppUser appUser = appUserRepository.findByUsername(username);
        AppRole appRole = appRoleRepository.findByRoleName(rolename);
        System.out.println("bonjour");// Changement ici
        Collection <AppRole> approlesc=appUser.getRoles();
        approlesc.add( appRole);
            appUser.setRoles(approlesc) ;
    }
}
