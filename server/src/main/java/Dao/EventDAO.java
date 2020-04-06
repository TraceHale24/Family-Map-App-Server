package Dao;

import Model.Event;
import Model.Person;

import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;

public class EventDAO {
        private final Connection conn;

        public EventDAO(Connection conn) {
            this.conn = conn;
        }


    /**
     *
     * @param event
     * Takes an Event object and adds it to the events of the user whos userName is attached to the Event
     */
    public void addEvent(Model.Event event) throws DataAccessException {
        //We can structure our string to be similar to a sql command, but if we insert question
        //marks we can change them later with help from the statement
        String sql = "INSERT INTO Events (EventID, associatedUsername, PersonID, Latitude, Longitude, " +
                "Country, City, EventType, Year) VALUES(?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            //Using the statements built-in set(type) functions we can pick the question mark we want
            //to fill in and give it a proper value. The first argument corresponds to the first
            //question mark found in our sql String
            if(event.getEventName() == null) {
                stmt.setString(1, UUID.randomUUID().toString());
            }
            else {
                stmt.setString(1, event.getEventName());
            }
            stmt.setString(2, event.getAssociatedUsername());
            stmt.setString(3, event.getPersonID());
            stmt.setDouble(4, event.getLatitude());
            stmt.setDouble(5, event.getLongitude());
            stmt.setString(6, event.getCountry());
            stmt.setString(7, event.getCity());
            stmt.setString(8, event.getEventType());
            stmt.setInt(9, event.getYear());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while inserting into the database");
        }
    }

    public Event getEvent(String eventToGet) throws DataAccessException {
        Event event;
        ResultSet rs = null;
        String sql = "SELECT * FROM Events WHERE EventID = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, eventToGet);
            rs = stmt.executeQuery();
            if (rs.next()) {
                event = new Event(rs.getString("EventID"), rs.getString("AssociatedUsername"),
                        rs.getString("PersonID"), rs.getFloat("Latitude"), rs.getFloat("Longitude"),
                        rs.getString("Country"), rs.getString("City"), rs.getString("EventType"),
                        rs.getInt("Year"));
                return event;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding event");
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }

    /**
     *
     * @param userName
     * deletes all of the events of a given userName (i.e. goes to the associated userName and deletes all of those that match userName)
     */
    public void delete(String userName) throws SQLException {
        //delete event
        PreparedStatement stmt = null;
        try {

            String sql = "DELETE FROM Events where AssociatedUsername = '" + userName + "'";
            stmt = conn.prepareStatement(sql);
            stmt.executeUpdate();

        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    /**
     *Clears all events our of the Event table
     */
    public void clear() throws SQLException {
        PreparedStatement stmt = null;
        try {
            String sql = "DELETE FROM Events";
            stmt = conn.prepareStatement(sql);

            int count = stmt.executeUpdate();

            sql = "delete from Events where EventID = 'eventID'";
            stmt = conn.prepareStatement(sql);
            stmt.executeUpdate();

            System.out.printf("Deleted %d events\n", count);
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    public ArrayList<Event> getEvents(String userName) throws SQLException {
        ArrayList<Event> allEvents = new ArrayList<>();
        //getEveryone in a tree or related to a person
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT * FROM Events WHERE associatedUsername = '" + userName + "'";
            stmt = conn.prepareStatement(sql);

            rs = stmt.executeQuery();
            while(rs.next()) {
                allEvents.add(new Event(rs.getString("EventID"), rs.getString("AssociatedUsername"),
                        rs.getString("PersonID"), rs.getFloat("Latitude"), rs.getFloat("Longitude"),
                        rs.getString("Country"), rs.getString("City"), rs.getString("EventType"),
                        rs.getInt("Year")));
            }
        } finally {
            if(rs != null) {
                rs.close();
            }

            if(stmt != null) {
                stmt.close();
            }
        }

        return allEvents;
        //get all events for a user, good for displaying multiple pieces of data
    }

    /**
     *
     * @param Event
     * Checks to see if an event already exists so we don't have duplicate events...
     * @return
     */
    public boolean eventExists(Event event) throws DataAccessException {
        if(event == null) {
            return false;
        }
        return getEvent(event.getEventName()) != null;
    }



}
