package utilsTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import modelsTest.Site;
import modelsTest.User;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

public class ApiHelpers {

	
	
	public static String SERVER_ADDRESS = "http://192.168.1.4:8080/";
	
	public static String APP_BASEURL = "PruebaSpringWicket/rest";
	
	public static String getContent(String URL) throws Exception {

		HttpGet request = new HttpGet(URL);
		HttpParams httpParameters = new BasicHttpParams();
		// Set the timeout in milliseconds until a connection is established.
		// The default value is zero, that means the timeout is not used. 
		int timeoutConnection = 100000;
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
		// Set the default socket timeout (SO_TIMEOUT) 
		// in milliseconds which is the timeout for waiting for data.
		int timeoutSocket = 200000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

		HttpClient httpclient = new DefaultHttpClient(httpParameters);

		request.setHeader("Content-Type", "application/json");
		
		String result = "";
		try {
			HttpResponse response = httpclient.execute(request);
            result=Userrequest(response); 
            if(response.getStatusLine().getStatusCode()==HttpStatus.SC_BAD_REQUEST){
				throw new Exception(result);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		
		httpclient.getConnectionManager().shutdown();
		return result;
	}
	
	public static String putContent(String URL,  JSONObject o) throws Exception {     

		HttpParams httpParameters = new BasicHttpParams();
		HttpPut request = new HttpPut(URL);
		// Set the timeout in milliseconds until a connection is established.
		// The default value is zero, that means the timeout is not used. 
		int timeoutConnection = 100000;
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
		// Set the default socket timeout (SO_TIMEOUT) 
		// in milliseconds which is the timeout for waiting for data.
		int timeoutSocket = 200000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		HttpClient httpclient = new DefaultHttpClient(httpParameters);
		String result = "";
		try {         

			StringEntity se = new StringEntity(o.toString());

			request.setEntity(se);
			request.setHeader("Content-Type", "application/json");
			
			HttpResponse response = httpclient.execute(request);
            result=Userrequest(response);
            
            if(response.getStatusLine().getStatusCode()==HttpStatus.SC_BAD_REQUEST){
				throw new Exception(result);
			}            			
		} 
		catch (ClientProtocolException e) {   
			e.printStackTrace();
			throw new Exception(e.getMessage());     
		} 
		catch (IOException e) {  
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return result;
	}
	
	public static String putContent(String URL,  JSONArray a) throws Exception {     

		HttpParams httpParameters = new BasicHttpParams();
		HttpPut request = new HttpPut(URL);
		// Set the timeout in milliseconds until a connection is established.
		// The default value is zero, that means the timeout is not used. 
		int timeoutConnection = 100000;
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
		// Set the default socket timeout (SO_TIMEOUT) 
		// in milliseconds which is the timeout for waiting for data.
		int timeoutSocket = 200000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		HttpClient httpclient = new DefaultHttpClient(httpParameters);
		String result = "";
		try {         

			StringEntity se = new StringEntity(a.toString());

			request.setEntity(se);
			request.setHeader("Content-Type", "application/json");
			//ResponseHandler<String> handler = new BasicResponseHandler();
			
			HttpResponse response = httpclient.execute(request);
            result=Userrequest(response);
            
            if(response.getStatusLine().getStatusCode()==HttpStatus.SC_BAD_REQUEST){
				throw new Exception(result);
			}            			
		} 
		catch (ClientProtocolException e) {   
			e.printStackTrace();
			throw new Exception(e.getMessage());     
		} 
		catch (IOException e) {  
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return result;
	}
	
	public static String Userrequest(HttpResponse response){
	 String result = "";
        try     
        {
            InputStream in = response.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder str = new StringBuilder();
            String line = null;            
            while((line = reader.readLine()) != null){                 
            	str.append(line);                
            }
            in.close();
            result = str.toString();
            //updateData(result);         
        }
        catch(Exception ex) {
            //responsetxt.setText(ex.getMessage());
        }
        return result;
    }
	
	public static void register(User user)throws Exception{
		JSONObject userJSON = JSONParser.userToJSON(user);
		ApiHelpers.putContent(SERVER_ADDRESS + APP_BASEURL+"/users/register", userJSON);
		
	}

	public static List<User> getListFriends(List<String> listUserIds, String idFacebook) throws Exception {
		JSONArray listUserIdsJSON = JSONParser.listStringsToJSON(listUserIds);
		List<User> list = JSONParser.parseListUsers(ApiHelpers.putContent(SERVER_ADDRESS + APP_BASEURL + "/users/friends?idFacebook="+idFacebook, listUserIdsJSON));
		return list;
	}
	
	public static void registerSite(Site site, String idFacebook)throws Exception{
		JSONObject siteJSON = JSONParser.siteToJSON(site);
		ApiHelpers.putContent(SERVER_ADDRESS + APP_BASEURL+"/sites/register?idFacebook="+idFacebook, siteJSON);
		
	}
	
	public static void removeSite(String idSite, String idFacebook)throws Exception{
		ApiHelpers.getContent(SERVER_ADDRESS + APP_BASEURL+"/sites/remove?idSite="+idSite+"&idFacebook="+idFacebook);
		
	}
	
	public static List<Site> getMySites(String idFacebook) throws Exception {
		List<Site> list = JSONParser.parseListSites(ApiHelpers.getContent(SERVER_ADDRESS + APP_BASEURL + "/sites/mine?idFacebook="+idFacebook));
		return list;
	}
	
	public static List<Site> getSitesNamesList(List<String> listFriendsIds) throws Exception {
		JSONArray listFriendsIdsJSON = JSONParser.listStringsToJSON(listFriendsIds);
		List<Site> list = JSONParser.parseListSites(ApiHelpers.putContent(SERVER_ADDRESS + APP_BASEURL + "/sites/friends", listFriendsIdsJSON));
		return list;
	}
	public static List<Site> getSitesList(List<String> listSiteIds) throws Exception {
		JSONArray listSiteIdsJSON = JSONParser.listStringsToJSON(listSiteIds);
		List<Site> list = JSONParser.parseListSites(ApiHelpers.putContent(SERVER_ADDRESS + APP_BASEURL + "/sites/detailed", listSiteIdsJSON));
		return list;
	}
	

}
