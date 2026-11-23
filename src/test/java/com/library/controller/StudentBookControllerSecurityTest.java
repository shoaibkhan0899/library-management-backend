package com.library.controller;

import com.library.config.JwtAuthenticationFilter;
import com.library.config.SecurityConfig;
import com.library.entity.User;
import com.library.enums.Role;
import com.library.repository.UserRepository;
import com.library.service.BookService;
import com.library.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = StudentBookController.class,
        properties = {
                "jwt.secret=MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDE=",
                "jwt.expiration=86400000",
                "app.cors.allowed-origins=http://localhost:4200"
        }
)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtService.class})
class StudentBookControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    void studentCanReadBooks() throws Exception {
        User student = userWithRole("student@example.com", Role.STUDENT);
        when(userRepository.findByEmail(student.getEmail()))
                .thenReturn(Optional.of(student));
        when(bookService.getAllBooks()).thenReturn(List.of());

        mockMvc.perform(get("/api/student/books")
                        .header("Authorization", "Bearer "
                                + jwtService.generateToken(student.getEmail())))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(bookService).getAllBooks();
    }

    @Test
    void adminCannotAccessStudentBooksEndpoint() throws Exception {
        User admin = userWithRole("admin@example.com", Role.ADMIN);
        when(userRepository.findByEmail(admin.getEmail()))
                .thenReturn(Optional.of(admin));

        mockMvc.perform(get("/api/student/books")
                        .header("Authorization", "Bearer "
                                + jwtService.generateToken(admin.getEmail())))
                .andExpect(status().isForbidden());

        verifyNoInteractions(bookService);
    }

    private User userWithRole(String email, Role role) {
        User user = new User();
        user.setEmail(email);
        user.setRole(role);
        return user;
    }
}
