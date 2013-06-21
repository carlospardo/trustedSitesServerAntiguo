package dao;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.List;

import models.Site;
import models.User;
import neo4j.Types.NodeTypes;
import neo4j.Types.RelationTypes;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.kernel.impl.util.FileUtils;

public class TestSites {

	static EmbeddedGraphDatabase graphDb;
	static Index<Node> nodeIndex;
	static String pathDb;
	
	@BeforeClass
	public static void prepareTestDatabase(){
		
//		ap<String, String> config = new HashMap<String, String>();
//		config.put( "neostore.nodestore.db.mapped_memory", "10M" );
//		config.put( "string_block_size", "60" );
//		config.put( "array_block_size", "300" );
//		GraphDatabaseService db = new ImpermanentGraphDatabase( config );
		
	    //graphDb = (EmbeddedGraphDatabase) new TestGraphDatabaseFactory().newImpermanentDatabaseBuilder().newGraphDatabase();
		
		pathDb = System.getProperty("user.dir") + "/Data/graph.db"; 
		pathDb = pathDb.replace(File.separator, "/");		
		clearDb();
		graphDb = (EmbeddedGraphDatabase) new GraphDatabaseFactory().newEmbeddedDatabase(pathDb);
		nodeIndex = graphDb.index().forNodes("nodes");
		registerShutdownHook(graphDb);
		Transaction tx = graphDb.beginTx();
		try {

			//Comprobamos si existen los nodos referencia
			Node usersReferenceNode = nodeIndex.get( "title","UsersReference").getSingle();
			if (usersReferenceNode == null){							
				// Creamos los tres nodos
				Node nodoReferenciaUsuarios = graphDb.createNode();
				Node nodoReferenciaSitios = graphDb.createNode();
	
				// Les ponemos titulo
				nodoReferenciaUsuarios.setProperty("title", "UsersReference");
				nodoReferenciaSitios.setProperty("title", "SitesReference");
				
				//Indexamos los nodos
				nodeIndex.add(nodoReferenciaUsuarios, "title", "UsersReference");
				nodeIndex.add(nodoReferenciaSitios, "title", "SitesReference");
				
				// Los relacionamos con el nodo raiz
				graphDb.getReferenceNode().createRelationshipTo(
						nodoReferenciaUsuarios, NodeTypes.NodesReference);
				graphDb.getReferenceNode().createRelationshipTo(
						nodoReferenciaSitios, NodeTypes.SitesReference);
			}
			tx.success();
		} finally {
			tx.finish();
		}		
	}
	
	@AfterClass
	public static void destroyTestDatabase(){
		graphDb.shutdown();		
	}

