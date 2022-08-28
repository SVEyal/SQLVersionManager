package api;

import exeptions.DatabaseIOException;
import exeptions.EntityNotFoundException;
import exeptions.FieldNotFoundException;
import exeptions.RevisionNotFoundException;
import persistance.db_connectors.MongoDBConnector;
import persistance.managers.FieldPersistentManager;
import rest_controller.FieldRestController;
import sql_entities.FieldType;

import java.util.HashMap;
import java.util.List;

public class ServerController {
    private final MongoDBConnector dbConnector = new MongoDBConnector();
    private final FieldPersistentManager fpm = new FieldPersistentManager(dbConnector);
    private final FieldRestController rc = new FieldRestController(fpm);

    public HashMap<String, List<String>> getAll() throws DatabaseIOException {
        return rc.getAll();
    }

    public String addEntity(String entityId) throws DatabaseIOException {
        return fpm.addEntity(entityId);
    }

    public void createOrUpdateField(String entityIdentifier,
                                    String fieldIdentifier,
                                    FieldType fieldType,
                                    String sqlCode,
                                    String description,
                                    String username)
            throws EntityNotFoundException, FieldNotFoundException, DatabaseIOException {
        rc.createOrUpdateField(entityIdentifier,
                fieldIdentifier,
                fieldType,
                sqlCode,
                description,
                username);
    }

    public void deleteField(String entity, String field)
            throws FieldNotFoundException, EntityNotFoundException {
        rc.deleteField(entity, field);
    }

    public Object readField(String entity, String field)
            throws EntityNotFoundException, FieldNotFoundException, DatabaseIOException {
        return rc.readField(entity, field);
    }


    public Object readNFieldVersions(String entity, String pathParam, Integer n)
            throws FieldNotFoundException, RevisionNotFoundException, DatabaseIOException, EntityNotFoundException {
        return rc.readNFieldVersions(entity, pathParam, n);
    }
}
