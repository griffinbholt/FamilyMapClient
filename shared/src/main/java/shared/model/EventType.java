package shared.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * A class to manage the eventType field of {@link ServerEvent} objects
 * @author griffinbholt
 */
@SuppressWarnings("StaticVariableOfConcreteClass")
public class EventType implements Comparable<EventType>, Serializable {
    /**
     * A default <code>EventType</code> object for birth events
     */
    public static final EventType BIRTH = new EventType("BIRTH");

    /**
     * A default <code>EventType</code> object for marriage events
     */
    public static final EventType MARRIAGE = new EventType("MARRIAGE");

    /**
     * A default <code>EventType</code> object for death events
     */
    public static final EventType DEATH = new EventType("DEATH");

    /**
     * The name of the event (acc. to the input passed to the <code>generate</code> method)
     */
    private String eventName;

    /**
     * Generates an <code>EventType</code> object from the given event name.
     * @param name Input name of the event
     * @return An <code>EventType</code> object with the input event name
     */
    public static EventType generate(String name) {
        return new EventType(name);
    }

    private EventType(String name) {
        this.eventName = name;
    }

    /**
     * Tests if the input <code>EventType</code> is equal to the current instance
     * @param o Input <code>Object</code> to be tested for equality with the current <code>EventType</code> instance
     * @return true, if the two authorization tokens are the same; false, if otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventType)) return false;
        EventType eventType = (EventType) o;
        return getEventName().equals(eventType.getEventName());
    }

    /**
     * Generates the hashcode of the <code>EventType</code> object
     * @return The hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(getEventName());
    }

    // Getter
    public String getEventName() {
        return (null != this.eventName) ? this.eventName : this.toString();
    }

    // Setter
    public void setEventName(String name) {
        this.eventName = name;
    }

    @Override
    public int compareTo(EventType o) {
        return this.eventName.compareTo(o.eventName);
    }
}
