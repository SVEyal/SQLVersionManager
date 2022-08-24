package persistance.db_connectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.*;
import com.mongodb.client.model.ReplaceOptions;
import exeptions.DatabaseIOException;
import org.bson.BsonDocument;
import org.bson.Document;
import sql_entities.VersionedField;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;


public class MongoDBConnector {
    // Configuration
    private MongoClient client;
    private ClientSession session;
    private MongoCollection collection;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void connect() {
        client = MongoClients.create(MongoConfig.CONNECTION_URI);
        session = client.startSession();
        MongoDatabase database = client.getDatabase(MongoConfig.DB_NAME);
        collection = database.getCollection(MongoConfig.COLLECTION_NAME);
    }

    public void persist(Object o) {
        try {
            String OString = objectMapper.writeValueAsString(o);
            collection.replaceOne(session, BsonDocument.parse("{}"), new Document(MongoConfig.DOCUMENT_NAME, OString), new ReplaceOptions().upsert(true));
        } catch (JsonProcessingException e) {
            System.out.println("Error reading obj, persist aborted");
        }
    }

    public HashMap<String, HashMap<String, List<VersionedField>>> read() throws DatabaseIOException {
         Document doc = (Document)collection.find(session, BsonDocument.parse("{}")).first();
         if(doc == null) {
             return new HashMap<>();
         }
         String data = (String) doc.get(MongoConfig.DOCUMENT_NAME);

         try {
             return objectMapper.readValue(data, new TypeReference<HashMap<String, HashMap<String, List<VersionedField>>>>() {});
         } catch (IOException e) {
             throw new DatabaseIOException();
         }
    }

    public void close() {
        client.close();
    }
}
