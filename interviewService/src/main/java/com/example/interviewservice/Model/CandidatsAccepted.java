package com.example.interviewservice.Model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CandidatsAccepted {

    private String id;
    private String email;
    private String jobName;
    private String createdBy;
    private String createdDate;
    private String phone;
    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getJobName() {
        return jobName;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public String getPhone() {
        return phone;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


}
