package Dao;

import Model.Person;
import java.sql.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class PersonDAO {
    private final Connection conn;

    public PersonDAO(Connection conn) { this.conn = conn; }

    /**
     * @param personToAdd adds a NewPerson to the family tree of another given person
     */

    public void addPerson(Model.Person personToAdd) throws DataAccessException {
            //We can structure our string to be similar to a sql command, but if we insert question
            //marks we can change them later with help from the statement
            String sql = "INSERT INTO Person (personID, userName, firstName, lastName, gender, " +
                    "fatherID, MotherID, spouseID) VALUES(?,?,?,?,?,?,?,?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                //Using the statements built-in set(type) functions we can pick the question mark we want
                //to fill in and give it a proper value. The first argument corresponds to the first
                //question mark found in our sql String
                if(personToAdd.getPersonID() == null) {
                    stmt.setString(1, UUID.randomUUID().toString());
                }
                else {
                    stmt.setString(1, personToAdd.getPersonID());
                }
                stmt.setString(2, personToAdd.getUsername());
                stmt.setString(3, personToAdd.getFirstName());
                stmt.setString(4, personToAdd.getLastName());
                stmt.setString(5, personToAdd.getGender());
                stmt.setString(6, personToAdd.getFatherID());
                stmt.setString(7, personToAdd.getMotherID());
                stmt.setString(8, personToAdd.getSpouseID());

                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new DataAccessException("Error encountered while inserting into the database");
            }
        }

        public Person getPerson (String personToGet)throws DataAccessException {
            Person person;
            ResultSet rs = null;
            String sql = "SELECT * FROM Person WHERE personID = ?;";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, personToGet);
                rs = stmt.executeQuery();
                if (rs.next()) {
                    person = new Person(rs.getString("personID"), rs.getString("userName"),
                            rs.getString("firstName"), rs.getString("lastName"), rs.getString("gender"),
                            rs.getString("fatherID"), rs.getString("MotherID"), rs.getString("spouseID"));
                    return person;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                throw new DataAccessException("Error encountered while finding person");
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
         * Removes all of the people associated with a user(userName)
         *
         */
        public void deletePerson (String userName) throws SQLException {
            //delete someone from a tree
            PreparedStatement stmt = null;
            try {

                String sql = "DELETE FROM Person where userName = '" + userName + "'";
                stmt = conn.prepareStatement(sql);
                stmt.executeUpdate();

            } finally {
                if (stmt != null) {
                    stmt.close();
                }
            }
        }

        /**
         *
         */
        public void clear() throws SQLException {
            PreparedStatement stmt = null;
            try {
                String sql = "delete from Person";
                stmt = conn.prepareStatement(sql);

                int count = stmt.executeUpdate();

                // Reset the auto-increment counter so new books start over with an id of 1
                sql = "delete from Person where personID = 'person'";
                stmt = conn.prepareStatement(sql);
                stmt.executeUpdate();

                System.out.printf("Deleted %d persons\n", count);
            } finally {
                if (stmt != null) {
                    stmt.close();
                }
            }
        }

        public ArrayList<Person> getEveryone (String userName) throws SQLException {
            ArrayList<Person> allPeople = new ArrayList<>();
            //getEveryone in a tree or related to a person
            PreparedStatement stmt = null;
            ResultSet rs = null;

            try {
                String sql = "SELECT * FROM Person WHERE userName = '" + userName + "'";
                stmt = conn.prepareStatement(sql);

                rs = stmt.executeQuery();
                while(rs.next()) {
                    allPeople.add(new Person(rs.getString("personID"), rs.getString("userName"),
                            rs.getString("firstName"), rs.getString("lastName"), rs.getString("gender"),
                            rs.getString("fatherID"), rs.getString("MotherID"), rs.getString("spouseID")));
                }
            } finally {
                if(rs != null) {
                    rs.close();
                }

                if(stmt != null) {
                    stmt.close();
                }
            }

            return allPeople;
        }

        /**
         *
         * @param personToFind
         * Checks the tables to see if a given person exists
         * @return
         */
        public boolean personExists (Person personToFind) throws DataAccessException {

            if(personToFind == null) {
                return false;
            }
            else if(getPerson(personToFind.getPersonID()) != null) {
                return true;
            }
            else {
                return false;
            }
        }
    }//end person
