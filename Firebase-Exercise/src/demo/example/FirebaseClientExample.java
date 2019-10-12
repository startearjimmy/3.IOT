package demo.example;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseCredentials;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import demo.object.User;
/*
 * @Author : Gary Gan
 * @Email : gary.gan@pet.cs.nctu.edu.tw
 * 
 * @ref: https://firebase.google.com/docs/database/admin/save-data
 * @ref: https://firebase.google.com/docs/database/admin/retrieve-data
 * 
 * Example usage of Firebase Admin SDK
 */
public class FirebaseClientExample {
	private final String FIREBASE_JSON_FILE_PATH = "PATH/TO/YOUR/FIREBASE_SERVICE_JSON_FILE";

	private final String FIREBASE_DB_NAME = "YOUR_FIREBASE_DATABASE_NAME";

    /*
     * Constructor
     */
    public FirebaseClientExample() {
        System.out.println("Initializing Firebase Admin SDK...");
        initializeFirebaseAdminSDK(FIREBASE_JSON_FILE_PATH, FIREBASE_DB_NAME);
    }
    
    private void queryData() {
       	final FirebaseDatabase database = FirebaseDatabase.getInstance();
    	DatabaseReference score = database.getReference("score");
    	
    	Query queryRef = score.orderByValue();
    	queryRef.addChildEventListener(new ChildEventListener() {
			
			@Override
			public void onChildRemoved(DataSnapshot dataSnapshot) {
				
			}
			
			@Override
			public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
				
			}
			
			@Override
			public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
				
			}
			
			@Override
			public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
		        System.out.println("The " + dataSnapshot.getKey() + " score is " + dataSnapshot.getValue());
			}
			
