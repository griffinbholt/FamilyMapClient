package server;

import com.sun.net.httpserver.HttpServer;
import server.dao.DatabaseChecker;
import server.exception.DataAccessException;
import server.handler.*;
import shared.http.FamilyMapUrl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Main Family Map Server Singleton
 * @author griffinbholt
 */
public final class Server {
    @SuppressWarnings("StaticVariableOfConcreteClass")
    private static final Server familyMapServer = new Server();

    private static final String USAGE = "USAGE: java familymapserver.server portNumber";

    private final Logger logger = Logger.getLogger("Server");

    /**
     * Starts up the server, which runs perpetually or until a fatal error occurs
     * USAGE: java familymapserver.server portNumber
     * @param args The number of the port on which to run the server
     */
    public static void main(String[] args) {
        checkDatabase();
        int portNumber = getPortNumber(args);
        familyMapServer.startServer(portNumber);
    }

    private static void checkDatabase() {
        try {
            DatabaseChecker.checkDatabase();
        } catch (IOException | DataAccessException e) {
            familyMapServer.log(Level.SEVERE, e.getMessage());
            closeServer();
        }
    }

    private static int getPortNumber(String[] args) {
        if (1 != args.length) {
            familyMapServer.log(Level.SEVERE, "Invalid number of arguments.\n" + USAGE);
            closeServer();
        }

        return Integer.parseInt(args[0]);
    }

    private void startServer(int portNumber) {
        try {
            InetSocketAddress serverAddress = new InetSocketAddress(portNumber);

            HttpServer server = HttpServer.create(serverAddress, 10);

            registerHandlers(server);

            server.start();

            log(Level.INFO, "FamilyMapServer listening on port " + portNumber);
        } catch (IOException e) {
            log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
            closeServer();
        }
    }

    private void registerHandlers(HttpServer server) {
        server.createContext(FamilyMapUrl.REGISTER, new RegisterRequestHandler());

        server.createContext(FamilyMapUrl.LOGIN, new LoginRequestHandler());

        server.createContext(FamilyMapUrl.CLEAR, new ClearRequestHandler());

        server.createContext(FamilyMapUrl.FILL, new FillRequestHandler());

        server.createContext(FamilyMapUrl.LOAD, new LoadRequestHandler());

        server.createContext(FamilyMapUrl.PERSON, new PersonRequestHandler());

        server.createContext(FamilyMapUrl.EVENT, new EventRequestHandler());

        server.createContext(FamilyMapUrl.DEFAULT_PATH, new FileRequestHandler());
    }

    private void log(Level level, String message) {
        logger.log(level, message + "\n");
    }

    private static void closeServer() {
        System.exit(0);
    }
}

/*
 * TODO - For future development: (not required for the scope of the project)
 *          - Handle erroneous JSON strings
 */
