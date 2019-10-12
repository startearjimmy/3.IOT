package demo.blogpost;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseCredentials;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import demo.object.Post;
import demo.object.User;
/*
 * Author : Guang-Yu Zheng
 * Modified by: Gary Gan
 * Email : gyzheng@cs.nctu.edu.tw
 * Email: gary.gan@pet.cs.nctu.edu.tw
 * Feature : Blog post writer example
 * 
 */
public class PostWriter {
	private final String FIREBASE_JSON_FILE_PATH = "PATH/TO/YOUR/FIREBASE_SERVICE_JSON_FILE";

	private final String FIREBASE_DB_NAME = "YOUR_FIREBASE_DATABASE_NAME";

	/*
     * UI related variables
     */
    private JFrame mainFrame;
    private JTextField textField;
    private JTextPane txtShow;
    private JScrollPane txtPane;
    private JTextField txtName;
    private JLabel lblNickname;
    /*
     * Constructor
     */
    public PostWriter() {
        //Initialize UI
        initUI();
       
        System.out.println("Initializing Firebase Admin SDK...");
        initializeFirebaseAdminSDK(FIREBASE_JSON_FILE_PATH, FIREBASE_DB_NAME);
        
    }
    
    private void postBlog(String author, String title) {
       	final FirebaseDatabase database = FirebaseDatabase.getInstance();
    	DatabaseReference ref = database.getReference("myblog");
    	DatabaseReference postsRef = ref.child("posts").push();
    	
    	output("Posting new post: author=" + author + ", Title=" + title);
    	
    	postsRef.setValue(new Post(author, title), new DatabaseReference.CompletionListener() {
    	    @Override
    	    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
    	        if (databaseError != null) {
    	        	output("Fail");
    	            System.out.println("Data could not be saved " + databaseError.getMessage());
    	        } else {
    	        	output("Success");
    	            System.out.println("Data saved successfully.");
    	        }
    	    }
    	});
    }
    
    public void initUI()
    {
        mainFrame = new JFrame();
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.getContentPane().setLayout(null);
        mainFrame.setSize(452, 413);
        txtShow = new JTextPane();
        txtShow.setText("Post Writer Demo...");
        txtShow.setBounds(6, 6, 438, 327);
        txtPane = new JScrollPane(txtShow);
        txtPane.setBounds(6, 6, 438, 327);
        mainFrame.getContentPane().add(txtPane);
        textField = new JTextField();
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode()==10)//Enter
                {
                	String author = txtName.getText().toString();
                	String title = textField.getText().toString();
                    postBlog(author, title);
                }
            }
        });
        textField.setBounds(81, 356, 363, 28);
        mainFrame.getContentPane().add(textField);
        textField.setColumns(10);
        txtName = new JTextField();
        txtName.setText("John Doe");
        txtName.setBounds(6, 356, 74, 29);
        mainFrame.getContentPane().add(txtName);
        txtName.setColumns(10);       
        lblNickname = new JLabel("Author");
        lblNickname.setBounds(6, 330, 74, 28);
        mainFrame.getContentPane().add(lblNickname);
        mainFrame.setVisible(true);
    }
//    public void listenFireBaseData()
//    {
//        //here we listen for child event
//        rootRef.addChildEventListener(new ChildEventListener() {
//            public void onChildAdded(DataSnapshot snapshot, String arg1) {
//                String name="";
//                String msg="";
//                Map<String, String> message ;
//                /*
//                 * Please get value from snapshot
//                 * Hint: getValue
//                 */
//                message = (Map<String, String>) snapshot.getValue();
//                
//                //show the data to UI
//                name = message.get("name").toString();
//                msg = message.get("msg").toString();
//                output(name,msg);
//            }           
//            public void onChildRemoved(DataSnapshot snapshot) {
//                //noting here
//            }
//            public void onChildMoved(DataSnapshot snapshot, String arg1) {
//                //nothing here
//            }            
//            public void onChildChanged(DataSnapshot snapshot, String arg1) {
//                //nothing here
//            }    
//            public void onCancelled(FirebaseError error) {
//                System.out.println(error.getMessage());
//            }
//        });
//    }
//    
//    public void uploadData()
//    {
//        //Get name from UI
//        String name = txtName.getText();
//        //Get text from UI
//        String text = textField.getText();
//        Map <String,String> message = new HashMap<String,String>();
//        //Put name and msg into message
//        message.put("name",name);
//        message.put("msg",text);
//        /*
//         * Please use "push" to write data to FireBase
//         * Hint: rootRef.push().setValue(......)
//         */
//        rootRef.push().setValue(message,new Firebase.CompletionListener() {   
//            public void onComplete(FirebaseError arg0, Firebase arg1) {
//                textField.setText("");
//                System.out.println("Message Sent!");  
//            }
//        });
//    }
    
    public void output(String text) {
        String ori = txtShow.getText().toString();
        txtShow.setText(ori + "\r\n" + text);
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
    
    /*
     * Main Program here!!
     */
    public static void main(String[] args) {
    	System.out.println("====================");
    	System.out.println("POST WRITER");
    	System.out.println("====================");
        /*
         * Create Chat object and start the chat room!
         */
        PostWriter postWriter = new PostWriter();
               
    }
}
