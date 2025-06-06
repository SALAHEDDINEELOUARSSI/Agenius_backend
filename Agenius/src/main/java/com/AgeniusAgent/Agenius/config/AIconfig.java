package com.AgeniusAgent.Agenius.config;

import com.AgeniusAgent.Agenius.agent.*;
import com.AgeniusAgent.Agenius.repository.CandidatsAcceptedRepository;
import com.AgeniusAgent.Agenius.repository.CandidatsSelectedRepository;
import com.AgeniusAgent.Agenius.service.OffresService;

import com.AgeniusAgent.Agenius.repository.UserResponseRepository;
import com.AgeniusAgent.Agenius.context.JobOfferContext;

import com.AgeniusAgent.Agenius.service.*;
import com.AgeniusAgent.Agenius.web.QCMWebController;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentTransformer;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.model.Tokenizer;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.model.embedding.EmbeddingModel;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.github.GitHubModelsEmbeddingModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;

import javax.sql.DataSource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


import static dev.langchain4j.model.github.GitHubModelsEmbeddingModelName.TEXT_EMBEDDING_3_SMALL;
import static dev.langchain4j.store.embedding.filter.MetadataFilterBuilder.metadataKey;

@Configuration
public class AIconfig {


    private final OffresService offresRepository;
    private final CandidatsSelectedRepository candidatsSelectedRepository;
    //part 2
    private final JdbcTemplate jdbcTemplate;
    private final QcmEmailService qcmEmailService;
    private final GeminiQCMService geminiService;
    private final UserResponseService userResponseService;
    private final UserResponseRepository userResponseRepository;
    private final CandidatsAcceptedRepository candidatsAcceptedRepository;
    private final CandidatQCMService candidatQCMService;
    private final QCMWebController qcmWebController;
    private final QuestionCacheService questionCacheService;

    private final AgentProgressNotifier progressNotifier;
    public AIconfig(OffresService offresRepository,
                    CandidatsSelectedRepository candidatsSelectedRepository,
                    QcmEmailService qcmEmailService,
                    GeminiQCMService geminiService,
                    @Lazy JdbcTemplate jdbcTemplate,
                    UserResponseService userResponseService,
                    UserResponseRepository userResponseRepository,
                    CandidatsAcceptedRepository candidatsAcceptedRepository,
                    CandidatQCMService candidatQCMService,
                    QCMWebController qcmWebController,
                    QuestionCacheService questionCacheService,
                    AgentProgressNotifier progressNotifier
    ) {
        this.offresRepository = offresRepository;
        this.candidatsSelectedRepository=candidatsSelectedRepository;
        this.candidatQCMService = candidatQCMService;
        this.userResponseRepository = userResponseRepository;
        this.qcmEmailService = qcmEmailService;
        this.geminiService = geminiService;
        this.jdbcTemplate = jdbcTemplate;
        this.userResponseService=userResponseService;
        this.candidatsAcceptedRepository=candidatsAcceptedRepository;
        this.qcmWebController=qcmWebController;
        this.questionCacheService=questionCacheService;
        this.progressNotifier=progressNotifier;
    }

    @Bean
    public SelectionCvsAgents selectionCvsAsistent(EmbeddingStoreIngestorProvider embeddingStoreIngestor,HttpServletRequest request) {
        return AiServices.builder(SelectionCvsAgents.class)
                .chatLanguageModel(chatLanguageModel())
                .chatMemory(  MessageWindowChatMemory.withMaxMessages(20))
                .tools(new TestMultiAgentTools(
                                candidatsSelectedRepository,
                                offresRepository,
                                jdbcTemplate,
                        qcmEmailService,
                        geminiService,
                        qcmWebController,
                        candidatQCMService,
                        questionCacheService,
                        progressNotifier

                                )
                ).contentRetriever(contentRetriever(GitHubModelsEmbeddingModel.builder()
                        .gitHubToken("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")
                        .modelName(TEXT_EMBEDDING_3_SMALL)
                        .logRequestsAndResponses(true)
                        .build(), embeddingStoreIngestor.getIngestor(),request))
                .build();
    }

    @Bean
    public ChatLanguageModel chatLanguageModel() {
        return GoogleAiGeminiChatModel.builder()
                .apiKey("AIzaSyAAQpHa5dXv6aJQMbbgTCAOiqc4FD8swGQ") // 🔒 Clé API sécurisée
                .modelName("gemini-1.5-flash")
                .logRequestsAndResponses(true)
                .temperature(0.0)
                .build();
    }
    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }


    //had lbean je pense ytssuprima hit asslan rah mdyour explicitement lfo9
    @Bean
    public EmbeddingModel embeddingModel() {
        return GitHubModelsEmbeddingModel.builder()
                .gitHubToken("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")
                .modelName(TEXT_EMBEDDING_3_SMALL)
                .logRequestsAndResponses(true)
                .build();
    }


    @Bean
    EmbeddingStore<TextSegment> embeddingStore(EmbeddingModel embeddingModel){
       return PgVectorEmbeddingStore.builder()
            .host("localhost")
               .port(5432)
               .database("agentDb")
               .user("postgres")
               .password("salah")
               .table("data_vs_v4")
               .dimension(embeddingModel.dimension())
               .dropTableFirst(true)
               .build();


    }


    public ContentRetriever contentRetriever(EmbeddingModel embeddingModel,EmbeddingStoreIngestor  embeddingStoreIngestor,HttpServletRequest request) {

        String username=offresRepository.getCurrentUsername();




        EmbeddingStore<TextSegment> embeddingStore =PgVectorEmbeddingStore.builder()
                .host("localhost")
                .port(5432)
                .database("agentDb")
                .user("postgres")
                .password("salah")
                .table("data_vs_v4")
                .dimension(embeddingModel.dimension())
                .dropTableFirst(true)
                .build();



/// /
        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(20)
                .minScore(0.50)

                .dynamicFilter(query -> {


                    String joboffrename = JobOfferContext.getJobOfferName();


                    return metadataKey("userId").isEqualTo(username).and(metadataKey("offres").isEqualTo(joboffrename));
                })                .build();



    }



}





