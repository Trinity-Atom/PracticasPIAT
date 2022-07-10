package piat.opendatasearch;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

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

    public void generarJson() throws IOException {
        
        // Valores simples o listas de valores
        String query = "";
        String numDataset = "";
        List<String> idList = new ArrayList<>();
        List<String> numList = new ArrayList<>();
        List<String> titleList = new ArrayList<>();

        //Contadores
        int idCount=0;
        
        // Obtener los valores de las propiedades
        for (Propiedad propiedad : lpropiedades) {
            String nombre = propiedad.getNombre();
            if(nombre.equals("query")){
                query=propiedad.getValor();
            } else if (nombre.equals("numDataset")) {
                numDataset=propiedad.getValor();
            } else if (nombre.equals("id")) {
                idList.add(propiedad.getValor());
            } else if (nombre.equals("num")) {
                numList.add(propiedad.getValor());
            } else if (nombre.equals("title")) {
                titleList.add(propiedad.getValor());
            }
        }
        
        //Instanciar gson y writer
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonWriter write = gson.newJsonWriter(new FileWriter(jsonFile));

        //Comienzo JSON
        write.beginObject();
        write.name("query").value(query);
        write.name("numDataset").value(numDataset);
        write.name("infDatasets");
        write.beginArray();
        for (String valor : idList) {
            write.beginObject();
            write.name("id").value(valor);
            write.name("num").value(numList.get(idCount));
            write.endObject();
            idCount++;
        }
        write.endArray();
        write.name("titles");
        write.beginArray();
        for (String valor : titleList) {
            write.beginObject();
            write.name("title").value(valor);
            write.endObject();
        }
        write.endArray();
        write.endObject();
        write.close();
        //Final JSON
    }
}
