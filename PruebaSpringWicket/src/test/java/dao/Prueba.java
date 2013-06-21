package dao;

import java.util.ArrayList;
import java.util.List;

public class Prueba {

	
	static String friendsIds;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		List<String> listSitesIds = new ArrayList<String>();		
		String sitesIds = ":23:567:12:";
		String[] s = sitesIds.split(":");
		for (String id : s){
			System.out.println("id: " + id);
			if(!id.equals("")){
				listSitesIds.add(id);
			}
		}
		System.out.println(listSitesIds);
		
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
