package com.proyecto.engine;

/**
 * Representa el resultado final de la evaluación de una cadena por el motor del autómata.
 * Al ser un 'record', es inmutable, lo que garantiza que el resultado de la evaluación 
 * no sea alterado accidentalmente por otras partes del programa.
 *
 * @param esAceptada     true si la cadena es válida (termina en un estado de aceptación),
 * false si es rechazada (termina en un estado normal o no hay transición válida).
 * @param trazaPasoAPaso Texto que contiene el recorrido detallado de las transiciones.
 * Cumple con el requisito de Trazabilidad (Ej: "(q0, a) -> q1, (q1, b) -> q2").
 */
public record ResultadoEvaluacion(boolean esAceptada, String trazaPasoAPaso) {
}
