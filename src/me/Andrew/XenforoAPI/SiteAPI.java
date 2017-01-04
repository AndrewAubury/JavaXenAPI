package me.Andrew.XenforoAPI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;

import me.Andrew.XenforoAPI.APIAction;

public class SiteAPI {
	
	public String API_KEY;
	public String API_LINK;

	public SiteAPI(String link, String key) {
		API_KEY = key;
		API_LINK = link;
	}
	public String editUser(String user, HashMap<String, String> args){
		args.put("user", user);
		return callAPI(APIAction.edituser, args);
	}
	public String callAPI(APIAction action, HashMap<String, String> args) {
		String sAction = action.toString();
		String url = API_LINK + "?hash=" + API_KEY + "&action=" + sAction;
		if (args != null) {
			for (Entry<String, String> arg : args.entrySet()) {
				url += "&" + arg.getKey() + "=" + arg.getValue();
			}
		}
		String url2 = "";
		try {
			url2 = URLEncoder.encode(url, "UTF-8");

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (url2 != "") {
			return readUrl(url2);
		}
		return readUrl(url);
	}
	public String addGroup(String name, int group) {
		HashMap<String, String> args = new HashMap<String,String>();
		args.put("add_groups",group+"");
		return editUser(name,args);
	}
	public String removeGroup(String name, int group) {
		HashMap<String, String> args = new HashMap<String,String>();
		args.put("remove_groups",group+"");
		return editUser(name,args);
	}
	public boolean userExists(String name) {
		HashMap<String, String> args = new HashMap<>();
		args.put("value", name);
		try {
			String getLink = callAPI(APIAction.getuser, args);
			JSONObject json = new JSONObject(readUrl(getLink));
			if (json.has("user_id")) {
				return true;
			} else {
				return false;
			}
		} catch (JSONException e) {
			return false;
		}
	}
	public boolean registerUser(String name, String email, String password) {
		HashMap<String, String> args = new HashMap<>();
		args.put("username", name);
		args.put("password", password);
		args.put("email", email);
		args.put("user_state", "valid");
		try {
			String registerLink = callAPI(APIAction.register, args);
			JSONObject json = new JSONObject(readUrl(registerLink));
			return !json.has("error");
		} catch (JSONException e) {
			return false;
		}

	}
	public boolean registerUser(String name, String email, String password, HashMap<String,String> argsIn) {
		HashMap<String, String> args = argsIn;
		args.put("username", name);
		args.put("password", password);
		args.put("email", email);
		args.put("user_state", "valid");
		try {
			String registerLink = callAPI(APIAction.register, args);
			JSONObject json = new JSONObject(readUrl(registerLink));
			return !json.has("error");
		} catch (JSONException e) {
			return false;
		}

	}
	public String readUrl(String urlString) {
		BufferedReader reader = null;
		String res = "";
		try {
			URL url = new URL(urlString);
			reader = new BufferedReader(new InputStreamReader(url.openStream()));
			StringBuffer buffer = new StringBuffer();
			int read;
			char[] chars = new char[1024];
			while ((read = reader.read(chars)) != -1)
				buffer.append(chars, 0, read);
			if (reader != null)
				reader.close();
			res = buffer.toString();
		} catch (IOException e) {
		}
		return res;
	}
	public String authenticate(String user, String pass){
		HashMap<String, String> args = new HashMap<>();
		args.put("username", user);
		args.put("password", pass);
		args.put("user_state", "valid");
		return callAPI(APIAction.authenticate, args);
	}
	public String createConversation(String fromUser, List<String> recipients, String title, String message){
		HashMap<String, String> args = new HashMap<>();
		String allRecipients = "";
		if(recipients.size() > 1){
			for(String user : recipients){
				if(allRecipients != ""){
					allRecipients += ",";
				}
				allRecipients += user;
			}
		}else if(recipients.size() == 1){
			allRecipients = recipients.get(0);
		}
		args.put("grab_as", fromUser);
		args.put("recipients", allRecipients);
		args.put("title", title);
		args.put("message", message);
		return callAPI(APIAction.createconversation, args);
	}
	public String createConversationReply(String fromUser, int conoID, String message){
		HashMap<String, String> args = new HashMap<>();
		args.put("grap_as", fromUser);
		args.put("conversation_id", conoID+"");
		args.put("message", message);
		return callAPI(APIAction.createconversationreply, args);
	}
	public boolean canConnect() {
		try {
			String testLink = API_LINK + "?action=getProfilePosts&hash=" + API_KEY;
			URL url = new URL(testLink);
			HttpURLConnection request;
			request = (HttpURLConnection) url.openConnection();
			request.connect();
			return request.getResponseCode() == 200;
		} catch (IOException e) {
			return false;
		}
	}

	

}
