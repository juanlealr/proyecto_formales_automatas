package com.proyecto.model;

/**
 * Representa la función de transición ($\delta$) del autómata.
 * Define la regla de movimiento matemático: si estoy en 'estadoOrigen' y leo el 'simbolo',
 * entonces me muevo al 'estadoDestino'.
 * Utiliza Strings para los estados para evitar referencias circulares y facilitar
 * la conversión a formato JSON.
 * * @param estadoOrigen  El nombre del estado donde inicia la transición.
 * @param simbolo       El carácter o cadena del alfabeto ($\Sigma$) que acciona el cambio.
 * @param estadoDestino El nombre del estado al que se llega tras leer el símbolo.
 */
public record Transicion(String estadoOrigen, String simbolo, String estadoDestino) {
}
