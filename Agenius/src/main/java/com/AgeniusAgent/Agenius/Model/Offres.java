package com.AgeniusAgent.Agenius.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Collection;


@Data


public class Offres {
    private String id;
    private String name;
    private String description;
    private String company;
    private Collection <String> competencies;
    private String createdBy;

    public  String getCreatedBy() {
        return createdBy;
    }
    public String getName(){
        return name;
    }

    public String getDescription(){ return description;}
}
