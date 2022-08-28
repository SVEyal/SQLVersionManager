package rest_controller;

import exeptions.DatabaseIOException;
import exeptions.EntityNotFoundException;
import exeptions.FieldNotFoundException;
import exeptions.RevisionNotFoundException;
import persistance.managers.FieldPersistentManager;
import sql_entities.FieldType;
import sql_entities.VersionedField;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;

/**
 * Main manager for CRUD operations in the system
 */
public class FieldRestController {
    private final FieldPersistentManager pm;

    public FieldRestController(FieldPersistentManager fieldPersistentManager) {
        this.pm = fieldPersistentManager;
    }

    /**
     * CREATE or UPDATE method
     *
     * @param entityIdentifier - Parent entity id
     * @param fieldIdentifier  - field id
     * @param fieldType        - field data type
     * @param SQLCode          - Code for sql calculation of field
     * @param description      - fields description
     * @param username         - username of revision owner
     * @throws FieldNotFoundException  - throws this exception if field doesn't exist
     * @throws EntityNotFoundException - throws this exception if entity doesn't exist
     */
    public void createOrUpdateField(String entityIdentifier,
                                    String fieldIdentifier,
                                    FieldType fieldType,
                                    String SQLCode,
                                    String description,
                                    String username) throws EntityNotFoundException, FieldNotFoundException, DatabaseIOException {
        VersionedField versionedField = new VersionedField(fieldIdentifier, fieldType, SQLCode, description, Instant.now().getEpochSecond(), username);
        pm.persist(entityIdentifier, versionedField);
    }


    /**
     * DELETE method
     *
     * @param entityIdentifier - Parent entity id
     * @param fieldIdentifier  - field id
     * @throws FieldNotFoundException  - throws this exception if field doesn't exist
     * @throws EntityNotFoundException - throws this exception if entity doesn't exist
     */
    public void deleteField(String entityIdentifier, String fieldIdentifier) throws EntityNotFoundException, FieldNotFoundException {
        pm.delete(entityIdentifier, fieldIdentifier);
    }


    /**
     * READ method
     *
     * @param entityIdentifier - Parent entity id
     * @param fieldIdentifier  - field id
     * @return - the value of the latest version of the field
     * @throws FieldNotFoundException  - throws this exception if field doesn't exist
     * @throws EntityNotFoundException - throws this exception if entity doesn't exist
     * @throws DatabaseIOException     - throws this exception if could not read from DB
     */
    public VersionedField readField(String entityIdentifier, String fieldIdentifier) throws EntityNotFoundException, FieldNotFoundException, DatabaseIOException {
        return pm.read(entityIdentifier, fieldIdentifier);
    }

    /**
     * multi READ method, reads N latest versions
     *
     * @param entityIdentifier - Parent entity id
     * @param fieldIdentifier  - field id
     * @param n                - number of latest versions to read
     * @return - list of latest versions of the field
     * @throws FieldNotFoundException    - throws this exception if field doesn't exist
     * @throws EntityNotFoundException   - throws this exception if entity doesn't exist
     * @throws RevisionNotFoundException - throws this exception if revision doesn't exist
     * @throws DatabaseIOException       - throws this exception if could not read from DB
     */
    public List<VersionedField> readNFieldVersions(String entityIdentifier, String fieldIdentifier, int n) throws EntityNotFoundException, FieldNotFoundException, RevisionNotFoundException, DatabaseIOException {
        return pm.readNRevisions(entityIdentifier, fieldIdentifier, n);
    }


    /**
     * returns full entity - field structure
     *
     * @return - entity field structure via map of names
     * @throws DatabaseIOException - throws this exception if could not read from DB
     */
    public HashMap<String, List<String>> getAll() throws DatabaseIOException {
        return pm.getAll();
    }
}
