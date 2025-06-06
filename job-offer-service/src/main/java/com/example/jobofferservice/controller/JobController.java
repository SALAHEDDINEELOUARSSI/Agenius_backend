package com.example.jobofferservice.controller;

import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpStatus;

import com.example.jobofferservice.model.PhaseData;
import com.example.jobofferservice.repository.JobRepository;
import com.example.jobofferservice.model.Job;
import com.example.jobofferservice.service.JobService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

import java.net.URLDecoder;

@RestController
@RequestMapping("/user/api/jobs")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class JobController {
    @Autowired
    private JobService jobService;
    @Autowired
    private JobRepository jobRepository;

    // Obtenir username depuis JWT
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) return null;

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return principal.toString();
    }

    // Lister les offres créées par l'utilisateur connecté
    @GetMapping
    public List<Job> getAllJobs() {
        String username = getCurrentUsername();
        System.out.println(username);
        return jobService.GetJobBycretedBy(username);

    }

    // Rechercher une offre par nom (accessible seulement si elle appartient à l'utilisateur)
    @GetMapping("/name/{name}")
    public ResponseEntity<Job> findJobByName(@PathVariable String name) {
        String username = getCurrentUsername();
        Job job = jobService.getJobByName(name);
        if (job != null && username.equals(job.getcreatedBy())) {
            return ResponseEntity.ok(job);
        }
        return ResponseEntity.status(403).build(); // interdit
    }

    // Obtenir une offre par ID (si elle appartient à l'utilisateur)
    @GetMapping("/{id}")
    public ResponseEntity<Job> getJobById(@PathVariable String id) {
        String username = getCurrentUsername();
        return jobService.getJobById(id)
                .filter(job -> username.equals(job.getcreatedBy()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(403).build());
    }

    // Créer une nouvelle offre (associe automatiquement le créateur)
    @GetMapping("/{id}/numberOfCVs")
    public ResponseEntity<Integer> getNumberOfCVs(@PathVariable String id) {
        Optional<Job> offre = jobService.getJobById(id);
        if (offre.isPresent()) {
            return ResponseEntity.ok(offre.get().getNumberOfCVs());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Job> createJob(@RequestBody Job job) {
        String username = getCurrentUsername();
        job.setcreatedBy(username);
        job.setStatus("not-started");
        job.setProgressPercent(0);
        job.setProcessingSteps(new ArrayList<>());
        Job createdJob = jobService.saveJob(job);
        return ResponseEntity.ok(createdJob);
    }

    // Mettre à jour une offre si elle appartient à l'utilisateur
    @PutMapping("/{id}")
    public ResponseEntity<Job> updateJob(@PathVariable String id, @RequestBody Job job) {
        String username = getCurrentUsername();
        return jobService.getJobById(id)
                .filter(existingJob -> username.equals(existingJob.getcreatedBy()))
                .map(existingJob -> {
                    job.setId(id);
                    job.setcreatedBy(username); // garder l'auteur original
                    job.setStatus("Not-started");
                    job.setProgressPercent(0);
                    job.setProcessingSteps(new ArrayList<>());
                    return ResponseEntity.ok(jobService.saveJob(job));
                })
                .orElse(ResponseEntity.status(403).build());
    }

    @CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", methods = RequestMethod.DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJob(@PathVariable String id) {
        String username = getCurrentUsername();
        return jobService.getJobById(id)
                .filter(job -> username.equals(job.getcreatedBy()))
                .map(job -> {
                    jobService.deleteJob(id);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.status(403).build());
    }

    // Liste des candidats postulés à une offre (si elle appartient à l'utilisateur)
    @GetMapping("/import-cvs/{offerName}")
    public ResponseEntity<?> getCandidatesForOffer(@PathVariable String offerName) {
        String username = getCurrentUsername();
        Job job = jobService.getJobByName(offerName);
        if (job != null && username.equals(job.getcreatedBy())) {
            List<Map<String, String>> candidates = jobService.getCandidatesForOffer(offerName);
            return ResponseEntity.ok(candidates);
        }
        return ResponseEntity.status(403).build();
    }



    //  Sauvegarde des données de phase (aucune restriction appliquée ici)
    @PostMapping("/save-phase-data")
    public ResponseEntity<?> savePhaseData(@RequestBody PhaseData phaseData) {
        jobService.savePhaseData(phaseData);
        return ResponseEntity.ok("Data saved successfully");
    }

    //importer les cvs si luser veut ca , et les stocker dans le doss de loffre selected




    @GetMapping("/{offerName}/updateNumberOfCVs/{numbercv}")
    public ResponseEntity<Integer> updateNumberOfCVs(@PathVariable String offerName,@PathVariable int numbercv) {
        int newNumberOfCVs = jobService.updateNumberOfCVs(offerName,numbercv);// je lai besoin pour la MAJ de numberofcvs pour la recuperer en list jobs
        return ResponseEntity.ok(newNumberOfCVs);
    }

    @GetMapping("/get-phase-data/{offerName}")
    public ResponseEntity<?> getPhaseData(@PathVariable("offerName") String offerName) {
        PhaseData phaseData = jobService.getPhaseData(offerName);
        System.out.println("Phase data: " + phaseData);
        System.out.println("Offer name: " + offerName);
        if (phaseData != null) {
            return ResponseEntity.ok(phaseData);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Phase data not found");
        }
    }
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", methods = {RequestMethod.PUT})
@PutMapping("/updateStatus/{offerName}")
public ResponseEntity<Void> updateJobStatus(
        @PathVariable("offerName") String offerName,
        @RequestBody Map<String, Object> payload) {

    System.out.println("Received PUT for offer offerName: " + offerName);
    String offerNameEncoded = URLDecoder.decode(offerName, StandardCharsets.UTF_8);
    System.out.println("Decoded offerName: " + offerNameEncoded);

    Job job = jobRepository.findByName(offerNameEncoded);
    if (job == null) {
        return ResponseEntity.notFound().build();
    }
    // Update progressPercent if present
    if (payload.containsKey("progressPercent")) {
        job.setProgressPercent((Integer) payload.get("progressPercent"));
    }
    // Update status if present
    if (payload.containsKey("status")) {
        job.setStatus((String) payload.get("status"));
    }
    
    if (payload.containsKey("toolName")) {
        String toolName = (String) payload.get("toolName");
        List<Job.ProcessingStep> steps = job.getProcessingSteps();
        steps = Optional.ofNullable(job.getProcessingSteps()).orElseGet(ArrayList::new);


        boolean found = false;
        for (Job.ProcessingStep step : steps) {
            if (step.getName().equals(toolName)) {
                // Mark completed only if not already done
                if (!step.isCompleted()) {
                    step.setCompleted(true);
                    step.setCompletedAt(new Date());
                }
                found = true;
                break;
            }
        }
        if (!found) {
            Job.ProcessingStep step = new Job.ProcessingStep();
            step.setName(toolName);
            step.setCompleted(true);
            step.setCompletedAt(new Date());
            steps.add(step);
        }

        job.setProcessingSteps(steps);
    }

    jobRepository.save(job);
    return ResponseEntity.ok().build();
}

}

