package dao;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.List;

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

public class TestUsers {

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

		
//	    graphDb = (EmbeddedGraphDatabase) new TestGraphDatabaseFactory().newImpermanentDatabaseBuilder().newGraphDatabase();	
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
	public void registerUser(){

		User user = new User("1111", "Carlos", "http://www.as.com", 
				"444.45", "222.23");
		user.putConectionInfo(graphDb, nodeIndex);
		try {
			user.register(NodeTypes.User);
		}catch (Exception e) {
			e.printStackTrace();
		}   
		Node foundNode = nodeIndex.get( "idFacebook","1111").getSingle();
		//System.out.println( "The name of user carlos is " + foundNode.getProperty("name"));  	        	    		
		assertEquals((String) foundNode.getProperty("name"),"Carlos");	 	
	}
	@Test
	public void updateUser(){
		
		User user = new User("2222", "Maria", "http://www.marca.com", 
				"555.45", "333.23");
		user.putConectionInfo(graphDb, nodeIndex);
		try {
			user.register(NodeTypes.User);
			user = new User("2222", "Ana", "http://www.mundo.com", 
					"666.45", "444.23");
			user.putConectionInfo(graphDb, nodeIndex);
			user.register(NodeTypes.User);
		}catch (Exception e) {
			e.printStackTrace();
		}			      		
		IndexHits<Node> nodes = nodeIndex.get( "idFacebook","2222");
    	assertEquals(nodes.size(), 1);	 
    	
    	Node foundNode =nodes.getSingle();
		//System.out.println( "The name of user carlos is " + foundNode.getProperty("name"));
  	        	    		
		assertEquals((String) foundNode.getProperty("name"),"Ana");	 			
	}
	
	@Test
	public void  initThroughNode(){
		User user = new User("3333", "Juan", "http://www.sport.com", 
				"777.45", "555.23");
		user.putConectionInfo(graphDb, nodeIndex);
		try {
			user.register(NodeTypes.User);			
		}catch (Exception e) {		
			e.printStackTrace();
		}				      
		Node foundNode = nodeIndex.get("idFacebook","3333").getSingle();
    	//System.out.println( "The name of site Due Torri is " + foundNode.getProperty("name"));
      	        	    		
    	assertEquals((String) foundNode.getProperty("name"),"Juan");	 
    	
    	User user2 = new User(graphDb, nodeIndex);
		try {
			user2.initThroughNode(foundNode);
			
			assertEquals((String) foundNode.getProperty("name"), user2.getName());	
			
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	@Test
	public void createRelationship(){
		
		User user = new User("4444", "Mario", "http://www.000.com", 
				"000.45", "001.33");
		user.putConectionInfo(graphDb, nodeIndex);
		User user2 = new User("5555", "Paco", "http://www.000.com", 
				"000.45", "002.33");
		user2.putConectionInfo(graphDb, nodeIndex);
		
		try {
			user.register(NodeTypes.User);
			user2.register(NodeTypes.User);
			user.createRelationship("idFacebook", "5555", RelationTypes.Knows);
	    	user2.createRelationship("idFacebook", "4444", RelationTypes.Knows);
	    	
	    	Node foundNode = nodeIndex.get("idFacebook","4444").getSingle();		
	    	assertEquals(foundNode.hasRelationship(RelationTypes.Knows, Direction.BOTH), true);	
	    	
	    	Node foundNode2 = nodeIndex.get("idFacebook","5555").getSingle();		
	    	assertEquals(foundNode2.hasRelationship(RelationTypes.Knows, Direction.BOTH), true);    	
	    	
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void getUserFriends(){
		
		User user = new User("1000", "Ralph", "http://www.000.com", 
				"000.45", "000.23");
		user.putConectionInfo(graphDb, nodeIndex);
		User user1 = new User("1001", "Bart", "http://www.000.com", 
				"000.45", "000.23");
		user1.putConectionInfo(graphDb, nodeIndex);
		User user2 = new User("1002", "Lisa", "http://www.000.com", 
				"000.45", "000.23");
		user2.putConectionInfo(graphDb, nodeIndex);
	
		try {
			user.register(NodeTypes.User);
			user1.register(NodeTypes.User);
			user2.register(NodeTypes.User);
			user.createRelationship("idFacebook", "1001", RelationTypes.Knows);
			user1.createRelationship("idFacebook", "1000", RelationTypes.Knows);
			user.createRelationship("idFacebook", "1002", RelationTypes.Knows);
			user2.createRelationship("idFacebook", "1000", RelationTypes.Knows);
		
			
			List<User> list = (List<User>) user.getEndEntitiesOfMyRelationships(RelationTypes.Knows);
			assertEquals(list.size(),2);
						
		}catch (Exception e) {			
			e.printStackTrace();
		}						
	}
	
	private static void registerShutdownHook( final GraphDatabaseService graphDb ){
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
