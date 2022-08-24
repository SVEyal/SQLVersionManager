package sql_entities;

import java.time.OffsetDateTime;

public class VersionedField extends Field {
    private final OffsetDateTime revisionCreationTime;
    private final String revisingUser;

    public VersionedField(String name,
                          FieldType type,
                          String sqlCode,
                          String description,
                          OffsetDateTime revisionCreationTime,
                          String revisingUser) {
        super(name, type, sqlCode, description);
        this.revisionCreationTime = revisionCreationTime;
        this.revisingUser = revisingUser;
    }

    public OffsetDateTime getRevisionCreationTime() {
        return revisionCreationTime;
    }

    public String getRevisingUser() {
        return revisingUser;
    }
}
