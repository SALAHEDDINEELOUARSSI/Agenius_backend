package com.example.agenius_back.repository;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.agenius_back.entity.AppRole;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@RepositoryRestResource

public interface AppRoleRepository extends MongoRepository<AppRole,String> {
    public AppRole findByRoleName(String roleName);
    public Collection<AppRole> findAppRoleById(long id );

    AppRole getAppRoleByRoleName(String roleName);
}
