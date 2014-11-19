package simpleDB.example;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import simpleDB.SimpleDBObject;
import simpleDB.SimpleDatabase;
import simpleDB.SimpleDatabase.CompletionCallback;
import simpleDB.SimpleDatabase.RetrievalCallback;

public class Example {
    
    private static SimpleDatabase myDatabase;
    
    public static void main (String args[]) {
        myDatabase = new SimpleDatabase();
        myDatabase.initializeDB("example_app", "example_app_secret", new CompletionCallback(){
            public void done (String error) {
                if (error == null){
                    //database was initialized successfully
                    saveData();
                }
            }
        });
    }
    
    public static void saveData(){
        myDatabase.saveAppData("data_to_save".getBytes(), "data_identifier");
        fetchData();
    }
    
    public static void fetchData(){
        myDatabase.getDataForAppWithIdentifier("data_identifier", new RetrievalCallback(){
            public void done (ArrayList<SimpleDBObject> response) {
                System.out.println("Retrieved data is " + convertDataToString(response.get(0).getData()));
            }
        });
    }
    
    public static String convertDataToString(byte[] data){
        try {
            return new String(data, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
}
