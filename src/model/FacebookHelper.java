package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import facebook4j.auth.AccessToken;
import facebook4j.internal.org.json.JSONException;
import facebook4j.internal.org.json.JSONObject;

public enum FacebookHelper {
	INSTANCE;
	public final static String LOGIN_SUCCESS_URL_RESPONSE_PATTERN = "https://www.facebook.com/connect/login_success.html?code=";
	public final static String CLIENT_ID="";
	public final static String APP_SECRET = "";
	public final static String REDIRECT_URI="https://www.facebook.com/connect/login_success.html";
	
	public String getUrlToLogin(){
		return "https://www.facebook.com/v2.9/dialog/oauth?client_id=" + CLIENT_ID + "&redirect_uri="+ REDIRECT_URI + "&scope=publish_actions";
	}
    
    public AccessToken getAccessToken(String code) {
		URL fbGraphURL;
		try {
			fbGraphURL = new URL(getFBGraphUrl(code));
		} catch (MalformedURLException e) {
			throw new RuntimeException("Invalid code received " + e);
		}
		URLConnection fbConnection;
		StringBuffer b = null;
		try {
			fbConnection = fbGraphURL.openConnection();
			BufferedReader in;
			in = new BufferedReader(new InputStreamReader(
					fbConnection.getInputStream()));
			String inputLine;
			b = new StringBuffer();
			while ((inputLine = in.readLine()) != null)
				b.append(inputLine + "\n");
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Unable to connect with Facebook " + e);
		}

		String accessToken = b.toString();
		if (accessToken.startsWith("{")) {
			try {
				System.out.println(accessToken);
				JSONObject json = new JSONObject(accessToken);
				accessToken = (String)json.get("access_token");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		AccessToken token = new AccessToken(accessToken);
		return token;
	}
    
    private String getFBGraphUrl(String code) {
		String fbGraphUrl = "";
		try {
			fbGraphUrl = "https://graph.facebook.com/oauth/access_token?"
					+ "client_id=" + CLIENT_ID 
					+ "&redirect_uri=" + URLEncoder.encode(REDIRECT_URI, "UTF-8")
					+ "&client_secret=" + APP_SECRET 
					+ "&code=" + code;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return fbGraphUrl;
	}

}
