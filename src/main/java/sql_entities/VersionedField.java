package sql_entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VersionedField that = (VersionedField) o;
        return Objects.equals(revisionCreationTime, that.revisionCreationTime) &&
                Objects.equals(revisingUser, that.revisingUser) &&
                Objects.equals(getName(), ((VersionedField) o).getName()) &&
                Objects.equals(getDescription(), ((VersionedField) o).getDescription()) &&
                Objects.equals(getType(), ((VersionedField) o).getType()) &&
                Objects.equals(getSqlCode(), ((VersionedField) o).getSqlCode());
    }
}
