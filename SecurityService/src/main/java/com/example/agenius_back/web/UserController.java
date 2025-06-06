package com.example.agenius_back.web;

import com.auth0.jwt.algorithms.Algorithm;
import com.example.agenius_back.repository.AppRoleRepository;
import com.example.agenius_back.entity.AppRole;
import com.example.agenius_back.sec.SecurityParams;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import com.example.agenius_back.entity.AppUser;
import com.example.agenius_back.service.AccountService;
import org.bouncycastle.math.ec.rfc8032.Ed25519;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.Collection;
@RestController
public class UserController {
    @Autowired
    private AccountService accountService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private AppRoleRepository appRoleRepository;
    @PostMapping("/register")
    public AppUser register(@RequestBody  UserForm userForm){


        return  accountService.saveUser(
                userForm.getUsername(),userForm.getPassword(),userForm.getConfirmedPassword());
    }
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/login")
    public String login(@RequestBody UserForm userForm) {
        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userForm.getUsername(), userForm.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return "Login successful for user: " + userForm.getUsername();
        }
        catch (Exception e ){
            return "Login failed: " + e.getMessage();
        }

    }
    @PostMapping("/refresh")
    public void refresh(HttpServletRequest request, HttpServletResponse response) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String authToken = authHeader.substring(7);
            Algorithm algorithm = Algorithm.HMAC256(SecurityParams.SECRET);
        }

    }

//    @GetMapping("/getRole")
//    public Collection<AppRole> login(@RequestParam("id") Long id) {
//       return appRoleRepository.findAppRoleById(id);
//    }
}
@Data
class UserForm{
    private String username;private String password;private String confirmedPassword;
}
