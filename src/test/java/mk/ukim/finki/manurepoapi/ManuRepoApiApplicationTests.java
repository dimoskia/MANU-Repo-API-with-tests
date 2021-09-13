package mk.ukim.finki.manurepoapi;

import mk.ukim.finki.manurepoapi.dto.request.AccountRequest;
import mk.ukim.finki.manurepoapi.enums.Department;
import mk.ukim.finki.manurepoapi.enums.MemberType;
import mk.ukim.finki.manurepoapi.model.Account;
import mk.ukim.finki.manurepoapi.model.VerificationToken;
import mk.ukim.finki.manurepoapi.repository.AccountRepository;
import mk.ukim.finki.manurepoapi.service.VerificationTokenService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-test.properties")
class ManuRepoApiApplicationTests {

    @MockBean
    JavaMailSender javaMailSender;

    @MockBean
    VerificationTokenService verificationTokenService;

    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    AccountRepository accountRepository;

    @Captor
    ArgumentCaptor<SimpleMailMessage> captor;

    @LocalServerPort
    int randomServerPort;

    @Test
    void createAccountFlow_validAccountRequest_accountAndVerificationTokenCreatedVerificationMailSent() {
        //given
        AccountRequest accountRequest = AccountRequest.builder()
                .email("aleksandar.dimoski@students.finki.ukim.mk")
                .password("Password1!")
                .confirmPassword("Password1!")
                .firstName("Aleksandar")
                .lastName("Dimoski")
                .memberType(MemberType.CORRESPONDING)
                .department(Department.LLS)
                .build();

        VerificationToken verificationToken = VerificationToken.builder().token("uuidVerificationToken").build();
        when(verificationTokenService.createToken(any(Account.class))).thenReturn(verificationToken);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<AccountRequest> httpEntity = new HttpEntity<>(accountRequest, httpHeaders);

        // when
        ResponseEntity<String> memberDetailsResponse =
                testRestTemplate.postForEntity("/accounts", httpEntity, String.class);

        // then
        assertThat(memberDetailsResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        verify(javaMailSender).send(captor.capture());
        SimpleMailMessage actualMailMessage = captor.getValue();

        final String expectedText = String.format("Please click the link below to finish the registration process:\n" +
                "http://localhost:%d/accounts/confirmRegistration?token=uuidVerificationToken", randomServerPort);
        SimpleMailMessage expectedMailMessage = new SimpleMailMessage();
        expectedMailMessage.setTo("aleksandar.dimoski@students.finki.ukim.mk");
        expectedMailMessage.setSubject("Registration Confirmation");
        expectedMailMessage.setFrom("ukimrepository@gmail.com");
        expectedMailMessage.setText(expectedText);

        assertThat(actualMailMessage).isEqualTo(expectedMailMessage);
    }

    @AfterEach
    void tearDown() {
        accountRepository.deleteAll();
    }
}
