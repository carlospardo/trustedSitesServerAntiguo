package dao;

import java.lang.reflect.InvocationTargetException;

import org.neo4j.graphdb.Node;

public interface UserDAO {

	void register(String nomClave, Object clave) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException;
	void initThroughNode(Node node) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException;
}