			@Override
			public void onCancelled(DatabaseError databaseError) {
				
			}
		});
    		
    }
    
    /**
     * Example of updating multipath data
     */
    private void updateDataMultipath() {
    	final FirebaseDatabase database = FirebaseDatabase.getInstance();
    	DatabaseReference ref = database.getReference("dummy");
    	DatabaseReference usersRef = ref.child("users");
    	
    	Map<String, Object> userUpdates = new HashMap<>();
    	userUpdates.put("alanisawesome/nickname", "Alan The Machine");
    	userUpdates.put("gracehop/nickname", "Amazing Grace");

    	usersRef.updateChildrenAsync(userUpdates);
    }
   
    /**
     * Example of updating an existing data.
     */
    private void updateData() {
    	final FirebaseDatabase database = FirebaseDatabase.getInstance();
    	DatabaseReference ref = database.getReference("dummy");
    	DatabaseReference usersRef = ref.child("users");
    	
    	DatabaseReference hopperRef = usersRef.child("gracehop");
    	Map<String, Object> hopperUpdates = new HashMap<>();
    	hopperUpdates.put("full_name", "Amazing Grace2");

    	hopperRef.updateChildrenAsync(hopperUpdates);
    }
    
    /**
     * Example of saving a new data, all existing data under the child will be replaced.
     */
    private void saveData() {
    	final FirebaseDatabase database = FirebaseDatabase.getInstance();
    	DatabaseReference ref = database.getReference("dummy");
    	DatabaseReference usersRef = ref.child("users");

    	Map<String, User> users = new HashMap<>();
    	users.put("alanisawesome", new User("Alan Turing", "1989"));
    	users.put("gracehop", new User("Grace Hop", "1992"));

    	usersRef.setValueAsync(users);
    }
    
    /**
     * Example of saving a new data with a unique key generation. Ideal for use
     * when multiple concurrent save is expected, e.g. multiple writer posting their posts.
     */
    private void saveDataWithPush() {
    	final FirebaseDatabase database = FirebaseDatabase.getInstance();
    	DatabaseReference ref = database.getReference("dummy");
    	DatabaseReference usersRef = ref.child("users").push();
    	System.out.println("Post ID=" + usersRef.getKey());

    	usersRef.setValueAsync(new User("Alan", "1999"));
    }
    
    /**
     * Example of saving a new data with callback upon completion or failure.
     */
    private void saveDataWithCallback() {
    	final FirebaseDatabase database = FirebaseDatabase.getInstance();
    	DatabaseReference ref = database.getReference("dummy");
    	DatabaseReference usersRef = ref.child("users");

    	Map<String, User> users = new HashMap<>();
    	users.put("alanisawesome", new User("Alan Turing", "1989"));
    	users.put("gracehop", new User("Grace Hop", "1992"));

    	usersRef.setValue(users, new DatabaseReference.CompletionListener() {
    	    @Override
    	    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
    	        if (databaseError != null) {
    	            System.out.println("Data could not be saved " + databaseError.getMessage());
    	        } else {
    	            System.out.println("Data saved successfully.");
    	        }
    	    }
    	});
    }
    
    /**
     * Initialize connection with Firebase service using Google OAuth2 credential.
     * @param jsonFilePath
     * @param firebaseDbName
     */
	private void initializeFirebaseAdminSDK(String jsonFilePath, String firebaseDbName) {
		this.initializeFirebaseAdminSDK(jsonFilePath, firebaseDbName, false);
	}
	
    @SuppressWarnings("deprecation")
    /**
     * Initialize connection with Firebase service using Firebase or Google OAuth2 credential
     * @param jsonFilePath
     * @param firebaseDbName
     * @param usingFirebaseCredential
     */
	private void initializeFirebaseAdminSDK(String jsonFilePath, String firebaseDbName, boolean usingFirebaseCredential) {
    	FirebaseOptions options = null;

    	System.out.println("Authenticating with Firebase service using Google credential=" + !usingFirebaseCredential);
		try {
	    	FileInputStream serviceAccount = new FileInputStream(jsonFilePath);
	    	if (usingFirebaseCredential)
				options = new FirebaseOptions.Builder()
				  .setCredential(FirebaseCredentials.fromCertificate(serviceAccount))
				  .setDatabaseUrl("https://" + firebaseDbName + ".firebaseio.com/")
				  .build();
	    	else // using Google OAuth2 credential
				options = new FirebaseOptions.Builder()
			  .setCredentials(GoogleCredentials.fromStream(serviceAccount))
			  .setDatabaseUrl("https://" + firebaseDbName + ".firebaseio.com/")
			  .build();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	FirebaseApp.initializeApp(options);
    }
    

    public static void main(String[] args) {
    	System.out.println("====================");
    	System.out.println("Firebase Client Example");
    	System.out.println("====================");
        
        FirebaseClientExample firebaseClientExample = new FirebaseClientExample();
        
        int input = -1;
        do {
        	dispUsage();

        	input = Integer.parseInt(new Scanner(System.in).nextLine());
        	switch (input) {
        	case 1:
        		System.out.println("Save data");
        		firebaseClientExample.saveData();
        		break;
        	
        	case 2:
        		System.out.println("Save data with push");
        		firebaseClientExample.saveDataWithPush();
        		break;
        		
        	case 3:
        		System.out.println("Save data with callback");
        		firebaseClientExample.saveDataWithCallback();
        		break;
        		
        	case 4:
        		System.out.println("Update data");
        		firebaseClientExample.updateData();
        		break;
        		
        	case 5:
        		System.out.println("Update data multipath");
        		firebaseClientExample.updateDataMultipath();
        		break;
        		
        	case 6:
        		System.out.println("Query data");
        		firebaseClientExample.queryData();
        	}
        } while (input != 88); 
               
    }
    
    private static void dispUsage() {
    	System.out.println("-----------------------------");
    	System.out.println("1. Save data");
    	System.out.println("2. Save data with push");
    	System.out.println("3. Save data with callback");
    	System.out.println("4. Update data");
    	System.out.println("5. Update data multipath");
    	System.out.println("6. Query data");
    	System.out.println("-----------------------------");
    }
}
