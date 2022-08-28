package api;

import exeptions.DatabaseIOException;
import exeptions.EntityNotFoundException;
import exeptions.FieldNotFoundException;
import exeptions.RevisionNotFoundException;
import io.javalin.Javalin;
import org.eclipse.jetty.http.HttpStatus;
import sql_entities.actions.CreateOrUpdateFieldParams;
import sql_entities.actions.CreateUserParams;

public class ServerAPI {
    private static final int PORT = 80;

    public static void start() {
        Javalin app = Javalin.create().start(PORT);
        init(app);
    }

    private static void init(Javalin app) {
        // Get all entities
        ServerController sc = new ServerController();
        JavalinSimpleAuth auth = new JavalinSimpleAuth();

        app.get("/entities", ctx -> auth.authenticate(ctx, () -> {
            try {
                ctx.json(sc.getAll());
            } catch (DatabaseIOException e) {
                ctx.status(HttpStatus.BAD_REQUEST_400).json("Error while trying to read from db");
            }
        }));

        // CREATE new entity
        app.get("/entities/create/:id", ctx -> auth.authenticate(ctx, () -> {
            try {
                ctx.json(sc.addEntity(ctx.pathParam("id")));
            } catch (DatabaseIOException e) {
                ctx.status(HttpStatus.BAD_REQUEST_400).json("Error while trying to read from db");
            }
        }));

        // CREATE or UPDATE field
        app.post("/field/create_or_update", ctx -> auth.authenticate(ctx, () -> {
            CreateOrUpdateFieldParams params = ctx.bodyAsClass(CreateOrUpdateFieldParams.class);
            try {
                sc.createOrUpdateField(params.getEntityIdentifier(),
                        params.getFieldIdentifier(),
                        params.getFieldType(),
                        params.getSqlCode(),
                        params.getDescription(),
                        auth.getUser(ctx));
            } catch (FieldNotFoundException e) {
                ctx.status(HttpStatus.BAD_REQUEST_400).json("Field " + params.getFieldIdentifier() + " doesn't exist for entity " + params.getEntityIdentifier());
            } catch (EntityNotFoundException e) {
                ctx.status(HttpStatus.BAD_REQUEST_400).json("Could not find entity:" + params.getEntityIdentifier());
            } catch (DatabaseIOException e) {
                ctx.status(HttpStatus.BAD_REQUEST_400).json("Error while trying to read from db");
            }
        }));

        // DELETE field
        app.delete("/field/:entity/:field", ctx -> auth.authenticate(ctx, () -> {
            try {
                sc.deleteField(ctx.pathParam("entity"), ctx.pathParam("field"));
            } catch (FieldNotFoundException e) {
                ctx.status(HttpStatus.BAD_REQUEST_400).json("Field " + ctx.pathParam("field") + " doesn't exist for entity " + ctx.pathParam("entity"));
            } catch (EntityNotFoundException e) {
                ctx.status(HttpStatus.BAD_REQUEST_400).json("Could not find entity:" + ctx.pathParam("entity"));
            } catch (Exception e) {
                ctx.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
            }
        }));

        // READ values
        app.get("/field/:entity/:field", ctx -> auth.authenticate(ctx, () -> {
            try {
                ctx.json(sc.readField(ctx.pathParam("entity"), ctx.pathParam("field")));
            } catch (FieldNotFoundException e) {
                ctx.status(HttpStatus.BAD_REQUEST_400).json("Field " + ctx.pathParam("field") + " doesn't exist for entity " + ctx.pathParam("entity"));
            } catch (EntityNotFoundException e) {
                ctx.status(HttpStatus.BAD_REQUEST_400).json("Could not find entity:" + ctx.pathParam("entity"));
            } catch (DatabaseIOException e) {
                ctx.status(HttpStatus.BAD_REQUEST_400).json("Error while trying to read from db");
            }
        }));

        app.get("/field/:entity/:field/:n", ctx -> auth.authenticate(ctx, () -> {
            try {
                ctx.json(sc.readNFieldVersions(ctx.pathParam("entity"),
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
        }));

        // CREATE user
        app.post("/users", ctx -> auth.authenticate(ctx, () -> {
            CreateUserParams params = ctx.bodyAsClass(CreateUserParams.class);
            if(auth.addUser(params.getUsername(), params.getPassword())) {
                ctx.status(HttpStatus.CREATED_201).json("Created user " + params.getUsername());
            } else {
                ctx.status(HttpStatus.BAD_REQUEST_400).json("Could not create user " + params.getUsername());
            }
        }));

        // DELETE user
        app.delete("/users/:username", ctx -> auth.authenticate(ctx, () -> {
            if(auth.deleteUser(ctx.pathParam("username"))) {
                ctx.status(HttpStatus.OK_200).json("Deleted user " + ctx.pathParam("username"));
            } else {
                ctx.status(HttpStatus.BAD_REQUEST_400).json("Could not delete user " + ctx.pathParam("username"));
            }
        }));

        app.get("/users", ctx -> ctx.json(auth.getUsers()));
    }
}
