/*package com.AgeniusAgent.Agenius.agent;


import com.AgeniusAgent.Agenius.entity.*;
import com.AgeniusAgent.Agenius.service.*;

import com.AgeniusAgent.Agenius.repository.CandidatsSelectedRepository;
import com.AgeniusAgent.Agenius.web.QCMWebController;
import dev.langchain4j.agent.tool.Tool;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import com.AgeniusAgent.Agenius.Model.Offres;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class TestMultiAgentTools {
    private final CandidatsSelectedRepository candidatsSelectedRepository;

    private final JdbcTemplate jdbcTemplate;

    private final AgentProgressNotifier progressNotifier;
    private final OffresService offresRepository;
    private final AgentProgressContext context;

    //2
    private final QcmEmailService qcmEmailService;//ok
    private final GeminiQCMService geminiQCMService;//ok
    private final CandidatQCMService candidatQCMService;
    private final QCMWebController qcmWebController;
    private final QuestionCacheService questionCacheService; // Injection du service de cache des questions

    public TestMultiAgentTools(CandidatsSelectedRepository candidatsSelectedRepository,
                               OffresService offresRepository,
                               JdbcTemplate jdbcTemplate,
                               QcmEmailService qcmEmailService,
                               GeminiQCMService geminiService,
                               QCMWebController qcmWebController,
                               CandidatQCMService candidatQCMService,
                               QuestionCacheService questionCacheService,
                               AgentProgressNotifier progressNotifier

) {
        this.qcmEmailService = qcmEmailService;
        this.geminiQCMService = geminiService;
        this.candidatsSelectedRepository = candidatsSelectedRepository;
        this.offresRepository = offresRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.candidatQCMService=candidatQCMService;
        this.qcmWebController=qcmWebController;
        this.questionCacheService=questionCacheService;
        this.progressNotifier = progressNotifier;
        this.context = new AgentProgressContext(5);

    }

    //1
    @Tool("Save a selected candidate to the CandidatsSelected entity.")
    public void saveCandidatsSelected(CandidatsSelected candidatsSelected,String offername) {

        candidatsSelectedRepository.save(candidatsSelected);

        context.setProgress();
        int progress = context.incrementAndGetProgress();
        String toolName = "Save the selected candidate to the CandidatsSelected entity !";
        progressNotifier.notifyProgress(offername, toolName, progress);
        offresRepository.updateJobStatus(offername, progress, "in-progress",toolName);
        System.out.println("offre name : "+offername);
    }

    @Tool("Search for a job offer by its name.")
    public Offres findOffreByName(String name) {
        int progress = context.incrementAndGetProgress();
        String toolName = "Search for the job offer by its name !";
        progressNotifier.notifyProgress(name, toolName, progress);
        offresRepository.updateJobStatus(name, progress, "in-progress",toolName);
       return offresRepository.findOffresByName(name);

    }


    @Tool("Generate  multiple-choice question tests (QCM) for the selected candidates ")
    public void generateQcmForSelected(String offreName) throws IOException {
        List<Question> questions = geminiQCMService.generateQuestions(offreName);
        // Mettre les questions dans le cache pour l'offre donnée
        questionCacheService.save(offreName, questions);
        int progress = context.incrementAndGetProgress();
        String toolName = "Generate multiple-choice question tests (QCM) for the selected candidates !";
        System.out.println("generateQcmForSelected"+offreName);
        progressNotifier.notifyProgress(offreName, toolName, progress);
        offresRepository.updateJobStatus(offreName, progress, "in-progress",toolName);
    }

    @Tool("inject the questions into the hTML template for the given  job title")
    public void injectQuestions(String poste, String email,String phone)throws IOException
    {

        Model model = new ExtendedModelMap();
        qcmWebController.getQuestions(poste,email,phone,model);
        int progress = context.incrementAndGetProgress();
        String toolName = "Inject the questions into the hTML template for the given job title !";
        System.out.println("poste injectQuestions : "+poste);
        progressNotifier.notifyProgress(poste, toolName, progress);
        offresRepository.updateJobStatus(poste, progress, "in-progress",toolName);

    }



    @Tool("Send the QCM HTML template by email to all selected candidates for the given job title.")
    public void sendQcm(String offreName) throws MessagingException {
        qcmEmailService.sendQcmToAllCandidates(offreName);
        int progress = context.incrementAndGetProgress();
        String toolName = "Send the QCM HTML template by email to all selected candidates for the given job title !";
        System.out.println("offre name sendQcm : "+offreName);
        progressNotifier.notifyProgress(offreName, toolName, progress);
        offresRepository.updateJobStatus(offreName, progress, "waiting-responses",toolName);

    }

}
*/package com.AgeniusAgent.Agenius.agent;

