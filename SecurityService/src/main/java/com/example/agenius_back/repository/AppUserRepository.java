package com.example.agenius_back.repository;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
import com.example.agenius_back.entity.AppUser;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@RepositoryRestResource


public interface AppUserRepository extends MongoRepository<AppUser,String> {
    public AppUser findByUsername(String username);
    //pour rénitialisation mdp :
    Optional<AppUser> findByResetToken(String resetToken);
    List<AppUser> findAll();


}
