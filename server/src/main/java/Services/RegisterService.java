package Services;

import Dao.*;
import Model.AuthorizationToken;
import Model.Event;
import Model.Person;
import Model.User;
import Results.RegisterResult;
import Results.Result;
import Request.RegisterReq;
import com.google.gson.Gson;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class RegisterService {
    ArrayList<Event> eventArray = new ArrayList<>();
    ArrayList<Person> personArray = new ArrayList<>();

    /**
     * Creates a new user account, generates 4 generations of ancestor data for the new user, logs the user in, and returns authToken
     * @param r

     * @return result of register
     */
    public Result register(RegisterReq r) throws DataAccessException, SQLException {
        Gson gson = new Gson();
        Database db = new Database();
        Connection conn = null;

        try {
            conn = db.openConnection();
        } catch (DataAccessException e) {
            System.out.println("Failure to open connection: " + e);
        }
        UserDAO uDao = new UserDAO(conn);
        AuthorizationTokenDAO aDao  = new AuthorizationTokenDAO(conn);
        PersonDAO pDao = new PersonDAO(conn);
        EventDAO eDao = new EventDAO(conn);

        RegisterResult response = new RegisterResult();

        if(uDao.userExists(uDao.getUser(r.getUserName()))) {
            response.setMessage("error: Username already exists, please choose another!");
            db.closeConnection(true);
            conn.close();
            return response;
        }


        String data = gson.toJson(r);
        User user = gson.fromJson(data, User.class);
        Person person = gson.fromJson(data, Person.class);

        String personID = UUID.randomUUID().toString();
        person.setPersonID(personID);
        System.out.println(personID);
        user.setPersonID(personID);

        String token = UUID.randomUUID().toString();
        AuthorizationToken newToken = new AuthorizationToken(token, r.getUserName());
        uDao.addUser(user);
        aDao.addAuthToken(newToken);

        String userName = r.getUserName();

        response.setAuthToken(token);
        response.setUserName(userName);
        response.setSuccess(true);
        response.setPersonID(personID);

        Person usersPerson = new Person(personID, userName, uDao.getUser(userName).getFirstName(), uDao.getUser(userName).getLastName(), uDao.getUser(userName).getGender(), UUID.randomUUID().toString(), UUID.randomUUID().toString(), null);
        pDao.addPerson(usersPerson);

        int defaultYear = 2020;

        eventArray.add(createBirth(defaultYear, usersPerson, uDao.getUser(userName)));
        eventArray.add(createRandomEvent(defaultYear, usersPerson, uDao.getUser(userName)));
        eventArray.add(createRandomEvent(defaultYear, usersPerson, uDao.getUser(userName)));

        //default 4 generations
        CreateGenerations(4, usersPerson, uDao.getUser(userName), defaultYear - 20, conn);

        //Add All events and Persons
        for (Event event : eventArray) {
            eDao.addEvent(event);
        }
        for (Person value : personArray) {
            pDao.addPerson(value);
        }

        db.closeConnection(true);
        conn.close();

        return response;
    }


    /**
     *
     * @param numGenerations - Number of Generations to run
     * @param root - The Child of the generation we are creating
     * @param originalUser - The user who we are creating generations for
     * @param rootBirthYear - Birth year of the child whos parents we are creating!
     *
     * This function is going to create all of the generations for a newUser who has just registered, it will create four generations!
     * Will also call the function to create 3 events per person!
     *
     * @throws DataAccessException
     * @throws SQLException
     */
    public void CreateGenerations(int numGenerations, Person root, User originalUser, int rootBirthYear, Connection conn) throws DataAccessException, SQLException {

        String[] momNames = {"Regina", "Donna", "Mary", "Hadley", "Amy", "Renee", "Jessica"};
        String[] dadNames = {"Jason", "Trace", "Jeff", "Brad", "Andrew", "George", "Bill"};
        String[] lastNames = {"Nichols", "Hale", "Elkins", "Brann", "Lesch-Wragge", "Simpson"};

        String dadMomID = UUID.randomUUID().toString();
        String dadDadID = UUID.randomUUID().toString();
        String momMomID = UUID.randomUUID().toString();
        String momDadID = UUID.randomUUID().toString();
        String momID = root.getMotherID();
        String dadID = root.getFatherID();
        //Create Mom and Dad using ^

        Person mom = new Person(momID, originalUser.getUserName(), momNames[new Random().nextInt(momNames.length)], lastNames[new Random().nextInt(lastNames.length)], "F", momDadID, momMomID, dadID);
        Person dad = new Person(dadID, originalUser.getUserName(), dadNames[new Random().nextInt(dadNames.length)], lastNames[new Random().nextInt(lastNames.length)], "M", dadDadID, dadMomID, momID);
        EventDAO eDao = new EventDAO(conn);

        if(numGenerations == 1) {
            mom.setMotherID(null);
            dad.setMotherID(null);
            dad.setFatherID(null);
            mom.setFatherID(null);
        }

        //Generate BirthYears for Mom and Dad
        int max = rootBirthYear - 20;
        int min = rootBirthYear - 35;
        int momBirthYear = new Random().nextInt(max - min + 1) + min;
        int dadBirthYear = new Random().nextInt(max - min + 1) + min;
        createEvents(originalUser, mom, dad, rootBirthYear);

        if (numGenerations > 1) {
            numGenerations--;
            //Re-Run the CreateFamily Function
            //Pass it moms birth year
            CreateGenerations(numGenerations, mom, originalUser, momBirthYear, conn);
            //Pass it dads birth year
            CreateGenerations(numGenerations, dad, originalUser, dadBirthYear, conn);
        }
        //Add both people to the final array of people, so we can see the size.

        personArray.add(mom);
        personArray.add(dad);
    }


    /**
     * @param origin - User we are creating generations for
     * @param mom - mom of the child
     * @param dad - dad of the child
     * @param newPersonBirthYear - Childs birth year
     *
     * This function is used to create all new events for each of the people that the CreateGeneration function creates!
     * Per Specifications it will create a Birth, Death, and Marriage!
     *
     */
    public void createEvents(User origin, Person mom, Person dad, int newPersonBirthYear) {

        eventArray.add(createBirth(newPersonBirthYear, mom, origin));
        eventArray.add(createBirth(newPersonBirthYear, dad, origin));

        //Create Marriage Call once, will create both events
        createMarriage(newPersonBirthYear, mom, dad, origin);


        //Create Third Event, if the users age is 70+ Make the event a death
        eventArray.add(createDeath(newPersonBirthYear, mom, origin));
        eventArray.add(createDeath(newPersonBirthYear, dad, origin));
    }

    /**
     * Creates a new User Birth based on the birth Year of their child
     * @param childBirthYear - Childs Birth Year
     * @param person - Person we are creating the Birth of
     * @param origin - User who we are creating the events for
     * @return - BirthEvent
     */
    public Event createBirth(int childBirthYear, Person person, User origin) {
        String eventID = UUID.randomUUID().toString();
        int min = childBirthYear - 25;
        int max = childBirthYear - 18;
        int birthYear = new Random().nextInt(max - min + 1) + min;
        //Could have a random Generation of Latitudes and Longitudes, or find corresponding position for the Place and City...

        return new Event(eventID, origin.getUserName(), person.getPersonID(), 100, 200, "United States", "Richmond", "Birth", birthYear);
    }


    /**
     * Creates a death event for every new user also based on the age of their child!
     * @param childBirthYear - Birth year of the person we are creating the death of
     * @param person - Person whos death we are creating
     * @param origin - User who we are creating the generations for
     * @return - DeathEvent
     */
    public Event createDeath(int childBirthYear, Person person, User origin) {
        String eventID = UUID.randomUUID().toString();
        int max = childBirthYear + 70;
        int min = childBirthYear + 55;
        int deathYear = new Random().nextInt(max - min + 1) + min;
        return new Event(eventID, origin.getUserName(), person.getPersonID(), 100, 200, "United States", "FakeCity", "Death", deathYear);
    }


    /**
     * Creates a marriage event and then shares it between both the mother and the father!
     * This allows the event to contain the proper "spouseID" for husband and wife!
     * @param birthYearOfChild - Birth year of the child whos parents marriage we are creating
     * @param mom - Mom of the child
     * @param dad - Dad of the child
     * @param origin - User who we are creating the generations for
     */
    public void createMarriage(int birthYearOfChild, Person mom, Person dad, User origin) {
        int marriageYear = new Random().nextInt((birthYearOfChild + 15) - (birthYearOfChild + 5)) + (birthYearOfChild + 5);
        Event dadMarriage = new Event(UUID.randomUUID().toString(), origin.getUserName(), dad.getPersonID(), 150,250, "United States", "FakeCityTwo", "Marriage", marriageYear);
        Event momMarriage = new Event(UUID.randomUUID().toString(), origin.getUserName(), mom.getPersonID(), 150,250, "United States", "FakeCityTwo", "Marriage", marriageYear);
        eventArray.add(dadMarriage);
        eventArray.add(momMarriage);

    }

    /**
     * This is for the new users personObject, they will have a random event because we are assuming that they are not married just yet!
     * @param birthYearOfChild - Birth year of the person we are creating a random event for!
     * @param person - Person who we are creating the event for!
     * @param origin - User who we are creating generations for!
     * @return randomEvent
     */
    public Event createRandomEvent(int birthYearOfChild, Person person, User origin) {
        String[] events = {"Got Ice Cream", "Went on a Hike", "Bought a Car", "Got a Dog", "Dog Died:("};
        String eventID = UUID.randomUUID().toString();
        //Change to generate new latitude and Longitude
        return new Event(eventID, origin.getUserName(), person.getPersonID(), 100, 200, "United States", "Lexington", events[new Random().nextInt(events.length)], birthYearOfChild + 12);
    }


}
