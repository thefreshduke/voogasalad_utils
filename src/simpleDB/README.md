SimpleDB - Java Wrapper for [Parse](https://parse.com)
====================================

Ths wrapper uses Parse's REST api to allow a simple connection to a database, data storage and retrieval, and the creation of users.

Summary
-------

The Parse platform provides a complete backend solution for applications. This wrapper handles the majority of the details to allow you to easily save and fetch data.


Getting Started
---------------

To start, you'll need to create a new instance of SimpleDatabase and then call

```Java
myDatabase.initializeDB(APP_STRING, APP_SECRET, new CompletionCallback(){
            public void done (String error) {
                if (error != null){
                    System.out.println("Failed to initialize DB. " + error);
                }
            }
        });
```

This method accesses a database based on your appString and appSecret.  Any variation of the appString or appSecret will result in a different database being accessed.  Database creation is taken care of automatically for new apps.

You'll see that the initialization method also contains an asynchronous CompletionCallback.  This is optional, but allows you to check whether initialization was successful.  Alternatively, you can use

```Java
myDatabase.isInitialized()
```

To see if your DB has been initialized successfully.


Storing
-------

There are a few ways to store data to your database.  The first set of methods allows you to save data globally to your application.  This can be accessed by anyone who uses your appString and appSecret.  All of these methods return a boolean indicating if the operation succeeded.

To save a raw array of bytes, simply use
```Java
saveAppData (byte[] data, String identifier);
```

To save a File object, use
```Java
saveAppFile ()File file, String identifier);
```

To save a SimpleDBObject, use
```Java
saveObject (SimpleDBObject obj);
```

You will see that these methods optionally take in an identifier string.  This string can be used as a unique identifier or as a way to group data when retrieving from the DB.

Optionally, you can register or login as a user which allows for authenticated saving and retrieving of objects.  Read below for more on users.  The below methods require an authenticated session and can only be retrieved by the same user:

To save a raw array of bytes, use
```Java
saveUserData (byte[] data, String identifier);
```

To save a File object, use
```Java
saveUserFile ()File file, String identifier);
```

To save a SimpleDBObject, use
```Java
saveUserObject (SimpleDBObject obj);
```

Retrieving
-------

Saved data can be retrieved using the following methods:

To get all of the data saved at the application level (not at the user level), use
```Java
getAllDataForApp (RetrievalCallback cb);
```

To get data saved at the application level with a specific identifier, use
```Java
getDataForAppWithIdentifier (String identifier, RetrievalCallback cb);
```

To get all of the data for the current user, use
```Java
getAllDataForUser (RetrievalCallback cb)
```

To get the data for the current user with a specific identifier, use
```Java
getAllDataForUserWithIdentifier (String identifier, RetrievalCallback cb);
```

You will see all of these methods use a RetrievalCallback to asynchronously get the data when the network request completes.  Here's an example:
```Java
myDatabase.getDataForAppWithIdentifier(identifier, new RetrievalCallback(){
    public void done (ArrayList<SimpleDBObject> response) {
        //handle the response
    } 
});
```

SimpleDBObject
-------
A **SimpleDBObject** is just a representation of data retrieved from the database.  It contains the following properties

```Java
String myCustomId;
byte[] myData;
String myFileName;
```

**SimpleDBObjects** are returned by the database, but they can also be created or modified and saved using `saveObject` or `saveUserObject`


Delete
------

To delete an object, just call

```Java
deleteObject (SimpleDBObject obj);;
```

Users
-----

User accounts let users access their information in a secure manner.  All of these return a boolean indicating operation success.  To register a user, simply call:

```Java
registerUser (String username, String password);
```

To login:

```Java
signIn (String username, String password);
```

The signed in user persists in the SimpleDatabase instance.  To sign out, call:

```Java
signOut ();
```

See Storing and Retrieving for what to do with this user session.


Dependencies
-------
This wrapper uses Parse4J, the unofficial Parse REST API library.  It is statically included in the repo.  Thought about using Maven, but didn't want people to have to mess with that for a single dependency.  https://github.com/thiagolocatelli/parse4j
 
 