package com.AgeniusAgent.Agenius.service;

import com.AgeniusAgent.Agenius.Model.Offres;
import jakarta.servlet.http.HttpServletRequest;
import com.AgeniusAgent.Agenius.Model.PhaseData;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
 import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
@Service
public class OffresService {

    private final RestTemplate restTemplate;
    private final RestTemplate restTemplate1;
   

    @Autowired
    public OffresService(@Qualifier("loadBalancedRestTemplate") RestTemplate loadBalancedRestTemplate,@Qualifier("externalRestTemplate1") RestTemplate restTemplate ) {
        this.restTemplate= loadBalancedRestTemplate;
        this.restTemplate1= restTemplate;
    }

    public List<Offres> findAll() {
        ResponseEntity<Offres[]> response = restTemplate.exchange(
                "http://job-offer-service/user/api/jobs",
                HttpMethod.GET,
                null,
                Offres[].class
        );
        return Arrays.asList(response.getBody());
    }

    public Offres findOffresByName(String name) {
        ResponseEntity<Offres> response = restTemplate.exchange(
                "http://job-offer-service/user/api/jobs/name/" + name,
                HttpMethod.GET,
                null,
                Offres.class
        );
        return response.getBody();
    }

    public Offres findOffreById(String id) {
        ResponseEntity<Offres> response = restTemplate.exchange(
                "http://job-offer-service/user/api/jobs/" + id,
                HttpMethod.GET,
                null,
                Offres.class
        );
        return response.getBody();
    }

    public Offres saveJob(Offres job) {
        String safeTitle = job.getName().replaceAll("[^a-zA-Z0-9\\-]", "_");
        String directoryPath = "docs/" + safeTitle;
        File directory = new File(directoryPath);
        if (!directory.exists()) directory.mkdirs();

        HttpEntity<Offres> requestEntity = new HttpEntity<>(job);
        ResponseEntity<Offres> response = restTemplate.exchange(
                "http://job-offer-service/user/api/jobs",
                HttpMethod.POST,
                requestEntity,
                Offres.class
        );
        return response.getBody();
    }

    public List<Map<String, String>> getCandidatesForOffer(String offerName) {
        String safeOfferName = offerName.replaceAll(" ", "_");
        String folderPath = "docs/" + safeOfferName;
        File folder = new File(folderPath);

        List<Map<String, String>> candidates = new ArrayList<>();
        if (!folder.exists() || !folder.isDirectory()) return candidates;

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
        if (!folder.exists() || !folder.isDirectory()) return 0;

        File[] files = folder.listFiles();
        return files != null ? files.length : 0;
    }

    public int updateNumberOfCVs(String offerName) {
        String safeOfferName = offerName.replaceAll(" ", "_");
        String folderPath = "docs/" + safeOfferName;
        File folder = new File(folderPath);

        int numberOfCVs = (folder.exists() && folder.isDirectory())
                ? (int) Arrays.stream(folder.listFiles())
                .filter(file -> file.getName().toLowerCase().endsWith(".pdf"))
                .count()
                : 0;

        // Appel à l'endpoint distant pour mise à jour
        String url = "http://job-offer-service/user/api/jobs/{offerName}/updateNumberOfCVs/{numbercv}";
        restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                Integer.class,
                offerName,
                numberOfCVs
        );

        return numberOfCVs;
    }

   public PhaseData getPhaseData(String offerName) {
        String encodedOfferName = URLEncoder.encode(offerName, StandardCharsets.UTF_8);
        String url = "http://job-offer-service/user/api/jobs/get-phase-data/" + encodedOfferName;

        ResponseEntity<PhaseData> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            PhaseData.class
        );

        return response.getBody();
    
}


    public void deleteJobDirectory(String jobTitle) {
        String safeTitle = jobTitle.replaceAll("[^a-zA-Z0-9\\-]", "_");
        String directoryPath = "docs/" + safeTitle;
        File directory = new File(directoryPath);

        if (directory.exists()) {
            deleteDirectoryRecursively(directory);
            System.out.println("Dossier supprimé pour le job : " + safeTitle);
        } else {
            System.out.println("Aucun dossier à supprimer pour le job : " + safeTitle);
        }
    }

    private void deleteDirectoryRecursively(File file) {
        if (file.isDirectory()) {
            for (File subFile : Objects.requireNonNull(file.listFiles())) {
                deleteDirectoryRecursively(subFile);
            }
        }
        file.delete();
    }

    public ResponseEntity<?> uploadLocalFiles(@PathVariable("offerName") String offerName,
                                              @RequestParam("files") MultipartFile[] files) {
        String sanitizedOfferName = offerName.replace(" ", "_");
        String uploadDir = "docs/" + sanitizedOfferName;

        File directory = new File(uploadDir);
        if (!directory.exists()) directory.mkdirs();

        for (MultipartFile file : files) {
            try {
                Path filePath = Paths.get(uploadDir, file.getOriginalFilename());
                Files.write(filePath, file.getBytes());
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Erreur lors de l'importation des fichiers.");
            }
        }

        return ResponseEntity.ok("Fichiers importés avec succès.");
    }

    public ResponseEntity<?> getCandidatesForOffer1(@PathVariable("offerName") String offerName) {
        String username = getCurrentUsername();
        Offres job = findOffresByName(offerName);
        if (job != null && username.equals(job.getCreatedBy())) {
            List<Map<String, String>> candidates = getCandidatesForOffer(offerName);
            return ResponseEntity.ok(candidates);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    public boolean deleteCandidateFile(String offerName, String fileName) {
        String safeOfferName = offerName.replaceAll(" ", "_");
        String directoryPath = "docs/" + safeOfferName;
        File file = new File(directoryPath, fileName);
        return file.exists() && file.isFile() && file.delete();
    }

    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) return null;

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return principal.toString();
    }

    public void updateJobStatus(String offerName, int progressPercent, String status, String toolName) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = Map.of(
                    "progressPercent", progressPercent,
                    "status", status,
                    "toolName", toolName
            );

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            String encodedOfferName = URLEncoder.encode(offerName, StandardCharsets.UTF_8);
            String url = "http://job-offer-service/user/api/jobs/updateStatus/" + encodedOfferName;

            ResponseEntity<Void> response = restTemplate1.exchange(
                    url,
                    HttpMethod.PUT,
                    requestEntity,
                    Void.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("✅ Job status updated for offer: " + offerName);
            } else {
                System.err.println("⚠️ Job status update failed with HTTP " + response.getStatusCode());
            }

        } catch (Exception e) {
            System.err.println("❌ Exception while updating job status: " + e.getMessage());
            e.printStackTrace();
        }
    }






}

