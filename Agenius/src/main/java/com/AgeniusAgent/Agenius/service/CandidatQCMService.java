package  com.AgeniusAgent.Agenius.service;

import com.AgeniusAgent.Agenius.Model.Offres;
import com.AgeniusAgent.Agenius.entity.CandidatScoreStats;
import com.AgeniusAgent.Agenius.entity.CandidatsAccepted;
import com.AgeniusAgent.Agenius.entity.UserResponse;
import com.AgeniusAgent.Agenius.repository.CandidatsAcceptedRepository;
import com.AgeniusAgent.Agenius.repository.CandidatsSelectedRepository;
import com.AgeniusAgent.Agenius.repository.UserResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CandidatQCMService {
    @Autowired
    private UserResponseRepository repository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private CandidatsAcceptedRepository candidatsAcceptedRepository;
    @Autowired
    private CandidatsSelectedRepository candidatsSelectedRepository;
    @Autowired
    private OffresService offresService;
    public void sendEmailsToCandidates(String jobName) {



        // Étape 1 : Récupérer les candidats ayant score > 70 pour cette offre précise
        List<UserResponse> acceptes = repository.findByScoreGreaterThanAndOffrename(70, jobName);

         
        // Envoyer l'e-mail aux acceptés
        for (UserResponse candidat : acceptes) {
            emailService.sendSuccessEmail(candidat.getEmail(), jobName,offresService.getPhaseData(jobName).getDeadline2());
            //System.out.println(candidat.getEmail()+""+candidat.getScore()+" score accepted for job: "+jobName);
            // Sauvegarder le candidat accepté dans MongoDB
            CandidatsAccepted savedCandidat = new CandidatsAccepted();

            savedCandidat.setEmail(candidat.getEmail());
            savedCandidat.setJobName(jobName);  // Enregistrer le nom de l'offre
            savedCandidat.setScore(candidat.getScore()); // Enregistrer le score du candidat
            savedCandidat.setName(candidatsSelectedRepository.getNameByEmail(candidat.getEmail()));

            // Sauvegarder dans la base de données
            candidatsAcceptedRepository.save(savedCandidat);
        }
    }

    public CandidatScoreStats getCandidatScoreStatsByJobId(String jobId) {

        // Récupérer l'offre par ID via le service
        Offres offre = offresService.findOffreById(jobId);

        if (offre == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Offre introuvable pour l'ID : " + jobId);
        }

        // Récupérer le nom de l'offre
        String jobName = offre.getName(); // ou getTitle(), selon ton objet Offres

        List<UserResponse> candidats = repository.findByOffrename(jobName);
        int reussis = 0;
        int moyens = 0;
        int echoues = 0;

        for (UserResponse c : candidats) {
            if (c.getScore() > 70) {
                reussis++;
            } else if (c.getScore() >= 50) {
                moyens++;
            } else {
                echoues++;
            }
        }
        return new CandidatScoreStats(reussis, moyens, echoues);
    }

    public void sendEmailsAndUpdateStatus(String jobName) {
    // Step 1: Send emails and store accepted candidates
    
         sendEmailsToCandidates(jobName);

    // Step 2: Update job status to finished
    offresService.updateJobStatus(
        jobName,
        100,
        "finished",
        "Candidates Accepted have been notified via email!"
    );
}

}
