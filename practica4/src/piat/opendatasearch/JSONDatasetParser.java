
package piat.opendatasearch;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.google.gson.stream.JsonReader;

/* En esta clase se comportará como un hilo */

public class JSONDatasetParser implements Runnable {
	private String fichero;
	private List<String> lConcepts;
	private Map<String, List<Map<String,String>>> mDatasetConcepts;
	private String nombreHilo;
	private int procesarCount;
	private boolean finProcesar;
	
	
	public JSONDatasetParser (String fichero, List<String> lConcepts, Map<String, List<Map<String,String>>> mDatasetConcepts) { 
		this.fichero=fichero;
		this.lConcepts=lConcepts;
		this.mDatasetConcepts=mDatasetConcepts;
		procesarCount=1;
		//finProcesar=false;
	}

	
	@Override
	public void run (){
		List<Map<String,String>> graphs=new ArrayList<Map<String,String>>();	// Aquí se almacenarán todos los graphs de un dataset cuyo objeto de nombre @type se corresponda con uno de los valores pasados en el la lista lConcepts
		boolean finProcesar=false;	// Para detener el parser si se han agregado a la lista graphs 5 graph
		
		Thread.currentThread().setName("JSON " + fichero);
		nombreHilo="["+Thread.currentThread().getName()+"] ";
	    System.out.println(nombreHilo+"Empezar a descargar de internet el JSON");
	    try {
	    	InputStreamReader inputStream = new InputStreamReader(new URL(fichero).openStream(), "UTF-8");
			//TODO this thask is done
			//	- Crear objeto JsonReader a partir de inputStream
			//  - Consumir el primer "{" del fichero
			//  - Procesar los elementos del fichero JSON, hasta el final de fichero o hasta que finProcesar=true
			//		Si se encuentra el objeto @graph, invocar a procesar_graph()
			//		Descartar el resto de objetos
			//	- Si se ha llegado al fin del fichero, consumir el último "}" del fichero
			//  - Cerrar el objeto JsonReader

			//	1) Crear objeto JsonReader a partir de inputStream
			JsonReader jsonReader = new JsonReader(inputStream);

			//	2) Consumir el primer "{" del fichero
			jsonReader.beginObject();
			String linea;

			//  3) Procesar los elementos del fichero JSON, hasta el final de fichero o hasta que finProcesar=true
			//		Si se encuentra el objeto @graph, invocar a procesar_graph()
			//		Descartar el resto de objetos
			while (jsonReader.hasNext() || !finProcesar) {
				linea = jsonReader.nextName();
				switch (linea) {
					case "@context":
						jsonReader.skipValue();
						break;
					case "@graph":
						finProcesar=procesar_graph(jsonReader, graphs, lConcepts);
						if (finProcesar==false)
							System.out.println("Error: No se ha podido procesar el fichero \""+fichero+"\"");
						break;
					default:
						jsonReader.skipValue();
						break;
				}
			}
			//	- Si se ha llegado al fin del fichero, consumir el último "}" del fichero
			jsonReader.endObject();
			//	5) Cerrar el objeto JsonReader
			jsonReader.close();

			inputStream.close();
		} catch (FileNotFoundException e) {
			System.out.println(nombreHilo+"El fichero no existe. Ignorándolo");
		} catch (IOException e) {
			System.out.println(nombreHilo+"Hubo un problema al abrir el fichero. Ignorándolo" + e);
		}
	    mDatasetConcepts.put(fichero, graphs); 	// Se añaden al Mapa de concepts de los Datasets
	    
	}

	/* 	procesar_graph()
	 * 	Procesa el array @graph
	 *  Devuelve true si ya se han añadido 5 objetos a la lista graphs
	 */
	private boolean procesar_graph(JsonReader jsonReader, List<Map<String, String>> graphs, List<String> lConcepts) throws IOException {
		finProcesar=false;
		// TODO this task is done
		//	- Consumir el primer "[" del array @graph
		//  - Procesar todos los objetos del array, hasta el final de fichero o hasta que finProcesar=true
		//  	- Consumir el primer "{" del objeto
		//  	- Procesar un objeto del array invocando al método procesar_un_graph()
		//  	- Consumir el último "}" del objeto
		// 		- Ver si se han añadido 5 graph a la lista, para en ese caso poner la variable finProcesar a true
		//	- Si se ha llegado al fin del array, consumir el último "]" del array
		
		//	1) Consumir el primer "[" del array @graph
		jsonReader.beginArray();

		//  2) Procesar todos los objetos del array, hasta el final de fichero o hasta que finProcesar=true
		while (jsonReader.hasNext()) {
			if(!finProcesar){
				//  - Consumir el primer "{" del objeto	
				jsonReader.beginObject();
				//  - Procesar un objeto del array invocando al método procesar_un_graph()
				procesar_un_graph(jsonReader, graphs, lConcepts);
				//  - Consumir el último "}" del objeto
				jsonReader.endObject();
				// 	- Ver si se han añadido 5 graph a la lista, para en ese caso poner la variable finProcesar a true
				if(procesarCount==6){
					finProcesar=true;
				}
			} else {
				jsonReader.skipValue();
			}
				
		}

		//	3) Si se ha llegado al fin del array, consumir el último "]" del array
		jsonReader.endArray();

	    return finProcesar;
		
	}

	/*	procesar_un_graph()
	 * 	Procesa un objeto del array @graph y lo añade a la lista graphs si en el objeto de nombre @type hay un valor que se corresponde con uno de la lista lConcepts
	 */
	
