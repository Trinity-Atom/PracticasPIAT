package piat.opendatasearch;

import java.io.File;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Salvador Fernández García-Morales 53823973V
 *
 */

public class P3_SAX {

	/**
	 * Clase principal de la aplicación de extracción de información del 
	 * Portal de Datos Abiertos del Ayuntamiento de Madrid
	 *
	 */

	public static void main(String[] args) {
		
		// Verificar nº de argumentos correcto
		if (args.length!=3){
			String mensaje="ERROR: Argumentos incorrectos.";
			if (args.length>0)
				mensaje+=" He recibido estos argumentos: "+ Arrays.asList(args).toString()+"\n";
			mostrarUso(mensaje);
			System.exit(1);
		}		
		
		// TODO
		/* 
		 * (DONE) Validar los argumentos recibidos en main()
		 * Instanciar un objeto ManejadorXML pasando como parámetro el código de la categoría recibido en el segundo argumento de main()
		 * Instanciar un objeto SAXParser e invocar a su método parse() pasando como parámetro un descriptor de fichero, cuyo nombre se recibió en el primer argumento de main(), y la instancia del objeto ManejadorXML 
		 * Invocar al método getConcepts() del objeto ManejadorXML para obtener un List<String> con las uris de los elementos <concept> cuyo elemento <code> contiene el código de la categoría buscado
		 * Invocar al método getLabel() del objeto ManejadorXML para obtener el nombre de la categoría buscada
		 * Invocar al método getDatasets() del objeto ManejadorXML para obtener un mapa con los datasets de la categoría buscada 
		 * Crear el fichero de salida con el nombre recibido en el tercer argumento de main()
		 * Volcar al fichero de salida los datos en el formato XML especificado por ResultadosBusquedaP3.xsd
		 */
		
		// Validar los argumentos recibidos en main()
		
		// TESTED
		// Regex pattern: .*\.xml$
		Pattern xmlPattern = Pattern.compile(".*\\.xml$",Pattern.MULTILINE);
		// Regex pattern: ^[\d]{3,4}-[-0-9A-Z]{3,8}
		Pattern codePattern1 = Pattern.compile("^[\\d]{3,4}-[-0-9A-Z]{3,8}",Pattern.MULTILINE);
		// Regex pattern: ^[\d]{3,4}
		Pattern codePattern2 = Pattern.compile("[\\d]{3,4}",Pattern.MULTILINE);

		Matcher m0 = xmlPattern.matcher(args[0]);
		Matcher m1 = codePattern1.matcher(args[1]);
		Matcher m2 = codePattern2.matcher(args[1]);
		Matcher m3 = xmlPattern.matcher(args[2]);
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
		// NOT TESTED
		File filein = new File(args[0]);
		File fileout = new File(args[2]);
		if(!filein.canRead()){
			System.err.println("ERROR: el archivo " + args[0] + " no tiene permisos de lectura");
			System.exit(1);
		}
		if(!fileout.canWrite()){
			System.err.println("ERROR: el archivo " + args[2] + " no tiene permisos de escritura");
			System.exit(1);
		}

		
		// Instanciar un objeto ManejadorXML pasando como parámetro el código de la categoría recibido en el segundo argumento de main()
		

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
				"Uso: " + thisClass.getEnclosingClass().getCanonicalName() + " <ficheroCatalogo> <códigoCategoría> <ficheroSalida>\n" +
				"donde:\n"+
				"\t ficheroCatalogo:\t path al fichero XML con el catálogo de datos\n" +
				"\t códigoCategoría:\t código de la categoría de la que se desea obtener datos\n" +
				"\t ficheroSalida:\t\t nombre del fichero XML de salida\n"	
				);				
	}		

}