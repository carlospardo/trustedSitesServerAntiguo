package dao;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import modelsTest.Site;
import modelsTest.User;

import org.junit.Test;

import utilsTest.ApiHelpers;

public class TestWebServices {
	
	//@Test
	public void registerUser(){
		
		User user = new User("111", "Carlos", "http://www.as2.com", "444.45", "222.23");		
//		User user = new User("544794145", "Dogan", "http://graph.facebook.com/1523497691/picture?type=square", "444.45", "222.23");
//		User user = new User("561940442", "Sandra", "http://graph.facebook.com/561940442/picture?type=square", "444.45", "222.23");
		try {
			ApiHelpers.register(user);
			assertTrue(true);
		} catch (Exception e) {			
			e.printStackTrace();
			assertTrue(false);
		}		
	}
	//@Test
	public void updateFriendsAndGetFriendsList(){
		try {
				
			ArrayList<String> listUserIds = new ArrayList<String>();
			listUserIds.add("000");
			listUserIds.add("20");
			assertTrue(true);
			List<User> listFriends =ApiHelpers.getListFriends(listUserIds, "111");
			assertEquals(listFriends.size(), 1);
			assertEquals(listFriends.get(0).getName(), "Dogan");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertTrue(false);
		}
	}
	@Test
	public void registerSite(){
				
//		Site site = new Site("5", "Due Torri", "http://graph.facebook.com/434323479966340/picture?type=thumbnail", "3333.45", "4442", 
//				"Good place in the Bologna Center", "Carlos", "111");
//		Site site = new Site("10", "Piazza Maggiore", "http://graph.facebook.com/285931851535123/picture?type=thumbnail",
//				"44.493995", "11.343201", 
//				"Good place in the Center", "Dogan", "544794145");
		Site site = new Site("9", "Due Torri", "http://graph.facebook.com/50672883378/picture?type=square",
				"44.494301", "11.346835", 
				"Good place in the Center", "JCarlos Pardo Duran", "1523497691");
		try {		
			ApiHelpers.registerSite(site, "1523497691");
			assertTrue(true);								
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	//@Test
	public void getMySites(){
			
		List<Site> listSites;
		try {
			listSites = ApiHelpers.getMySites("111");
			assertEquals(listSites.size(), 1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	//@Test
	public void getSitesNameList(){
		
		try {			
			List<String> listFriendsIds = new ArrayList<String>();			
			listFriendsIds.add("111");
			
			List<Site> listSites = ApiHelpers.getSitesNamesList(listFriendsIds);
			
			assertEquals(listSites.size(), 1);
			
			assertEquals(listSites.get(0).getNameOwner(),"Carlos");
			assertEquals(listSites.get(0).getInfo(), null);
						
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	//@Test
	public void getSitesList(){
		
		try{
			List<String> listSitesIds = new ArrayList<String>();
			listSitesIds.add("5");
			
			List<Site> listSites = ApiHelpers.getSitesList(listSitesIds);
			
			assertEquals(listSites.size(), 1);
			
			assertEquals(listSites.get(0).getUrlPhoto(), "http://");
			Assert.assertNotNull(listSites.get(0).getInfo());
						
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	//@Test
	public void removeSite(){
		
		Site site = new Site("", "Due Eliminada", "http://", "3333.45", "4442", 
				"Good place in the Bologna Center", "Carlos", "111");		
		try {		
			ApiHelpers.registerSite(site, "111");
			
			List<Site> listSites = ApiHelpers.getMySites("111");
			site = null;
			boolean encontrado=false;
			for (int i=0;i<listSites.size() && !encontrado; i++){
				if (listSites.get(i).getName().equals("Due Eliminada")){
					site = listSites.get(i);
					System.out.println("site.getIdSite(): " + site.getIdSite());
					encontrado=true;
				}
			}	
			assertNotNull(site);
			ApiHelpers.removeSite(site.getIdSite(),"111");
			assertTrue(true);
			
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}	
	}
}
