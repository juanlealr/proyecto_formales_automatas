package com.proyecto.model;

/**
 * Representa un estado individual dentro del conjunto finito de estados ($Q$) del autómata.
 * Al ser un 'record', garantiza la inmutabilidad del estado durante la ejecución.
 * Se identifica de manera única por su nombre (ej. "q0", "q1", "A").
 * * @param nombre El identificador o etiqueta en texto del estado.
 */
public record Estado(String nombre) {
}
