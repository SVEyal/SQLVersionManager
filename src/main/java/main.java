import io.javalin.Javalin;
import persistance.FieldPersistentManager;
import sql_actions.FieldCrudManager;
import sql_entities.rest_classes.CreateOrUpdateFieldParams;

public class main {
    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7070);
        init(app);
    }

    private static void init(Javalin app) {
        final FieldPersistentManager fpm = new FieldPersistentManager();
        final FieldCrudManager fcm = new FieldCrudManager(fpm);

        // Get all entities
        app.get("/entities", ctx -> ctx.json(fcm.getAll()));

        // CREATE new entity
        app.get("/entities/create/:id", ctx -> ctx.json(fpm.addEntity(ctx.pathParam("id"))));

        // CREATE or UPDATE field
        app.post("/field/create_or_update", ctx -> {
            CreateOrUpdateFieldParams params = ctx.bodyAsClass(CreateOrUpdateFieldParams.class);
            fcm.createOrUpdateField(params.getEntityIdentifier(),
                    params.getFieldIdentifier(),
                    params.getFieldType(),
                    params.getSqlCode(),
                    params.getDescription());
        });

        // DELETE field
        app.delete("/field/:entity/:field", ctx -> fcm.deleteField(ctx.pathParam("entity"), ctx.pathParam("field")));

        // READ values
        app.get("/field/:entity/:field", ctx -> ctx.json(fcm.readField(ctx.pathParam("entity"), ctx.pathParam("field"))));
        app.get("/field/:entity/:field/:n", ctx -> ctx.json(fcm.readNFieldVersions(ctx.pathParam("entity"), ctx.pathParam(":field"),
                                                                                        ctx.validatedPathParam("n").asInt().getOrThrow())));
    }
}