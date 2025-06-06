package com.example.agenius_back.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Document(collection = "roles") // Change collection name if needed
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AppRole {
    @Id
    private String id;  // Change from Long to String for MongoDB compatibility
    private String roleName;

    public AppRole(String roleName) {
        this.roleName = roleName;
    }
}
