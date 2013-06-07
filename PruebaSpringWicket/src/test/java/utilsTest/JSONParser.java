package utilsTest;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import modelsTest.Site;
import modelsTest.User;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class JSONParser {

	protected final static Logger log = Logger.getLogger(JSONParser.class.getName());
	
	public static JSONObject userToJSON(User u) throws JSONException{
		Gson gson = new Gson();
		JSONObject jObject = null;
		 
		jObject = new JSONObject(gson.toJson(u, User.class));
		log.trace("jObject: " + jObject);
				
		return jObject;
	}
	
	public static JSONObject siteToJSON(Site s) throws JSONException{
		Gson gson = new Gson();
		JSONObject jObject = null;
		 
		jObject = new JSONObject(gson.toJson(s, Site.class));
		log.trace("jObject: " + jObject);
				
		return jObject;
	}
	
	public static JSONArray listStringsToJSON(List<String> listStrings) throws JSONException{
		Gson gson = new Gson();
		JSONArray jArray = null;
		
		jArray = new JSONArray(gson.toJson(listStrings, List.class));
		log.trace("jArray: " + jArray);
				
		return jArray;
	}
	
	public static List<User> parseListUsers(String jString) throws Exception {
		Set<User> lista = new HashSet<User>();
	
		if(!jString.equals("")){
			Gson gson = new Gson();
		
			if(jString.startsWith("[")){
				Type collectionType = new TypeToken<HashSet<User>>(){}.getType();
				lista = gson.fromJson(jString, collectionType);
			}else{
				lista.add(gson.fromJson(jString, User.class));
			}
		}
		else{
			lista=null;			
		}
		List<User> listUsers=new ArrayList<User>(lista);
		return listUsers;
	}
	
	public static List<Site> parseListSites(String jString) throws Exception {
		Set<Site> lista = new HashSet<Site>();
	
		if(!jString.equals("")){
			Gson gson = new Gson();
		
			if(jString.startsWith("[")){
				Type collectionType = new TypeToken<HashSet<Site>>(){}.getType();
				lista = gson.fromJson(jString, collectionType);
			}else{
				lista.add(gson.fromJson(jString, Site.class));
			}
		}
		else{
			lista=null;			
		}
		List<Site> listSites=new ArrayList<Site>(lista);
		return listSites;
	}

}
