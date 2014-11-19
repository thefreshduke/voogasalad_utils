package simpleDB;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.parse4j.Parse;
import org.parse4j.ParseException;
import org.parse4j.ParseFile;
import org.parse4j.ParseObject;
import org.parse4j.ParseQuery;
import org.parse4j.ParseUser;
import org.parse4j.callback.FindCallback;
import org.parse4j.callback.SaveCallback;

/**
 * A parse.com REST API wrapper that allows:
 * data storage and retrieval
 * app sessions
 * user sessions
 * 
 * @author Davis
 *
 */

public class SimpleDatabase {
    private static final String APP_ID = "iy0nEPwQaOJiwt4DpEiee0PNaxfqvPQYswO6gmjI";
    private static final String APP_REST_API_ID = "31xMYzDDAPmwYN58gTJ8XQTumhuU7XNr1dj65Iiu";
    private static final String DEFAULT_FILE_NAME = "file.bif";
    private ParseUser myCurrentUser;
    private ParseObject myCurrentApplication;

    public SimpleDatabase () {
        Parse.initialize(APP_ID, APP_REST_API_ID);
    }

    /**
     * creates a database connection with an app identifier and an app secret
     * each unique app identifier and app secret gives access to a new DB
     * cannot access another apps data
     * 
     * @param appString
     *        the string representing your app. this is NOT unique meaning the same appString
     *        could be used with different secrets to represent different DBs
     * @param appSecret
     *        the secret used for authenticating the connection. Not encrypted.
     * @param cb
     *        an optional CompletionCallback to know when done initializing
     */
    public void initializeDB (String appString, String appSecret, CompletionCallback cb) {
        checkForExistingApplication(appString, appSecret, new CompletionCallback() {
            public void done (String error) {
                triggerCompletion(cb, error);
            }
        });
    }

    /*
     * user operations
     */

    /**
     * register a user with the DB
     * 
     * @param username
     *        username for the specific user
     * @param password
     *        password for the specific user. Encrypted.
     * @return
     *         boolean representing operation success
     */
    public boolean registerUser (String username, String password) {
        ParseUser parseUser = new ParseUser();
        parseUser.setUsername(username);
        parseUser.setPassword(password);
        try {
            parseUser.signUp();
        }
        catch (ParseException e) {
            handleParseException(e);
            return false;
        }
        myCurrentUser = parseUser;
        return true;
    }

    /**
     * sign in a specific user. this persists in the SimpleDatabase instance
     * 
     * @param username
     *        username for the specific user
     * @param password
     *        password for the specific user. Encrypted.
     * @return
     *         boolean representing operation success
     */
    public boolean signIn (String username, String password) {
        try {
            myCurrentUser = ParseUser.login(username, password);
        }
        catch (ParseException e) {
            handleParseException(e);
            return false;
        }
        return true;
    }

    /**
     * signout the logged in user
     */
    public void signOut () {
        myCurrentUser = null;
    }

    /**
     * check if any user is signed in
     * 
     * @return
     *         boolean representing user signed in or not
     */
    public boolean userIsSignedIn () {
        return myCurrentUser != null;
    }

    /*
     * database save operations
     */

