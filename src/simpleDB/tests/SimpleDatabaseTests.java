package simpleDB.tests;

import static org.junit.Assert.*;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import org.junit.Test;
import simpleDB.SimpleDatabase.RetrievalCallback;
import simpleDB.SimpleDBObject;

public class SimpleDatabaseTests extends SimpleDatabaseTestCase {
    

    private ArrayList<SimpleDBObject> myDataResponse;

    @Test
    public void testInitialized () {
        assertTrue(myDatabase.isInitialized());
    }
    
    //test not included by default to avoid unneeded registrations
    public void testRegister () {
        assertTrue(myDatabase.registerUser("testUser", "password"));
    }
    
    @Test
    public void testSigninAndUserDataSaveandFetch () {
        assertTrue(myDatabase.signIn("testUser", "password"));
        assertTrue(myDatabase.userIsSignedIn());
        testUserDataSave();
    }
    
    public void testUserDataSave(){
        byte[] sampleData = "so much data".getBytes();
        assertTrue(myDatabase.saveUserData(sampleData, "myIdentifier"));
        testUserDataRetrievalByIdentifier("myIdentifier");
    }
    
    public void testUserDataRetrievalByIdentifier(String identifier){
        myDatabase.getAllDataForUserWithIdentifier(identifier, new RetrievalCallback(){
            public void done (ArrayList<SimpleDBObject> response) {
                myDataResponse = response;
            } 
        });
        try {
            //bad work around for not wanting to deal with async unit tests
            Thread.sleep(3000);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        assertNotNull("Data response was null.",myDataResponse);
        String fromBytes = "";
        try {
            fromBytes = new String(myDataResponse.get(0).getData(), "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        assertEquals(fromBytes,"so much data");
        testDeletion(myDataResponse.get(0));
        myDataResponse = null;
        myDatabase.signOut();
    }
    
    
    @Test
    public void testAppDataSaveandFetch(){
        byte[] sampleData = "even more data".getBytes();
        assertTrue(myDatabase.saveAppData(sampleData, "myIdentifier"));
        testAppDataRetrievalByIdentifier("myIdentifier","even more data");
    }
    
    public void testAppDataRetrievalByIdentifier(String identifier, String expectedString){
        myDatabase.getDataForAppWithIdentifier(identifier, new RetrievalCallback(){
            public void done (ArrayList<SimpleDBObject> response) {
                myDataResponse = response;
            } 
        });
        try {
            //bad work around for not wanting to deal with async unit tests
            Thread.sleep(3000);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        assertNotNull("Data response was null.",myDataResponse);
        String fromBytes = "";
        try {
            fromBytes = new String(myDataResponse.get(0).getData(), "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        assertEquals(fromBytes,expectedString);
        testDeletion(myDataResponse.get(0));
        myDataResponse = null;
    }
    
    @Test
    public void testSaveModifySave(){
        //if we construct a SimpleDBObject, we can modify it and save it as needed
        byte[] myData = "my original string".getBytes();
        SimpleDBObject myObject = new SimpleDBObject(myData,"data.bif","uniqueIdentifier");
        myDatabase.saveObject(myObject);
        assertNotNull(myObject.getData());
        myObject.setData("my new string".getBytes());
        assertTrue(myDatabase.saveObject(myObject));
        testAppDataRetrievalByIdentifier("uniqueIdentifier","my new string");
    }
    
    public void testDeletion(SimpleDBObject obj){
        assertTrue(myDatabase.deleteObject(obj));
    }
    
    
    
}