	private void procesar_un_graph(JsonReader jsonReader, List<Map<String, String>> graphs, List<String> lConcepts) throws IOException {
		// TODO this task is done
		//	- Procesar todas las propiedades de un objeto del array @graph, guardándolas en variables temporales
		//	- Una vez procesadas todas las propiedades, ver si la clave @type tiene un valor igual a alguno de los concept de la lista lConcepts. Si es así
		//	  guardar en un mapa Map<String,String> todos los valores de las variables temporales recogidas en el paso anterior y añadir este mapa al mapa graphs
	
		//	1) Procesar todas las propiedades de un objeto del array @graph, guardándolas en variables temporales
		Hashtable<String, String> properties = new Hashtable<String, String>();
		Map<String,String> mapjson = new HashMap<String,String>();
		String nombre;
		while (jsonReader.hasNext()) {
			if(procesarCount<6){
				nombre = jsonReader.nextName();
				
				//************** PROCESAR PROPIEDADES **************/
				
				// propiedad @type
				if(nombre.equals("@type")){
					properties.put("type", jsonReader.nextString());
				}
				// propiedad link
				else if(nombre.equals("link")){
					properties.put("link", jsonReader.nextString());
				}
				// propiedad title
				else if(nombre.equals("title")){
					properties.put("title", jsonReader.nextString());
				}
				// propiedad dtstart
				else if(nombre.equals("dtstart")){
					properties.put("start", jsonReader.nextString());
				}
				// propiedad dtend
				else if(nombre.equals("dtend")){
					properties.put("end", jsonReader.nextString());
				}
				// propiedad event-location
				else if(nombre.equals("event-location")){
					properties.put("eventLocation", jsonReader.nextString());
				}
				// propiedad address -> area
				else if(nombre.equals("address")){
					jsonReader.beginObject();
					String addressName;
					while (jsonReader.hasNext()) {
						addressName=jsonReader.nextName();
						if(addressName.equals("area")){
							jsonReader.beginObject();
							String areaName;
							while (jsonReader.hasNext()) {
								areaName=jsonReader.nextName();
								if(areaName.equals("@id")){
									properties.put("area", jsonReader.nextString());
								} else{
									jsonReader.skipValue();
								}
							}
							
							jsonReader.endObject();
						} else{
							jsonReader.skipValue();
						}
					}
					jsonReader.endObject();
				}
				// propiedad location
				else if(nombre.equals("location")){
					jsonReader.beginObject();
					String locationName;
					while (jsonReader.hasNext()) {
						locationName=jsonReader.nextName();
						if(locationName.equals("latitude")){
							properties.put("latitude", jsonReader.nextString());
						} else if(locationName.equals("longitude")){
							properties.put("longitude", jsonReader.nextString());
						} else {
							jsonReader.skipValue();
						}
					}
					jsonReader.endObject();
				}
				// propiedad organization
				else if(nombre.equals("organization")){
					jsonReader.beginObject();
					String organizationNombre;
					while (jsonReader.hasNext()) {
						organizationNombre=jsonReader.nextName();
						if (organizationNombre.equals("organization-name")) {
							properties.put("organizationName", jsonReader.nextString());
						} else if (organizationNombre.equals("accesibility")) {
							properties.put("accesibility", jsonReader.nextString());
						}
					}
					jsonReader.endObject();
				}
				// propiedad description
				else if(nombre.equals("description")){
					properties.put("description", jsonReader.nextString());
				}
				// propiedad relation
				else if(nombre.equals("relation")){
					jsonReader.beginObject();
					String relationNombre;
					while (jsonReader.hasNext()) {
						relationNombre=jsonReader.nextName();
						if (relationNombre.equals("@id")) {
							//coger la propiedad "relation -> @id" cuando no hay "link"	
							if(properties.get("link")==null) {
								properties.put("link", jsonReader.nextString());
							} else {
								jsonReader.skipValue();
							}
						}
					}
					jsonReader.endObject();
				} 
				// resto de propiedades
				else{
					jsonReader.skipValue();	
				}	

				//************** GUARDAR PROPIEDADES **************/

				//	2) Una vez procesadas todas las propiedades, ver si la clave @type tiene un valor igual a alguno de los concept de la lista lConcepts. Si es así
				//	  guardar en un mapa Map<String,String> todos los valores de las variables temporales recogidas en el paso anterior y añadir este mapa al mapa graphs
				if (properties.get("type")!=null && properties.get("accesibility")!=null){
					if(lConcepts.contains(properties.get("type"))&&!properties.get("accesibility").equals("")){
					mapjson.put("@type", properties.get("type"));
					mapjson.put("link", properties.get("link"));
					mapjson.put("title", properties.get("title"));
					mapjson.put("eventLocation", properties.get("eventLocation"));
					mapjson.put("area", properties.get("area"));
					mapjson.put("start", properties.get("start"));
					mapjson.put("end", properties.get("end"));
					mapjson.put("latitude", properties.get("latitude"));
					mapjson.put("longitude", properties.get("longitude"));
					mapjson.put("accesibility", properties.get("accesibility"));
					mapjson.put("organizationName", properties.get("organizationName"));
					mapjson.put("description", properties.get("description"));
					graphs.add(mapjson);

					System.out.println(properties.get("area"));
					//eliminar la propiedad mas baja para evitar que vuelva a entrar
					properties.put("accesibility", "");
					//incrementar el contador de las propiedades procesadas
					procesarCount++;
					}
				}		
			} else {
				jsonReader.skipValue();
			}
		}
	}
}
