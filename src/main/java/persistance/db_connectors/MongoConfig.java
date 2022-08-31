package persistance.db_connectors;

import org.bson.BsonDocument;

public class MongoConfig {
    protected static final String HOST = "mongodb.db_connection";
    protected static final int PORT = 27017;
    protected static final String CONNECTION_URI = String.format("mongodb://%s:%s", HOST, PORT);
    protected static final String DB_NAME = "SQLVersioningDB";
    protected static final String COLLECTION_NAME = "DATA";
    protected static final String DOCUMENT_NAME = "DATA_DOC";
    protected static final BsonDocument EMPTY_FILTER = BsonDocument.parse("{}");
}
