package dao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utilsTest.ApiHelpers;

import com.google.gson.Gson;

public class Prueba {

	
	static String friendsIds;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		

		List<String> list = getListFriendsIds();
		System.out.println("list.size(): " + list.size());
		setFriendId("1");
		removeFriendId("1");
		List<String> list2 = getListFriendsIds();
		System.out.println("list2.size(): " + list2.size());
		setFriendId("1");
		List<String> list3 = getListFriendsIds();
		System.out.println("list3.size(): " + list3.size());
		
	}

	public static String getFriendsIds() {
		return friendsIds;
	}

	public static void setFriendsIds(String Ids) {
		friendsIds=Ids;
	}
	
	public static List<String> getListFriendsIds(){
		String friendsIds = getFriendsIds();
		List<String> listFriendsIds = new ArrayList<String>();
		if (friendsIds != null){
			String[] s = friendsIds.split(":");
			for (String id : s){
				listFriendsIds.add(id);
			}
		}	
		return listFriendsIds;
	}
	public void setListFriendsIds(List<String> listFriendsIds) {	
		String friendsIds = "";
		for (String id : listFriendsIds){
			friendsIds=friendsIds + id + ":";
		}
		setFriendsIds(friendsIds);
		
	}
	public static void setFriendId(String id) {
		String friendsIds =getFriendsIds();
		if(friendsIds == null){
			friendsIds = "";
		}
		if (!friendsIds.contains(id)){
			friendsIds=friendsIds + id + ":";
			setFriendsIds(friendsIds);
		}
	}
	public static void removeFriendId(String id) {
		String friendsIds = getFriendsIds();
		friendsIds= friendsIds.replace(id+":", "");
		if (friendsIds.equals("")){
			friendsIds = null;
		}
		setFriendsIds(friendsIds);
	}

}
