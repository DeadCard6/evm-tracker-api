package com.trycore.evmTracker.presentation.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProjectActivityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void managesProjectsActivitiesAndIndicatorsThroughRestEndpoints() throws Exception {
        String projectLocation = mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Implementacion EVM"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name").value("Implementacion EVM"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String projectId = projectLocation.replaceAll(".*\\\"id\\\":(\\d+).*", "$1");

        mockMvc.perform(get("/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(get("/projects/{id}", projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Implementacion EVM"));

        mockMvc.perform(put("/projects/{id}", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Implementacion EVM actualizada"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Implementacion EVM actualizada"));

        String activityBody = """
                {
                  "name": "Construir API",
                  "bac": 1000,
                  "plannedPercentComplete": 50,
                  "actualPercentComplete": 60,
                  "actualCost": 500
                }
                """;

        String activityResponse = mockMvc.perform(post("/projects/{projectId}/activities", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(activityBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.indicators.costInterpretation").value("EFICIENTE_EN_COSTOS"))
                .andExpect(jsonPath("$.indicators.scheduleInterpretation").value("ADELANTADO"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String activityId = activityResponse.replaceAll(".*\\\"id\\\":(\\d+).*", "$1");

        mockMvc.perform(get("/projects/{projectId}/activities", projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Construir API"));

        mockMvc.perform(get("/activities/{id}", activityId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.indicators.plannedValue").value(500.0000))
                .andExpect(jsonPath("$.indicators.earnedValue").value(600.0000));

        mockMvc.perform(get("/projects/{id}/indicators", projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.costPerformanceIndex.value").value(1.2000))
                .andExpect(jsonPath("$.schedulePerformanceIndex.value").value(1.2000));

        mockMvc.perform(put("/activities/{id}", activityId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Construir API y docs",
                                  "bac": 1000,
                                  "plannedPercentComplete": 80,
                                  "actualPercentComplete": 40,
                                  "actualCost": 700
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Construir API y docs"))
                .andExpect(jsonPath("$.indicators.costInterpretation").value("SOBRE_PRESUPUESTO"))
                .andExpect(jsonPath("$.indicators.scheduleInterpretation").value("ATRASADO"));

        mockMvc.perform(delete("/activities/{id}", activityId))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/projects/{id}", projectId))
                .andExpect(status().isNoContent());
    }
}
