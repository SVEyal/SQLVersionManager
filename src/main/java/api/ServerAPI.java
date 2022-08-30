package api;

import exeptions.DatabaseIOException;
import exeptions.EntityNotFoundException;
import exeptions.FieldNotFoundException;
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
        ServerController serverController = new ServerController();
        JavalinSimpleAuth authenticator = new JavalinSimpleAuth();

        app.get("/entities", ctx -> authenticator.authenticate(ctx, () -> {
            try {
                ctx.json(serverController.getAll());
            } catch (DatabaseIOException e) {
                ctx.status(HttpStatus.BAD_REQUEST_400).json("Error while trying to read from db");
            }
        }));

        // CREATE new entity
        app.get("/entities/create/:id", ctx -> authenticator.authenticate(ctx, () -> {
            try {
                ctx.json(serverController.addEntity(ctx.pathParam("id")));
            } catch (DatabaseIOException e) {
                ctx.status(HttpStatus.BAD_REQUEST_400).json("Error while trying to read from db");
            }
        }));

        // CREATE or UPDATE field
        app.post("/field/create_or_update", ctx -> authenticator.authenticate(ctx, () -> {
            CreateOrUpdateFieldParams params = ctx.bodyAsClass(CreateOrUpdateFieldParams.class);
            try {
                serverController.createOrUpdateField(params.getEntityIdentifier(),
                        params.getFieldIdentifier(),
                        params.getFieldType(),
                        params.getSqlCode(),
                        params.getDescription(),
                        authenticator.getUser(ctx));
            }  catch (EntityNotFoundException e) {
                ctx.status(HttpStatus.BAD_REQUEST_400).json("Could not find entity:" + params.getEntityIdentifier());
            } catch (DatabaseIOException e) {
                ctx.status(HttpStatus.BAD_REQUEST_400).json("Error while trying to read from db");
            }
        }));

        // DELETE field
        app.delete("/field/:entity/:field", ctx -> authenticator.authenticate(ctx, () -> {
            try {
                serverController.deleteField(ctx.pathParam("entity"), ctx.pathParam("field"));
            } catch (FieldNotFoundException e) {
                ctx.status(HttpStatus.BAD_REQUEST_400).json("Field " + ctx.pathParam("field") + " doesn't exist for entity " + ctx.pathParam("entity"));
            } catch (EntityNotFoundException e) {
                ctx.status(HttpStatus.BAD_REQUEST_400).json("Could not find entity:" + ctx.pathParam("entity"));
            } catch (Exception e) {
                ctx.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
            }
        }));

        // READ values
        app.get("/field/:entity/:field", ctx -> authenticator.authenticate(ctx, () -> {
            try {
                ctx.json(serverController.readField(ctx.pathParam("entity"), ctx.pathParam("field")));
            } catch (FieldNotFoundException e) {
                ctx.status(HttpStatus.BAD_REQUEST_400).json("Field " + ctx.pathParam("field") + " doesn't exist for entity " + ctx.pathParam("entity"));
            } catch (EntityNotFoundException e) {
                ctx.status(HttpStatus.BAD_REQUEST_400).json("Could not find entity:" + ctx.pathParam("entity"));
            } catch (DatabaseIOException e) {
                ctx.status(HttpStatus.BAD_REQUEST_400).json("Error while trying to read from db");
            }
        }));

        app.get("/field/:entity/:field/:n", ctx -> authenticator.authenticate(ctx, () -> {
            try {
                ctx.json(serverController.readNFieldVersions(ctx.pathParam("entity"),
                        ctx.pathParam(":field"),
                        ctx.validatedPathParam("n").asInt().getOrThrow()));
            } catch (FieldNotFoundException e) {
                ctx.status(HttpStatus.BAD_REQUEST_400).json("Field " + ctx.pathParam("field") + " doesn't exist for entity " + ctx.pathParam("entity"));
            } catch (EntityNotFoundException e) {
                ctx.status(HttpStatus.BAD_REQUEST_400).json("Could not find entity: " + ctx.pathParam("entity"));
            } catch (DatabaseIOException e) {
                ctx.status(HttpStatus.BAD_REQUEST_400).json("Error while trying to read from db");
            }
        }));

        // CREATE user
        app.post("/users", ctx -> authenticator.authenticate(ctx, () -> {
            CreateUserParams params = ctx.bodyAsClass(CreateUserParams.class);
            if(authenticator.addUser(params.getUsername(), params.getPassword())) {
                ctx.status(HttpStatus.CREATED_201).json("Created user " + params.getUsername());
            } else {
                ctx.status(HttpStatus.BAD_REQUEST_400).json("Could not create user " + params.getUsername());
            }
        }));

        // DELETE user
        app.delete("/users/:username", ctx -> authenticator.authenticate(ctx, () -> {
            if(authenticator.deleteUser(ctx.pathParam("username"))) {
                ctx.status(HttpStatus.OK_200).json("Deleted user " + ctx.pathParam("username"));
            } else {
                ctx.status(HttpStatus.BAD_REQUEST_400).json("Could not delete user " + ctx.pathParam("username"));
            }
        }));

        app.get("/users", ctx -> ctx.json(authenticator.getUsers()));
    }
}
