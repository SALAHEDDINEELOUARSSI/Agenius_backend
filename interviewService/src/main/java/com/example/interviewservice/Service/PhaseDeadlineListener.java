package com.example.interviewservice.Service;
import com.example.interviewservice.Model.CandidatsAccepted;
import com.example.interviewservice.events.DeadlineEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PhaseDeadlineListener {


    private final TwilioService twilioService;
    private final CandidasAcceptedService candidasAcceptedService;


    public  PhaseDeadlineListener(TwilioService twilioService, CandidasAcceptedService candidasAcceptedService) {
        this.twilioService = twilioService;
        this.candidasAcceptedService = candidasAcceptedService;

    }

    @RabbitListener(queues = "phase-deadline-queue")
    public void handleDeadlineReached(DeadlineEvent event) {
        String offerName = event.getOfferName();
        List<CandidatsAccepted> candidats = new ArrayList<>(candidasAcceptedService.findAll(offerName));
        CandidatsAccepted first = candidats.isEmpty() ? null : candidats.get(0);


        String from = "+13169993872" ;



        twilioService.makeCall(first.getPhone(), from,offerName);

        System.out.println("📨 Deadline reached for: " + offerName );

        }
}
