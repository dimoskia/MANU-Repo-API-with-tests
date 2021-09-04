package mk.ukim.finki.manurepoapi.service.impl;

import mk.ukim.finki.manurepoapi.dto.request.AdminRecordsFilter;
import mk.ukim.finki.manurepoapi.exception.EntityNotFoundException;
import mk.ukim.finki.manurepoapi.model.Record;
import mk.ukim.finki.manurepoapi.repository.RecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

    @Mock
    RecordRepository recordRepository;

    @InjectMocks
    AdminServiceImpl adminService;

    private final Long recordId = 1L;

    @Test
    void getNewRecordsPage_givenBrowsingFilters_shouldConstructSpecAndCallRepository() {
        // given
        AdminRecordsFilter recordsFilter = new AdminRecordsFilter();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Record> expectedRecordsPage = new PageImpl<>(List.of(new Record()));

        when(recordRepository.findAll(ArgumentMatchers.<Specification<Record>>any(), any(Pageable.class))).thenReturn(expectedRecordsPage);

        // when
        Page<Record> actualRecordsPage = adminService.getNewRecordsPage(recordsFilter, pageable);

        // then
        assertThat(actualRecordsPage).isEqualTo(expectedRecordsPage);
    }

    @Nested
    class GetRecordDetails {
        @Test
        void getRecordDetails_recordDoesNotExists_exceptionIsThrown() {
            // given
            when(recordRepository.fetchRecordWithAuthors(recordId)).thenReturn(Optional.empty());

            // when, then
            assertThatThrownBy(() -> adminService.getRecordDetails(recordId))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("Record was not found for {id=1}");
        }

        @Test
        void getRecordDetails_recordExists_recordIsReturned() {
            // given
            Record record = Record.builder().id(recordId).build();

            when(recordRepository.fetchRecordWithAuthors(recordId)).thenReturn(Optional.of(record));

            // when
            Record actualRecord = adminService.getRecordDetails(recordId);

            // then
            assertThat(actualRecord).isEqualTo(record);
        }
    }

    @Nested
    class ApproveRecord {

        private Record record;

        @BeforeEach
        void setUp() {
            record = Record.builder()
                    .id(recordId)
                    .approved(false)
                    .build();
        }

        @Test
        void approveRecord_recordExists_recordIsApproved() {
            // given
            when(recordRepository.findById(recordId)).thenReturn(Optional.of(record));

            // when
            adminService.approveRecord(recordId);

            // then
            verify(recordRepository).save(argThat(Record::getApproved));
        }

        @Test
        void approveRecord_recordDoesNotExist_exceptionIsThrown() {
            // given
            when(recordRepository.findById(recordId)).thenReturn(Optional.empty());

            // when, then
            assertThatThrownBy(() -> adminService.approveRecord(recordId))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("Record was not found for {id=1}");
        }
    }

    @Nested
    class DeclineRecord {
        @Test
        void declineRecord_recordExists_recordIsDeleted() {
            // when
            adminService.declineRecord(recordId);

            // then
            verify(recordRepository).deleteById(recordId);
        }

        @Test
        void declineRecord_recordDoesNotExits_BadRequestExceptionIsThrown() {
            // given
            doThrow(EmptyResultDataAccessException.class).when(recordRepository).deleteById(recordId);

            // when, then
            assertThatThrownBy(() -> adminService.declineRecord(recordId))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasFieldOrPropertyWithValue("status", HttpStatus.BAD_REQUEST);
        }
    }
}
