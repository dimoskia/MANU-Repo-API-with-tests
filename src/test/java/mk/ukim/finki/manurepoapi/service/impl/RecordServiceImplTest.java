package mk.ukim.finki.manurepoapi.service.impl;

import mk.ukim.finki.manurepoapi.dto.request.ManageRecordsFilter;
import mk.ukim.finki.manurepoapi.dto.request.RecordRequest;
import mk.ukim.finki.manurepoapi.dto.request.RecordsFilter;
import mk.ukim.finki.manurepoapi.enums.Collection;
import mk.ukim.finki.manurepoapi.enums.Department;
import mk.ukim.finki.manurepoapi.enums.PublicationStatus;
import mk.ukim.finki.manurepoapi.enums.Role;
import mk.ukim.finki.manurepoapi.exception.EntityNotFoundException;
import mk.ukim.finki.manurepoapi.model.Account;
import mk.ukim.finki.manurepoapi.model.Record;
import mk.ukim.finki.manurepoapi.repository.RecordRepository;
import mk.ukim.finki.manurepoapi.security.model.UserPrincipal;
import mk.ukim.finki.manurepoapi.service.AccountService;
import mk.ukim.finki.manurepoapi.utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecordServiceImplTest {

    @Mock
    RecordRepository recordRepository;

    @Mock
    AccountService accountService;

    @InjectMocks
    RecordServiceImpl recordService;

    @Mock
    Authentication authentication;

    private final Long recordId = 1L;

    @Test
    void getRecordsPage_givenBrowsingFilters_shouldConstructSpecAndCallRepository() {
        // given
        RecordsFilter recordsFilter = new RecordsFilter();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Record> expectedRecordsPage = new PageImpl<>(List.of(new Record()));

        when(recordRepository.findAll(ArgumentMatchers.<Specification<Record>>any(), any(Pageable.class))).thenReturn(expectedRecordsPage);

        // when
        Page<Record> actualRecordsPage = recordService.getRecordsPage(recordsFilter, pageable);

        // then
        assertThat(actualRecordsPage).isEqualTo(expectedRecordsPage);
    }

    @Nested
    class GetPublicRecord {
        @Test
        void getPublicRecord_recordExists_recordIsReturned() {
            // given
            Record record = Record.builder().id(recordId).build();
            when(recordRepository.findByIdAndApprovedTrueAndPrivateRecordFalse(recordId)).thenReturn(Optional.of(record));

            // when
            Record actualRecord = recordService.getPublicRecord(recordId);

            // then
            assertThat(actualRecord).isEqualTo(record);
        }

        @Test
        void getPublicRecord_recordDoesNotExist_exceptionIsThrown() {
            // given
            when(recordRepository.findByIdAndApprovedTrueAndPrivateRecordFalse(recordId)).thenReturn(Optional.empty());

            // when, then
            assertThatThrownBy(() -> recordService.getPublicRecord(recordId))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("Record was not found for {id=1}");
        }
    }

    @Nested
    class DeleteRecord {

        private Account accountRef;

        @BeforeEach
        void setUp() {
            accountRef = Account.builder().id(10L).build();
            when(accountService.getAccountRef(authentication)).thenReturn(accountRef);
        }

        @Test
        void deleteRecord_accountIsRecordAuthor_recordIsDeleted() {
            // given
            Record record = Record.builder().id(recordId).build();
            when(recordRepository.findByIdAndAuthorAccountsContaining(recordId, accountRef)).thenReturn(Optional.of(record));

            // when
            recordService.deleteRecord(authentication, recordId);

            // then
            verify(recordRepository).delete(record);
        }

        @Test
        void deleteRecord_accountIsNotRecordAuthor_badRequestExceptionIsThrown() {
            // given
            when(recordRepository.findByIdAndAuthorAccountsContaining(recordId, accountRef))
                    .thenReturn(Optional.empty());

            // when, then
            assertThatThrownBy(() -> recordService.deleteRecord(authentication, recordId))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasFieldOrPropertyWithValue("status", HttpStatus.BAD_REQUEST);
            verifyNoMoreInteractions(recordRepository);
        }
    }

    @Test
    void createRecord_givenRecordRequest_shouldCreateRecordAndSetPopulateData() {
        // given
        UserPrincipal userPrincipal = new UserPrincipal(2L, Role.ROLE_USER);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);

        List<Account> authorAccounts = TestUtils.createAccounts(2);
        when(accountService.getMultipleAccounts(List.of(1L, 2L))).thenReturn(authorAccounts);

        Record expectedRecord = Record.builder()
                .id(null)
                .title("title")
                .authorAccounts(new HashSet<>(authorAccounts))
                .authors("firstName1 lastName1, firstName2 lastName2")
                .collection(Collection.ARTICLE)
                .department(Department.A)
                .subject("subject")
                .descriptionOrAbstract("descriptionOrAbstract")
                .keywords("keywords")
                .language("language")
                .numPages(100)
                .publicationDate(LocalDate.of(2020, 1, 1))
                .publicationStatus(PublicationStatus.PUBLISHED)
                .privateRecord(false)
                .build();
        when(recordRepository.save(expectedRecord)).then(returnsFirstArg());

        RecordRequest recordRequest = buildRecordRequest();

        // when
        Record actualRecord = recordService.createRecord(authentication, recordRequest);

        // then
        assertThat(actualRecord).isEqualTo(expectedRecord);
    }

    @Test
    void getManageRecordsPage_givenBrowsingFilters_shouldConstructSpecAndCallRepository() {
        // given
        when(authentication.getPrincipal()).thenReturn(new UserPrincipal(1L, Role.ROLE_USER));

        ManageRecordsFilter manageRecordsFilter = new ManageRecordsFilter();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Record> expectedRecordsPage = new PageImpl<>(List.of(new Record()));

        when(recordRepository.findAll(ArgumentMatchers.<Specification<Record>>any(), any(Pageable.class))).thenReturn(expectedRecordsPage);

        // when
        Page<Record> actualRecordsPage = recordService.getManageRecordsPage(manageRecordsFilter, pageable, authentication);

        // then
        assertThat(actualRecordsPage).isEqualTo(expectedRecordsPage);
    }

    @Nested
    class CheckRecordPermissions {

        private final Long accountId = 1L;

        @Test
        void checkRecordPermissions_authenticatedUserIsAdmin_hasPermissionsForAllRecords() {
            // given
            when(authentication.getPrincipal()).thenReturn(new UserPrincipal(accountId, Role.ROLE_ADMIN));

            // when
            boolean recordEditPermission = recordService.checkRecordPermissions(recordId, authentication);

            // then
            assertThat(recordEditPermission).isTrue();
        }

        @Test
        void checkRecordPermissions_givenRecordAndAuthenticatedUser_returnsIfUserHasEditPermissions() {
            // given
            when(authentication.getPrincipal()).thenReturn(new UserPrincipal(accountId, Role.ROLE_USER));
            Account accountRef = Account.builder().id(accountId).build();
            when(accountService.getAccountRef(authentication)).thenReturn(accountRef);
            when(recordRepository.existsByIdAndAuthorAccountsContaining(recordId, accountRef)).thenReturn(true);

            // when
            boolean recordEditPermission = recordService.checkRecordPermissions(recordId, authentication);

            // then
            assertThat(recordEditPermission).isTrue();
        }
    }

    @Test
    void getRecordRef_givenRecordId_callsGetOneRepositoryMethod() {
        // when
        Record recordRef = Record.builder().id(recordId).build();
        when(recordRepository.getOne(recordId)).thenReturn(recordRef);

        // when
        Record actualRecordRef = recordService.getRecordRef(recordId);

        // then
        assertThat(actualRecordRef).isEqualTo(recordRef);
    }

    @Test
    void incrementDownloads_givenRecordId_shouldCallRepoMethodForIncrementingDownloads() {
        // given, when
        recordService.incrementDownloads(recordId);

        // then
        verify(recordRepository).incrementDownloads(recordId);
    }

    @Test
    void isRecordPublic_givenRecordId_checksIfRecordCanBePubliclyAccessed() {
        // given
        when(recordRepository.existsByIdAndApprovedTrueAndPrivateRecordFalse(recordId)).thenReturn(true);

        // when
        boolean isRecordPublic = recordService.isRecordPublic(recordId);

        // then
        assertThat(isRecordPublic).isTrue();
    }

    @Nested
    class EditRecord {

        private Account account;
        private final Long accountId = 2L;

        @BeforeEach
        void setUp() {
            account = Account.builder().id(accountId).build();
            when(accountService.getAccountRef(authentication)).thenReturn(account);
        }

        @Test
        void editRecord_recordIsNotFound_exceptionIsThrown() {
            // given
            when(recordRepository.findByIdAndAuthorAccountsContaining(recordId, account)).thenReturn(Optional.empty());

            // when, then
            assertThatThrownBy(() -> recordService.editRecord(authentication, recordId, null))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("Record was not found for {id=1}");
        }

        @Test
        void editRecord_recordIsFound_recordIsEditedAndPersisted() {
            // given
            Record originalRecord = Record.builder().id(10L).build();
            when(recordRepository.findByIdAndAuthorAccountsContaining(recordId, account)).thenReturn(Optional.of(originalRecord));

            UserPrincipal userPrincipal = new UserPrincipal(accountId, Role.ROLE_USER);
            when(authentication.getPrincipal()).thenReturn(userPrincipal);

            List<Account> authorAccounts = TestUtils.createAccounts(2);
            when(accountService.getMultipleAccounts(List.of(1L, accountId))).thenReturn(authorAccounts);

            Record expectedRecord = Record.builder()
                    .id(10L)
                    .title("title")
                    .authorAccounts(new HashSet<>(authorAccounts))
                    .authors("firstName1 lastName1, firstName2 lastName2")
                    .collection(Collection.ARTICLE)
                    .department(Department.A)
                    .subject("subject")
                    .descriptionOrAbstract("descriptionOrAbstract")
                    .keywords("keywords")
                    .language("language")
                    .numPages(100)
                    .publicationDate(LocalDate.of(2020, 1, 1))
                    .publicationStatus(PublicationStatus.PUBLISHED)
                    .privateRecord(false)
                    .build();
            when(recordRepository.save(expectedRecord)).thenReturn(expectedRecord);

            RecordRequest recordRequest = buildRecordRequest();

            // when
            Record actualRecord = recordService.editRecord(authentication, recordId, recordRequest);

            // then
            assertThat(actualRecord).isEqualTo(expectedRecord);
        }
    }

    private RecordRequest buildRecordRequest() {
        List<Long> authorIds = new ArrayList<>();
        authorIds.add(1L);
        return RecordRequest.builder()
                .title("title")
                .authorIds(authorIds)
                .collection(Collection.ARTICLE)
                .department(Department.A)
                .subject("subject")
                .descriptionOrAbstract("descriptionOrAbstract")
                .keywords("keywords")
                .language("language")
                .numPages(100)
                .publicationDate(LocalDate.of(2020, 1, 1))
                .publicationStatus(PublicationStatus.PUBLISHED)
                .privateRecord(false)
                .build();
    }
}
