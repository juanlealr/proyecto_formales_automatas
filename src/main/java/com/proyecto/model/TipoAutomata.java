package com.proyecto.model;

/**
 * Define la naturaleza del motor de procesamiento del autómata finito.
 * - AFD: Autómata Finito Determinista (solo una transición válida por símbolo).
 * - AFN: Autómata Finito No Determinista (múltiples transiciones por símbolo o transiciones vacías).
 * Ayuda al motor a decidir qué algoritmo de evaluación aplicar.
 */
public enum TipoAutomata {
    AFD,
    AFN
}
