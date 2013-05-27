package dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.Date;

import models.Site;
import models.User;
import neo4j.Types.NodeTypes;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.kernel.EmbeddedGraphDatabase;

public class TestSite {

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
	public void registerSite(){
			
		Site site = new Site("1", "Due Torri", "http://", "3333.45", "4442", 
				"Good place in the Bologna Center", graphDb, nodeIndex);
		try {
			site.register("idSite", "1");
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
	      
		Node foundNode = nodeIndex.get( "idSite","1").getSingle();
    	//System.out.println( "The name of site Due Torri is " + foundNode.getProperty("name"));
      	        	    		
    	assertEquals((String) foundNode.getProperty("name"),"Due Torri");	 
			
	}
	@Test
	public void updateSite(){
		
		Site site = new Site("2", "Piazza Verdi", "http://w.", "1111.45", "2222", 
				"Good place in the Zamboni", graphDb, nodeIndex);
		try {
			site.register("idSite", "2");
			site = new Site("2", "Piazza Verdi2", "http://www.", "2222.78", "3333", 
					"Good place in Zamboni2", graphDb, nodeIndex);
			site.register("idSite", "2");
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
			      
		IndexHits<Node> nodes = nodeIndex.get( "idSite","2");
		//System.out.println("nodes.size: " + nodes.size());
    	assertEquals(nodes.size(), 1);	 
    	
    	Node foundNode = nodes.getSingle();
		//System.out.println( "The name of site Piazza Verdi2 is " + foundNode.getProperty("name"));
  	        	    		
		assertEquals((String) foundNode.getProperty("name"),"Piazza Verdi2");	 
		 			      		  	    		
	}
	
	@Test
	public void  initThroughNode(){
		Site site = new Site("4", "Piazza Maggiore", "http://www.as.", "7777.21", "8888", 
				"Good place in Bologna", graphDb, nodeIndex);
		try {
			site.register("idSite", "4");			
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

				      
		Node foundNode = nodeIndex.get("idSite","4").getSingle();
    	//System.out.println( "The name of site Due Torri is " + foundNode.getProperty("name"));
      	        	    		
    	assertEquals((String) foundNode.getProperty("name"),"Piazza Maggiore");	 
    	
    	Site site2 = new Site(graphDb, nodeIndex);

		try {
			site2.initThroughNode(foundNode);
			
			assertEquals((String) foundNode.getProperty("name"), site2.getName());	
			
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
	
	@Test
	public void removeSite(){
		Site site = new Site("4", "Piazza Maggiore", "http://www.as.", "7777.21", "8888", 
				"Good place in Bologna", graphDb, nodeIndex);
		try {
			site.register("idSite", "4");			
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
			      
		Node foundNode = nodeIndex.get("idSite","4").getSingle();
    	//System.out.println( "The name of site Due Torri is " + foundNode.getProperty("name"));
      	        	    		
    	assertEquals((String) foundNode.getProperty("name"),"Piazza Maggiore");	 
    	
    	Site site2 = new Site(graphDb, nodeIndex);

		try {
			site2.initThroughNode(foundNode);
	    	site2.remove("idSite","4");		
	    	
			foundNode = nodeIndex.get("idSite","4").getSingle();
	    	//System.out.println( "The name of site Due Torri is " + foundNode.getProperty("name"));
			
	    	assertEquals(foundNode,null);	
	    	
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
