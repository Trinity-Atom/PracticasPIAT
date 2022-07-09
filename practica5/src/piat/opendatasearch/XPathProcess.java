package piat.opendatasearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



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

			// a) Contenido textual del elemento <query>
			String propiedad1 = (String) xpath.compile("//query").evaluate(doc, XPathConstants.STRING);
			lPropiedades.add(new Propiedad("query",propiedad1));

			// b) Número de elementos <dataset> hijos de <datasets>
			Double propiedad2 = (Double) xpath.evaluate("count(/searchResults/results/datasets/dataset)",doc,XPathConstants.NUMBER);
			lPropiedades.add(new Propiedad("numDataset",String.valueOf(propiedad2)));

			// c) Contenido de cada uno de los elementos <title>, hijos de <resource>
			NodeList nlResource= (NodeList) xpath.evaluate("/searchResults/results/resources/resource", doc, XPathConstants.NODESET);
			System.out.println("\tList Resource length: " + nlResource.getLength() + "\n");
			for(int i=0;i< nlResource.getLength(); i++) {
				Element resource=(Element)nlResource.item(i);
				String propiedad3 = (String) xpath.evaluate("./title", resource, XPathConstants.STRING);
				lPropiedades.add(new Propiedad("title",propiedad3));
			}
			// d) Por cada elemento <dataset>, hijo de <datasets>, número de elementos <resource> cuyo atributo id es igual al atributo id del elemento <dataset>
			int cuenta;
			//obtenemos el nodelist de <dataset>
			NodeList nlDataset = (NodeList) xpath.evaluate("/searchResults/results/datasets/dataset", doc, XPathConstants.NODESET);
			for(int i=0;i< nlDataset.getLength(); i++) {
				//iniciar/resetear el contador
				cuenta=0;
				//para cada nodelist obtener la lista de atributos de <dataset>
				NamedNodeMap attListDataset=nlDataset.item(i).getAttributes();
				//la lista solo contiene un atributo en este caso
				Node attDataset=attListDataset.item(0);
				//obtenemos el nombre del atributo (id)
				String nameAttDataset=attDataset.getNodeName();
				//y el valor del atributo (https://datos.madrid.es/...)
				String valueAttDataset=attDataset.getNodeValue();
				//añadimos a la lista de propiedades el valor del atributo id de <dataset> que estamos evaluando
				lPropiedades.add(new Propiedad(nameAttDataset,valueAttDataset));
				//recorremos el nlResource que hemos obtenido en c)
				for (int j = 0; j < nlResource.getLength(); j++) {
					//y sacamos la lista de atributos del elemento <resource>
					NamedNodeMap attListResource=nlResource.item(j).getAttributes();
					//la lista solo contiene un atributo en este caso
					Node attResource=attListResource.item(0);
					String valueAttResource=attResource.getNodeValue();
					//comparamos el valor de los atributos id de <resource> con el id <dataset> que estamos evaluando
					if(valueAttResource.equals(valueAttDataset)){
						cuenta++;
					}
				}
				//una vez recorridos todos los resources guardamos la propiedad cuenta y lo añadimos a la lista de propiedades
				String propiedad4=String.valueOf(cuenta);
				lPropiedades.add(new Propiedad("num",propiedad4));
			}
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
