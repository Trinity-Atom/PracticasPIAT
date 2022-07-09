package piat.opendatasearch;

import java.io.BufferedWriter;
import java.io.File;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;

import piat.opendatasearch.XPathProcess.Propiedad;

public class GenerarJSON {
    private List<Propiedad> lpropiedades;
    private File jsonFile;

    public GenerarJSON(List<Propiedad> lpropiedades, File jsonFile) {
        this.lpropiedades=lpropiedades;
        this.jsonFile=jsonFile;
    }

    public String generarJson() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        for (Propiedad propiedad : lpropiedades) {
            String jsonString = gson.toJson(propiedad.getNombre());
            System.out.println(jsonString);
        }
        
        




        // Borrar si se se consigue hacer con gson
        JsonWriter jsonWriter = new JsonWriter(new BufferedWriter(new OutputStreamWriter(System.out)));
        StringBuilder sbSalida = new StringBuilder();
        int numPropiedad=0;
        
        sbSalida.append("{");
        sbSalida.append("\n\t"+lpropiedades.get(numPropiedad).toString()); numPropiedad++;
        sbSalida.append("\n\t"+lpropiedades.get(numPropiedad).toString()); numPropiedad++;
        sbSalida.append("\n\t\"infDatasets\" : [");
        sbSalida.append("}");
        //if(lpropiedades.get(numPropiedad))
        sbSalida.append("\n\t\t"+lpropiedades.get(numPropiedad).toString()); numPropiedad++;
        sbSalida.append("\n\t\t"+lpropiedades.get(numPropiedad).toString()); numPropiedad++;


        return sbSalida.toString();
    }
}
