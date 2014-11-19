package simpleDB.tests;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.parse4j.ParseExecutor;
import simpleDB.SimpleDatabase;
import simpleDB.SimpleDatabase.CompletionCallback;

public class SimpleDatabaseTestCase {
    
    public static SimpleDatabase myDatabase;
    private static final String TEST_APP_STRING = "voogasalad_db_testing";
    private static final String TEST_APP_SECRET = "superSecretPassword";
    
    
    @BeforeClass
    public static void setup () {
        myDatabase = new SimpleDatabase();
        myDatabase.initializeDB(TEST_APP_STRING, TEST_APP_SECRET, new CompletionCallback(){
            public void done (String error) {
                if (error != null){
                    System.out.println("Failed to initialize DB. "+error);
                }
            }
        });
        try {
            //bad work around for not wanting to deal with async unit tests
            Thread.sleep(3000);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    @AfterClass
    public static void tearDown() {
        ParseExecutor.getExecutor().shutdown();
    }

}
