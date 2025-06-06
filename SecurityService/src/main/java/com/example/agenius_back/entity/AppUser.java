package com.example.agenius_back.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@Document(collection = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AppUser {
    @Id
    private String id; // Change from Long to String for MongoDB compatibility
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private boolean actived;
    private Collection<AppRole> roles = new ArrayList<>();
    //rénitialisation mdp :
    private String resetToken;
    private Date tokenExpiration;

}
