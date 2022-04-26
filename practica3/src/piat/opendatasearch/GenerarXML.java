package piat.opendatasearch;

import java.util.List;

public class GenerarXML {
    private static final String conceptPattern = "\n\t\t\t<concept id=\"#ID#\"/>";
    public static String conceptsToXML(List <String> lConcepts){
        StringBuilder sbSalida = new StringBuilder();
        sbSalida.append("\n\t\t<concepts>" );
        for(String unConcepto: lConcepts){
        sbSalida.append(conceptPattern.replace("#ID#", unConcepto));
        }
        sbSalida.append("\n\t\t</concepts>");
        return sbSalida.toString();
        }
}
