package dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import java.lang.reflect.InvocationTargetException;

import models.Site;
import models.User;
import neo4j.Types.NodeTypes;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.kernel.EmbeddedGraphDatabase;

public class TestUser {

	static EmbeddedGraphDatabase graphDb;
	static Index<Node> nodeIndex;
	
	@Before
	public void prepareTestDatabase(){
		
//		ap<String, String> config = new HashMap<String, String>();
//		config.put( "neostore.nodestore.db.mapped_memory", "10M" );
//		config.put( "string_block_size", "60" );
//		config.put( "array_block_size", "300" );
//		GraphDatabaseService db = new ImpermanentGraphDatabase( config );
		
	    //graphDb = (EmbeddedGraphDatabase) new TestGraphDatabaseFactory().newImpermanentDatabaseBuilder().newGraphDatabase();
		
		graphDb = (EmbeddedGraphDatabase) new GraphDatabaseFactory()
		.newEmbeddedDatabase("C:/neo4j");
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
	
	@After
	public void destroyTestDatabase(){
	    graphDb.shutdown();
	}	
	
	@Test
	public void registerUser(){
		
		User user = new User("1111", "Carlos", "http://www.as.com", 
				"444.45", "222.23", graphDb, nodeIndex);
		try {
			user.register("idFacebook", "1111");
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			System.out.println("fallo1");
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			System.out.println("fallo2");
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			System.out.println("fallo3");
			e.printStackTrace();
		}   
		Node foundNode = nodeIndex.get( "idFacebook","1111").getSingle();
		//System.out.println( "The name of user carlos is " + foundNode.getProperty("name"));  	        	    		
		assertEquals((String) foundNode.getProperty("name"),"Carlos");	 		 			      		  
				
	}
	@Test
	public void updateUser(){
		
		User user = new User("2222", "Maria", "http://www.marca.com", 
				"555.45", "333.23", graphDb, nodeIndex);
		try {
			user.register("idFacebook", "2222");
			user = new User("2222", "Ana", "http://www.mundo.com", 
					"666.45", "444.23", graphDb, nodeIndex);
			user.register("idFacebook", "2222");
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			System.out.println("fallo1");
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			System.out.println("fallo2");
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			System.out.println("fallo3");
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
				"777.45", "555.23", graphDb, nodeIndex);
		try {
			user.register("idFacebook", "3333");			
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			System.out.println("fallo1");
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			System.out.println("fallo2");
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			System.out.println("fallo3");
			e.printStackTrace();
		}				      
		Node foundNode = nodeIndex.get("idFacebook","3333").getSingle();
    	//System.out.println( "The name of site Due Torri is " + foundNode.getProperty("name"));
      	        	    		
    	assertEquals((String) foundNode.getProperty("name"),"Juan");	 
    	
    	User user2 = new User(graphDb, nodeIndex);
		try {
			user2.initThroughNode(foundNode);
			
			assertEquals((String) foundNode.getProperty("name"), user2.getName());	
			
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	private static void registerShutdownHook( final GraphDatabaseService graphDb )
	{
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
}
