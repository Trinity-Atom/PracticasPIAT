package piat.opendatasearch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.helpers.NamespaceSupport;



public class XPathProcess{

	/**
	 * Método que se encarga de evaluar las expresiones xpath sobre el fichero XML generado en la práctica 4
	 * @return
	 *  - Una lista con la propiedad resultante de evaluar cada expresion xpath
	 * @throws IOException

	 * @throws ParserConfigurationException 
	 */


	public static List<Propiedad> evaluar (String ficheroXML) throws IOException, XPathExpressionException {
		// TODO: Realizar las 4 consultas xpath al documento XML de entrada que se indican en el enunciado en el apartado "3.2 Búsqueda de información y generación del documento de resultados."
		// Cada consulta devolverá una información que se añadirá a la una colección List <Propiedad>
		// Una consulta puede devolver una propiedad o varias
		List<Propiedad> lPropiedades = new ArrayList<Propiedad>();
		try {
			//File fichSource = new File(ficheroXML);
			
			// Get DOM
			DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
			//domFactory.setNamespaceAware(true);
			//domFactory.setIgnoringElementContentWhitespace(true);
			DocumentBuilder builder = domFactory.newDocumentBuilder();
			//System.out.println(fichSource.getCanonicalPath());
			Document doc = builder.parse(ficheroXML);
			
			// Get XPATH
			XPath xpath = XPathFactory.newInstance().newXPath();
			//xpath.setNamespaceContext(new NamespaceResolver(doc));
			//XPathExpression expr = xpath.compile("/xmlns:searchResults/xmlns:summary/xmlns:query");

			// Contenido textual del elemento <query>
			String propiedad1 = (String) xpath.compile("//query").evaluate(doc, XPathConstants.STRING);
			 System.out.println(propiedad1);
			// lPropiedades.add(propiedad1);
		}catch (Exception e) {
			System.err.println ("Se ha producido una excepción: " + e.getMessage());
		}

		return lPropiedades;
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

	/* La clase NamespaceResolver implementa la interfaz NamespaceContext para poder configurar
 * un procesador XPath que usa espacios de nombres. 
 * Utiliza un objeto DOM del que puede obtener las URIs de los espacios de nombres
 * asociados a los prefijos
 */
class NamespaceResolver implements NamespaceContext {
	private Document sourceDocument;

	public NamespaceResolver(Document document) {
		sourceDocument = document;
	}

	@Override
	public String getNamespaceURI(String prefix) {
		if (prefix.equals(XMLConstants.DEFAULT_NS_PREFIX)) {
			return sourceDocument.lookupNamespaceURI(null);
		} else {
			return sourceDocument.lookupNamespaceURI(prefix);
		}
	}

	@Override
	public String getPrefix(String namespaceURI) {
		return sourceDocument.lookupPrefix(namespaceURI);
	}

	@Override
	public Iterator<String> getPrefixes(String namespaceURI) {
		throw new UnsupportedOperationException();
	}

}

