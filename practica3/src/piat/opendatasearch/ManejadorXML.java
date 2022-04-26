package piat.opendatasearch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Salvador Fernández García-Morales 53823973V
 *
 */

public class ManejadorXML extends DefaultHandler implements ParserCatalogo {
	private String sNombreCategoria;	// Nombre de la categoría
	private List<String> lConcepts; 	// Lista con los uris de los elementos <concept> que pertenecen a la categoría
	private Map <String, HashMap<String,String>> hDatasets;	// Mapa con información de los dataset que pertenecen a la categoría
	private StringBuilder contenidoElemento;
	private String sCodigoConcepto;
	private int conceptCounter;
	private String valorAtributoID;

	/**  
	 * @param sCodigoConcepto código de la categoría a procesar
	 * @throws ParserConfigurationException 
	 */
	public ManejadorXML (String sCodigoConcepto) throws SAXException, ParserConfigurationException {
		// TODO
		this.sCodigoConcepto = sCodigoConcepto; //Codigo 018
		sNombreCategoria = "";
		lConcepts = new ArrayList<String>();
		conceptCounter = 0;
		hDatasets = null;
		valorAtributoID = "";
		contenidoElemento= null;
	}

	 //===========================================================
	 // Métodos a implementar de la interfaz ParserCatalogo
	 //===========================================================

	/**
	 * <code><b>getLabel</b></code> 
	 * @return Valor de la cadena del elemento <code>label</code> del <code>concept</code> cuyo 
	 * elemento <code><b>code</b></code> sea <b>igual</b> al criterio a búsqueda.
	 * <br>
	 * null si no se ha encontrado el concept pertinente o no se dispone de esta información  
	 */
	@Override
	public String getLabel() {
		// TODO
		if(sNombreCategoria.isEmpty())
			return null;
		else
			return sNombreCategoria;
	}

	/**
	 * <code><b>getConcepts</b></code>
	 *	Devuelve una lista con información de los <code><b>concepts</b></code> resultantes de la búsqueda. 
	 * <br> Cada uno de los elementos de la lista contiene la <code><em>URI</em></code> del <code>concept</code>
	 * 
	 * <br>Se considerarán pertinentes el <code><b>concept</b></code> cuyo código
	 *  sea igual al criterio de búsqueda y todos sus <code>concept</code> descendientes.
	 *  
	 * @return
	 * - List  con la <em>URI</em> de los concepts pertinentes.
	 * <br>
	 * - null  si no hay concepts pertinentes.
	 * 
	 */
	@Override	
	public List<String> getConcepts() {
		// TODO 
		if(lConcepts.isEmpty())
			return null;
		else
			return lConcepts;
	}

	/**
	 * <code><b>getDatasets</b></code>
	 * 
	 * @return Mapa con información de los <code>dataset</code> resultantes de la búsqueda.
	 * <br> Si no se ha realizado ninguna  búsqueda o no hay dataset pertinentes devolverá el valor <code>null</code>
	 * <br> Estructura de cada elemento del map:
	 * 		<br> . <b>key</b>: valor del atributo ID del elemento <code>dataset</code>con la cadena de la <code><em>URI</em></code>  
	 * 		<br> . <b>value</b>: Mapa con la información a extraer del <code>dataset</code>. Cada <code>key</code> tomará los valores <em>title</em>, <em>description</em> o <em>theme</em>, y <code>value</code> sus correspondientes valores.

	 * @return
	 *  - Map con información de los <code>dataset</code> resultantes de la búsqueda.
	 *  <br>
	 *  - null si no hay datasets pertinentes.  
	 */	
	@Override
	public Map<String, HashMap<String, String>> getDatasets() {
		// TODO 
		if(hDatasets.isEmpty())
			return null;
		else
			return hDatasets;
	}
	

	 //===========================================================
	 // Métodos a implementar de SAX DocumentHandler
	 //===========================================================
	
	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		// TODO 
		System.out.println("Empieza el documento");
		
	}

	
	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
		// TODO 
		System.out.println("Finaliza el documento");
				
	}


	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		// TODO 

		// PASO 1) Detectar si es elemento concept (DONE)
		String nombreAtributo;
		if (localName.equals("concept")){
			// Si hay atributos 
			if (attributes.getLength() > 0){
				// Comprobar si contiene algún atributo cuyo nombre sea id
				for (int i=1; i <= attributes.getLength(); i++){
					nombreAtributo=attributes.getLocalName(i);
					// Si contiene un atributo id guardar el valor del atributo en lConcepts 
					if (nombreAtributo.equals("id")){
						valorAtributoID=attributes.getValue(i);
					}
				}
			}
			// Anotar que hemos entrado en un concept incrementando conceptCounter
			conceptCounter++;
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		super.endElement(uri, localName, qName);
		// TODO
		if (localName.equals("concept"))
			conceptCounter--;

		// PASO 2) EVENTO ELEMENT </code>
		if(localName.equals("code")){
			// TODO: Revisar como obtener el contenidoElemento
			// Si se está dentro de un concept y el contenido del elemento Code es igual que el de sCodigoConcepto
			if(conceptCounter>0 && sCodigoConcepto.equals(contenidoElemento.toString()))
			// Almacenar el valor del atributo id guardado en el paso1 en lConcepts
			lConcepts.add(valorAtributoID);
		}
		// PASO 3) EVENTO ELEMENT </label>
		if(localName.equals("label")){
			// Si se está dentro de un concept, se está en el primer nivel y se ha encontrado la categoría
			if(conceptCounter==1 && getLabel()!=null){
				// TODO: Revisar como obtener el contenidoElemento
				// Almacenar el contenido del elemento en el atributo sNombreCategoria
				 sNombreCategoria=contenidoElemento.toString();
			}
		}		
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		super.characters(ch, start, length);
		// TODO 
		contenidoElemento.append(ch, start, length);
		contenidoElemento.setLength(0);		
	}

}
