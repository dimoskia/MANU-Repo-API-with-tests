package mk.ukim.finki.manurepoapi.security.controller;

import mk.ukim.finki.manurepoapi.security.MockUserDetailsService;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthenticationController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@Import(MockUserDetailsService.class)
class AuthenticationControllerTest {

    @Autowired
    MockMvc mockMvc;

    @ParameterizedTest
    @MethodSource("getAuthRequestPayloads")
    void authenticate_invalidAuthenticationRequest_unauthorized(String invalidAuthenticationRequestJSON) throws Exception {
        // when, then
        mockMvc.perform(post("/authenticate")
                .content(invalidAuthenticationRequestJSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(status().reason("No or invalid authentication details provided"));
    }

    static List<Arguments> getAuthRequestPayloads() {
        final String missingEmail = "{" +
                "    \"email\": \"\"," +
                "    \"password\": \"password\"" +
                "}";
        final String missingPassword = "{" +
                "    \"email\": \"aleksandar.dimoski@students.finki.ukim.mk\"," +
                "    \"password\": \"\"" +
                "}";
        final String incorrectPasswordAuthRequestJSON = "{" +
                "    \"email\": \"aleksandar.dimoski@students.finki.ukim.mk\"," +
                "    \"password\": \"wrongPassword\"" +
                "}";
        return List.of(
                Arguments.of(missingEmail),
                Arguments.of(missingPassword),
                Arguments.of(incorrectPasswordAuthRequestJSON)
        );
    }

    @Test
    void authenticate_correctAuthenticationCredentials_userAuthenticatedAndJwtGenerated() throws Exception {
        // given
        final String incorrectPasswordAuthRequestJSON = "{" +
                "    \"email\": \"aleksandar.dimoski@students.finki.ukim.mk\"," +
                "    \"password\": \"correctPassword\"" +
                "}";

        // when, then
        mockMvc.perform(post("/authenticate")
                .content(incorrectPasswordAuthRequestJSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.avatar.firstName", is("Aleksandar")))
                .andExpect(jsonPath("$.avatar.lastName", is("Dimoski")))
                .andExpect(jsonPath("$.avatar.imageUrl", IsNull.nullValue()));
    }
}
