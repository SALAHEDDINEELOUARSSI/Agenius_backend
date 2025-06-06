package  com.AgeniusAgent.Agenius.web;


import com.AgeniusAgent.Agenius.Model.Offres;
import com.AgeniusAgent.Agenius.entity.CandidatScoreStats;
import com.AgeniusAgent.Agenius.repository.CandidatsAcceptedRepository;
import com.AgeniusAgent.Agenius.service.CandidatQCMService;
import com.AgeniusAgent.Agenius.service.OffresService;
import com.AgeniusAgent.Agenius.service.UserResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/candidats")
public class CandidatQCMController {
    @Autowired
    private CandidatQCMService candidatQCMService;
    @Autowired
    CandidatsAcceptedRepository candidatsAcceptedRepository;
    @Autowired
    private UserResponseService userResponseService;
    @Autowired
    private OffresService offresService;

    @GetMapping("/send-success-emails")
    public String sendSuccessEmails(@RequestParam("jobName") String jobName) {
        // Passer l'ID de l'offre à la méthode qui envoie les e-mails
        candidatQCMService.sendEmailsToCandidates(jobName);
        return "Emails de succès envoyés aux candidats avec un score > 70 pour l'offre " + jobName;
    }



    @GetMapping("/score-stats/{jobId}")
    public CandidatScoreStats getScoreStats(@PathVariable("jobId") String jobId) {
        return candidatQCMService.getCandidatScoreStatsByJobId(jobId);
    }
    @GetMapping("/accepted/count")
    public long getNombreCandidatsAcceptes(@RequestParam("jobId") String jobId) {
        // Récupérer l'offre par ID via le service
        Offres offre = offresService.findOffreById(jobId);

        if (offre == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Offre introuvable pour l'ID : " + jobId);
        }

        // Récupérer le nom de l'offre
        String jobName = offre.getName(); // ou getTitle(), selon ton objet Offres

        // Compter les candidats acceptés par nom de l'offre
        return candidatsAcceptedRepository.countByJobName(jobName);
    }
    @GetMapping("/average-score/{jobId}")
    public double getAverageScore(@PathVariable("jobId") String jobId)
    {
        return userResponseService.calculateAverageScoreByJobId(jobId);
    }


}
