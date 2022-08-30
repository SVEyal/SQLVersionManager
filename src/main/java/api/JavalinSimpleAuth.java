package api;

import io.javalin.Context;
import org.eclipse.jetty.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class JavalinSimpleAuth {
    private final Map<String, String> userToPassword;
    private final String ADMIN_USER = "admin";

    public JavalinSimpleAuth() {
        userToPassword = new HashMap<>();
        userToPassword.put(ADMIN_USER, ADMIN_USER);
    }

    private boolean authenticate(Context ctx) {
        final String username = Objects.requireNonNull(ctx.basicAuthCredentials()).getUsername();
        final String password = Objects.requireNonNull(ctx.basicAuthCredentials()).getPassword();

        return userToPassword.containsKey(username) &&
                userToPassword.get(username).equals(password);
    }

    public void authenticate(Context ctx, Runnable runnable) {
        try {
            if (authenticate(ctx)) {
                runnable.run();
            } else {
                ctx.status(HttpStatus.UNAUTHORIZED_401).json("User is not authorized");
            }
        } catch (NullPointerException e) {
            ctx.status(HttpStatus.BAD_REQUEST_400).json("Cannot authenticate, must supply username and password");
        }
    }

    public boolean addUser(String username, String password) {
        if(!userToPassword.containsKey(username)) {
            userToPassword.put(username, password);
            return true;
        }
        return false;
    }

    public boolean deleteUser(String username) {
        if (!username.equals(ADMIN_USER)) {
            return userToPassword.remove(username) != null;
        }
        return false;
    }

    public String getUser(Context ctx) {
        return Objects.requireNonNull(ctx.basicAuthCredentials()).getUsername();
    }

    public Set<String> getUsers() {
        return this.userToPassword.keySet();
    }
}
