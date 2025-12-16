package com.transaction;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.transaction.api.dto.*;
import com.transaction.service.BasicInputValidator;
import com.transaction.service.TransactionService;
import io.helidon.config.Config;
import io.helidon.http.media.jackson.JacksonSupport;
import io.helidon.webserver.WebServer;
import io.helidon.webserver.http.HttpRouting;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class RestApiRouter extends BasicInputValidator {
    public final Logger logger = LoggerFactory.getLogger(RestApiRouter.class);
    public final TransactionService transactionService;

    public RestApiRouter(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    public void startServer(Config applicationConf) {
        ObjectMapper mapper = JsonMapper.builder()
                .serializationInclusion(JsonInclude.Include.NON_ABSENT)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .disable(new SerializationFeature[]{SerializationFeature.WRITE_DATES_AS_TIMESTAMPS})
                .addModule(new JavaTimeModule())
                .addModule(new Jdk8Module())
                .build();
        WebServer.builder()
                .mediaContext(mc -> mc.addMediaSupport(JacksonSupport.create(mapper)).mediaSupportsDiscoverServices(false))
                .routing(this::routing)
                .config(applicationConf.get("server"))
                .build()
                .start();
        logger.info("Helidon WebServer started successfully.");
    }

    private void routing(HttpRouting.Builder routing) {
        routing.post("/accounts", this::postAccount);
        routing.post("/transactions", this::postTransaction);
        routing.get("/accounts/{accountId}", this::getAccountById);
        routing.error(BadInputException.class, (req, res, ex) -> {
            res.status(400).send(ex.getMessage());
        });
    }

    private void getAccountById(ServerRequest serverRequest, ServerResponse serverResponse) {
        String accountId = serverRequest.path().pathParameters().get("accountId");
        transactionService.getAccountById(getValidatedAccountId(accountId)).ifPresentOrElse(
                serverResponse.status(200)::send,
                () -> serverResponse.status(404).send()
        );
    }

    private void postTransaction(ServerRequest serverRequest, ServerResponse serverResponse) {
        CreateTransactionInput createTransactionInput = serverRequest.content().as(CreateTransactionInput.class);
        validateTransactionInput(createTransactionInput);
        try {
            long transactionId = transactionService.createNewTransaction(createTransactionInput);
            serverResponse.status(201).send(ImmutableCreateTransactionOutput.builder().transactionId(transactionId).build());
        } catch (RuntimeException exception) {
            logger.error("Error creating new transaction", exception);
            serverResponse.status(500).send();
        }
    }

    private void validateTransactionInput(CreateTransactionInput createTransactionInput) {
        if (createTransactionInput.operationType() == OperationType.PAYMENT) {
            if (createTransactionInput.amount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                throw new BadInputException("Amount must be positive for PAYMENT operation type");
            }
        } else if (createTransactionInput.operationType() != OperationType.PAYMENT && createTransactionInput.amount().compareTo(java.math.BigDecimal.ZERO) >= 0) {
            throw new BadInputException("Amount must be negative for " + createTransactionInput.operationType() + " operation types");
        }
        transactionService.getAccountById(createTransactionInput.accountId()).orElseThrow(() -> new BadInputException("Invalid Account ID " + createTransactionInput.accountId()));
    }

    private void postAccount(ServerRequest serverRequest, ServerResponse serverResponse) {
        CreateAccountInput createAccountInput = serverRequest.content().as(CreateAccountInput.class);
        validateCreateAccountInput(createAccountInput);
        try {
            long accountId = transactionService.createNewAccount(createAccountInput);
            serverResponse.status(201).send(ImmutableCreateAccountOutput.builder().accountId(accountId).build());
        } catch (RuntimeException exception) {
            logger.error("Error creating new account", exception);
            serverResponse.status(500).send();
        }
    }

}
