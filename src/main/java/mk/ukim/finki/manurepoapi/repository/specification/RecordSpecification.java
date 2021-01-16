package mk.ukim.finki.manurepoapi.repository.specification;

import mk.ukim.finki.manurepoapi.dto.RecordsFilter;
import mk.ukim.finki.manurepoapi.enums.Collection;
import mk.ukim.finki.manurepoapi.model.Record;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static mk.ukim.finki.manurepoapi.repository.specification.SpecificationUtils.propertyContains;
import static mk.ukim.finki.manurepoapi.repository.specification.SpecificationUtils.propertyEquals;

public class RecordSpecification {

    public static Specification<Record> browseRecordsSpec(RecordsFilter filter) {
        return Specification.where(searchByTitleOrKeyword(filter.getTitleOrKeyword()))
                .and(isFromAnyCollection(filter.getCollections()))
                .and(propertyEquals("department", filter.getDepartment()))
                .and(propertyEquals("subject", filter.getSubject()))
                .and(isArchivedInYear(filter.getYear()))
                .and(propertyEquals("privateRecord", false))
                .and(propertyEquals("approved", true));
    }

    private static Specification<Record> searchByTitleOrKeyword(String searchTerm) {
        if (searchTerm == null || searchTerm.isEmpty()) {
            return null;
        }
        return SpecificationUtils.<Record>propertyContains("title", searchTerm)
                .or(propertyContains("keywords", searchTerm));
    }

    private static Specification<Record> isFromAnyCollection(List<Collection> collections) {
        if (collections == null || collections.isEmpty()) {
            return null;
        }
        Specification<Record> initialSpec = propertyEquals("collection", collections.get(0));
        return collections.stream()
                .skip(1)
                .map(collection -> SpecificationUtils.<Record>propertyEquals("collection", collection))
                .reduce(initialSpec, Specification::or);
    }

    private static Specification<Record> isArchivedInYear(Integer year) {
        if (year == null) {
            return null;
        }
        return (root, query, builder) ->
                builder.equal(builder.function("YEAR", Integer.class, root.get("dateArchived")), year);
    }

}