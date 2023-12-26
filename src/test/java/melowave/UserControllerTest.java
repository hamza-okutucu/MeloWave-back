package melowave;

import com.fasterxml.jackson.databind.ObjectMapper;

import melowave.model.Role;
import melowave.model.User;
import melowave.service.UserService;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Disabled
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;
    
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetUsers() throws Exception {
        when(userService.getUsers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/user/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetUserById() throws Exception {
        Long userId = 1L;
        User mockUser = new User();

        when(userService.getUserById(userId)).thenReturn(mockUser);

        mockMvc.perform(get("/user/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userId))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testCreateUser() throws Exception {
        User newUser = new User();
        newUser.setUsername("testUsername");
        newUser.setPassword("testPassword");
        Role role = new Role();
        role.setName("ROLE_ADMIN");
        newUser.setRole(role);

        when(userService.createUser(newUser)).thenReturn(newUser);

        mockMvc.perform(post("/user/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andDo(MockMvcResultHandlers.print());
    }
}
