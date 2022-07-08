package piat.opendatasearch;

import java.io.IOException;
import java.util.List;

import javax.xml.xpath.XPathExpressionException;



public class XPathProcess{

	/**
	 * Método que se encarga de evaluar las expresiones xpath sobre el fichero XML generado en la práctica 4
	 * @return
	 *  - Una lista con la propiedad resultante de evaluar cada expresion xpath
	 * @throws IOException

	 * @throws ParserConfigurationException 
	 */


	public static List<Propiedad> evaluar (String ficheroXML) throws IOException, XPathExpressionException {
		// TODO: Realizar las 4 consultas xpath al documento XML de entrada qeu se indican en el enunciado en el apartado "3.2 Búsqueda de información y generación del documento de resultados."
		// Cada consulta devolverá una información que se añadirá a la una colección List <Propiedad>
		// Una consulta puede devolver una propiedad o varias



		
	}
	/**
	 * Esta clase interna define una propiedad equivalente a "nombre":"valor" en JSON
	 */
	public static class Propiedad {
		public final String nombre;
		public final String valor;

		public Propiedad (String nombre, String valor){
			this.nombre=nombre;
			this.valor=valor;		
		}

		@Override
		public String toString() {
			return this.nombre+": "+this.valor;

		}

	} //Fin de la clase interna Propiedad

} //Fin de la clase XPathProcess
