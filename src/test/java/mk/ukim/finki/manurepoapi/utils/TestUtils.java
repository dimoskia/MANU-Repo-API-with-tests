package mk.ukim.finki.manurepoapi.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.experimental.UtilityClass;
import mk.ukim.finki.manurepoapi.enums.Collection;
import mk.ukim.finki.manurepoapi.enums.Department;
import mk.ukim.finki.manurepoapi.enums.MemberType;
import mk.ukim.finki.manurepoapi.enums.PublicationStatus;
import mk.ukim.finki.manurepoapi.enums.Role;
import mk.ukim.finki.manurepoapi.model.Account;
import mk.ukim.finki.manurepoapi.model.File;
import mk.ukim.finki.manurepoapi.model.FileData;
import mk.ukim.finki.manurepoapi.model.ProfileImage;
import mk.ukim.finki.manurepoapi.model.Record;
import mk.ukim.finki.manurepoapi.model.VerificationToken;
import mk.ukim.finki.manurepoapi.repository.projection.MemberProjection;
import mk.ukim.finki.manurepoapi.security.model.UserPrincipal;
import mk.ukim.finki.manurepoapi.security.service.JwtUtils;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@UtilityClass
public class TestUtils {

    public Account createAccount(String firstName, String lastName, boolean enabled) {
        return Account.builder()
                .email(String.format("%s.%s@email.com", firstName, lastName))
                .password("password")
                .firstName(firstName)
                .lastName(lastName)
                .enabled(enabled)
                .memberType(MemberType.CORRESPONDING)
                .department(Department.MBS)
                .build();
    }

    public Account createAccount(boolean enabled) {
        return createAccount("firstName", "lastName", enabled);
    }

    public Account createAccount(String firstName, String lastName) {
        return createAccount(firstName, lastName, true);
    }

    public Account createAccount(String firstName, Department department) {
        Account enabledAccount = createAccount(firstName, firstName, true);
        enabledAccount.setDepartment(department);
        return enabledAccount;
    }

    public List<Account> createAccounts(int count) {
        return LongStream.range(0, count)
                .map(idx -> idx + 1)
                .mapToObj(id -> Account.builder()
                        .id(id)
                        .firstName(String.format("firstName%d", id))
                        .lastName(String.format("lastName%d", id))
                        .build())
                .collect(Collectors.toList());
    }

    public VerificationToken createVerificationToken(Account account, LocalDateTime expiration) {
        return VerificationToken.builder()
                .token(UUID.randomUUID().toString())
                .expiration(expiration)
                .account(account)
                .build();
    }

    public File createFile(Record record) {
        return File.builder()
                .id(11L)
                .fileName("fileName.txt")
                .size(123L)
                .contentType("text/plain")
                .record(record)
                .fileData(new FileData("someFileData".getBytes()))
                .build();
    }

    public Record createRecord() {
        return Record.builder()
                .title("Organic client-driven secured line")
                .collection(Collection.CONFERENCE_ITEM)
                .department(Department.SS)
                .subject("Other")
                .descriptionOrAbstract("Vestibulum rutrum rutrum neque.")
                .keywords("spring, testing, mockito")
                .language("English")
                .numPages(123)
                .publicationDate(LocalDate.parse("2015-05-12"))
                .publicationStatus(PublicationStatus.PUBLISHED)
                .downloadsCount(10)
                .dateArchived(LocalDateTime.of(2021, 1, 1, 10, 10))
                .approved(true)
                .privateRecord(false)
                .authors("authorsPlaceholder")
                .build();
    }

    public Record createRecordWithFilesAndAuthors(Long recordId) {
        Record record = createRecord();
        record.setId(recordId);
        Account account = createAccount("Aleksandar", "Dimoski");
        account.setProfileImage(ProfileImage.builder().id(111L).build());
        record.setAuthorAccounts(Set.of(account));
        record.setFiles(Set.of(createFile(record)));
        return record;
    }

    public Record createRecord(Set<Account> authorAccounts) {
        Record record = createRecord();
        record.setAuthorAccounts(authorAccounts);
        return record;
    }

    public Record createRecord(String title, String keywords) {
        Record record = createRecord();
        record.setTitle(title);
        record.setKeywords(keywords);
        return record;
    }

    public Record createRecord(Collection collection) {
        Record record = createRecord();
        record.setCollection(collection);
        return record;
    }

    public Record createRecord(Department department) {
        Record record = createRecord();
        record.setDepartment(department);
        return record;
    }

    public Record createRecord(String subject) {
        Record record = createRecord();
        record.setSubject(subject);
        return record;
    }

    public Record createPrivateRecord() {
        Record record = createRecord();
        record.setPrivateRecord(true);
        return record;
    }

    public Record createNotApprovedRecord() {
        Record record = createRecord();
        record.setApproved(false);
        return record;
    }

    public MemberProjection createMember(Long id, String fullName) {
        return new MemberProjection() {
            @Override
            public Long getId() {
                return id;
            }

            @Override
            public String getFullName() {
                return fullName;
            }

        };
    }

    public VerificationToken createVerificationToken(Account account) {
        return VerificationToken.builder()
                .token(UUID.randomUUID().toString())
                .account(account)
                .expiration(LocalDateTime.now())
                .build();
    }

    public String createValidJwt(Long accountId, Role role) {
        long currentTimeMillis = System.currentTimeMillis();
        return JWT.create()
                .withSubject(accountId.toString())
                .withClaim("role", role.toString())
                .withIssuedAt(new Date(currentTimeMillis))
                .withExpiresAt(new Date(currentTimeMillis + JwtUtils.EXPIRATION_IN_HOURS * 60 * 60 * 1000))
                .sign(Algorithm.HMAC512(JwtUtils.SECRET.getBytes()));
    }

    public String createValidUserJwt(Long accountId) {
        return createValidJwt(accountId, Role.ROLE_USER);
    }

    public String createValidAdminJwt(Long accountId) {
        return createValidJwt(accountId, Role.ROLE_ADMIN);
    }

    public static Boolean hasId(Authentication authentication, Long accountId) {
        return Optional.ofNullable(authentication)
                .map(Authentication::getPrincipal)
                .filter(principal -> principal instanceof UserPrincipal)
                .map(principal -> ((UserPrincipal) principal).getAccount())
                .map(Account::getId)
                .map(principalAccountId -> Objects.equals(principalAccountId, accountId))
                .orElse(false);
    }
}
