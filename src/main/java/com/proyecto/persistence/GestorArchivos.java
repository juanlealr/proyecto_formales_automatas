package com.proyecto.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.proyecto.model.Automata;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * Gestiona la persistencia del autómata en disco.
 * Exporta (guarda) un objeto Automata a un archivo .json
 * e importa (carga) un archivo .json convirtiéndolo de vuelta a Automata.
 */
public class GestorArchivos {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Serializa el autómata a JSON y lo escribe en el archivo indicado.
     *
     * @param automata Objeto a guardar.
     * @param archivo  Destino en disco (se sobreescribe si ya existe).
     * @throws IOException Si hay un error de escritura.
     */
    public static void exportar(Automata automata, File archivo) throws IOException {
        String json = gson.toJson(automata);
        Files.writeString(archivo.toPath(), json, StandardCharsets.UTF_8);
    }

    /**
     * Lee un archivo .json y lo deserializa a un objeto Automata.
     *
     * @param archivo Archivo .json a cargar.
     * @return El objeto Automata reconstruido.
     * @throws IOException Si el archivo no existe o no se puede leer.
     */
    public static Automata importar(File archivo) throws IOException {
        String json = Files.readString(archivo.toPath(), StandardCharsets.UTF_8);
        return gson.fromJson(json, Automata.class);
    }
}
