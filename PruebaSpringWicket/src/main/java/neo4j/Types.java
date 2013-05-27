package neo4j;

import org.neo4j.graphdb.RelationshipType;

public class Types {

	public enum NodeTypes implements RelationshipType {
		ROOT, // Para pegar nodos especiales al nodo Raiz,
		NodesReference,
		SitesReference,
		User,
		Site,
	}

	public enum RelationTypes implements RelationshipType {
		Owns, 
		Likes, 
		Knows,
		Comments,
	}
}
