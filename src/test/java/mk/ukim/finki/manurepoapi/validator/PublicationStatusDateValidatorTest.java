package mk.ukim.finki.manurepoapi.validator;

import mk.ukim.finki.manurepoapi.dto.request.RecordRequest;
import mk.ukim.finki.manurepoapi.enums.PublicationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PublicationStatusDateValidatorTest {

    PublicationStatusDateValidator publicationStatusDateValidator;

    @BeforeEach
    void setUp() {
        publicationStatusDateValidator = new PublicationStatusDateValidator();
    }

    /*
         --- Interface based IDM ---

         Two parameters: publicationStatus (PublicationStatus) , publicationDate (LocalDate)
         Returns: Boolean

         ID  Characteristic                 Block 1     Block 2     Block 3
         ---------------------------------------------------------------------------------------------------------------------
         C1  State of publicationStatus     null        PUBLISHED   !PUBLISHED
         C2  State of publicationDate       null        defined

         COMPLETENESS and DISJOINTNESS satisfied

         All Combinations Coverage (ACoC): 6 Test Requirements (no infeasible TRs)
         publicationStatus      publicationDate
         --------------------------------------
         null                   null
         PUBLISHED              2020-01-01
         SUBMITTED              null
         null                   2020-01-01
         PUBLISHED              null
         SUBMITTED              2020-01-01
    */
    @ParameterizedTest
    @CsvSource(value = {
            "null, null, true",
            "PUBLISHED, 2020-01-01, true",
            "SUBMITTED, null, true",
            "null, 2020-01-01, false",
            "PUBLISHED, null, true",
            "SUBMITTED, 2020-01-01, false"
    }, nullValues = "null")
    void isValid(PublicationStatus publicationStatus, LocalDate publicationDate, Boolean expectedValidity) {
        // given
        RecordRequest recordRequest = RecordRequest.builder()
                .publicationStatus(publicationStatus)
                .publicationDate(publicationDate)
                .build();

        // when
        boolean isValid = publicationStatusDateValidator.isValid(recordRequest, null);

        // then
        assertThat(isValid).isEqualTo(expectedValidity);
    }

}
