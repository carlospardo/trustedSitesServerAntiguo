package dao;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import models.Site;
import models.User;
import neo4j.InitializeDB;
import neo4j.Types.NodeTypes;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.kernel.impl.util.FileUtils;

import services.SiteServices;
import services.UserServices;

public class TestServices {

	static EmbeddedGraphDatabase graphDb;
	static Index<Node> nodeIndex;
	static String pathDb;
	
	@BeforeClass
	public static void prepareTestDatabase(){
		
		//graphDb = (EmbeddedGraphDatabase) new TestGraphDatabaseFactory().newImpermanentDatabaseBuilder().newGraphDatabase();
				  
		pathDb = System.getProperty("user.dir") + "/Data/graph.db"; 
		pathDb = pathDb.replace(File.separator, "/");
		clearDb();
		//pathDb = "D:/Mis Documentos/cole carlos/UNIVERSIDAD/Escenario de Trabajo/eclipse/Data/graph.db";
		graphDb = (EmbeddedGraphDatabase) new GraphDatabaseFactory().newEmbeddedDatabase(pathDb);
		nodeIndex = graphDb.index().forNodes("nodes");
		registerShutdownHook(graphDb);
		Transaction tx = graphDb.beginTx();
		try {
			//Comprobamos si existen los nodos referencia
			Node usersReferenceNode = nodeIndex.get( "title","UsersReference").getSingle();
			if (usersReferenceNode == null){							
				// Creamos los tres nodos
				Node NodoReferenciaUsuarios = graphDb.createNode();
				Node NodoReferenciaSitios = graphDb.createNode();
	
				// Les ponemos titulo
				NodoReferenciaUsuarios.setProperty("title", "UsersReference");
				NodoReferenciaSitios.setProperty("title", "SitesReference");
				
				//Indexamos los nodos
				nodeIndex.add(NodoReferenciaUsuarios, "title", "UsersReference");
				nodeIndex.add(NodoReferenciaSitios, "title", "SitesReference");
				
				// Los relacionamos con el nodo raiz
				graphDb.getReferenceNode().createRelationshipTo(
						NodoReferenciaUsuarios, NodeTypes.NodesReference);
				graphDb.getReferenceNode().createRelationshipTo(
						NodoReferenciaSitios, NodeTypes.SitesReference);
			}
			InitializeDB.setGraphDb(graphDb);
			InitializeDB.setNodeIndex(nodeIndex);
			tx.success();
		} finally {
			tx.finish();
		}	
	}
	
	@AfterClass
	public static void destroyTestDatabase(){
		graphDb.shutdown();		
	}
	
