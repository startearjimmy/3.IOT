package exercise.chatapp;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
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
import com.google.firebase.database.ValueEventListener;

/**
 * Example console output:
 * 		Authenticating with Firebase service using Google credential=true
		Enter your username:
		Alice
		Enter a chat room ID to join:
		one
		You are now joining chat room [one] as [Alice]
		Subscribing for messages in this chat room...
		Enter a message:
		ChatRoomID=one, lastMessage=, lastMessagePosted=
		Hi
		Enter a message:
		[1511768854587] [Alice]: Hi
		I'm Alice
		Enter a message:
		[1511768856675] [Alice]: I'm Alice
		[1511768873251] [Bob]: Hi Alice, I'm Bob
 * 
 * @author Gary Gan
 * @email gary.gan@pet.cs.nctu.edu.tw
 * 
 * Chat application developed using Firebase Realtime Database as storage backend.
 *
 */
public class ChatApp {

	private final String FIREBASE_JSON_FILE_PATH = "PATH/TO/YOUR/FIREBASE_SERVICE_JSON_FILE";

	private final String FIREBASE_DB_NAME = "YOUR_FIREBASE_DATABASE_NAME";
	
	public ChatApp() {
		this.initializeFirebaseAdminSDK(FIREBASE_JSON_FILE_PATH, FIREBASE_DB_NAME);

		// 1. Enter username
		System.out.println("Enter your username:");
		this.username = new Scanner(System.in).nextLine();
		
		// 2. Select a chat room to join
		this.chatRoomList = new ArrayList<String>();
		System.out.println("Enter a chat room ID to join:");
		this.getChatRoomList();
		this.chatRoomId = new Scanner(System.in).nextLine();

		if (this.chatRoomList.size() == 0)				
			this.updateChat(this.chatRoomId, "", "");

		else {
			// Check whether we should create a new chat room node
			for (String chatRoomId : this.chatRoomList) {
				// if user entered a new chat room
				if (!chatRoomId.equalsIgnoreCase(this.chatRoomId)) {
					System.out.println("You had entered a new chat room, creating a new chat room node now...");
					this.updateChat(this.chatRoomId, "", "");
					break;
				}
			}
		}
		
		// 3. Update the [members] node to include this user to the chat room
		this.updateMember(this.chatRoomId, this.username);
		String notification = String.format("You are now joining chat room [%s] as [%s]",
											this.chatRoomId,
											this.username);
		System.out.println(notification);
		
		// 4. Listen for any new messages in this chat room
		System.out.println("Subscribing for messages in this chat room...");
		this.subscribe(this.chatRoomId);

		// 5. Enter a message, quit app if user enter 88
		
		String message = "";
		do {
			System.out.println("Enter a message:");
			message = new Scanner(System.in).nextLine();
			this.newMessage(this.chatRoomId, this.username, message);
		} while (!message.equalsIgnoreCase("88"));

	}

	/**
	 * Subscribe for any message entered in this chat room
	 */
	private void subscribe(String chatId) {
		final FirebaseDatabase database = FirebaseDatabase.getInstance();
		DatabaseReference score = database.getReference("mychat/messages/" + chatId);

		Query queryRef = score.orderByChild("timestamp");
		queryRef.addChildEventListener(new ChildEventListener() {

			@Override
			public void onChildRemoved(DataSnapshot dataSnapshot) {}

			@Override
			public void onChildMoved(DataSnapshot dataSnapshot,
					String prevChildKey) {}

			@Override
			public void onChildChanged(DataSnapshot dataSnapshot,
					String prevChildKey) {}

			@Override
			public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
				// TODO: Display a conversion in the following format when a new 
				// message is added
				// [1511768245428] [Alice]: Hello Bob! I'm Alice!
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {

			}
		});
	}
	
	/**
	 * Get a list of chat room
	 */
	private void getChatRoomList() {
		final FirebaseDatabase database = FirebaseDatabase.getInstance();
		DatabaseReference chats = database.getReference("mychat/chats");

		Query queryRef = chats.orderByChild("timestamp");
		
		queryRef.addChildEventListener(new ChildEventListener() {
			
			@Override
			public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
				// TODO: Get a list of chat rooms and update the list
				// ...
				
				// Update the local chat room list
				chatRoomList.add(dataSnapshot.getKey());
			}

			@Override
			public void onChildRemoved(DataSnapshot dataSnapshot) {}

			@Override
			public void onChildMoved(DataSnapshot dataSnapshot,
					String prevChildKey) {}

			@Override
			public void onChildChanged(DataSnapshot dataSnapshot,
					String prevChildKey) {}

			@Override
			public void onCancelled(DatabaseError databaseError) {}
		});
	}

	/**
	 * Update the [members] node
	 * @param chatId
	 * @param memberName
	 */
	private void updateMember(String chatId, String memberName) {
		// TODO ...
	}

	/**
	 * Update the [messages] node
	 * @param chatId
	 * @param memberName
	 * @param message
	 * @param timestamp
	 */
	private void updateMessage(String chatId, String memberName, String message, String timestamp) {
		// TODO ...
	}

	/** 
	 * Update the [chats] node
	 * @param chatId
	 * @param chatTitle
	 * @param lastMessage
	 * @param timestamp
	 */
	private void updateChat(String chatId, String message, String timestamp) {
		// TODO ...
	}

	/**
	 * Update both the [chats] and [messages] node whenever a user posted a new
	 * message.
	 */
	private void newMessage(String chatId, String memberName, String message) {
		String timestamp = String.valueOf(System.currentTimeMillis());

		this.updateChat(chatId, message, timestamp);
		this.updateMessage(chatId, memberName, message, timestamp);
	}

	/**
	 * Initialize connection with Firebase service using Google OAuth2
	 * credential.
	 * 
	 * @param jsonFilePath
	 * @param firebaseDbName
	 */
	private void initializeFirebaseAdminSDK(String jsonFilePath,
			String firebaseDbName) {
		this.initializeFirebaseAdminSDK(jsonFilePath, firebaseDbName, false);
	}

	@SuppressWarnings("deprecation")
	/**
	 * Initialize connection with Firebase service using Firebase or Google OAuth2 credential
	 * @param jsonFilePath
	 * @param firebaseDbName
	 * @param usingFirebaseCredential
	 */
	private void initializeFirebaseAdminSDK(String jsonFilePath,
			String firebaseDbName, boolean usingFirebaseCredential) {
		FirebaseOptions options = null;

		System.out
				.println("Authenticating with Firebase service using Google credential="
						+ !usingFirebaseCredential);
		try {
			FileInputStream serviceAccount = new FileInputStream(jsonFilePath);
			if (usingFirebaseCredential)
				options = new FirebaseOptions.Builder()
						.setCredential(
								FirebaseCredentials
										.fromCertificate(serviceAccount))
						.setDatabaseUrl(
								"https://" + firebaseDbName
										+ ".firebaseio.com/").build();
			else
				// using Google OAuth2 credential
				options = new FirebaseOptions.Builder()
						.setCredentials(
								GoogleCredentials.fromStream(serviceAccount))
						.setDatabaseUrl(
								"https://" + firebaseDbName
										+ ".firebaseio.com/").build();
		} catch (IOException e) {
			e.printStackTrace();
		}
		FirebaseApp.initializeApp(options);
	}

	// username in this session
	private String username;
	
	// chat room ID in this session
	private String chatRoomId;
	
	// A list of chat room list retrieved from [chats] node, use this list to
	// decide whether to create a new node.
	private ArrayList<String> chatRoomList;

	public static void main(String[] args) {
		ChatApp mChatApp = new ChatApp();

	}

}
