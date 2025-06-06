package com.example.jobofferservice.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Data
@Document(collection = "JobOffer")
public class Job {

    @Id
    private String id;
    private String name;
    private String description;
    private String company;
    private String docsPath;
    private int numberOfCVs;
    private Collection<String> competencies;
    private String createdBy;
    private String status;
    //private List<String> steps; // e.g. ["Matching CVs", "Sending QCMs"]
    private int progressPercent;
     private List<ProcessingStep> Steps;
    private Date lastUpdated;
    
    @Data
    public static class ProcessingStep {
        private String name;
        private boolean completed;
        private Date completedAt;
        // Getter and Setter for 'name'
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        // Getter and Setter for 'completed'
        public boolean isCompleted() {
            return completed;
        }

        public void setCompleted(boolean completed) {
            this.completed = completed;
        }

        // Getter and Setter for 'completedAt'
        public Date getCompletedAt() {
            return completedAt;
        }

        public void setCompletedAt(Date completedAt) {
            this.completedAt = completedAt;
        }
    }



    public Job() {
    }
    public Job(String name, String description, String company) {
        this.name = name;
        this.description = description;
        this.company = company;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCompany() {
        return company;
    }
    public void setCompany(String company) {
        this.company = company;
    }
    public String getDocsPath() {
        return docsPath;
    }
    public void setDocsPath(String docsPath) {
        this.docsPath = docsPath;
    }
    public int getNumberOfCVs() {
        return numberOfCVs;
    }
    
    public void setNumberOfCVs(int numberOfCVs) {
        this.numberOfCVs = numberOfCVs;
    }


    public String getcreatedBy() {
        return createdBy;
    }

    public void setcreatedBy(String username) {
        this.createdBy = username;
    }
     public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public List<ProcessingStep> getProcessingSteps() {
    
        return Steps;
    }
    public void setProcessingSteps(List<ProcessingStep> Steps) {
        this.Steps = Steps;
    }
    public int getProgressPercent() {
        return progressPercent;
    }
    public void setProgressPercent(int progressPercent) {
        this.progressPercent = progressPercent;
    }
}
