
package piat.opendatasearch;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;


/* En esta clase se comportará como un hilo */

public class JSONDatasetParser implements Runnable {
	private String fichero;
	private List<String> lConcepts;
	private Map<String, List<Map<String,String>>> mDatasetConcepts;
	private String nombreHilo;
	private int procesarCount;
	
	
	public JSONDatasetParser (String fichero, List<String> lConcepts, Map<String, List<Map<String,String>>> mDatasetConcepts) { 
		this.fichero=fichero;
		this.lConcepts=lConcepts;
		this.mDatasetConcepts=mDatasetConcepts;
		procesarCount=0;
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
			//TODO:
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
			while (jsonReader.hasNext() && !finProcesar) {
				linea = jsonReader.nextName();
				switch (linea) {
					case "@context":
						jsonReader.skipValue();
						break;
					case "@graph":
						procesar_graph(jsonReader, graphs, lConcepts);
						jsonReader.skipValue();
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
		boolean finProcesar=false;
		// TODO:
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
		while (jsonReader.hasNext() && !finProcesar) {
			//  - Consumir el primer "{" del objeto	
			jsonReader.beginObject();
			//  - Procesar un objeto del array invocando al método procesar_un_graph()
			procesar_un_graph(jsonReader, graphs, lConcepts);
			//  - Consumir el último "}" del objeto
			jsonReader.endObject();
			// 	- Ver si se han añadido 5 graph a la lista, para en ese caso poner la variable finProcesar a true
			if(procesarCount==5)
				finProcesar=true;
		}
		//	3) Si se ha llegado al fin del array, consumir el último "]" del array
		jsonReader.endArray();

	    return finProcesar;
		
	}

	/*	procesar_un_graph()
	 * 	Procesa un objeto del array @graph y lo añade a la lista graphs si en el objeto de nombre @type hay un valor que se corresponde con uno de la lista lConcepts
	 */
	
	private void procesar_un_graph(JsonReader jsonReader, List<Map<String, String>> graphs, List<String> lConcepts) throws IOException {
		// TODO:
		//	- Procesar todas las propiedades de un objeto del array @graph, guardándolas en variables temporales
		//	- Una vez procesadas todas las propiedades, ver si la clave @type tiene un valor igual a alguno de los concept de la lista lConcepts. Si es así
		//	  guardar en un mapa Map<String,String> todos los valores de las variables temporales recogidas en el paso anterior y añadir este mapa al mapa graphs
	
		//	1) Procesar todas las propiedades de un objeto del array @graph, guardándolas en variables temporales
		// SIMPLE PROPERTIES
		String id="";
		String type="";
		String link="";
		String title="";
		String description="";

		// MULTIPLE PROPERTIES
		// Location 
		String eventLocation="";
		String area="";
		String start="";
		String end="";
		String latitude="";
		String longitude="";
		
		// Organization
		String organizationName="";
		String accesibility="";


		Map<String,String> mapjson = new HashMap<String,String>();
		while (jsonReader.hasNext()) {
			String nombre = jsonReader.nextName();
			if(nombre.equals("@id")){
				id=jsonReader.nextString();
			} else if(nombre.equals("@type")){
				type=jsonReader.nextString();
			} else if(nombre.equals("link")){
				link=jsonReader.nextString();
			} else if(nombre.equals("title")){
				title=jsonReader.nextString();
			} else if(nombre.equals("dtstart")){
				start = jsonReader.nextString();
			} else if(nombre.equals("dtend")){
				end = jsonReader.nextString();
			} else if(nombre.equals("event-location")){
				eventLocation = jsonReader.nextString();
			} else if(nombre.equals("area")){
				jsonReader.beginObject();
				while (jsonReader.hasNext()) {
					if(jsonReader.nextName().equals("id")){
						area = jsonReader.nextString();
					}
				}
				jsonReader.endObject();
			} else if(nombre.equals("location")){
				jsonReader.beginObject();
				String locationName;
				while (jsonReader.hasNext()) {
					locationName=jsonReader.nextName();
					if(locationName.equals("latitude")){
						latitude = jsonReader.nextString();
					} else if(locationName.equals("longitude")){
						longitude = jsonReader.nextString();
					}
				}
				jsonReader.endObject();
			} else if(nombre.equals("organization")){
				jsonReader.beginObject();
				String organizationNombre;
				while (jsonReader.hasNext()) {
					organizationNombre=jsonReader.nextName();
					if (organizationNombre.equals("organization-name")) {
						organizationName=jsonReader.nextString();
					} else if (organizationNombre.equals("accesibility")) {
						accesibility=jsonReader.nextString();
					}
				}
				jsonReader.endObject();
			} else if(nombre.equals("description")){
				description=jsonReader.nextString();
			}

			//	2) Una vez procesadas todas las propiedades, ver si la clave @type tiene un valor igual a alguno de los concept de la lista lConcepts. Si es así
			//	  guardar en un mapa Map<String,String> todos los valores de las variables temporales recogidas en el paso anterior y añadir este mapa al mapa graphs
			if(lConcepts.contains(type)){
				mapjson.put("@id", id);
				mapjson.put("@type", type);
				mapjson.put("link", link);
				mapjson.put("title", title);
				mapjson.put("eventLocation", eventLocation);
				mapjson.put("area", area);
				mapjson.put("start", start);
				mapjson.put("end", end);
				mapjson.put("latitude", latitude);
				mapjson.put("longitude", longitude);
				mapjson.put("accesibility", accesibility);
				mapjson.put("organizationName", organizationName);
				mapjson.put("description", description);
				graphs.set(procesarCount, mapjson);
				procesarCount++;
			}
		}
	}
}
