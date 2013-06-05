package dao;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import neo4j.Types.NodeTypes;
import neo4j.Types.RelationTypes;

import org.neo4j.graphdb.Node;

public interface EntityDAO {

	void register(NodeTypes nodeType) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;
	void initThroughNode(Node node) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException;
	void remove() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException;
	void createRelationship(String nameKey, String key, RelationTypes nameRelationship) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException;
	List<? extends Object> getEndEntitiesOfMyRelationships(RelationTypes nameRelationship) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException;
}
