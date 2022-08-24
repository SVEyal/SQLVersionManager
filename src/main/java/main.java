import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import exeptions.DatabaseIOException;
import exeptions.EntityNotFoundException;
import exeptions.FieldNotFoundException;
import exeptions.RevisionNotFoundException;
import io.javalin.Javalin;
import org.eclipse.jetty.http.HttpStatus;
import persistance.db_connectors.MongoDBConnector;
import persistance.managers.FieldPersistentManager;
import sql_actions.FieldCrudManager;
import sql_entities.rest_classes.CreateOrUpdateFieldParams;

public class main {
    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7070);
        init(app);
    }

    private static void init(Javalin app) {
        final MongoDBConnector dbConnector = new MongoDBConnector();
        final FieldPersistentManager fpm = new FieldPersistentManager(dbConnector);
        final FieldCrudManager fcm = new FieldCrudManager(fpm);

        // Get all entities
        app.get("/entities", ctx -> {
            try {
                ctx.json(fcm.getAll());
            } catch (DatabaseIOException e) {
                ctx.status(HttpStatus.BAD_REQUEST_400).json("Error while trying to read from db");
            }
        });

        // CREATE new entity
        app.get("/entities/create/:id", ctx -> {
            try {
                ctx.json(fpm.addEntity(ctx.pathParam("id")));
            } catch (DatabaseIOException e) {
                ctx.status(HttpStatus.BAD_REQUEST_400).json("Error while trying to read from db");
            }
        });

        // CREATE or UPDATE field
        app.post("/field/create_or_update", ctx -> {
            CreateOrUpdateFieldParams params = ctx.bodyAsClass(CreateOrUpdateFieldParams.class);
            try {
                fcm.createOrUpdateField(params.getEntityIdentifier(),
                        params.getFieldIdentifier(),
                        params.getFieldType(),
                        params.getSqlCode(),
                        params.getDescription());
            } catch (FieldNotFoundException e) {
                ctx.status(HttpStatus.BAD_REQUEST_400).json("Field " + params.getFieldIdentifier() + " doesn't exist for entity " + params.getEntityIdentifier());
            } catch (EntityNotFoundException e) {
                ctx.status(HttpStatus.BAD_REQUEST_400).json("Could not find entity:" + params.getEntityIdentifier());
            } catch (DatabaseIOException e) {
                ctx.status(HttpStatus.BAD_REQUEST_400).json("Error while trying to read from db");
            }
        });

        // DELETE field
        app.delete("/field/:entity/:field", ctx -> {
            try {
                fcm.deleteField(ctx.pathParam("entity"), ctx.pathParam("field"));
            } catch (FieldNotFoundException e) {
                ctx.status(HttpStatus.BAD_REQUEST_400).json("Field " + ctx.pathParam("field") + " doesn't exist for entity " + ctx.pathParam("entity"));
            } catch (EntityNotFoundException e) {
                ctx.status(HttpStatus.BAD_REQUEST_400).json("Could not find entity:" + ctx.pathParam("entity"));
            } catch (Exception e) {
                ctx.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
            }
        });

        // READ values
        app.get("/field/:entity/:field", ctx -> {
            try {
                ctx.json(fcm.readField(ctx.pathParam("entity"), ctx.pathParam("field")));
            } catch (FieldNotFoundException e) {
                ctx.status(HttpStatus.BAD_REQUEST_400).json("Field " + ctx.pathParam("field") + " doesn't exist for entity " + ctx.pathParam("entity"));
            } catch (EntityNotFoundException e) {
                ctx.status(HttpStatus.BAD_REQUEST_400).json("Could not find entity:" + ctx.pathParam("entity"));
            } catch (DatabaseIOException e) {
                ctx.status(HttpStatus.BAD_REQUEST_400).json("Error while trying to read from db");
            }
        });

        app.get("/field/:entity/:field/:n", ctx -> {
            try {
                ctx.json(fcm.readNFieldVersions(ctx.pathParam("entity"),
                        ctx.pathParam(":field"),
                        ctx.validatedPathParam("n").asInt().getOrThrow()));
            } catch (RevisionNotFoundException e) {
                ctx.status(HttpStatus.BAD_REQUEST_400).json("Field " + ctx.pathParam("field") + " of entity " + ctx.pathParam("entity")
                        + " doesn't have " + ctx.pathParam("n") + " revisions");
            } catch (FieldNotFoundException e) {
                ctx.status(HttpStatus.BAD_REQUEST_400).json("Field " + ctx.pathParam("field") + " doesn't exist for entity " + ctx.pathParam("entity"));
            } catch (EntityNotFoundException e) {
                ctx.status(HttpStatus.BAD_REQUEST_400).json("Could not find entity:" + ctx.pathParam("entity"));
            } catch (DatabaseIOException e) {
                ctx.status(HttpStatus.BAD_REQUEST_400).json("Error while trying to read from db");
            }
        });
    }
}