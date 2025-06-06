package com.example.interviewservice.Service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Call;
import com.twilio.type.PhoneNumber;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class TwilioService {

    // Mettre vos identifiants ici (ou les récupérer via des variables d'environnement)
    private static final String ACCOUNT_SID ="AC1b7942c3a77f9569683021392d1fc7d0";
    private static final String AUTH_TOKEN = "42ba1875e726acd523cb0271ab2baa8a";

    public void makeCall(String toPhoneNumber, String fromTwilioNumber,String offrename) {
        // Initialisation une seule fois
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        try {

            // Lancer l'appel
            Call call = Call.creator(
                            new PhoneNumber(toPhoneNumber),
                            new PhoneNumber(fromTwilioNumber),
                            URI.create("https://0c14-196-70-227-118.ngrok-free.app/gather/"+ URLEncoder.encode(offrename, StandardCharsets.UTF_8)))
                    .create();

            System.out.println("Call initiated with SID: " + call.getSid());

        } catch (Exception e) {
            System.err.println("Error initiating call: " + e.getMessage());
        }
    }



}

