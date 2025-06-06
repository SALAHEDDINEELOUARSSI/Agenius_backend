package com.AgeniusAgent.Agenius.web;

//import com.AgeniusAgent.Agenius.agent.SelectionCVsAgentsImpl;

import com.AgeniusAgent.Agenius.agent.SelectionCvsAgents;
import com.AgeniusAgent.Agenius.entity.UserResponse;
import com.AgeniusAgent.Agenius.repository.CandidatsAcceptedRepository;
import com.AgeniusAgent.Agenius.service.EmbeddingStoreIngestorProvider;
import com.AgeniusAgent.Agenius.Model.Offres;
import com.AgeniusAgent.Agenius.config.AIconfig;
import com.AgeniusAgent.Agenius.repository.CandidatsSelectedRepository;
import com.AgeniusAgent.Agenius.repository.UserResponseRepository;
import com.AgeniusAgent.Agenius.service.AgentProgressNotifier;
import com.AgeniusAgent.Agenius.config.AIconfig.*;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.AgeniusAgent.Agenius.service.OffresService;
import org.springframework.web.multipart.MultipartFile;
import dev.langchain4j.model.embedding.EmbeddingModel;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.tools.ant.types.resources.MultiRootFileSet.SetType.file;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequestMapping("/user/api")

public class SelectionCvsAgentsController {

    private final SelectionCvsAgents selectionCvsAssistent;
    private final CandidatsSelectedRepository candidatsSelectedRepository;
    private final CandidatsAcceptedRepository candidatesAcceptedRepository;
    private final OffresService offresService;
    private final UserResponseRepository userResponseRepository;
    private final AgentProgressNotifier agentProgressNotifier;
    private final DocumentParser documentParser;
    private  final  EmbeddingStoreIngestorProvider embeddingStoreIngestorProvider;





    public SelectionCvsAgentsController(
            @Qualifier("selectionCvsAsistent") SelectionCvsAgents selectionCvsAssistent, CandidatsSelectedRepository candidatsSelectedRepository,CandidatsAcceptedRepository candidatsAcceptedRepository
            , OffresService offresRepository, UserResponseRepository userResponseRepository, AgentProgressNotifier agentProgressNotifier, EmbeddingStoreIngestorProvider ingestor

      ) {
        this.selectionCvsAssistent = selectionCvsAssistent;
        this.candidatsSelectedRepository = candidatsSelectedRepository;
        this.offresService = offresRepository;
        this.userResponseRepository = userResponseRepository;
        this.agentProgressNotifier = agentProgressNotifier;
        this.embeddingStoreIngestorProvider = ingestor;
        this.documentParser = new ApachePdfBoxDocumentParser();
        this.candidatesAcceptedRepository = candidatsAcceptedRepository;

    }


  @GetMapping("/CandidatsSelected1/{joboffrename}")//pour phasedata=0
    public String Selectionphase1(@PathVariable("joboffrename") String joboffrename) {
        String question;
        question = " Process candidates for the" +joboffrename +" position. just the first 5 steps(last tool to execute is saveCandidatsSelected) ";
        return selectionCvsAssistent.chat(question);
    }
    @GetMapping("/CandidatsSelected/{joboffrename}")
    public String Selection(@PathVariable("joboffrename") String joboffrename) {
        String question;
        question = " Process candidates for the  " +joboffrename +" position.  ";

        return selectionCvsAssistent.chat(question);
    }
   
    
    @GetMapping("/GetCandidatsSelectedbyoffres")
    public   Map<String, Integer>  GetCandidatsSelectedbyoffres() {

        List<Offres> offres = offresService.findAll();
        Map<String, Integer> CandidatByOffres = new HashMap<>();
        for (int i = 0; i < offres.size(); i++) {

            CandidatByOffres.put(offres.get(i).getName(), candidatsSelectedRepository.findByOffresName(offres.get(i).getName()).size());

        }

        return CandidatByOffres;
    }
    //1 :

    @GetMapping("/GetCandidatsSelectedbyscore")
    public   Map<String, Integer>  GetCandidatsSelectedbyScore(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");

        List<Offres> offres = offresService.findAll();
        Map<String, Integer> CandidatByOffres = new HashMap<>();
        for (int i = 0; i < offres.size(); i++) {


                CandidatByOffres.put(offres.get(i).getName(), userResponseRepository.findByOffrename(offres.get(i).getName()).size());

        }

        return CandidatByOffres;
    }
    // 1 : L'endpoint pour récupérer le temps d'exécution moyen

