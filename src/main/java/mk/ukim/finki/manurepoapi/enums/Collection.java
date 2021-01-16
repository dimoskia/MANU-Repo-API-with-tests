package mk.ukim.finki.manurepoapi.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Collection {

    ARTICLE("Article"),
    REVIEW("Review"),
    PAPER_REPORT("Paper or Report"),
    CONFERENCE_ITEM("Conference Contribution"),
    BOOK("Book"),
    MONOGRAPH("Monograph"),
    THESIS("Thesis"),
    ARTEFACT("Artefact"),
    EVENT("Event, Show or Exhibition"),
    IMAGE("Image"),
    VIDEO("Video"),
    AUDIO("Audio"),
    DATASET("Dataset"),
    EXPERIMENT("Experiment"),
    LEARNING_RESOURCE("Learning Resource"),
    PROJECT("Project"),
    PROCEEDINGS("Proceedings"),
    OTHER("Other");

    private final String fullCollection;

}