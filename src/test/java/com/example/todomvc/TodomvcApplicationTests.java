package com.example.todomvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TodomvcApplicationTests {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;

    @Test
    void contextLoads() {
    }

    @Test
    void shouldChangeCompletionStatus() throws Exception {
        var result = mockMvc.perform(
                post("/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                         {
                                            "value": "Implement backend for Todo app!"
                                         }
                                         """)
        ).andReturn();
        var todoId = mapper.readValue(result.getResponse().getContentAsString(), TodoId.class);

        mockMvc.perform(get("/todos/" + todoId.id))
                .andExpect(jsonPath("$.completed").value(false));

        mockMvc.perform(post("/todos/" + todoId.id + "/complete"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/todos/" + todoId.id))
                .andExpect(jsonPath("$.completed").value(true));

        mockMvc.perform(post("/todos/" + todoId.id + "/incomplete"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/todos/" + todoId.id))
                .andExpect(jsonPath("$.completed").value(false));
    }

    static class TodoId {
        Long id;

        public void setId(Long id) {
            this.id = id;
        }
    }
}
