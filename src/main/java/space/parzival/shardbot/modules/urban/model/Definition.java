package space.parzival.shardbot.modules.urban.model;

import java.time.OffsetDateTime;

import lombok.Setter;
import lombok.Getter;

@Getter @Setter
public class Definition {

    /**
     * The definition of the word.
     */
    private String definition;

    /**
     * A link to the word definition on Urban Dictionary.
     */
    private String permalink;

    /**
     * The number of positiv votes on the entry.
     */
    private long thumbsUp;

    /**
     * The author of the definition-
     */
    private String author;

    /**
     * The word that is defined by this definition.
     */
    private String word;

    /**
     * The id of the definition.
     */
    private long defid;

    /**
     * ???
     */
    private String currentVote;

    /**
     * The time when this definition was created.
     */
    private OffsetDateTime writtenOn;

    /**
     * An example text to simplify the definition.
     */
    private String example;

    /**
     * The number of negative votes for this entry
     */
    private long thumbsDown;
}
