package com.exercise.coding;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.sql.Date.valueOf;
import static java.time.LocalDate.now;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionService transactionService;

    private Transaction transaction;

    @BeforeEach
    void setUpTest() {
        transaction = Transaction.builder()
                .clientId("9d078bd3-ec13-4728-b484-8dbc2191c32c")
                .cardNumber("1234-5678-9012-1234")
                .transactionAmount(100.00)
                .transactionDate(valueOf(now()))
                .build();
    }

    @Test
    public void testGetTransactions() throws Exception {
        Transaction transaction2 = Transaction.builder()
                .clientId("1d078bd3-ec13-4728-b484-8dbc2191c31a")
                .cardNumber("9999-5678-9012-9999")
                .transactionAmount(5.99)
                .transactionDate(valueOf("2020-09-24"))
                .build();

        List<Transaction> transactions = new ArrayList<>(Arrays.asList(transaction, transaction2));

        when(transactionRepository.findAll()).thenReturn(transactions);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/transactions")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        verify(transactionRepository).findAll();
    }

    @Test
    public void testGetTransactionById() throws Exception {
        transaction.setTransactionId(999L);
        when(transactionRepository.findById(transaction.getTransactionId())).thenReturn(of(transaction));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/transactions/999")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("transactionId", is(transaction.getTransactionId().intValue())));
    }

    @Test
    public void testPostTransaction() throws Exception {
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/transactions")
                .content(new ObjectMapper().writeValueAsString(transaction))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void testPostTransactionsThrowsException() throws Exception {
        when(transactionRepository.save(any())).thenThrow(IllegalArgumentException.class);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/transactions")
                .content(new ObjectMapper().writeValueAsString(transaction))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(transactionRepository).save(any());
    }

    @Test
    public void testPutTransaction() throws Exception {
        transaction.setTransactionId(999L);

        when(transactionRepository.findById(transaction.getTransactionId())).thenReturn(of(transaction));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        mockMvc.perform(MockMvcRequestBuilders
                .put("/api/transactions/" + transaction.getTransactionId())
                .content(new ObjectMapper().writeValueAsString(transaction))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testPutTransactionWhenIdNotFound() throws Exception {
        transaction.setTransactionId(999L);
        when(transactionRepository.findById(transaction.getTransactionId())).thenReturn(ofNullable(null));

        mockMvc.perform(MockMvcRequestBuilders
                .put("/api/transactions/" + transaction.getTransactionId())
                .content(new ObjectMapper().writeValueAsString(transaction))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteTransaction() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/transactions/123")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isNoContent());
        verify(transactionRepository).deleteById(123L);
    }

    @TestConfiguration
    static class TestContextConfiguration {
        @Bean
        public TransactionService transactionService() {
            return new TransactionService();
        }
    }
}