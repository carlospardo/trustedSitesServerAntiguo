package neo4j;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


import org.neo4j.graphdb.GraphDatabaseService;


public class InicializeDB implements ServletContextListener {

	/**
	 * Tareas a realizar cuando se inicia el servlet.
	 */
	public void contextInitialized(ServletContextEvent event) {

	}

	/**
	 * Tareas a realizar antes de parar el servlet.
	 */
	public void contextDestroyed(ServletContextEvent event) {

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

}
