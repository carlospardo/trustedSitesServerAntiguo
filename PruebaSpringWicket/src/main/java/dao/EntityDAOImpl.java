package dao;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import models.Site;
import models.User;
import neo4j.Types.NodeTypes;
import neo4j.Types.RelationTypes;

import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.kernel.EmbeddedGraphDatabase;

@JsonIgnoreProperties({ "fechaCreacion","fechaModificacion","fechaFinalizacion"})
public abstract class EntityDAOImpl implements EntityDAO {

	public EmbeddedGraphDatabase graphDb;	
	public Index<Node> nodeIndex;
	
	protected final static Logger log = Logger.getLogger(EntityDAOImpl.class.getName());
	
	public void register(NodeTypes nodeType) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
		
		Transaction tx = graphDb.beginTx();	
		try {
			Method method = this.getIdMethod();
			String nameAttribute = this.getNameAttribute(method);
			Node node = nodeIndex.get(nameAttribute, method.invoke(this, null)).getSingle();		
			boolean update = true;
			if (node == null){				
				node = graphDb.createNode();
				update = false;	
				if(nodeType.equals(NodeTypes.Site)){
					log.info(nodeType + " with " + nameAttribute + ": " + node.getId() + " create");
				}else{
					log.info(nodeType + " with " + nameAttribute + ": " + method.invoke(this, null) + " create");
				}
			}else{
				log.info(nodeType + " with " + nameAttribute + ": " + method.invoke(this, null) + " update");
			}
			
			Method[] vectorMetodos = this.getClass().getDeclaredMethods();		
			for (Method metodo : vectorMetodos){			 
				if (metodo.getName().startsWith("get")){
					
					//System.out.println(metodo.getName());
					Object valor=metodo.invoke(this, null);
					//System.out.println("valor= " + valor);
					
					nameAttribute = this.getNameAttribute(metodo);
					
					if(metodo.getName().startsWith("getId")){
						/*Si es un site, su clave primaria sera el 
						 * identificador del nodo para que sea unica*/
						if(nodeType.equals(NodeTypes.Site)){
							valor = (Long) node.getId();
							valor = valor.toString();
							this.getClass().getDeclaredMethod("setIdSite", String.class).invoke(this, valor);
						}
						/*Guardamos en el nodeIndex la referencia del nodo creado*/
						nodeIndex.add(node, nameAttribute, valor);					
					}
										
					/*Añadimos todos los atributos al nodo*/
					node.setProperty(nameAttribute, valor);	
														
				}			 
			}				
			if (!update){	
				/*Creamos la relacion entre el nodo referencia y el nodo creado*/
				if(nodeType.equals(NodeTypes.Site)){
					Node usersReferenceNode = nodeIndex.get("title","SitesReference").getSingle();
					usersReferenceNode.createRelationshipTo(node, NodeTypes.Site);
				}else if (nodeType.equals(NodeTypes.User)){
					Node usersReferenceNode = nodeIndex.get( "title","UsersReference").getSingle();
				    usersReferenceNode.createRelationshipTo( node, NodeTypes.User);
				}
				
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
	
	Method getIdMethod(){
		
		Method[] vectorMetodos = this.getClass().getDeclaredMethods();		
		
		for (Method metodo : vectorMetodos){			 
			if (metodo.getName().startsWith("getId"))
				return metodo;
		}
		return null;
		
	}
	
	String getNameAttribute(Method method){
		
		String nameAttribute = method.getName();
		/*Quitamos el "get" del nombre del metodo*/
		nameAttribute = nameAttribute.substring(3, nameAttribute.length());			
		/*Ponemos la primera letra en minusculas*/
		nameAttribute = nameAttribute.substring(0, 1).toLowerCase() + nameAttribute.substring(1, nameAttribute.length());
		
		return nameAttribute;
		
	}
	
	public void remove() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{		
		Transaction tx = graphDb.beginTx();	
		try {	
			Method method = this.getIdMethod();
			String nameAttribute = this.getNameAttribute(method);
			Node node = nodeIndex.get(nameAttribute, method.invoke(this, null)).getSingle();	
			
	    	while (node.hasRelationship()){
	    		node.getRelationships().iterator().next().delete();
	    	}
	    	node.delete();
			nodeIndex.remove(node, "identifier", method.invoke(this, null));			
		    tx.success();
		} finally {			  
			tx.finish();			
		}	
	}
	
	public void createRelationship(String nameKey, String key, RelationTypes nameRelationship) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		Transaction tx = graphDb.beginTx();	
		try {	
			Method method = this.getIdMethod();
			String nameAttribute = this.getNameAttribute(method);
			Node myNode = nodeIndex.get(nameAttribute, method.invoke(this, null)).getSingle();	
				
			Node externNode = nodeIndex.get(nameKey, key).getSingle();
			
			Iterable<Relationship> relations = externNode.getRelationships(nameRelationship, Direction.OUTGOING);
			boolean encontrado=false;
			if (relations!= null){				
				while(relations.iterator().hasNext() && !encontrado){
					if (relations.iterator().next().getEndNode().getProperty(nameAttribute).equals(method.invoke(this, null))){		
						encontrado=true;
					}
				}					
			}
			if (!encontrado){
				externNode.createRelationshipTo(myNode, nameRelationship);
			}
							
		    tx.success();
		} finally {			  
			tx.finish();			
		}	
	}
	
	public List<? extends Object> getEndEntitiesOfMyRelationships(RelationTypes nameRelationship) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
				
		Method method = this.getIdMethod();
		String nameAttribute = this.getNameAttribute(method);
		Node node = nodeIndex.get(nameAttribute, method.invoke(this, null)).getSingle();
		List<Object> listEntities = new ArrayList<Object>();
		
		Iterable<Relationship> relations = node.getRelationships(nameRelationship, Direction.OUTGOING);
		Node node2;
		if (relations!= null){				
			while(relations.iterator().hasNext()){
				node2 = relations.iterator().next().getEndNode();
				
				if(nameRelationship.equals(RelationTypes.Owns)){
					Site site = new Site(this.graphDb, this.nodeIndex);
					site.initThroughNode(node2);
					listEntities.add(site);
				}
				else if(nameRelationship.equals(RelationTypes.Knows)){
					User user = new User(this.graphDb, this.nodeIndex);
					user.initThroughNode(node2);
					listEntities.add(user);
				}
			}					
		}
		
		return listEntities;		
		
	}
}
