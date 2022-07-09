package piat.opendatasearch;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

/**
 * @author Salvador Fernández García-Morales 53823973V
 *
 */
public class P5_XPATH {

	
	/**
	 * Clase principal de la aplicación de extracción de información del 
	 * Portal de Datos Abiertos del Ayuntamiento de Madrid
	 *
	 */

	public static void main(String[] args) throws InterruptedException {
		//TODO this task is done
		// 1) Validar los argumentos recibidos en main()
		
		// Número de argumentos del programa
		final int numArgumentos=4;

		// Verificar nº de argumentos correcto
		if (args.length!=numArgumentos){
			String mensaje="ERROR: Argumentos incorrectos. Debe introducir "+numArgumentos+" argumentos.\n\n";
			if (args.length>0)
				mensaje+="\tHe recibido estos argumentos: "+ Arrays.asList(args).toString()+"\n";
			mostrarUso(mensaje);
			System.exit(1);
		}		

		// Regex pattern: .*\.xml$
		Pattern xmlPattern = Pattern.compile(".*\\.xml$",Pattern.MULTILINE);
		// Regex pattern: ^[\d]{3,4}-[-0-9A-Z]{3,8}
		Pattern codePattern1 = Pattern.compile("^[\\d]{3,4}-[-0-9A-Z]{3,8}",Pattern.MULTILINE);
		// Regex pattern: ^[\d]{3,4}
		Pattern codePattern2 = Pattern.compile("[\\d]{3,4}",Pattern.MULTILINE);
		// Regex pattern: .*\.json$
		Pattern codePattern3 = Pattern.compile(".*\\.json$",Pattern.MULTILINE);

		Matcher m0 = xmlPattern.matcher(args[0]);
		Matcher m1 = codePattern1.matcher(args[1]);
		Matcher m2 = codePattern2.matcher(args[1]);
		Matcher m3 = xmlPattern.matcher(args[2]);
		Matcher m4 = codePattern3.matcher(args[3]);

		if (!m0.matches()){
			System.err.println("ERROR: el primer argumento debe ser una ruta a un archivo XML");
			System.exit(1);
		}
		if (!m1.matches() && !m2.matches()){
			System.err.println("ERROR: el segundo argumento no es un código válido");
			System.exit(1);
		}
		if (!m3.find()){
			System.err.println("ERROR: el tercer argumento debe ser una ruta a un archivo XML");
			System.exit(1);
		}
		if (!m4.find()){
			System.err.println("ERROR: el cuarto argumento debe ser una ruta a un archivo JSON");
			System.exit(1);
		}

		// Validar los permisos de los archivos pasados como argumentos
		File filein = new File(args[0]);
		File fileout = new File(args[2]);
		File filejson = new File(args[3]);
		if(!filein.canRead()){
			System.err.println("ERROR: el archivo " + args[0] + " no tiene permisos de lectura");
			System.exit(1);
		}
		if(!fileout.canWrite()){
			System.err.println("ERROR: el archivo " + args[2] + " no tiene permisos de escritura");
			System.exit(1);
		}
		if(!filejson.canWrite()){
			System.err.println("ERROR: el archivo " + args[3] + " no tiene permisos de escritura");
			System.exit(1);
		}
		
		// 2) Generación del documento XML de salida. El mismo documento que se generaba en la practica 4

		// Instanciar un objeto ManejadorXML pasando como parámetro el código de la categoría recibido en el segundo argumento de main()
		try {
			ManejadorXML manejador = new ManejadorXML(args[1]);
			// BORRAR LUEGO
			XPathProcess.evaluar (args[2]);
			// Instanciar un objeto SAXParser e invocar a su método parse() pasando como parámetro un descriptor de fichero, cuyo nombre se 
			// recibió en el primer argumento de main(), y la instancia del objeto ManejadorXML 
			SAXParserFactory fac = SAXParserFactory.newInstance();
			SAXParser saxp = fac.newSAXParser();
			saxp.parse(filein, manejador);
			
			// Invocar al método getConcepts() del objeto ManejadorXML para obtener un List<String> con las uris de los elementos <concept>
			// cuyo elemento <code> contiene el código de la categoría buscado
			List<String> uris = manejador.getConcepts();
			
			// Invocar al método getLabel() del objeto ManejadorXML para obtener el nombre de la categoría buscada
			@SuppressWarnings("unused")
			String nombreCategoria = manejador.getLabel();

			// Invocar al método getDatasets() del objeto ManejadorXML para obtener un mapa con los datasets de la categoría buscada
			Map<String,HashMap<String,String>> datasets = manejador.getDatasets();
			
			// Invocar al método getDatasetConcepts() de P4_JSON que utiliza la clase JSONDatasetParser para obtener un mapa con los concepts de los datasets
			Map<String, List<Map<String,String>>> mDatasetConcepts = getDatasetConcepts(uris,datasets);

			// Crear el fichero de salida con el nombre recibido en el tercer argumento de main()
			if(fileout.createNewFile()){
				System.out.println("El fichero se ha creado correctamente");	
			} 
			else{
				if(!fileout.exists()){
					System.err.println("No se ha podido crear el fichero");
					System.exit(1);	
				}
			}

			// Volcar al fichero de salida los datos en el formato XML especificado por ResultadosBusquedaP4.xsd
			GenerarXML salida = new GenerarXML();
			String output=salida.generateXML(uris,args[1],datasets,mDatasetConcepts);
			// System.out.println(output);
			BufferedWriter writer =new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileout), "utf-8"));
			writer.write(output);
			writer.close();
			
			// TODO busqueda de información
			// 3) Búsqueda de información y generación del documento de resultados
			

		} catch (SAXException e) {
			System.err.println("No se ha podido crear el fichero");
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			System.err.println("No se ha podido crear el fichero");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("No se ha podido crear el fichero");
			e.printStackTrace();
		} catch (XPathExpressionException e){
			System.err.println("No se ha podido crear el fichero");
			e.printStackTrace();
		}

		System.exit(0);
	}
	

	
	/**
	 * Muestra mensaje de los argumentos esperados por la aplicación.
	 * Deberá invocase en la fase de validación ante la detección de algún fallo
	 *
	 * @param mensaje  Mensaje adicional informativo (null si no se desea)
	 */
	private static void mostrarUso(String mensaje){
		Class<? extends Object> thisClass = new Object(){}.getClass();
		
		if (mensaje != null)
			System.err.println(mensaje+"\n");
		System.err.println(
				"Uso: " + thisClass.getEnclosingClass().getCanonicalName() + " <ficheroCatalogo> <códigoCategoría> <ficheroSalida> <ficheroJSON>\n" +
				"donde:\n"+
				"\t ficheroCatalogo:\t path al fichero XML con el catálogo de datos\n" +
				"\t códigoCategoría:\t código de la categoría de la que se desea obtener datos\n" +
				"\t ficheroSalida:\t\t nombre del fichero XML de salida\n" +
				"\t ficheroJSON:\t\t path al fichero JSON que contendrá el resultado de las búsquedas de la aplicación\n"
				);				
	}		
	
	/** 
	 * Ordena el procesamiento de los archivos json y devuelve al menos 5 resources de cada dataset
	 * @param lConcepts Lista que contiene los id de los concepts
	 * @param mDatasets	Contiene como máximo 5 concept de el dataset id
	 * @return Retorna un mapa con todos los json procesados .Es null si no se procesa ningún dataset.
	 */
	private static Map<String, List<Map<String,String>>> getDatasetConcepts(List<String> lConcepts, Map<String,HashMap<String,String>> mDatasets) throws InterruptedException {
		
		final int numDeNucleos = Runtime.getRuntime().availableProcessors();
		final AtomicInteger numFicherosProcesados = new AtomicInteger(0);
		final ExecutorService ejecutor = Executors.newFixedThreadPool(numDeNucleos);
		Map<String, List<Map<String, String>>> mDatasetConcepts=new HashMap<String, List<Map<String,String>>>();
		int numFicheros = mDatasetConcepts.size();
		for (Map.Entry<String,HashMap<String,String>> theDataset: mDatasets.entrySet()){		
			// Procesar y guardar los resources en el mDatasetConcepts
			ejecutor.execute(new JSONDatasetParser(theDataset.getKey(), lConcepts, mDatasetConcepts));	
		}
		// Cerrar el ejecutor cuando se termine de procesar el último JSON
		ejecutor.shutdown();
			// Cada 10 segundos mostrar cuantos ficheros se han ejecutado y los que quedan
			while (!ejecutor.awaitTermination(10, TimeUnit.SECONDS)) {
				final int terminados=numFicherosProcesados.get();
				System.out.print("\nYa han terminado "+terminados+". Esperando a los "+(numFicheros-terminados)+" que quedan ");
			}
		return mDatasetConcepts;
	}
}  
