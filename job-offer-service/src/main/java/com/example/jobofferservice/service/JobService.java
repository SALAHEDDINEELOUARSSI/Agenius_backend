package com.example.jobofferservice.service;
import com.example.jobofferservice.model.PhaseData;
import com.example.jobofferservice.events.DeadlineEvent;
import com.example.jobofferservice.model.Job;
import com.example.jobofferservice.repository.PhaseDataRepository;

import com.example.jobofferservice.repository.JobRepository;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class JobService {

    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private RabbitTemplate rabbitTemplate;


    public Optional<Job> getJobById(String id) {
        return jobRepository.findById(id);
    }
    public Job getJobByName(String name) {
        return jobRepository.getJobByName(name);
    }


    public Job saveJob(Job job) {




        int numberOfCVs = calculateNumberOfCVs(job.getName());
        job.setNumberOfCVs(numberOfCVs);

        // Sauvegarder dans la base
        return jobRepository.save(job);
    }

    // Supprime un fichier ou un dossier avec tout son contenu
    private void deleteDirectoryRecursively(java.io.File file) {
        if (file.isDirectory()) {
            for (java.io.File subFile : file.listFiles()) {
                deleteDirectoryRecursively(subFile);
            }
        }
        file.delete();
    }
    public List<Job> getJobsByCreator(String username) {
        return jobRepository.findByCreatedBy(username);
    }


    // Supprime le dossier associé à un job
    private void deleteJobDirectory(String jobTitle) {
        String safeTitle = jobTitle.replaceAll("[^a-zA-Z0-9\\-]", "_");
        String directoryPath = "docs/" + safeTitle;
        java.io.File directory = new java.io.File(directoryPath);

        if (directory.exists()) {
            deleteDirectoryRecursively(directory);
            System.out.println("Dossier supprimé pour le job : " + safeTitle);
        } else {
            System.out.println("Aucun dossier à supprimer pour le job : " + safeTitle);
        }
    }
    // Supprime le job + son dossier associé
    public void deleteJob(String id) {
        Optional<Job> jobOptional = jobRepository.findById(id);
        if (jobOptional.isPresent()) {
            Job job = jobOptional.get();
            deleteJobDirectory(job.getName()); // utiliser le titre
            jobRepository.deleteById(id);
        } else {
            System.out.println("Job non trouvé pour suppression !");
        }
    }


    // Retourne la liste des fichiers (CVs) présents dans le dossier de l'offre spécifiée
    public List<Map<String, String>> getCandidatesForOffer(String offerName) {
        String safeOfferName = offerName.replaceAll(" ", "_");
        String folderPath = "docs/" + safeOfferName;
        File folder = new File(folderPath);

        List<Map<String, String>> candidates = new ArrayList<>();

        if (!folder.exists() || !folder.isDirectory()) {
            return candidates;
        }

        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    Map<String, String> candidate = new HashMap<>();
                    candidate.put("name", file.getName());
                    candidates.add(candidate);
                }
            }
        }

        return candidates;
    }

    public int calculateNumberOfCVs(String offerName) {
        String safeOfferName = offerName.replaceAll(" ", "_");
        String folderPath = "docs/" + safeOfferName;
        File folder = new File(folderPath);

        if (!folder.exists() || !folder.isDirectory()) {
            return 0;
        }

        File[] files = folder.listFiles();
        return files != null ? files.length : 0;
    }

    public int updateNumberOfCVs(String offerName,int numberOfCVs) {
        // mise a jour de numberofcv

        // Mettre à jour la base de données
        Job job = jobRepository.getJobByName(offerName);
        if (job != null) {
            job.setNumberOfCVs(numberOfCVs);
            jobRepository.save(job);
        }
        return numberOfCVs;
    }
    public PhaseData getPhaseData(String offerName) {
        return phaseDataRepository.findByOfferName(offerName);
    }



    public List<Job> getAllJobs() {
        List<Job> jobs = jobRepository.findAll();

        for (Job job : jobs) {
            String safeTitle = job.getName().replaceAll("[^a-zA-Z0-9\\-]", "_");
            int cvCount = calculateNumberOfCVs(safeTitle);

            if (job.getNumberOfCVs() != cvCount) {
                job.setNumberOfCVs(cvCount);
                jobRepository.save(job); // mise à jour en base
            }
        }

        return jobs;
    }

    // Méthode pour supprimer un fichier CV spécifique dans une offre
    public boolean deleteCandidateFile(String offerName, String fileName) {
        String safeOfferName = offerName.replaceAll(" ", "_");
        String directoryPath = "docs/" + safeOfferName;
        File file = new File(directoryPath, fileName);

        if (file.exists() && file.isFile()) {
            return file.delete();
        }

        return false;
    }


    @Autowired
    private PhaseDataRepository phaseDataRepository;

    public void savePhaseData(PhaseData phaseData) {
        // Pas besoin de recréer l'objet PhaseData, on utilise celui passé en paramètre
        System.out.println("Phase Status: " + phaseData.getPhaseStatus());
        System.out.println("phase3: " + phaseData.getPhase3());
        System.out.println("Deadline: " + phaseData.getDeadline());
        System.out.println("Deadline2: " + phaseData.getDeadline2());
        System.out.println("Offer Name: " + phaseData.getOfferName());

        // Sauvegarder dans la base de données MongoDB
        phaseDataRepository.save(phaseData);
    }
    public List <Job> GetJobBycretedBy(String cretedBy) {

        return jobRepository.findByCreatedBy(cretedBy);
    }

