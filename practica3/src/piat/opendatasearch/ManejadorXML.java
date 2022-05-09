package piat.opendatasearch;

import java.util.ArrayList;
import java.util.HashMap;
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
	private HashMap <String,String> mapaDataset;
	private String sCodigoConcepto;
	private String idConcept;
	private String idDataset;
	private int nivelConcepts;
	private int nivelConcept;
	private int nivelDatasets;
	private int nivelDataset;
	private boolean esCategoria;
	private int nivelInicioCategoria;

	/**  
	 * @param sCodigoConcepto código de la categoría a procesar
	 * @throws ParserConfigurationException 
	 */
	public ManejadorXML (String sCodigoConcepto) throws SAXException, ParserConfigurationException {
		// TODO
		this.sCodigoConcepto = sCodigoConcepto; //Codigo 018
		sNombreCategoria = "";
		idConcept = "";
		lConcepts = new ArrayList<String>();
		hDatasets = new HashMap<>();
		mapaDataset = new HashMap<>();
		contenidoElemento = new StringBuilder();
		nivelDatasets = 0;
		nivelDataset = 0;
		nivelConcepts = 0;
		nivelConcept = 0;
		nivelInicioCategoria = 0;
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

		// PASO 1) Detectar si es elemento concept (DONE & TESTED)
		if (qName.equals("concept")){
			// Si hay atributos 
			if (attributes.getLength() > 0){
				// Comprobar si contiene algún atributo cuyo nombre sea id
				for (int i=0; i < attributes.getLength(); i++){
					// Si contiene un atributo cuyo nombre es id, guardar temporalmente el valor del atributo
					if (attributes.getQName(i).equals("id")){
						idConcept=attributes.getValue(i);
						// PASO 4) Almacenar valor de atributos id en lConcepts
						// mientras esté abierto el concept correspondiente a la categoria buscada
						if(nivelConcept>=nivelInicioCategoria&&esCategoria==true)
							lConcepts.add(idConcept);
					}
				}
			}
			// PASO 4) incrementar una variable que indique el nivel
			nivelConcept++;
			contenidoElemento.setLength(0);
			if(nivelDatasets==nivelDataset && nivelDatasets==nivelConcepts && nivelDatasets==nivelConcept){
				//Si el atributo id coincide con alguno de lConcepts
				if(lConcepts.contains(idConcept))
				for (int i=0; i < attributes.getLength(); i++){
					if (attributes.getQName(i).equals("id"))
						hDatasets.put(attributes.getValue(i), mapaDataset);
				}
			}
		}
		// DATASETS
		if(qName.equals("datasets")){
			nivelDatasets++;
		}
		if(qName.equals("dataset")){
			// Si dataset tiene atributos
			if(attributes.getLength()>0){
				// Comprobar si contiene algún atributo cuyo nombre sea id
				for (int i=0; i < attributes.getLength(); i++){
					if (attributes.getQName(i).equals("id")){
						// Guardar el valor del atributo id en una variable temporal
						idDataset=attributes.getValue(i);
					}
				}
			}
			nivelDataset++;
		}
		if(qName.equals("concepts")){
			nivelConcepts++;
		}
		if(qName.equals("title")){
			contenidoElemento.setLength(0);
		}
		if(qName.equals("description")){
			contenidoElemento.setLength(0);
		}
		if(qName.equals("theme")){
			contenidoElemento.setLength(0);
		}
		if (qName.equals("code")){
			contenidoElemento.setLength(0);
		}
		if(qName.equals("label")){
			contenidoElemento.setLength(0);
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		super.endElement(uri, localName, qName);
		// TODO
		
		// PASO 2)
		if(qName.equals("code")){
			// Si se está dentro de un concept y el contenido del elemento Code es igual que el de sCodigoConcepto
			if(nivelConcept>0 && sCodigoConcepto.equals(contenidoElemento.toString())){
				// Añadimos el id al lconcepts
				lConcepts.add(idConcept);
				// Se ha encontrado la categoría
				esCategoria=true;
				nivelInicioCategoria=nivelConcept;
			}
			contenidoElemento.setLength(0);
		}

		// PASO 3)
		if(qName.equals("label")){
			// Si se está dentro de un concept, se está en el primer nivel y se ha encontrado la categoría
			if(nivelConcept>0 && nivelInicioCategoria==nivelConcept && esCategoria==true){
				
				// Almacenar el contenido del elemento en sNombreCategoria
				 sNombreCategoria=contenidoElemento.toString();
			}
			contenidoElemento.setLength(0);
		}

		// PASO 4)
		if (qName.equals("concept")){			
			if(nivelConcept==nivelInicioCategoria && esCategoria==true){
				nivelInicioCategoria=-1;
				esCategoria=false;
			}
			nivelConcept--;
		}
		// END DATASETS
		if(qName.equals("datasets")){
			nivelDatasets--;
			contenidoElemento.setLength(0);
		}
		if(qName.equals("dataset")){
			nivelDataset--;
			contenidoElemento.setLength(0);
		}
		if(qName.equals("concepts")){
			nivelConcepts--;
			contenidoElemento.setLength(0);
		}
		if(qName.equals("title")){ // DONE
			mapaDataset.put("title", contenidoElemento.toString());
			contenidoElemento.setLength(0);
		}
		if(qName.equals("description")){ // DONE
			mapaDataset.put("description", contenidoElemento.toString());
			contenidoElemento.setLength(0);
		}
		if(qName.equals("theme")){ // DONE
			mapaDataset.put("theme", contenidoElemento.toString());
			contenidoElemento.setLength(0);
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		super.characters(ch, start, length);
		// TODO 
		contenidoElemento.append(ch, start, length);		
	}

}
