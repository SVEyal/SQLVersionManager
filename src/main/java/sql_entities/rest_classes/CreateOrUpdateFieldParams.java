package sql_entities.rest_classes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import sql_entities.FieldType;

public class CreateOrUpdateFieldParams {
    private final String entityIdentifier;
    private final String fieldIdentifier;
    private final FieldType fieldType;
    private final String sqlCode;
    private final String description;

    @JsonCreator
    public CreateOrUpdateFieldParams(@JsonProperty("entityIdentifier") String entityIdentifier,
                                     @JsonProperty("fieldIdentifier") String fieldIdentifier,
                                     @JsonProperty("fieldType") FieldType fieldType,
                                     @JsonProperty("sqlCode") String sqlCode,
                                     @JsonProperty("description") String description) {
        this.entityIdentifier = entityIdentifier;
        this.fieldIdentifier = fieldIdentifier;
        this.fieldType = fieldType;
        this.sqlCode = sqlCode;
        this.description = description;
    }

    public String getEntityIdentifier() {
        return entityIdentifier;
    }

    public String getFieldIdentifier() {
        return fieldIdentifier;
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    public String getSqlCode() {
        return sqlCode;
    }

    public String getDescription() {
        return description;
    }
}
