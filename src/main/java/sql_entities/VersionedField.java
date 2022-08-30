package sql_entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class VersionedField extends Field {
    private final Long revisionCreationTime;
    private final String revisingUser;

    @JsonCreator
    public VersionedField(@JsonProperty("name") String name,
                          @JsonProperty("type") FieldType type,
                          @JsonProperty("sqlCode") String sqlCode,
                          @JsonProperty("description") String description,
                          @JsonProperty("revisionCreationTime") Long revisionCreationTime,
                          @JsonProperty("revisingUser") String revisingUser) {
        super(name, type, sqlCode, description);
        this.revisionCreationTime = revisionCreationTime;
        this.revisingUser = revisingUser;
    }

    public Long getRevisionCreationTime() {
        return revisionCreationTime;
    }

    public String getRevisingUser() {
        return revisingUser;
    }
}
