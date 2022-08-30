package persistance.managers;

import exeptions.DatabaseIOException;
import exeptions.EntityNotFoundException;
import exeptions.FieldNotFoundException;
import persistance.db_connectors.MongoDBConnector;
import sql_entities.VersionedField;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Manages persistence for the system over MongoDB
 * Data format is a HashMap<EntityId, HashMap<FieldId, VersionField>>>
 * changed to db
 */

public class FieldPersistentManager {
    private HashMap<String, HashMap<String, List<VersionedField>>> data;
    private final MongoDBConnector dbConnector;

    public FieldPersistentManager(MongoDBConnector dbConnector) {
        this.dbConnector = dbConnector;
        this.dbConnector.connect();
        try {
            this.data = dbConnector.read();
        } catch (DatabaseIOException e) {
            System.out.println("Could not read from database with error, " + e);
        }
    }

    /**
     * Persists data in persistence solution, either create or update
     *
     * @param entityId - id of the Parent entity
     * @throws EntityNotFoundException - throws this exception if entity doesn't exist
     * @throws DatabaseIOException     - throws this exception if could not read from DB
     */
    public void persist(String entityId, VersionedField versionedField) throws EntityNotFoundException, DatabaseIOException {
        updateDataFromDB();
        if (data.containsKey(entityId)) {
            if (data.get(entityId).containsKey(versionedField.getId())) {
                final int size = data.get(entityId).get(versionedField.getId()).size();
                if (!data.get(entityId).get(versionedField.getId()).get(size-1).equals(versionedField)) {
                    data.get(entityId).get(versionedField.getId()).add(versionedField);
                }
            } else {
                data.get(entityId).put(versionedField.getId(), new  ArrayList<>(Collections.singletonList(versionedField)));
            }
            dbConnector.persist(data);
        } else {
            throw new EntityNotFoundException();
        }
    }

    /**
     * Deletes data in persistence solution
     *
     * @param entityId - id of the Parent entity
     * @param fieldId  - id of the field
     * @throws FieldNotFoundException  - throws this exception if field doesn't exist
     * @throws EntityNotFoundException - throws this exception if entity doesn't exist
     */
    public void delete(String entityId, String fieldId) throws FieldNotFoundException, EntityNotFoundException {
        if (data.containsKey(entityId)) {
            if (data.get(entityId).containsKey(fieldId)) {
                data.get(entityId).remove(fieldId);
                dbConnector.persist(data);
            } else {
                throw new FieldNotFoundException();
            }
        } else {
            throw new EntityNotFoundException();
        }
    }

    /**
     * Reads field by entity and field names
     *
     * @param entityId - id of the Parent entity
     * @param fieldId  - id of the field
     * @return - latest version of data of field
     * @throws FieldNotFoundException  - throws this exception if field doesn't exist
     * @throws EntityNotFoundException - throws this exception if entity doesn't exist
     */
    public VersionedField read(String entityId, String fieldId) throws FieldNotFoundException, EntityNotFoundException, DatabaseIOException {
        updateDataFromDB();
        if (data.containsKey(entityId)) {
            if (data.get(entityId).containsKey(fieldId)) {
                return data.get(entityId).get(fieldId).get(data.get(entityId).get(fieldId).size() - 1);
            } else {
                throw new FieldNotFoundException();
            }
        } else {
            throw new EntityNotFoundException();
        }
    }

    /**
     * Reads N versions of field by entity and field names
     *
     * @param entityId - id of the Parent entity
     * @param fieldId  - id of the field
     * @param n        - amount of versions to return
     * @return - n last versions of field
     * @throws FieldNotFoundException    - throws this exception if field doesn't exist
     * @throws EntityNotFoundException   - throws this exception if entity doesn't exist
     */
    public List<VersionedField> readNRevisions(String entityId, String fieldId, int n) throws FieldNotFoundException, EntityNotFoundException, DatabaseIOException {
        updateDataFromDB();
        if (data.containsKey(entityId)) {
            if (data.get(entityId).containsKey(fieldId)) {
                final int lastPos = data.get(entityId).get(fieldId).size();
                return data.get(entityId).get(fieldId).subList(Math.max(lastPos - n, 0), lastPos);
            } else {
                throw new FieldNotFoundException();
            }
        } else {
            throw new EntityNotFoundException();
        }
    }

    /**
     * Get all the entities to field structure
     *
     * @return - entities to field structure map
     */
    public HashMap<String, List<String>> getAll() throws DatabaseIOException {
        updateDataFromDB();
        HashMap<String, List<String>> entityToFieldMap = new HashMap<>();
        for (String entity :
                data.keySet()) {
            entityToFieldMap.put(entity, new ArrayList<>());
            for (String field :
                    data.get(entity).keySet()) {
                entityToFieldMap.get(entity).add(field);
            }
        }
        return entityToFieldMap;
    }


    /**
     * Creates a new entity
     *
     * @param entityId - the id of a new entity
     * @return - returns the ID of the newly created entity
     * @throws DatabaseIOException - throws when fails to read entity from DB
     */
    public String addEntity(String entityId) throws DatabaseIOException {
        updateDataFromDB();
        data.put(entityId, new HashMap<>());
        dbConnector.persist(data);
        return entityId;
    }

    private void updateDataFromDB() throws DatabaseIOException {
        data = dbConnector.read();
    }
}
