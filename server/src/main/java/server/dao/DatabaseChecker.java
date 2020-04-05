package server.dao;

import server.exception.DataAccessException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;

/**
 * A singleton class designed to ensure that the database files exist and are correctly set up
 * @author griffinbholt
 */
public final class DatabaseChecker {
    @SuppressWarnings("StaticVariableOfConcreteClass")
    private static final DatabaseChecker dbCreator = new DatabaseChecker();

    private static final String CREATE_USER_TABLE =
            "CREATE TABLE IF NOT EXISTS users\n" +
                    "(\n" +
                    "\tid VARCHAR(255) NOT NULL PRIMARY KEY,\n" +
                    "\tusername VARCHAR(255) NOT NULL,\n" +
                    "\tpassword VARCHAR(255) NOT NULL,\n" +
                    "\temail_address VARCHAR(320) NOT NULL,\n" +
                    "\tfirst_name VARCHAR(255) NOT NULL,\n" +
                    "\tlast_name VARCHAR(255) NOT NULL,\n" +
                    "\tgender CHARACTER(1) NOT NULL,\n" +
                    "\tperson_id VARCHAR(255) NOT NULL\n" +
                    ");\n";

    private static final String CREATE_AUTH_TOKEN_TABLE =
            "CREATE TABLE IF NOT EXISTS auth_tokens\n" +
                    "(\n" +
                    "\tuser_id VARCHAR(255) NOT NULL,\n" +
                    "\tauth_token VARCHAR(255) NOT NULL,\n" +
                    "\tFOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE\n" +
                    ");\n";

    private static final String CREATE_PERSON_TABLE =
            "CREATE TABLE IF NOT EXISTS persons\n" +
                    "(\n" +
                    "\tid VARCHAR(255) NOT NULL PRIMARY KEY,\n" +
                    "\tuser_id VARCHAR(255) NOT NULL,\n" +
                    "\tfirst_name VARCHAR(255) NOT NULL,\n" +
                    "\tlast_name VARCHAR(255) NOT NULL,\n" +
                    "\tgender CHARACTER(1) NOT NULL,\n" +
                    "\tfather_id VARCHAR(255),\n" +
                    "\tmother_id VARCHAR(255),\n" +
                    "\tspouse_id VARCHAR(255),\n" +
                    "\tFOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE,\n" +
                    "\tFOREIGN KEY(father_id) REFERENCES persons(id) ON DELETE CASCADE,\n" +
                    "\tFOREIGN KEY(mother_id) REFERENCES persons(id) ON DELETE CASCADE,\n" +
                    "\tFOREIGN KEY(spouse_id) REFERENCES persons(id)\n" +
                    ");\n";

    private static final String CREATE_EVENT_TABLE =
            "CREATE TABLE IF NOT EXISTS events\n" +
                    "(\n" +
                    "\tid VARCHAR(255) NOT NULL PRIMARY KEY,\n" +
                    "\tuser_id VARCHAR(255) NOT NULL,\n" +
                    "\tperson_id VARCHAR(255) NOT NULL,\n" +
                    "\tlatitude DOUBLE NOT NULL,\n" +
                    "\tlongitude DOUBLE NOT NULL,\n" +
                    "\tcountry VARCHAR(74) NOT NULL,\n" +
                    "\tcity VARCHAR(58) NOT NULL,\n" +
                    "\ttype VARCHAR(255) NOT NULL,\n" +
                    "\tyear INTEGER NOT NULL,\n" +
                    "\tFOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE,\n" +
                    "\tFOREIGN KEY(person_id) REFERENCES persons(id) ON DELETE CASCADE\n" +
                    ");\n";

    private Connection conn;

    /**
     * Checks to make sure the database file exists and that the data tables needed for the server
     * are constructed correctly. If the file does not exist, it creates the file.
     * If the data tables do not exist in the database, it creates the tables.
     * @throws DataAccessException An error that occurs when attempting to access the database
     * @throws IOException An error that occurs when attempting to interact with the database files
     */
    public static void checkDatabase() throws DataAccessException, IOException {
        dbCreator.checkForDatabaseFiles();
        dbCreator.createTablesIfNotExist();
    }

    private void checkForDatabaseFiles() throws IOException {
        checkForDirectory();
        checkForFile();
    }

    private void checkForDirectory() throws IOException {
        Path dbDirectory = Paths.get(GeneralDao.DATABASE_FOLDER);

        if (!(Files.isDirectory(dbDirectory))) {
            Files.deleteIfExists(dbDirectory);
            Files.createDirectory(dbDirectory);
        }
    }

    private void checkForFile() throws IOException {
        Path dbFile = Paths.get(GeneralDao.DATABASE_FOLDER, GeneralDao.DATABASE_FILE);

        if (!(Files.isRegularFile(dbFile))) {
            Files.deleteIfExists(dbFile);
            Files.createFile(dbFile);
        }
    }

    private void createTablesIfNotExist() throws DataAccessException {
        openConnection();

        try (Statement stmt = conn.createStatement()) {
            stmt.addBatch(CREATE_USER_TABLE);
            stmt.addBatch(CREATE_PERSON_TABLE);
            stmt.addBatch(CREATE_AUTH_TOKEN_TABLE);
            stmt.addBatch(CREATE_EVENT_TABLE);
            stmt.executeBatch();
        } catch (SQLException e) {
            throw new DataAccessException("Unable to create database tables.");
        } finally {
            closeConnection();
        }
    }

    private void openConnection() throws DataAccessException {
        try {
            conn = DriverManager.getConnection(GeneralDao.DB_CONNECTION_URL);
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    private void closeConnection() throws DataAccessException {
        try {
            conn.close();
            conn = null;
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
