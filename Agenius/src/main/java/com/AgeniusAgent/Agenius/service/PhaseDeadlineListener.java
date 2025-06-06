package com.AgeniusAgent.Agenius.service;
import com.AgeniusAgent.Agenius.events.DeadlineEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PhaseDeadlineListener {

    private final CandidatQCMService candidatQCMService;
    private final AgentProgressNotifier progressNotifier;

    public PhaseDeadlineListener(CandidatQCMService candidatQCMService,
                                 AgentProgressNotifier progressNotifier,
                                 OffresService offresService) {
        this.candidatQCMService = candidatQCMService;
        this.progressNotifier = progressNotifier;
    }

    @RabbitListener(queues = "phase-deadline-queue")
    public void handleDeadlineReached(DeadlineEvent event) {
        String offerName = event.getOfferName();
        candidatQCMService.sendEmailsAndUpdateStatus(offerName);

        System.out.println("📨 Deadline reached for: " + offerName );
        progressNotifier.notifyProgress(offerName, "EmailSending", 100);

        }
}
