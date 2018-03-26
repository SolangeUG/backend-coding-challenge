package com.engagetech.expenses.controller;

import com.engagetech.expenses.model.Expense;
import com.engagetech.expenses.model.ValueAddedTaxRate;
import com.engagetech.expenses.service.ExpensesService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.text.SimpleDateFormat;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * REST Controller Unit Tests
 */
@RunWith(SpringRunner.class)
@WebMvcTest(ExpensesController.class)
public class ExpensesControllerTests {

    @Autowired
    private MockMvc mvc;

    @Value("${spring.security.user.name}")
    private String username;

    @Value("${spring.security.user.password}")
    private String password;

    @Value("${application.api.path}")
    private String apiPath;

    @Value("${application.api.expenses.endpoint}")
    private String endpoint;

    @MockBean
    private ExpensesService service;

    @Autowired
    private ExpensesController controller;


    @Test
    @DisplayName("GET Request should return a 200 status code")
    public void shouldReturn200WhenSendingGetRequestToController() throws Exception {
        String url = apiPath + endpoint + "/";
        mvc.perform(get(url)
                .with(user(username).password(password))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST Request should return a 206 status code")
    public void shouldReturn2xxWhenSendingPostRequestToController() throws Exception {
        ValueAddedTaxRate rate = new ValueAddedTaxRate(21.0, true);
        Expense expense =
                new Expense(
                        new SimpleDateFormat("dd/MM/yyyy").parse("24/03/2018"),
                        205.50,
                        "Accomodation");
        expense.setRate(rate);

        given(service.addExpense(expense)).willReturn(true);

        String url = apiPath + endpoint + "/";
        mvc.perform(post(url)
                .with(user(username).password(password))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(getJsonBytes(expense)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON + ";charset=UTF-8"));
    }

    @Test
    @DisplayName("POST Request with ill-formed expense should return a 400 status code")
    public void shouldReturn400WhenSendingBadPostRequestToController() throws Exception {
        ValueAddedTaxRate rate = new ValueAddedTaxRate(21.0, true);
        Expense expense =
                new Expense(
                        new SimpleDateFormat("dd/MM/yyyy").parse("24/03/2018"),
                        205.50,
                        "");
        expense.setRate(rate);

        given(service.addExpense(expense)).willReturn(false);

        String url = apiPath + endpoint + "/";
        mvc.perform(post(url)
                .with(user(username).password(password))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(getJsonBytes(expense)))
                .andExpect(status().is4xxClientError());
    }

    /**
     * Convert Expense object to Json bytes
     * @param expense expense object to convert
     * @return Json bytes
     */
    private byte[] getJsonBytes(Expense expense) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsBytes(expense);
    }

    @Configuration
    @ComponentScan(basePackageClasses = {ExpensesController.class})
    public static class ExpensesControllerTestConfiguration {
        // this is to avoid JPA errors when @EnableJpaAuditing annotation is
        // added to the application main class.
        // Hence, with this configuration, WebMvcTest does not complain!
    }
}
