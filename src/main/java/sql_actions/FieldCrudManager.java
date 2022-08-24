package sql_actions;

import exeptions.EntityNotFoundException;
import exeptions.FieldNotFoundException;
import exeptions.RevisionNotFoundException;
import persistance.FieldPersistentManager;
import sql_entities.FieldType;
import sql_entities.VersionedField;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;

/**
 * Main manager for CRUD operations in the system
 */
public class FieldCrudManager {
    private final FieldPersistentManager pm;

    public FieldCrudManager(FieldPersistentManager fieldPersistentManager) {
        this.pm = fieldPersistentManager;
    }

    /**
     * CREATE or UPDATE method
     * @param entityIdentifier - Parent entity id
     * @param fieldIdentifier - field id
     * @param fieldType - field data type
     * @param SQLCode - Code for sql calculation of field
     * @param description - fields description
     * @throws FieldNotFoundException - throws this exception if field doesn't exist
     * @throws EntityNotFoundException - throws this exception if entity doesn't exist
     */
    public void createOrUpdateField(String entityIdentifier,
                                    String fieldIdentifier,
                                    FieldType fieldType,
                                    String SQLCode,
                                    String description) throws EntityNotFoundException, FieldNotFoundException {
        VersionedField versionedField = new VersionedField(fieldIdentifier, fieldType, SQLCode, description, OffsetDateTime.now(), "user"); // todo: user
        pm.persist(entityIdentifier, versionedField);
    }


    /**
     * DELETE method
     * @param entityIdentifier - Parent entity id
     * @param fieldIdentifier - field id
     * @throws FieldNotFoundException - throws this exception if field doesn't exist
     * @throws EntityNotFoundException - throws this exception if entity doesn't exist
     */
    public void deleteField(String entityIdentifier, String fieldIdentifier) throws EntityNotFoundException, FieldNotFoundException {
        pm.delete(entityIdentifier, fieldIdentifier);
    }


    /**
     * READ method
     * @param entityIdentifier - Parent entity id
     * @param fieldIdentifier - field id
     * @return - the value of the latest version of the field
     * @throws FieldNotFoundException - throws this exception if field doesn't exist
     * @throws EntityNotFoundException - throws this exception if entity doesn't exist
     */
    public VersionedField readField(String entityIdentifier, String fieldIdentifier) throws EntityNotFoundException, FieldNotFoundException {
        return pm.read(entityIdentifier, fieldIdentifier);
    }

    /**
     * multi READ method, reads N latest versions
     * @param entityIdentifier - Parent entity id
     * @param fieldIdentifier - field id
     * @param n - number of latest versions to read
     * @return - list of latest versions of the field
     * @throws FieldNotFoundException - throws this exception if field doesn't exist
     * @throws EntityNotFoundException - throws this exception if entity doesn't exist
     * @throws RevisionNotFoundException - throws this exception if revision doesn't exist
     */
    public List<VersionedField> readNFieldVersions(String entityIdentifier, String fieldIdentifier, int n) throws EntityNotFoundException, FieldNotFoundException, RevisionNotFoundException {
        return pm.readNVersions(entityIdentifier, fieldIdentifier, n);
    }


    /**
     * returns full entity - feild structure
     * @return - entity field structure via map of names
     */
    public HashMap<String, List<String>> getAll() {
        return pm.getAll();
    }
}
