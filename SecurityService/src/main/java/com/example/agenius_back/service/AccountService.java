package com.example.agenius_back.service;

import com.example.agenius_back.entity.AppRole;
import com.example.agenius_back.entity.AppUser;

public interface AccountService {
    public AppUser saveUser(String username,String password,String confirmedPassword);
    public AppRole save(AppRole role);
    public AppUser loadUserByUsername(String username);
    public void addRoleToUser(String username,String rolename);
}
