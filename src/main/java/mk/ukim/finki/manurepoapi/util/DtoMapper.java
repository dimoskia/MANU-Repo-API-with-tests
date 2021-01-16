package mk.ukim.finki.manurepoapi.util;

import mk.ukim.finki.manurepoapi.dto.RecordCard;
import mk.ukim.finki.manurepoapi.model.Record;
import org.modelmapper.ModelMapper;

public class DtoMapper {

    private static final ModelMapper modelMapper = new ModelMapper();

    public static RecordCard mapRecordToCard(Record record) {
        RecordCard recordCard = modelMapper.map(record, RecordCard.class);
        recordCard.setDateArchived(record.getDateArchived().toLocalDate());
        return recordCard;
    }

}