    /*@GetMapping("/averageExecutionTime")
    public double getAverageExecutionTime() {
        if (selectionCvsAssistent instanceof SelectionCVsAgentsImpl) {
            return ((SelectionCVsAgentsImpl) selectionCvsAssistent).getAverageExecutionTime();
        }
        return 0; // Valeur par défaut si l'instance n'est pas de type SelectionCVsAgentsImpl
    }*/



    @GetMapping("/GetAllJob")
    public List <Offres>  GetAllJob() {
         return  offresService.findAll();

    }








    @GetMapping("/GetScoreMeduime")
    public Map<String, Double> GetScoreMeduime() {

        List <Offres> offres =  offresService.findAll();
        Map<String, Double> CandidatAvergeScoreByOffres = new HashMap<>();
        for(int i=0;i<offres.size();i++){
            CandidatAvergeScoreByOffres.put(offres.get(i).getName(),userResponseRepository.findAverageScoreByOffreName(offres.get(i).getName()));

        }

        return   CandidatAvergeScoreByOffres  ;
//    }


}

    @GetMapping("/getCandidatsSelected/{offername}")

    public ResponseEntity<List<Map<String, Object>>> getCandidatsSelectedByOffer(@PathVariable("offername") String offerName) {
        var candidats = candidatsSelectedRepository.findByOffresName(offerName);
        List<Map<String, Object>> result = candidats.stream().map(c -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", c.getId());
            m.put("name", c.getName());
            m.put("email", c.getEmail());
            m.put("phone", c.getPhone());
            m.put("address", c.getAddress());
            m.put("city", c.getCity());
            m.put("country", c.getCountry());
            return m;
        }).toList();
        return ResponseEntity.ok(result);
    }

    // 2. Retourne les candidats acceptés (id, name, email) pour une offre (avec QCM complété)
   @GetMapping("/getCandidatsAccepted/{offername}")
