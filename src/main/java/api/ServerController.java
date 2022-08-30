package api;

import exeptions.DatabaseIOException;
import exeptions.EntityNotFoundException;
import exeptions.FieldNotFoundException;
import persistance.db_connectors.MongoDBConnector;
import persistance.managers.FieldPersistentManager;
import rest_controller.FieldRestController;
import sql_entities.FieldType;

import java.util.HashMap;
import java.util.List;

public class ServerController {
    private final MongoDBConnector dbConnector = new MongoDBConnector();
    private final FieldPersistentManager persistentManager = new FieldPersistentManager(dbConnector);
    private final FieldRestController restController = new FieldRestController(persistentManager);

    public HashMap<String, List<String>> getAll() throws DatabaseIOException {
        return restController.getAll();
    }

    public String addEntity(String entityId) throws DatabaseIOException {
        return persistentManager.addEntity(entityId);
    }

    public void createOrUpdateField(String entityIdentifier,
                                    String fieldIdentifier,
                                    FieldType fieldType,
                                    String sqlCode,
                                    String description,
                                    String username)
            throws EntityNotFoundException, DatabaseIOException {
        restController.createOrUpdateField(entityIdentifier,
                fieldIdentifier,
                fieldType,
                sqlCode,
                description,
                username);
    }

    public void deleteField(String entity, String field)
            throws FieldNotFoundException, EntityNotFoundException {
        restController.deleteField(entity, field);
    }

    public Object readField(String entity, String field)
            throws EntityNotFoundException, FieldNotFoundException, DatabaseIOException {
        return restController.readField(entity, field);
    }


    public Object readNFieldVersions(String entity, String pathParam, Integer n)
            throws FieldNotFoundException, DatabaseIOException, EntityNotFoundException {
        return restController.readNFieldVersions(entity, pathParam, n);
    }
}
