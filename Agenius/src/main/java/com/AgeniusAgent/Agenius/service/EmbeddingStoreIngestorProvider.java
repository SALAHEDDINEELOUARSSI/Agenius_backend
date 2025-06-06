package com.AgeniusAgent.Agenius.service;

import dev.langchain4j.model.Tokenizer;
import com.AgeniusAgent.Agenius.context.JobOfferContext;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmbeddingStoreIngestorProvider {

    @Autowired
    private OffresService offresRepository;

    @Autowired
    private EmbeddingModel embeddingModel;

    @Autowired
    private EmbeddingStore<TextSegment> embeddingStore;

    public EmbeddingStoreIngestor getIngestor() {
        return EmbeddingStoreIngestor.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .documentSplitter(DocumentSplitters.recursive(1000, 100, new Tokenizer() {
                    @Override
                    public int estimateTokenCountInText(String text) {
                        return text.split("\\s+").length;
                    }

                    @Override
                    public int estimateTokenCountInMessage(ChatMessage message) {
                        return estimateTokenCountInText(message.toString());
                    }

                    @Override
                    public int estimateTokenCountInMessages(Iterable<ChatMessage> messages) {
                        int total = 0;
                        for (ChatMessage msg : messages) total += estimateTokenCountInMessage(msg);
                        return total;
                    }
                }))
                .documentTransformer(document -> {
                    String username = offresRepository.getCurrentUsername();
                    if (username == null) username = "default-user";

                    String joboffrename = JobOfferContext.getJobOfferName();
                    if (joboffrename == null) joboffrename = "default-offres";

                    document.metadata().put("userId", username);
                    document.metadata().put("offres", joboffrename);
                    return document;
                })
                .build();
    }
}