public void markEmailsAsSent(String offerName) {
        PhaseData phaseData = phaseDataRepository.findByOfferName(offerName);
        if (phaseData != null) {
            phaseData.setEmailSent(true);

            phaseDataRepository.save(phaseData);
        }
    }

    //here


@Scheduled(fixedRate = 6 * 60 * 1000)
public void checkDeadlines() {
    List<PhaseData> all = phaseDataRepository.findAll();
    long now = System.currentTimeMillis();

    for (PhaseData phase : all) {
    if (!phase.isEmailSent() && phase.getDeadline() != null) {
        if (phase.getPhaseStatus() == 1 ) { // only send event for phase 1
            try {
                LocalDate localDate = LocalDate.parse(phase.getDeadline());
                Instant deadlineInstant = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
                long deadlineTime = deadlineInstant.toEpochMilli();

                if (deadlineTime <= now) {
                    DeadlineEvent event = new DeadlineEvent(phase.getOfferName(), phase.getPhaseStatus());
                    rabbitTemplate.convertAndSend("phase-exchange", "phase.deadline.reached", event);

                    phase.setEmailSent(true);
                    phaseDataRepository.save(phase);
                }
            } catch (DateTimeParseException e) {
                System.err.println("Invalid deadline format for offer: " + phase.getOfferName() + " → " + phase.getDeadline());
            }
        }
    }

    if(phase.isEmailSent() && phase.getDeadline2() != null ){
        if (phase.getPhase3() == 1 && !phase.isPhase3reashed()) { // only send event for phase 1
            try {
                LocalDate localDate = LocalDate.parse(phase.getDeadline2());
                Instant deadlineInstant = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
                long deadlineTime = deadlineInstant.toEpochMilli();

                if (deadlineTime <= now) {
                    DeadlineEvent event = new DeadlineEvent(phase.getOfferName(), phase.getPhaseStatus());
                    rabbitTemplate.convertAndSend("phase-exchange", "phase.deadline.reached", event);
                    phase.setPhase3reashed(true);
                    phaseDataRepository.save(phase);
                }
            } catch (DateTimeParseException e) {
                System.err.println("Invalid deadline format for offer: " + phase.getOfferName() + " → " + phase.getDeadline());
            }
        }
    }
    }

}

   /*  @Scheduled(fixedRate = 5 * 60 * 1000)
public void checkDeadlines() {
    List<PhaseData> all = phaseDataRepository.findAll();
    long now = System.currentTimeMillis();

    for (PhaseData phase : all) {
        if (!phase.isEmailSent() && phase.getDeadline() != null) {
            try {
                // Convert "2025-05-19" to Instant at start of day
                LocalDate localDate = LocalDate.parse(phase.getDeadline());
                Instant deadlineInstant = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
                long deadlineTime = deadlineInstant.toEpochMilli();

                if (deadlineTime <= now) {
                    Map<String, Object> event = new HashMap<>();
                    event.put("offerName", phase.getOfferName());
                    event.put("phaseStatus", phase.getPhaseStatus());
                    rabbitTemplate.convertAndSend("phase-exchange", "phase.deadline.reached", event);

                    phase.setEmailSent(true);
                    phaseDataRepository.save(phase);
                }
            } catch (DateTimeParseException e) {
                System.err.println("Invalid deadline format for offer: " + phase.getOfferName() + " → " + phase.getDeadline());
            }
        }
    }
}*/
}