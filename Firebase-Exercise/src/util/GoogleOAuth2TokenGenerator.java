package util;

import java.io.FileInputStream;
import java.util.Arrays;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;

/**
 * Generate GoogleOAuth2 Token for accessing RESTful API
 * @ref https://firebase.google.com/docs/reference/rest/database/
 * @author Gary Gan
 *
 */
public class GoogleOAuth2TokenGenerator {

	private static final String FIREBASE_SERVICE_ACCOUNT_KEY_JSON_PATH = "/Users/dsync/Desktop/___firebase lecture/petlab-demo-ae658-firebase-adminsdk-iyuqa-bf3b08a299.json";
	
	private static final String SEPARATOR = "============================";
	
	public static void main(String[] args) {
		try {
			// Load the service account key JSON file
			FileInputStream serviceAccount = new FileInputStream(FIREBASE_SERVICE_ACCOUNT_KEY_JSON_PATH);
	
			// Authenticate a Google credential with the service account
			GoogleCredential googleCred = GoogleCredential.fromStream(serviceAccount);
	
			// Add the required scopes to the Google credential
			GoogleCredential scoped = googleCred.createScoped(
			    Arrays.asList(
			      "https://www.googleapis.com/auth/firebase.database",
			      "https://www.googleapis.com/auth/userinfo.email"
			    )
			);
	
			// Use the Google credential to generate an access token
			scoped.refreshToken();
			String token = scoped.getAccessToken();
	
			System.out.println("Your Google OAuth2 Access Token is the following...");
			System.out.println(SEPARATOR);
			System.out.println(token);
			System.out.println(SEPARATOR);
			System.out.println("To use the token, simply append ?access_token following the .json file");
			System.out.println("E.g. https://<DATABASE_NAME>.firebaseio.com/users/ada/name.json?access_token=<ACCESS_TOKEN>");
			
			// See the "Using the access token" section below for information
			// on how to use the access token to send authenticated requests to the
			// Realtime Database REST API.
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
