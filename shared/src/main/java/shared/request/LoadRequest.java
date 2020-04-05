package shared.request;

import shared.model.ServerEvent;
import shared.model.ServerPerson;
import shared.model.User;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A Plain Old Java Object representing a request to load the database with user(s), person(s), and/or event(s)
 * @author griffinbholt
 */
public class LoadRequest {
    /**
     * The {@link User User} objects to be added to the database
     */
    private final List<User> users;

    /**
     * The {@link shared
     * .model.Person Person} objects to be added to the database
     */
    private final List<ServerPerson> persons;

    /**
     * The {@link shared
     * .model.ServerEvent ServerEvent} objects to be added to the database
     */
    private final List<ServerEvent> events;

    /**
     * Creates a new <code>LoadRequest</code> object with the instance variable values input through the parameters
     * @param users The {@link User User} objects to be added to the database
     * @param persons The {@link shared
     * .model.Person Person} objects to be added to the database
     * @param events The {@link shared
     * .model.ServerEvent ServerEvent} objects to be added to the database
     */
    public LoadRequest(List<User> users, List<ServerPerson> persons, List<ServerEvent> events) {
        this.users = users;
        this.persons = persons;
        this.events = events;
    }

    /**
     * Generates a string representation of the load request
     * @return A string representation of the fill request
     */
    @Override
    public String toString() {
        return "LoadRequest{" +
                "users=" + users +
                ", persons=" + persons +
                ", events=" + events +
                '}';
    }

    /**
     * Tests if the input <code>LoadRequest</code> object is equal to the current instance
     * @param o Input <code>Object</code> to be tested for equality with the current <code>LoadRequest</code> instance
     * @return true, if the two load requests contain the same information; false, if otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LoadRequest)) return false;
        LoadRequest request = (LoadRequest) o;
        return Objects.equals(getUsers(), request.getUsers()) &&
                Objects.equals(getPersons(), request.getPersons()) &&
                Objects.equals(getEvents(), request.getEvents());
    }

    /**
     * Generates the hashcode for the load request
     * @return The hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(getUsers(), getPersons(), getEvents());
    }

    // Getters
    public List<User> getUsers() {
        return Collections.unmodifiableList(users);
    }

    public List<ServerPerson> getPersons() {
        return Collections.unmodifiableList(persons);
    }

    public List<ServerEvent> getEvents() {
        return Collections.unmodifiableList(events);
    }
}
