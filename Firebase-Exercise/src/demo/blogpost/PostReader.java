package demo.blogpost;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.IOException;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
/*
 * Author : Guang-Yu Zheng
 * Modified by: Gary Gan
 * Email : gyzheng@cs.nctu.edu.tw
 * Email: gary.gan@pet.cs.nctu.edu.tw
 * Feature : Blog post reader example
 * 
 */
public class PostReader {
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
    public PostReader() {
        //Initialize UI
        initUI();
        
        System.out.println("Initializing Firebase Admin SDK...");
        initializeFirebaseAdminSDK(FIREBASE_JSON_FILE_PATH, FIREBASE_DB_NAME);
        
        output("Listening for new post on [myblog/posts]...");
        System.out.println("Listening for new post on [myblog/posts]...");
        listenForChildEvent("myblog/posts");
    }
    
    /**
     * Listen for child changes (added, removed, changed) event
     * @param databasePath
     */
    private void listenForChildEvent(String databasePath) {
       	final FirebaseDatabase database = FirebaseDatabase.getInstance();
    	DatabaseReference posts = database.getReference(databasePath);
    	
    	posts.addChildEventListener(new ChildEventListener() {

    	    @Override
    	    public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
    	    	
    	    	Map<String, String> post = (Map<String, String>) dataSnapshot.getValue();
    	        String str = String.format("New post added! Author=%s, Title=%s, Prev Post ID=%s", post.get("author"), post.get("title"), prevChildKey);
    	        output(str);
    	        
//    	        Post newPost = dataSnapshot.getValue(Post.class);
//    	        output(newPost.toString());
//    	        output("Author=" + newPost.author);
//    	        
//    	        System.out.println("Author: " + newPost.author);
//    	        System.out.println("Title: " + newPost.title);
//    	        System.out.println("Previous Post ID: " + prevChildKey);
    	    }

    	    @Override
    	    public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
    	    	Map<String, String> post = (Map<String, String>) dataSnapshot.getValue();
    	        output("The updated post title is: " + post.get("title"));
    	    }

    	    @Override
    	    public void onChildRemoved(DataSnapshot dataSnapshot) {
    	    	output("Post removed!");
    	    }

    	    @Override
    	    public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

    	    @Override
    	    public void onCancelled(DatabaseError databaseError) {}

    		
    	});
    }

    public void initUI()
    {
        mainFrame = new JFrame();
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.getContentPane().setLayout(null);
        mainFrame.setSize(452, 413);
        txtShow = new JTextPane();
        txtShow.setText("Post Reader Demo...");
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
                }
            }
        });
        textField.setBounds(81, 356, 363, 28);
        mainFrame.getContentPane().add(textField);
        textField.setColumns(10);
        txtName = new JTextField();
        txtName.setText("guest");
        txtName.setBounds(6, 356, 74, 29);
        mainFrame.getContentPane().add(txtName);
        txtName.setColumns(10);       
        lblNickname = new JLabel("NickName");
        lblNickname.setBounds(6, 330, 74, 28);
        mainFrame.getContentPane().add(lblNickname);
        mainFrame.setVisible(true);
    }
    
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
    	System.out.println("POST READER");
    	System.out.println("====================");
        PostReader postReader = new PostReader();
    }
}
