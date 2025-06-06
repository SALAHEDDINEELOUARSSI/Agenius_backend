package com.example.interviewservice.Web;

import com.example.interviewservice.Model.CandidatsAccepted;
import com.example.interviewservice.Service.CandidasAcceptedService;
import com.example.interviewservice.Service.GeminiService;
import com.example.interviewservice.Service.TwilioService;
import com.twilio.twiml.VoiceResponse;
import com.twilio.twiml.voice.Gather;
import com.twilio.twiml.voice.Say;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
public class Controlour {

    private  final GeminiService geminiService;
    private final TwilioService twilioService;
    private final CandidasAcceptedService candidasAcceptedService;

    public Controlour(GeminiService geminiService, TwilioService twilioService, CandidasAcceptedService candidasAcceptedService) {
     this.geminiService = geminiService;
     this.twilioService = twilioService;
     this.candidasAcceptedService = candidasAcceptedService;
 }



    @GetMapping ("/call")
    public String startCall(
            @RequestParam String to,
            @RequestParam String from,
            @PathVariable  String offrename

    ) {


        // L'URL publique qui expose ton TwiML de démarrage

        twilioService.makeCall(to, from,offrename);
        return "Appel lancé à " + to;
    }
    @PostMapping (value = "/gather/{offrename}", produces = "application/xml")
    @ResponseBody
    public String gatherPrompt(
            @PathVariable  String offrename

    ) {
        VoiceResponse response = new VoiceResponse.Builder()
                .gather(new Gather.Builder()
                        .inputs((java.util.Collections.singletonList(Gather.Input.SPEECH)))
                        .language(Gather.Language.FR_FR)
                        .action("/handle-speech/"+offrename) // va rediriger ici après que le client parle
                        .speechTimeout("auto")
                        .say(new Say.Builder("Bonjour. Je suis une agente IA et je vais réaliser avec vous une interview aujourd'hui. Vous êtes prêt(e) ? ").language(Say.Language.FR_FR).build())
                        .build())
                .say(new Say.Builder("Désolé, je n'ai rien entendu. Au revoir.").build())
                .build();

        return response.toXml();
    }
    @PostMapping(value = "/handle-speech/{offrename}", produces = "application/xml")
    @ResponseBody
    public String handleSpeech(
            @RequestParam(value = "SpeechResult", required = false) String speechResult,
            @RequestParam(value = "From") String fromNumber,
            @PathVariable  String offrename
    ) throws IOException {

        String aiResponse;

        if (speechResult != null && !speechResult.isBlank()) {
            System.out.println("Client a dit : " + speechResult);
            aiResponse = geminiService.continueHrInterview(speechResult, fromNumber,offrename);
        } else {
            aiResponse = "Je suis désolé, je n'ai pas compris.";
        }

        VoiceResponse response = new VoiceResponse.Builder()
                .say(new Say.Builder(aiResponse)
                        .language(Say.Language.FR_FR)
                        .voice(Say.Voice.POLLY_MATHIEU)
                        .build())
                .gather(new Gather.Builder()
                        .inputs(Gather.Input.SPEECH)
                        .language(Gather.Language.FR_FR)
                        .action("/handle-speech/"+offrename)
                        .speechTimeout("6")
                        .say(new Say.Builder("Vous pouvez répondre quand vous voulez.")
                                .language(Say.Language.FR_FR)
                                .build())
                        .build())
                .build();

        return response.toXml();
    }

//    @GetMapping("/score")
//    public String getScore(@RequestParam String phoneNumber) throws IOException {
//        //int score = geminiService.getFinalScore(phoneNumber);
//        return "Score du candidat " + phoneNumber + " : " + score + "/100";
//    }
    @GetMapping("/call-multiple/{offrename}")
    public String callMultiple(
            @PathVariable String offrename
    ) {


        return "Appels lancés à tous les numéros.";
    }



}
