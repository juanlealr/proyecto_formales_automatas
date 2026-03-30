package com.proyecto.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representa la estructura matemática de un Autómata Finito.
 * Modela la quíntupla formal $M = (Q, \Sigma, \delta, q_0, F)$ donde:
 * - $Q$ (estados): Conjunto finito de estados que componen el autómata.
 * - $\Sigma$ (alfabeto): Lista de símbolos o caracteres válidos permitidos.
 * - $\delta$ (transiciones): Función que define las reglas de movimiento entre estados.
 * - $q_0$ (estadoInicial): Estado donde arranca la ejecución de la máquina.
 * - $F$ (estadosAceptacion): Conjunto de estados finales que determinan si una cadena es ACEPTADA.
 * * Incluye también el 'tipo' para diferenciar entre la evaluación de un DFA o un NFA.
 * Utiliza anotaciones de Lombok para mantener el modelo de datos limpio.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Automata {
    private TipoAutomata tipo;
    private List<Estado> estados;
    private List<String> alfabeto;
    private List<Transicion> transiciones;
    private String estadoInicial;
    private List<String> estadosAceptacion;
}