	@Test
	public void registerSite(){
			
		Site site = new Site("", "Due Torri", "http://", "3333.45", "4442", 
				"Good place in the Bologna Center", "Scarlos", "45");
		site.putConectionInfo(graphDb, nodeIndex);
	
		try {	
			site.register(NodeTypes.Site);
						  
			Node foundNode = nodeIndex.get("idSite",site.getIdSite()).getSingle();	      	        	    		
	    	assertEquals((String) foundNode.getProperty("name"),"Due Torri");	 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
			
	}
	//@Test
	public void updateSite(){
		
		Site site = new Site("", "Piazza Verdi", "http://w.", "1111.45", "2222", 
				"Good place in the Zamboni", "Juan", "7");
		site.putConectionInfo(graphDb, nodeIndex);
		try {
			site.register(NodeTypes.Site);
			site = new Site(site.getIdSite(), "Piazza Verdi2", "http://www.", "2222.78", "3333", 
					"Good place in Zamboni2", "Juan", "7");
			site.putConectionInfo(graphDb, nodeIndex);
			site.register(NodeTypes.Site);
		}catch (Exception e) {
			e.printStackTrace();
		}
			      
		IndexHits<Node> nodes = nodeIndex.get( "idSite",site.getIdSite());
		//System.out.println("nodes.size: " + nodes.size());
    	assertEquals(nodes.size(), 1);	 
    	
    	Node foundNode = nodes.getSingle();
		//System.out.println( "The name of site Piazza Verdi2 is " + foundNode.getProperty("name"));
  	        	    		
		assertEquals((String) foundNode.getProperty("name"),"Piazza Verdi2");	 
		 			      		  	    		
	}
	
	//@Test
	public void  initThroughNode(){
		Site site = new Site("", "Piazza Maggiore", "http://www.as.", "7777.21", "8888", 
				"Good place in Bologna", "Juan", "7");
		site.putConectionInfo(graphDb, nodeIndex);
		try {
			site.register(NodeTypes.Site);			
		}catch (Exception e) {
			e.printStackTrace();
		}
				      
		Node foundNode = nodeIndex.get("idSite",site.getIdSite()).getSingle();
    	//System.out.println( "The name of site Due Torri is " + foundNode.getProperty("name"));
      	        	    		
    	assertEquals((String) foundNode.getProperty("name"),"Piazza Maggiore");	 
    	
    	Site site2 = new Site(graphDb, nodeIndex);

		try {
			site2.initThroughNode(foundNode);
			
			assertEquals((String) foundNode.getProperty("name"), site2.getName());	
			
		} catch (Exception e) {			
			e.printStackTrace();
		}	
	}
	
	//@Test
	public void createRelationship(){
		
		User user = new User("0000", "Pedro", "http://www.000.com", 
				"000.45", "000.23");
		user.putConectionInfo(graphDb, nodeIndex);
		Site site = new Site("", "Due 0", "http://00", "'0000.45", "000", 
				"Good place in the 0", user.getName(), user.getIdFacebook());
		site.putConectionInfo(graphDb, nodeIndex);
		try {
			user.register(NodeTypes.User);
			site.register(NodeTypes.Site);
			site.createRelationship("idFacebook", "0000", RelationTypes.Owns);	
			
			Node foundNode = nodeIndex.get("idSite",site.getIdSite()).getSingle();		
	    	assertEquals(foundNode.hasRelationship(RelationTypes.Owns, Direction.INCOMING), true);	
	    	assertEquals(foundNode.hasRelationship(RelationTypes.Owns, Direction.OUTGOING), false);
	    	
	    	foundNode = nodeIndex.get("idFacebook","0000").getSingle();		
	    	assertEquals(foundNode.hasRelationship(RelationTypes.Owns, Direction.OUTGOING), true);
	    	assertEquals(foundNode.hasRelationship(RelationTypes.Owns, Direction.INCOMING), false);	    	
			
		}catch (Exception e) {	
			e.printStackTrace();
		}		    							
	}
	//@Test
	@SuppressWarnings("unchecked")
	public void getUserSites(){
		
		User user = new User("0009", "Marcos", "http://www.000.com", 
				"000.45", "000.23");
		user.putConectionInfo(graphDb, nodeIndex);
		Site site = new Site("", "Due 01", "http://00", "'0000.45", "000", 
				"Good place in the 0", user.getName(), user.getIdFacebook());
		site.putConectionInfo(graphDb, nodeIndex);
		Site site2 = new Site("", "Due 02", "http://00", "'0000.45", "000", 
				"Good place in the 0", user.getName(), user.getIdFacebook());
		site2.putConectionInfo(graphDb, nodeIndex);
		Site site3 = new Site("", "Due 03", "http://00", "'0000.45", "000", 
				"Good place in the 0", user.getName(), user.getIdFacebook());
		site3.putConectionInfo(graphDb, nodeIndex);
		try {
			user.register(NodeTypes.User);
			site.register(NodeTypes.Site);
			site2.register(NodeTypes.Site);
			site3.register(NodeTypes.Site);
			site.createRelationship("idFacebook", "0009", RelationTypes.Owns);	
			site2.createRelationship("idFacebook", "0009", RelationTypes.Owns);	
			site3.createRelationship("idFacebook", "0009", RelationTypes.Owns);	
			
			List<Site> list = (List<Site>) user.getEndEntitiesOfMyRelationships(RelationTypes.Owns);
			assertEquals(list.size(),3);
						
		}catch (Exception e) {		
			e.printStackTrace();
		}					
	}
	
	//@Test
	public void removeSite(){
		Site site = new Site("", "Piazza Malpighi", "http://www.as.", "7777.21", "8888", 
				"Good place in Bo", "Mario", "3");
		site.putConectionInfo(graphDb, nodeIndex);
		try {
			site.register(NodeTypes.Site);		
			site.remove();
			
			Node foundNode = nodeIndex.get("idSite",site.getIdSite()).getSingle();
	    	//System.out.println( "The name of site Due Torri is " + foundNode.getProperty("name"));
			
	    	assertEquals(foundNode,null);	
		}catch (Exception e) {
			e.printStackTrace();
		}											
	}
	
	//@Test
	public void removeSiteWithRelationship(){
		
		User user = new User("0001", "Pedrito", "http://www.000.com", 
				"000.45", "000.23");
		user.putConectionInfo(graphDb, nodeIndex);
		Site site = new Site("", "Due 6", "http://00", "'0000.45", "000", 
				"Good place in the 0", user.getName(), user.getIdFacebook());
		site.putConectionInfo(graphDb, nodeIndex);
		try {
			user.register(NodeTypes.User);
			site.register(NodeTypes.Site);
			site.createRelationship("idFacebook", "0001", RelationTypes.Owns);	
			site.remove();		
	    	
			Node foundNode = nodeIndex.get("idSite",site.getIdSite()).getSingle();			
	    	assertEquals(foundNode,null);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void registerShutdownHook(final GraphDatabaseService graphDb){
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