	//@Test
	public void registerUser(){
		
		User user = new User("11", "Fran", "http://www.as.com", 
				"444.45", "222.23");
		
		try {
			UserServices.registerUser(user);
			
			Node foundNode = nodeIndex.get( "idFacebook","11").getSingle();
			
			assertEquals((String) foundNode.getProperty("name"),"Fran");	
			
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	//@Test
	public void updateFriendsAndGetFriendsList(){
		User user = new User("22", "Fran22", "http://www.as.com", 
				"444.45", "222.23");
		try {
			UserServices.registerUser(user);
			user = new User("23", "Fran23", "http://www.as.com", 
					"444.45", "222.23");
			UserServices.registerUser(user);
			
			
			user = new User("24", "Fran24", "http://www.as.com", 
					"444.45", "222.23");
			UserServices.registerUser(user);
			
			List<String> listFriends = new ArrayList<String>();
			listFriends.add("23");
			listFriends.add("24");
			UserServices.updateFriends("22", listFriends);
			
			List<User> finalList = UserServices.getFriendsList("22");
			
			assertEquals(finalList.size(), 2);
			
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//@Test
	public void updateManualFriends(){
		User user = new User("1523497691", "JCarlos Pardo Duran",
				"http://graph.facebook.com/1523497691/picture?type=square", 
				"444.45", "222.23");
		try {
			UserServices.registerUser(user);
			User user2 = new User("561940442", "Sandra", "http://graph.facebook.com/1523497691/picture?type=square", 
					"444.45", "222.23");
			UserServices.registerUser(user2);
						
			List<String> listFriends = new ArrayList<String>();
			listFriends.add(user2.getIdFacebook());
			
			UserServices.updateFriends(user.getIdFacebook(), listFriends);
			
			List<User> finalList = UserServices.getFriendsList(user.getIdFacebook());
			
			assertEquals(finalList.size(), 1);
			
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//@Test
	public void registerSiteAndGetMySites(){
		
		User user = new User("33", "Fran33", "http://www.as.com", 
				"444.45", "222.23");
		
		Site site = new Site("", "Due Fran33", "http://", "3333.45", "4442", 
				"Good place in the Bologna Center", user.getName(), user.getIdFacebook());
		
		try {
			
			UserServices.registerUser(user);
			
			SiteServices.registerSite(site, user.getIdFacebook());
						
			Node foundNode = nodeIndex.get("idSite",site.getIdSite()).getSingle();
			assertEquals((String) foundNode.getProperty("name"), site.getName());	
						
			
			List<Site> listSites = SiteServices.getMySites(user.getIdFacebook());
			assertEquals(listSites.size(), 1);
		
			
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//@Test
	public void updateSite(){
		
		User user = new User("44", "Fran44", "http://www.as.com", 
				"444.45", "222.23");
		
		Site site = new Site("", "Due Fran44", "http://", "3333.45", "4442", 
				"Good place in the Bologna Center", user.getName(), user.getIdFacebook());
		
		try {
			
			UserServices.registerUser(user);			
			SiteServices.registerSite(site, user.getIdFacebook());
			
			String oldId = site.getIdSite();
			site = new Site(oldId, "Due Fran45", "http://", "3333.45", "4442", 
					"Good place in the Bologna Center", user.getName(), user.getIdFacebook());
			SiteServices.registerSite(site, user.getIdFacebook());
			
			Node foundNode = nodeIndex.get("idSite",site.getIdSite()).getSingle();
			
			assertEquals((String) foundNode.getProperty("name"), site.getName());	
			
			assertEquals(oldId, site.getIdSite());
			
						
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//@Test
	public void removeSite(){
		
		User user = new User("55", "Fran55", "http://www.as.com", 
				"444.45", "222.23");
		
		Site site = new Site("", "Due Fran55", "http://", "3333.45", "4442", 
				"Good place in the Bologna Center", user.getName(), user.getIdFacebook());
		
		try {
			
			UserServices.registerUser(user);			
			SiteServices.registerSite(site, user.getIdFacebook());
												
			SiteServices.removeSite(site.getIdSite(), user.getIdFacebook());
			
			Node foundNode = nodeIndex.get("idSite",site.getIdSite()).getSingle();
			
			assertEquals(foundNode, null);	
												
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	//@Test
	public void getSitesNameList(){
		User user = new User("66", "Fran66", "http://www.as.com", 
				"444.45", "222.23");
		
		Site site = new Site("", "Due Fran66", "foto", "3333.45", "4442", 
				"Good place in the Bologna Center", user.getName(), user.getIdFacebook());
		
		User user2 = new User("67", "Fran67", "http://www.as.com", 
				"444.45", "222.23");
		
		Site site2 = new Site("", "Due Fran67", "foto", "3333.45", "4442", 
				"Good place in the Bologna Center", user2.getName(), user2.getIdFacebook());
		
		try {
			
			UserServices.registerUser(user);			
			SiteServices.registerSite(site, user.getIdFacebook());
			
			UserServices.registerUser(user2);			
			SiteServices.registerSite(site2, user2.getIdFacebook());
											
			List<String> listFriendsIds = new ArrayList<String>();
			listFriendsIds.add(user.getIdFacebook());
			listFriendsIds.add(user2.getIdFacebook());
			
			List<Site> listSites = SiteServices.getSitesNamesList(listFriendsIds);
			
			assertEquals(listSites.size(), 2);
			
			assertEquals(listSites.get(0).getUrlPhoto(), "foto");
			assertEquals(listSites.get(0).getInfo(), null);
						
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	@Test
	public void getSitesList(){
		User user = new User("77", "Fran77", "http://www.as.com", 
				"444.45", "222.23");
		
		Site site = new Site("", "Due Fran77", "http", "3333.45", "4442", 
				"Good place in the Bologna Center", user.getName(), user.getIdFacebook());
		
		User user2 = new User("78", "Fran78", "http://www.as.com", 
				"444.45", "222.23");
		
		Site site2 = new Site("", "Due Fran78", "http", "3333.45", "4442", 
				"Good place in the Bologna Center", user2.getName(), user2.getIdFacebook());
		
		try {
			
			UserServices.registerUser(user);			
			SiteServices.registerSite(site, user.getIdFacebook());
			
			UserServices.registerUser(user2);			
			SiteServices.registerSite(site2, user2.getIdFacebook());
											
			List<String> listSitesIds = new ArrayList<String>();
			listSitesIds.add(site.getIdSite());
			listSitesIds.add(site2.getIdSite());
			
			List<Site> listSites = SiteServices.getSitesList(listSitesIds);
			
			assertEquals(listSites.size(), 2);
			
			assertEquals(listSites.get(0).getUrlPhoto(), "http");
			Assert.assertNotNull(listSites.get(0).getInfo());
						
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


//	private static void openConnection(){
//		
//		String pathDb = System.getProperty("user.dir") + "/Data/graph.db"; 
//		pathDb = pathDb.replace(File.separator, "/");
//		graphDb = (EmbeddedGraphDatabase) new GraphDatabaseFactory().newEmbeddedDatabase(pathDb);
//		nodeIndex = graphDb.index().forNodes("nodes");
//		registerShutdownHook(graphDb);
//	}
//	private static void closeConnection(){
//		
//		graphDb.shutdown();
//	}
	
	private static void registerShutdownHook( final GraphDatabaseService graphDb){
	    // Registers a shutdown hook for the Neo4j instance so that it
	    // shuts down nicely when the VM exits (even if you "Ctrl-C" the
	    // running example before it's completed)
	    Runtime.getRuntime().addShutdownHook( new Thread()
	    {
	        @Override
	        public void run()
	        {
	            graphDb.shutdown();
	        }
	    } );
	}
	private static void clearDb(){
        try{
            FileUtils.deleteRecursively(new File(pathDb));
        }
        catch ( IOException e ){
            throw new RuntimeException( e );
        }
    }
}
