package dao;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import neo4j.Types.NodeTypes;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.kernel.EmbeddedGraphDatabase;


public abstract class UserDAOImpl implements UserDAO{
	
	public EmbeddedGraphDatabase graphDb;	
	public Index<Node> nodeIndex;
	
	public void register(String nomClave, Object clave) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		
		Transaction tx = graphDb.beginTx();	
		try {
			
			Node node = nodeIndex.get(nomClave, clave).getSingle();
			boolean update = true;
			if (node == null){				
				node = graphDb.createNode();								
				update = false;
			}
			
			Method[] vectorMetodos = this.getClass().getDeclaredMethods();		
			boolean primero = true;
			for (Method metodo : vectorMetodos){			 
				if (metodo.getName().startsWith("get")){
					//System.out.println(metodo.getName());
					Object valor=metodo.invoke(this, null);
					//System.out.println("valor= " + valor);
					
					String nomAtributo = metodo.getName();
					/*Quitamos el "get" del nombre del metodo*/
					nomAtributo = nomAtributo.substring(3, nomAtributo.length());			
					/*Ponemos la primera letra en minusculas*/
					nomAtributo = nomAtributo.substring(0, 1).toLowerCase() +  nomAtributo.substring(1, nomAtributo.length());
					
					/*Añadimos todos los atributos al nodo*/
					node.setProperty(nomAtributo, valor);	
					
					if(metodo.getName().startsWith("getId")){
						/*Guardamos en el nodeIndex la referencia del nodo creado*/
						//System.out.println("Atributo 00: " + nomAtributo + " valor 00: " + valor);
						nodeIndex.add(node, nomAtributo, valor);		
						primero=false;
					}					
				}			 
			}				
			if (!update){	
				/*Creamos la relacion entre el nodo referencia y el nodo creado*/
				Node usersReferenceNode = nodeIndex.get( "title","UsersReference").getSingle();
			    usersReferenceNode.createRelationshipTo( node, NodeTypes.User);
			}
			
		    tx.success();
		} finally {			  
			tx.finish();			
		}				
	}
	
	public void initThroughNode(Node node) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		
		Method[] vectorMetodos = this.getClass().getDeclaredMethods();		

		for (Method metodo : vectorMetodos){			 
			if (metodo.getName().startsWith("set")){
								
				String nomAtributo = metodo.getName();
				/*Quitamos el "get" del nombre del metodo*/
				nomAtributo = nomAtributo.substring(3, nomAtributo.length());			
				/*Ponemos la primera letra en minusculas*/
				nomAtributo = nomAtributo.substring(0, 1).toLowerCase() +  nomAtributo.substring(1, nomAtributo.length());
											
				/*Llamamos al metodo set*/				
				metodo.invoke(this, node.getProperty(nomAtributo));									
			}			 
		}
	}
}
