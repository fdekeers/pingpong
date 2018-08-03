package edu.uci.iotproject.analysis;

import java.time.Instant;

/**
 * Models a user's action, such as toggling the smart plug on/off at a given time.
 *
 * @author Janus Varmarken
 */
public class UserAction {

    /**
     * The specific type of action the user performed.
     */
    private final Type mType;

    /**
     * The time the action took place.
     */
    private final Instant mTimestamp;

    public UserAction(Type typeOfAction, Instant timeOfAction) {
        mType = typeOfAction;
        mTimestamp = timeOfAction;
    }

    /**
     * Get the specific type of action performed by the user.
     * @return the specific type of action performed by the user.
     */
    public Type getType() {
        return mType;
    }

    /**
     * Get the time at which the user performed this action.
     * @return the time at which the user performed this action.
     */
    public Instant getTimestamp() {
        return mTimestamp;
    }

    /**
     * Enum for indicating what type of action the user performed.
     */
    public enum Type {
        TOGGLE_ON, TOGGLE_OFF
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof UserAction) {
            UserAction that = (UserAction) obj;
            return this.mType == that.mType && this.mTimestamp.equals(that.mTimestamp);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int hashCode = 17;
        hashCode = prime * hashCode + mType.hashCode();
        hashCode = prime * hashCode + mTimestamp.hashCode();
        return hashCode;
    }

    @Override
    public String toString() {
       return String.format("[ %s @ %s ]", mType.name(), mTimestamp.toString());
    }
}