public ResponseEntity<List<Map<String, Object>>> getCandidatsAcceptedByOffer(@PathVariable("offername") String offerName) {
    var responses = candidatesAcceptedRepository.findByJobName(offerName);

    List<Map<String, Object>> result = responses.stream().map(r -> {
        Map<String, Object> m = new HashMap<>();
        m.put("id", r.getId());
        m.put("email", r.getEmail());
         m.put("name", r.getName());
        m.put("score", r.getScore());
         m.put("phone",r.getPhone());

        // Get the score for the current email
        List<UserResponse> scores = userResponseRepository.findByOffrenameAndEmail(offerName, r.getEmail());
        if (!scores.isEmpty()) {
            m.put("score", scores.get(0).getScore());
            System.out.println("Score: " + scores.get(0).getScore());
        } else {
            m.put("score", null); // or 0 if preferred
        }

        return m;
    }).toList();

    return ResponseEntity.ok(result);
}


    @PostMapping("/Savejob")
    public Offres Savejob(@RequestBody Offres offres) {
        return offresService.saveJob(offres);

    }

    @PostMapping("/{offerName}/upload")
    public ResponseEntity<?> uploadLocalFiles(
            @PathVariable("offerName") String offerName,
            @RequestParam("files") MultipartFile[] files) {
        List<Document> documents = new ArrayList<>();
System.out.println("test");
        ResponseEntity<?> res=offresService.uploadLocalFiles(offerName, files);
        System.out.println(offresService.getCurrentUsername());
        String safeTitle = offerName.replaceAll("[^a-zA-Z0-9\\-]", "_");
        File folder = new File("C:\\Users\\dell\\OneDrive\\Bureau\\PFA\\ProjetPFA\\backend\\Agenius\\docs\\"+safeTitle);

        File[] filesss = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));
        if (files != null) {
            for (File filess : filesss) {
                Document doc = FileSystemDocumentLoader.loadDocument(filess.getAbsolutePath(), documentParser);
                documents.add(doc);
            }

        }
        embeddingStoreIngestorProvider.getIngestor().ingest(documents);


        return res ;
    }

    @GetMapping("/{offerName}/upload")
    public ResponseEntity<?> uploadLocalFiles2(
            @PathVariable("offerName") String offerName,
            @RequestParam("files") MultipartFile[] files) {
        List<Document> documents = new ArrayList<>();
        System.out.println("test1");

        ResponseEntity<?> res=offresService.uploadLocalFiles(offerName, files);
        System.out.println(offresService.getCurrentUsername());
        String safeTitle = offerName.replaceAll("[^a-zA-Z0-9\\-]", "_");
        File folder = new File("C:\\Users\\dell\\OneDrive\\Bureau\\PFA\\ProjetPFA\\backend\\Agenius\\docs\\"+safeTitle);

        File[] filesss = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));
        if (files != null) {
            for (File filess : filesss) {
                Document doc = FileSystemDocumentLoader.loadDocument(filess.getAbsolutePath(), documentParser);
                documents.add(doc);
            }

        }
        embeddingStoreIngestorProvider.getIngestor().ingest(documents);


        return res ;
    }
    @PutMapping("/{offerName}/upload")
    public ResponseEntity<?> uploadLocalFiles1(
            @PathVariable("offerName") String offerName,
            @RequestParam("files") MultipartFile[] files) {
        List<Document> documents = new ArrayList<>();
        System.out.println("test2");

        ResponseEntity<?> res=offresService.uploadLocalFiles(offerName, files);
        System.out.println(offresService.getCurrentUsername());
        String safeTitle = offerName.replaceAll("[^a-zA-Z0-9\\-]", "_");
        File folder = new File("C:\\Users\\dell\\OneDrive\\Bureau\\PFA\\ProjetPFA\\backend\\Agenius\\docs\\"+safeTitle);

        File[] filesss = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));
        if (files != null) {
            for (File filess : filesss) {
                Document doc = FileSystemDocumentLoader.loadDocument(filess.getAbsolutePath(), documentParser);
                documents.add(doc);
            }

        }
        embeddingStoreIngestorProvider.getIngestor().ingest(documents);


        return res ;
    }
    @GetMapping("/{offerName}/candidates")
    public ResponseEntity<?> getCandidatesForOffer(@PathVariable("offerName") String offerName) {
        return offresService.getCandidatesForOffer1(offerName);
    }

    @GetMapping("/{offerName}/count")
    public ResponseEntity<Integer> calculateNumberOfCVs(@PathVariable("offerName") String offerName) {

        int count = offresService.calculateNumberOfCVs(offerName);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/{offerName}/update")
    public ResponseEntity<Integer> updateNumberOfCVs(@PathVariable("offerName") String offerName) {
        int updated = offresService.updateNumberOfCVs(offerName);
        return ResponseEntity.ok(updated);
    }


    @DeleteMapping("/{offerName}")
    public ResponseEntity<String> deleteJobDirectory(@PathVariable("offerName") String offerName) {
        offresService.deleteJobDirectory(offerName);
        return ResponseEntity.ok("Dossier supprimé pour l’offre : " + offerName);
    }

    private final String cvStoragePath = "docs";

    @GetMapping("/files/content/{offerName}/{fileName}")
    public ResponseEntity<Resource> getFile(
            @PathVariable("offerName") String offerName,
            @PathVariable("fileName") String fileName) {
        //elle sert a afficher le contenu dun cv
        String safeOfferName = offerName.replaceAll(" ", "_");

        try {
            Path filePath = Paths.get(cvStoragePath, safeOfferName, fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);

        } catch (MalformedURLException e) {
            return ResponseEntity.status(500).build();
        } catch (IOException e) {
            return ResponseEntity.status(500).build();
        }
    }
    @DeleteMapping("/files/{offerName}/{fileName}")
    public ResponseEntity<?> deleteCandidateFile(@PathVariable("offerName") String offerName, @PathVariable("fileName") String fileName) {
        String username = offresService.getCurrentUsername();
        Offres job = offresService.findOffresByName(offerName);
        if (job != null && username.equals(job.getCreatedBy())) {
            boolean deleted = offresService.deleteCandidateFile(offerName, fileName);
            if (deleted) {
                return ResponseEntity.ok("Fichier supprimé avec succès");
            } else {
                return ResponseEntity.status(404).body("Fichier non trouvé ou échec de suppression");
            }
        }
        return ResponseEntity.status(403).build();
    }



}