    /**
     * Save a file that can only be retrieved by this user
     * 
     * @param file
     *        File to save
     * @param identifier
     *        an identifier to retrieve your data. can be unique or can be used for a group of data
     * @return
     *         boolean representing operation success
     */
    public boolean saveUserFile (File file, String identifier) {
        Path filePath = file.toPath();
        try {
            SimpleDBObject obj =
                    new SimpleDBObject(Files.readAllBytes(filePath), file.getName(),
                                       identifier);
            return saveDataToDatabase(obj, true, myCurrentUser);
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Save data that can only be retrieved by this user
     * 
     * @param data
     *        data to save
     * @param identifier
     *        an identifier to retrieve your data. can be unique or can be used for a group of data
     * @return
     *         boolean representing operation success
     */
    public boolean saveUserData (byte[] data, String identifier) {
        SimpleDBObject obj = new SimpleDBObject(data, DEFAULT_FILE_NAME, identifier);
        return saveDataToDatabase(obj, true, myCurrentUser);
    }

    /**
     * Save a database object that can only be retrieved by this user
     * 
     * @param file
     *        File to save
     * @return
     *         boolean representing operation success
     */
    public boolean saveUserObject (SimpleDBObject obj) {
        return saveObject(obj, true, myCurrentUser);
    }

    /**
     * Save a database object that can be retrieved by this app
     * 
     * @param obj
     *        SimpleDBObject
     * @return
     *         boolean representing operation success
     */
    public boolean saveObject (SimpleDBObject obj) {
        return saveObject(obj, false, null);
    }

    /**
     * Save data that can be retrieved by this app
     * 
     * @param data
     *        data to save
     * @param identifier
     *        an identifier to retrieve your data. can be unique or can be used for a group of data
     * @return
     *         boolean representing operation success
     */
    public boolean saveAppData (byte[] data, String identifier) {
        SimpleDBObject obj = new SimpleDBObject(data, DEFAULT_FILE_NAME, identifier);
        return saveDataToDatabase(obj, false, null);
    }

    /**
     * Save a file that can be retrieved by this app
     * 
     * @param file
     *        File to save
     * @param identifier
     *        an identifier to retrieve your data. can be unique or can be used for a group of data
     * @return
     *         boolean representing operation success
     */
    public boolean saveAppFile (File file, String identifier) {
        Path filePath = file.toPath();
        try {
            SimpleDBObject obj =
                    new SimpleDBObject(Files.readAllBytes(filePath), file.getName(),
                                       identifier);
            return saveDataToDatabase(obj, false, null);
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /*
     * database fetch operations
     */

    /**
     * fetches all of the data saved by this app
     * 
     * @param cb
     *        RetrievalCallback to get the data
     */
    public void getAllDataForApp (RetrievalCallback cb) {
        getDataFromDatabase(false, null, null, new RetrievalCallback() {
            public void done (ArrayList<SimpleDBObject> response) {
                triggerRetrieval(cb, response);
            }
        });
    }

    /**
     * fetches all of the data saved by this app with a matching identifier
     * 
     * @param identifier
     *        an identifier to retrieve your data. can be unique or can be used for a group of data
     * @param cb
     *        RetrievalCallback to get the data
     */
    public void getDataForAppWithIdentifier (String identifier, RetrievalCallback cb) {
        getDataFromDatabase(false, null, identifier, new RetrievalCallback() {
            public void done (ArrayList<SimpleDBObject> response) {
                triggerRetrieval(cb, response);
            }
        });
    }

    /**
     * fetches all of the data belonging to this user
     * 
     * @param cb
     *        RetrievalCallback to get the data
     */
    public void getAllDataForUser (RetrievalCallback cb) {
        getDataFromDatabase(true, myCurrentUser, null, new RetrievalCallback() {
            public void done (ArrayList<SimpleDBObject> response) {
                triggerRetrieval(cb, response);
            }
        });
    }

    /**
     * fetches all of the data belonging to this user with a matching identifier
     * 
     * @param identifier
     *        an identifier to retrieve your data. can be unique or can be used for a group of data
     * @param cb
     *        RetrievalCallback to get the data
     */
    public void getAllDataForUserWithIdentifier (String identifier, RetrievalCallback cb) {
        getDataFromDatabase(true, myCurrentUser, identifier, new RetrievalCallback() {
            public void done (ArrayList<SimpleDBObject> response) {
                triggerRetrieval(cb, response);
            }
        });
    }

    /*
     * deletion and save operations on existing objects
     */

    /**
     * delete an object
     * 
     * @param obj
     *        database object to delete
     * @return
     *         boolean representing operation success
     */
    public boolean deleteObject (SimpleDBObject obj) {
        try {
            obj.getDataBacking().delete();
            return true;
        }
        catch (ParseException e) {
            handleParseException(e);
            return false;
        }
    }

    private boolean saveObject (SimpleDBObject obj, boolean authenticated, ParseUser user) {
        if (obj.getDataBacking() != null) {
            try {
                obj.getDataBacking().getParseFile("rawData").save();
                obj.getDataBacking().save();
                return true;
            }
            catch (ParseException e) {
                handleParseException(e);
                return false;
            }
        }
        else {
            return saveDataToDatabase(obj, authenticated, user);
        }
    }

    /*
     * status operations
     */
    /**
     * check if the database has been initialized (belongs to an app)
     * 
     * @return
     */
    public boolean isInitialized () {
        return myCurrentApplication != null;
    }

    /*
     * Application operations
     */

    private void checkForExistingApplication (String appString,
                                              String appSecret,
                                              CompletionCallback cb) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Application");
        query.whereEqualTo("identifier", appString);
        query.whereEqualTo("secret", appSecret);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done (List<ParseObject> objects, ParseException e) {
                if (objects == null || objects.size() == 0) {
                    registerApplication(appString, appSecret, new CompletionCallback() {
                        public void done (String error) {
                            triggerCompletion(cb, error);
                        }
                    });
                }
                else {
                    myCurrentApplication = objects.get(0);
                    triggerCompletion(cb, null);
                }
            }
        });
    }

    private void registerApplication (String appString, String appSecret, CompletionCallback cb) {
        ParseObject application = new ParseObject("Application");
        application.put("identifier", appString);
        application.put("secret", appSecret);
        application.saveInBackground(new SaveCallback() {
            public void done (ParseException arg0) {
                String error = arg0 == null ? null : arg0.getMessage();
                myCurrentApplication = application;
                triggerCompletion(cb, error);
            }
        });
    }

    /*
     * private database operations
     */

    private boolean saveDataToDatabase (SimpleDBObject obj,
                                        boolean authenticated,
                                        ParseUser user) {
        if (userIsSignedIn() || !authenticated) {
            try {
                ParseObject dataStore = new ParseObject("DataStore");
                ParseFile parseFile = new ParseFile(obj.getFileName(), obj.getData());
                parseFile.save();
                dataStore.put("rawData", parseFile);
                if (obj.getCustomId() != null){
                    dataStore.put("identifier", obj.getCustomId());
                }
                if (authenticated) {
                    // cast the userId into a data-less _User object to make parse happy
                    dataStore
                            .put("owner",
                                 ParseUser.createWithoutData("_User", myCurrentUser.getObjectId()));
                }
                dataStore.put("appOwner", myCurrentApplication);
                dataStore.save();
                obj.setDataBacking(dataStore);
            }
            catch (ParseException e) {
                handleParseException(e);
                return false;
            }
            return true;
        }
        else {
            System.out.println("You need to be logged in to save data.");
            return false;
        }
    }

    private void getDataFromDatabase (boolean authenticated,
                                      ParseUser user,
                                      String identifier,
                                      RetrievalCallback cb) {
        if (userIsSignedIn() || !authenticated) {
            ArrayList<SimpleDBObject> responseObjects = new ArrayList<SimpleDBObject>();
            ParseQuery<ParseObject> query = ParseQuery.getQuery("DataStore");
            if (authenticated) {
                // cast the userId into a data-less _User object to make parse happy
                query.whereEqualTo("owner",
                                   ParseUser.createWithoutData("_User", myCurrentUser.getObjectId()));
            }
            else {
                // only query app data, meaning data that doesn't belong to a user
                query.whereDoesNotExist("owner");
            }
            query.whereEqualTo("appOwner", myCurrentApplication);
            if (identifier != null) {
                query.whereEqualTo("identifier", identifier);
            }
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done (List<ParseObject> objects, ParseException e) {
                    if (objects == null) {
                        if (cb != null) {
                            cb.done(null);
                        }
                    }
                    else {
                        for (ParseObject obj : objects) {
                            try {
                                SimpleDBObject simpleObject = convertParseToSimple(obj);
                                responseObjects.add(simpleObject);
                            }
                            catch (ParseException e1) {
                                handleParseException(e1);
                            }
                        }
                        triggerRetrieval(cb, responseObjects);
                    }
                }

            });
        }
        else {
            System.out.println("You need to be signed in to do that.");
            triggerRetrieval(cb, null);
        }
    }

    private SimpleDBObject convertParseToSimple (ParseObject obj) throws ParseException {
        SimpleDBObject simpleObject = new SimpleDBObject();
        simpleObject.setCustomId(obj.get("identifier").toString());
        ParseFile file = obj.getParseFile("rawData");
        simpleObject.setData(file.getData());
        simpleObject.setFileName(file.getName());
        simpleObject.setDataBacking(obj);
        return simpleObject;
    }

    private void triggerCompletion (CompletionCallback cb, String error) {
        if (cb != null) {
            cb.done(error);
        }
    }

    private void triggerRetrieval (RetrievalCallback cb, ArrayList<SimpleDBObject> response) {
        if (cb != null) {
            cb.done(response);
        }
    }

    private void handleParseException (ParseException e) {
        System.out.println(e.getMessage());
    }

    /**
     * a callback for retrieving data from the DB
     */
    public interface RetrievalCallback {
        void done (ArrayList<SimpleDBObject> response);
    }

    /**
     * a callback for when an operation completes
     */
    public interface CompletionCallback {
        void done (String error);
    }

}
