package neo4j;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


import neo4j.Types.NodeTypes;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.kernel.impl.util.FileUtils;


public class InitializeDB implements ServletContextListener {

	private static EmbeddedGraphDatabase graphDb;
	private static Index<Node> nodeIndex;
	static String pathDb;
	
	/**
	 * Tareas a realizar cuando se inicia el servlet.
	 */
	public void contextInitialized(ServletContextEvent event) {

		pathDb = System.getProperty("user.dir") + "/Data/graph.db"; 
		pathDb = pathDb.replace(File.separator, "/");
		//clearDb();
		graphDb = (EmbeddedGraphDatabase) new GraphDatabaseFactory().newEmbeddedDatabase(pathDb);
		nodeIndex = graphDb.index().forNodes("nodes");
		registerShutdownHook(graphDb);
		Transaction tx = graphDb.beginTx();
		try {
			
			System.out.println("BD iniciada!");
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

	/**
	 * Tareas a realizar antes de parar el servlet.
	 */
	public void contextDestroyed(ServletContextEvent event) {
		
		graphDb.shutdown();
		System.out.println("BD cerrada!");
	}

	private static void registerShutdownHook(final GraphDatabaseService graphDb) {
		// Registers a shutdown hook for the Neo4j instance so that it
		// shuts down nicely when the VM exits (even if you "Ctrl-C" the
		// running example before it's completed)
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				graphDb.shutdown();
			}
		});
	}	
	@SuppressWarnings("unused")
	private static void clearDb(){
        try{
            FileUtils.deleteRecursively(new File(pathDb));
        }
        catch ( IOException e ){
            throw new RuntimeException( e );
        }
    }

	public static EmbeddedGraphDatabase getGraphDb() {
		return graphDb;
	}

	public static Index<Node> getNodeIndex() {
		return nodeIndex;
	}

	public static void setGraphDb(EmbeddedGraphDatabase graphDb) {
		InitializeDB.graphDb = graphDb;
	}

	public static void setNodeIndex(Index<Node> nodeIndex) {
		InitializeDB.nodeIndex = nodeIndex;
	}

}
