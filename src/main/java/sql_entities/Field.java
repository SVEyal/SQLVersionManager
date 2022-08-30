package sql_entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Objects;

public class Field implements Identified {
    private final String name;
    private final FieldType type;
    private final String sqlCode;
    private final String description;

    public Field(String name, FieldType type, String sqlCode, String description) {
        this.name = name;
        this.type = type;
        this.sqlCode = sqlCode;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public FieldType getType() {
        return type;
    }

    public String getSqlCode() {
        return sqlCode;
    }

    public String getDescription() {
        return description;
    }

    @Override
    @JsonIgnore
    public String getId() {
        return this.name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Field field = (Field) o;
        return  Objects.equals(name, field.name) &&
                type == field.type &&
                Objects.equals(sqlCode, field.sqlCode) &&
                Objects.equals(description, field.description);
    }
}
