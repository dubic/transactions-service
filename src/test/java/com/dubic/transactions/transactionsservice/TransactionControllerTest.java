package com.dubic.transactions.transactionsservice;

import com.dubic.transactions.transactionsservice.data.TransactionRequest;
import com.dubic.transactions.transactionsservice.models.Transaction;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest
@SpringJUnitWebConfig
class TransactionControllerTest {
    MockMvc mockMvc;
    @Autowired
    private TransactionService transactionService;

    @BeforeEach
    void setup(WebApplicationContext wac) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @DisplayName("Invalid amount string returns 422")
    void postTransactionInvalidAmount() throws Exception {
        mockMvc.perform(post("/transaction")
                .content(asJsonString(new TransactionRequest("12o,k", "2024-01-11T16:08:51.312Z")))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("Invalid timestamp string returns 422")
    void postTransactionInvalidTime() throws Exception {
        mockMvc.perform(post("/transaction")
                        .content(asJsonString(new TransactionRequest("12.896", "2024-014-11T16:08" +
                                ":51.312Z")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("Future timestamp string returns 422")
    void postTransactionFuture() throws Exception {
        mockMvc.perform(post("/transaction")
                        .content(asJsonString(new TransactionRequest("12.896", "2025-01-11T16:08" +
                                ":51.312Z")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }


    @Test
    @DisplayName("Invalid json request returns 400")
    void postTransactionInvalidJson() throws Exception {
        mockMvc.perform(post("/transaction")
                        .content("{\"amount_3\":\"23.912\", \"timestamp\":\"2024-01-11T16:08:51.312Z\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Valid request and timestamp < 30 secs")
    void postTransactionTimeWithin30secs() throws Exception {
        mockMvc.perform(post("/transaction")
                        .content(asJsonString(new TransactionRequest("12.896", "2024-01-11T16:08" +
                                ":10.312Z")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Valid request and timestamp > 30 secs")
    void postTransactionTimeNotWithin30secs() throws Exception {
        //Given first transaction
        transactionService.processTransaction(
                Transaction.createTransaction(
                        new TransactionRequest("12.655", "2024-01-11T16:08:10.312Z")
                )
        );
        //when
        mockMvc.perform(post("/transaction")
                        .content(asJsonString(new TransactionRequest("12.896", "2024-01-11T16:08" +
                                ":41.312Z")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void transactionStatistics() throws Exception {
        mockMvc.perform(get("/transaction")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void deleteAllTransactions() throws Exception {
        mockMvc.perform(delete("/transaction")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    public String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            final String jsonContent = mapper.writeValueAsString(obj);
            System.out.println("post Content :: "+jsonContent);
            return jsonContent;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}