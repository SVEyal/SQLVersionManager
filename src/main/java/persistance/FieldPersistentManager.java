package persistance;

import exeptions.EntityNotFoundException;
import exeptions.FieldNotFoundException;
import exeptions.RevisionNotFoundException;
import sql_entities.VersionedField;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Manages persistence for the system
 * this persistence is local and will be later
 * changed to db
 */
// todo: replace with database
public class FieldPersistentManager {
    private final HashMap<String, HashMap<String, List<VersionedField>>> data;

    public FieldPersistentManager() {
        this.data = new HashMap<>();
    }

    /**
     * Persists data in persistence solution, either create or update
     * @param entityId - id of the Parent entity
     * @param versionedField - field version info
     * @throws FieldNotFoundException - throws this exception if field doesn't exist
     * @throws EntityNotFoundException - throws this exception if entity doesn't exist
     */
    public void persist(String entityId, VersionedField versionedField) throws EntityNotFoundException, FieldNotFoundException {
        if(data.containsKey(entityId)) {
            if(data.get(entityId).containsKey(versionedField.getId())) {
                if(!read(entityId, versionedField.getId()).equals(versionedField)) {
                    data.get(entityId).get(versionedField.getId()).add(versionedField);
                }
            } else {
                data.get(entityId).put(versionedField.getId(), new ArrayList<>(Collections.singletonList(versionedField)));
            }
        } else {
            throw new EntityNotFoundException();
        }
    }

    /**
     * Deletes data in persistence solution
     * @param entityId - id of the Parent entity
     * @param fieldId - id of the field
     * @throws FieldNotFoundException - throws this exception if field doesn't exist
     * @throws EntityNotFoundException - throws this exception if entity doesn't exist
     */
    public void delete(String entityId, String fieldId) throws FieldNotFoundException, EntityNotFoundException {
        if(data.containsKey(entityId)) {
            if(data.get(entityId).containsKey(fieldId)) {
                data.get(entityId).remove(fieldId);
            } else {
                throw new FieldNotFoundException();
            }
        } else {
            throw new EntityNotFoundException();
        }
    }

    /**
     * Reads field by entity and field names
     * @param entityId - id of the Parent entity
     * @param fieldId - id of the field
     * @return - latest version of data of field
     * @throws FieldNotFoundException - throws this exception if field doesn't exist
     * @throws EntityNotFoundException - throws this exception if entity doesn't exist
     */
    public VersionedField read(String entityId, String fieldId) throws FieldNotFoundException, EntityNotFoundException {
        if(data.containsKey(entityId)) {
            if(data.get(entityId).containsKey(fieldId)) {
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
     * @param entityId - id of the Parent entity
     * @param fieldId - id of the field
     * @param n - amount of versions to return
     * @return - n last versions of field
     * @throws FieldNotFoundException - throws this exception if field doesn't exist
     * @throws EntityNotFoundException - throws this exception if entity doesn't exist
     * @throws RevisionNotFoundException - throws this exception if revision doesn't exist
     */
    public List<VersionedField> readNVersions(String entityId, String fieldId, int n) throws FieldNotFoundException, EntityNotFoundException, RevisionNotFoundException {
        if(data.containsKey(entityId)) {
            if(data.get(entityId).containsKey(fieldId)) {
                if(data.get(entityId).get(fieldId).size() >= n) {
                    final int lastPos = data.get(entityId).get(fieldId).size();
                    return data.get(entityId).get(fieldId).subList(lastPos - n ,lastPos);
                } else {
                    throw new RevisionNotFoundException();
                }
            } else {
                throw new FieldNotFoundException();
            }
        } else {
            throw new EntityNotFoundException();
        }
    }

    /**
     * Get all the entities to field structure
     * @return - entities to field structure map
     */
    public HashMap<String, List<String>> getAll() {
        HashMap<String, List<String>> entityToFieldMap = new HashMap<>();
        for (String entity:
             data.keySet()) {
            entityToFieldMap.put(entity, new ArrayList<>());
            for (String field:
                 data.get(entity).keySet()) {
                entityToFieldMap.get(entity).add(field);
            }
        }
        return entityToFieldMap;
    }

    //todo : remove
    public String addEntity(String entityId) {
        data.put(entityId, new HashMap<>());
        return entityId;
    }
}
