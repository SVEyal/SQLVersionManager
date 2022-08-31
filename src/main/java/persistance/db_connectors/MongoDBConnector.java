package persistance.db_connectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.*;
import com.mongodb.client.model.ReplaceOptions;
import exeptions.MongoReadException;
import org.bson.Document;
import sql_entities.FieldRevision;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;


/**
 * Simple DB Connection handler for MongoDB
 * Built to persist a DataStructure as a single object in DB
 * Saves Object within a given DB, in a single MongoCollection, in a single Document
 * as a single value within it
 */
public class MongoDBConnector {
    private MongoClient client;
    private ClientSession session;
    private MongoCollection<Document> collection;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Connects to DB and initializes it
     * opens a session to the DB
     */
    public void connect() {
        client = MongoClients.create(MongoConfig.CONNECTION_URI);
        session = client.startSession();
        MongoDatabase database = client.getDatabase(MongoConfig.DB_NAME);
        collection = database.getCollection(MongoConfig.COLLECTION_NAME);
    }

    /**
     * Persists Object within the database
     *
     * @param o - the entire data to persist in DB
     */
    public void persist(Object o) {
        try {
            String OString = objectMapper.writeValueAsString(o);
            collection.replaceOne(session, MongoConfig.EMPTY_FILTER, new Document(MongoConfig.DOCUMENT_NAME, OString), new ReplaceOptions().upsert(true));
        } catch (JsonProcessingException e) {
            System.out.println("Error reading obj, persist aborted");
        }
    }

    /**
     * @return the Data within the db
     * @throws MongoReadException - if cannot parse the data will throw exception
     */
    public HashMap<String, HashMap<String, List<FieldRevision>>> read() throws MongoReadException {
        Document doc = collection.find(session, MongoConfig.EMPTY_FILTER).first();
        if (doc == null) {
            return new HashMap<>();
        }
        String data = (String) doc.get(MongoConfig.DOCUMENT_NAME);

        try {
            return objectMapper.readValue(data, new TypeReference<HashMap<String, HashMap<String, List<FieldRevision>>>>() {});
        } catch (IOException e) {
            throw new MongoReadException();
        }
    }

    /**
     * Closes the connection to the database
     */
    public void close() {
        client.close();
    }
}
