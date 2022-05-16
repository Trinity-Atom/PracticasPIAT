package piat.opendatasearch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class GenerarXML {
    private static final String conceptPattern = "\n\t\t\t<concept id=\"#ID#\"/>";
    private Set <Entry<String, HashMap<String,String>>> setDatasets;
    private Set<Entry<String, List<Map<String, String>>>> setMapaDatasets;
    /*
    *   Clase GenerarXML para volcar los datos en un archivo
    */
    public GenerarXML (){
    }
    
    public String generateXML(List <String> lConcepts, String codigoCategoria,
        Map <String, HashMap<String,String>> datasets,
        Map<String, List<Map<String,String>>> mDatasetConcepts){
        
        StringBuilder sbSalida = new StringBuilder();
        try {
        // InitVariables
        setDatasets = datasets.entrySet();
        setMapaDatasets = mDatasetConcepts.entrySet();
        HashMap<String,String> mapaContenidoDataset;

        // XML INIT
        sbSalida.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sbSalida.append("\n<searchResults xmlns=\"http://www.piat.dte.upm.es/practica3\"");
        sbSalida.append("\nxmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
        sbSalida.append("\nxsi:schemaLocation=\"http://www.piat.dte.upm.es/practica3 ResultadosBusquedaP3.xsd\">");
        
        // SUMMARY
        sbSalida.append("\n\t<summary>\n\t\t<query>"+codigoCategoria+"</query>");
        sbSalida.append("\n\t\t<numConcepts>"+lConcepts.size()+"</numConcepts>");
        sbSalida.append("\n\t\t<numDatasets>"+setDatasets.size()+"</numDatasets>\n\t</summary>");

        // CONCEPTS
        sbSalida.append("\n\t<results>\n\t\t<concepts>" );
        for(String unConcepto: lConcepts){
        sbSalida.append(conceptPattern.replace("#ID#", unConcepto));
        }
        sbSalida.append("\n\t\t</concepts>");
        
        // DATASETS
        sbSalida.append("\n\t\t<datasets>");
        for (Map.Entry<String,HashMap<String,String>> theDataset : setDatasets) {
            // Añadir el idDataset
            sbSalida.append("\n\t\t\t<dataset id=\""+theDataset.getKey()+"\">");
            // Coger el hashMap de los contenidos del dataset
            mapaContenidoDataset = theDataset.getValue();
            sbSalida.append("\n\t\t\t\t<title>"+mapaContenidoDataset.get("title")+"</title>");
            sbSalida.append("\n\t\t\t\t<description>"+mapaContenidoDataset.get("description")+"</description>");
            sbSalida.append("\n\t\t\t\t<theme>"+mapaContenidoDataset.get("theme")+"</theme>\n\t\t\t</dataset>");
        }
        sbSalida.append("\n\t\t</datasets>\n\t</results>");
        
        // MAPA DATASETS
        sbSalida.append("\n\t<resources>");
        for (Entry<String, List<Map<String, String>>> mapajson : setMapaDatasets) {
            sbSalida.append("<resource id=\""+mapajson.getKey()+"\">");  
            //sbSalida.append("<concept id=\""++"\"/>"); 
        }
        sbSalida.append("\n\t</resources>\n</searchResults>");
        } catch (Exception e) {
            System.err.println("ERROR WHILE PARSING");
            e.printStackTrace();
        }
        return sbSalida.toString();
    }
}