import com.AgeniusAgent.Agenius.entity.*;
import com.AgeniusAgent.Agenius.service.*;
import com.AgeniusAgent.Agenius.repository.CandidatsSelectedRepository;
import com.AgeniusAgent.Agenius.web.QCMWebController;
import dev.langchain4j.agent.tool.Tool;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import com.AgeniusAgent.Agenius.Model.Offres;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class TestMultiAgentTools {

    private final CandidatsSelectedRepository candidatsSelectedRepository;
    private final JdbcTemplate jdbcTemplate;
    private final AgentProgressNotifier progressNotifier;
    private final OffresService offresRepository;
    private final QcmEmailService qcmEmailService;
    private final GeminiQCMService geminiQCMService;
    private final CandidatQCMService candidatQCMService;
    private final QCMWebController qcmWebController;
    private final QuestionCacheService questionCacheService;

    // New map for job-specific progress tracking
    private final Map<String, AgentProgressContext> contextMap = new ConcurrentHashMap<>();

    public TestMultiAgentTools(CandidatsSelectedRepository candidatsSelectedRepository,
                               OffresService offresRepository,
                               JdbcTemplate jdbcTemplate,
                               QcmEmailService qcmEmailService,
                               GeminiQCMService geminiService,
                               QCMWebController qcmWebController,
                               CandidatQCMService candidatQCMService,
                               QuestionCacheService questionCacheService,
                               AgentProgressNotifier progressNotifier) {
        this.qcmEmailService = qcmEmailService;
        this.geminiQCMService = geminiService;
        this.candidatsSelectedRepository = candidatsSelectedRepository;
        this.offresRepository = offresRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.candidatQCMService = candidatQCMService;
        this.qcmWebController = qcmWebController;
        this.questionCacheService = questionCacheService;
        this.progressNotifier = progressNotifier;
    }

    // Get or create AgentProgressContext per job offer
    private AgentProgressContext getContext(String offerName) {
        return contextMap.computeIfAbsent(offerName, k -> new AgentProgressContext(5));
    }

    // 1. Save selected candidates
    @Tool("Save a selected candidate to the CandidatsSelected entity.")
    public void saveCandidatsSelected(CandidatsSelected candidatsSelected, String offerName) {
        candidatsSelectedRepository.save(candidatsSelected);

        AgentProgressContext context = getContext(offerName);
        context.setProgress(); // Optional: reset at start
        int progress = context.incrementAndGetProgress();

        String toolName = "Save the selected candidate to the CandidatsSelected entity!";
        progressNotifier.notifyProgress(offerName, toolName, progress);
        offresRepository.updateJobStatus(offerName, progress, "finished-phase1", toolName);

        System.out.println("offre name : " + offerName);
    }

    // 2. Find job offer
    @Tool("Search for a job offer by its name.")
    public Offres findOffreByName(String name) {
        AgentProgressContext context = getContext(name);
        int progress = context.incrementAndGetProgress();

        String toolName = "Search for the job offer by its name!";
        progressNotifier.notifyProgress(name, toolName, progress);
        offresRepository.updateJobStatus(name, progress, "in-progress", toolName);

        return offresRepository.findOffresByName(name);
    }

    // 3. Generate QCM questions
    @Tool("Generate multiple-choice question tests (QCM) for the selected candidates")
    public void generateQcmForSelected(String offerName) throws IOException {
        List<Question> questions = geminiQCMService.generateQuestions(offerName);
        questionCacheService.save(offerName, questions);

        AgentProgressContext context = getContext(offerName);
        int progress = context.incrementAndGetProgress();

        String toolName = "Generate multiple-choice question tests (QCM) for the selected candidates!";
        progressNotifier.notifyProgress(offerName, toolName, progress);
        offresRepository.updateJobStatus(offerName, progress, "in-progress", toolName);

        System.out.println("generateQcmForSelected: " + offerName);
    }

    // 4. Inject questions into HTML
    @Tool("Inject the questions into the HTML template for the given job title")
    public void injectQuestions(String poste,String email,String phone) throws IOException {
        Model model = new ExtendedModelMap();
        qcmWebController.getQuestions(poste,email,phone, model);

        AgentProgressContext context = getContext(poste);
        int progress = context.incrementAndGetProgress();

        String toolName = "Inject the questions into the HTML template for the given job title!";
        progressNotifier.notifyProgress(poste, toolName, progress);
        offresRepository.updateJobStatus(poste, progress, "in-progress", toolName);

        System.out.println("poste injectQuestions : " + poste);
    }

    // 5. Send QCM by email
    @Tool("Send the QCM HTML template by email to all selected candidates for the given job title.")
    public void sendQcm(String offerName) throws MessagingException {
        qcmEmailService.sendQcmToAllCandidates(offerName);

        AgentProgressContext context = getContext(offerName);
        int progress = context.incrementAndGetProgress();

        String toolName = "Send the QCM HTML template by email to all selected candidates for the given job title!";
        progressNotifier.notifyProgress(offerName, toolName, progress);
        offresRepository.updateJobStatus(offerName, progress, "waiting-responses", toolName);

        System.out.println("offre name sendQcm : " + offerName);
    }
}
