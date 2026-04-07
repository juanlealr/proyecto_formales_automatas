package com.proyecto.engine;

import java.util.ArrayList;
import java.util.List;

import com.proyecto.model.Automata;
import com.proyecto.model.Estado;
import com.proyecto.model.TipoAutomata;
import com.proyecto.model.Transicion;

public class MotorEvaluacion {
    // Método principal que la Interfaz de Usuario va a llamar
    public ResultadoEvaluacion procesarCadena(Automata automata, String cadena) {
        // Redirigir según el tipo de autómata
        return switch (automata.getTipo()) {
            case AFD -> evaluarAFD(automata, cadena);
            case AFN -> evaluarAFN(automata, cadena);
        };
    }

    /**
     * Evalúa una cadena de texto utilizando la lógica estricta de un Autómata Finito Determinista (AFD).
     * <p>
     * El algoritmo procesa la cadena símbolo por símbolo. Por cada símbolo, busca en la lista
     * de transiciones aquella que coincida con el estado actual y el símbolo leído. Si la encuentra,
     * avanza al estado destino y registra el movimiento para generar la trazabilidad.
     * <p>
     * <b>Condiciones de rechazo:</b> 
     * 1. Si el autómata lee un símbolo para el cual no hay una transición definida desde el estado actual.
     * 2. Si termina de leer toda la cadena y el estado final no pertenece al conjunto de aceptación.
     *
     * @param automata El modelo del autómata (AFD) que contiene los estados, transiciones y reglas.
     * @param cadena   La secuencia de caracteres que se desea someter a evaluación.
     * @return Un objeto {@link ResultadoEvaluacion} que contiene un booleano (true si fue aceptada, 
     * false si fue rechazada) y un String con la traza del recorrido paso a paso.
     */
    private ResultadoEvaluacion evaluarAFD(Automata automata, String cadena) {
        String estadoActual = automata.getEstadoInicial();

        // Manejo explícito de la cadena vacía (épsilon)
        // Acepta si está literalmente vacía, o si se usan los comodines "E" o "ε"
        if (cadena == null || cadena.isEmpty() || cadena.equalsIgnoreCase("E") || cadena.equals("ε")) {
            boolean esAceptada = automata.getEstadosAceptacion().contains(estadoActual);
            String estadoTraza = esAceptada ? "Aceptada" : "Rechazada";
            return new ResultadoEvaluacion(esAceptada, "Cadena vacía (ε) " + estadoTraza + " en el estado inicial: " + estadoActual + "\n");
        }

        StringBuilder traza = new StringBuilder();
        String[] arregloSimbolos = cadena.split("");
        boolean esAceptada = false;

        for (String simboloActual : arregloSimbolos) {
            if (!simboloActual.isEmpty() && !automata.getAlfabeto().contains(simboloActual)) {
                return new ResultadoEvaluacion(false, "Error: El símbolo '" + simboloActual + "' no pertenece al alfabeto.\n");
            }
        }

        for (String simboloActual : arregloSimbolos) {
            boolean transicionEncontrada = false;

            for (Transicion transicion : automata.getTransiciones()) {
                if (transicion.estadoOrigen().equals(estadoActual) && transicion.simbolo().equals(simboloActual)) {
                    traza.append("( ").append(estadoActual).append(", ").append(simboloActual).append(" ) -> ").append(transicion.estadoDestino()).append("\n");
                    estadoActual = transicion.estadoDestino();
                    transicionEncontrada = true;
                    break; 
                }
            }

            if (!transicionEncontrada) {
                traza.append("Error: No hay transición definida desde '").append(estadoActual).append("' con el símbolo '").append(simboloActual).append("'.\n");
                return new ResultadoEvaluacion(false, traza.toString());
            }
        }

        if (automata.getEstadosAceptacion().contains(estadoActual)) {
            esAceptada = true;
        }

        return new ResultadoEvaluacion(esAceptada, traza.toString());
    }

    /**
     * Evalúa una cadena de texto utilizando la lógica de un Autómata Finito No Determinista (AFN).
     * <p>
     * A diferencia del AFD, el AFN puede tener múltiples caminos válidos para un mismo símbolo.
     * Este algoritmo utiliza Recursividad (Búsqueda en Profundidad / Backtracking) para explorar
     * los diferentes "universos paralelos" o rutas posibles.
     * <p>
     * <b>Lógica de exploración:</b>
     * 1. Si un camino llega al final de la cadena y el estado pertenece al conjunto de aceptación,
     * la cadena es ACEPTADA y se devuelve la traza exacta de ese camino exitoso.
     * 2. Si un camino se queda sin transiciones válidas antes de terminar, retrocede en silencio
     * y permite que el ciclo pruebe otra ruta alternativa.
     * 3. Si se agotan todos los caminos posibles y ninguno llega a la aceptación, es RECHAZADA.
     *
     * @param automata El modelo del autómata (AFN) con sus estados, transiciones y reglas.
     * @param cadena   La secuencia de caracteres que se desea evaluar.
     * @return Un objeto {@link ResultadoEvaluacion} indicando si fue aceptada y la traza del camino ganador.
     */
    private ResultadoEvaluacion evaluarAFN(Automata automata, String cadena) {
        // Manejo explícito de la cadena vacía (épsilon)
        if (cadena == null || cadena.isEmpty() || cadena.equalsIgnoreCase("E") || cadena.equals("ε")) {
            boolean esAceptada = automata.getEstadosAceptacion().contains(automata.getEstadoInicial());
            String estadoTraza = esAceptada ? "Aceptada" : "Rechazada";
            return new ResultadoEvaluacion(esAceptada, "Cadena vacía (ε) " + estadoTraza + " en el estado inicial: " + automata.getEstadoInicial() + "\n");
        }

        String[] arregloSimbolos = cadena.split("");
        for (String simboloActual : arregloSimbolos) {
            if (!simboloActual.isEmpty() && !automata.getAlfabeto().contains(simboloActual)) {
                return new ResultadoEvaluacion(false, "Error: El símbolo '" + simboloActual + "' no pertenece al alfabeto.\n");
            }
        }

        return explorarRuta(automata, arregloSimbolos, 0, automata.getEstadoInicial(), "");
    }

    private ResultadoEvaluacion explorarRuta(Automata automata, String[] simbolos, int indice, String estadoActual, String trazaActual) {
        
        if (indice == simbolos.length) {
            if (automata.getEstadosAceptacion().contains(estadoActual)) {
                return new ResultadoEvaluacion(true, trazaActual);
            } else {
                return new ResultadoEvaluacion(false, trazaActual);
            }
        }

        String simboloActual = simbolos[indice];

        for (Transicion transicion : automata.getTransiciones()) {
            
            if (transicion.estadoOrigen().equals(estadoActual) && transicion.simbolo().equals(simboloActual)) {
                
                String nuevaTraza = trazaActual + "( " + estadoActual + ", " + simboloActual + " ) -> " + transicion.estadoDestino() + "\n";
                
                ResultadoEvaluacion resultadoCamino = explorarRuta(automata, simbolos, indice + 1, transicion.estadoDestino(), nuevaTraza);
                
                if (resultadoCamino.esAceptada()) {
                    return resultadoCamino;
                }

            }
        }
        return new ResultadoEvaluacion(false, "Cadena rechazada. Ningún camino llegó a un estado de aceptación.");
    }

    /**
     * Procesa un lote de cadenas (hasta 10, según los requisitos) y devuelve
     * una lista con los resultados de cada evaluación.
     */
    public List<ResultadoEvaluacion> procesarLote(Automata automata, List<String> loteCadenas) {
        List<ResultadoEvaluacion> resultados = new ArrayList<>();
        
        for (String cadena : loteCadenas) {
            resultados.add(procesarCadena(automata, cadena));
        }
        
        return resultados;
    }

    /**
     * Valida exhaustivamente que la definición matemática del autómata sea coherente.
     * Comprueba la integridad de la quíntupla formal M = (Q, Sigma, delta, q0, F).
     * * @param automata El autómata a validar recién cargado o creado.
     * @return ResultadoEvaluacion donde 'esAceptada' es true si el autómata es válido.
     * Si es false, 'trazaPasoAPaso' contendrá la lista de errores matemáticos encontrados.
     */
    public ResultadoEvaluacion validarDefinicionAutomata(Automata automata) {
        StringBuilder errores = new StringBuilder();

        // Valida que existan estados y alfabeto (Q y Sigma no pueden ser nulos o vacíos)
        if (automata.getEstados() == null || automata.getEstados().isEmpty()) {
            errores.append("- Error fatal: El conjunto de estados (Q) está vacío.\n");
        }
        if (automata.getAlfabeto() == null || automata.getAlfabeto().isEmpty()) {
            errores.append("- Error fatal: El alfabeto (Sigma) está vacío.\n");
        }

        // Si no hay estados, no podemos validar lo demás, así que cortamos aquí
        if (errores.length() > 0) {
            return new ResultadoEvaluacion(false, "Autómata Inválido:\n" + errores.toString());
        }

        // Extraemos los nombres de los estados a una lista simple para facilitar las búsquedas
        List<String> nombresEstados = new ArrayList<>();
        for (Estado estado : automata.getEstados()) {
            nombresEstados.add(estado.nombre());
        }

        // Validar Estado Inicial (q0 pertenece a Q)
        if (automata.getEstadoInicial() == null || !nombresEstados.contains(automata.getEstadoInicial())) {
            errores.append("- El estado inicial '").append(automata.getEstadoInicial()).append("' no existe en el conjunto de estados (Q).\n");
        }

        // Validar Estados de Aceptación (F subconjunto de Q)
        if (automata.getEstadosAceptacion() != null) {
            for (String estadoAceptacion : automata.getEstadosAceptacion()) {
                if (!nombresEstados.contains(estadoAceptacion)) {
                    errores.append("- El estado de aceptación '").append(estadoAceptacion).append("' no existe en el conjunto de estados (Q).\n");
                }
            }
        }

        // Validar Transiciones (delta)
        if (automata.getTransiciones() != null) {
            // Lista para recordar las transiciones y verificar el determinismo
            List<String> transicionesVistas = new java.util.ArrayList<>();

            for (Transicion t : automata.getTransiciones()) {
                // a) estadoOrigen pertenece a Q
                if (!nombresEstados.contains(t.estadoOrigen())) {
                    errores.append("- Transición inválida: El estado origen '").append(t.estadoOrigen()).append("' no existe en (Q).\n");
                }
                // b) estadoDestino pertenece a Q
                if (!nombresEstados.contains(t.estadoDestino())) {
                    errores.append("- Transición inválida: El estado destino '").append(t.estadoDestino()).append("' no existe en (Q).\n");
                }
                // c) símbolo pertenece a Sigma
                if (!t.simbolo().isEmpty() && !automata.getAlfabeto().contains(t.simbolo())) {
                    errores.append("- Transición inválida: El símbolo '").append(t.simbolo()).append("' no pertenece al alfabeto.\n");
                }

                // d) Validar Determinismo si el tipo es AFD
                if (automata.getTipo() == TipoAutomata.AFD) {
                    // Creamos una "firma" de la transición (ej: "q0-a")
                    String firmaTransicion = t.estadoOrigen() + "-" + t.simbolo();
                    
                    if (transicionesVistas.contains(firmaTransicion)) {
                        errores.append("- Error de Determinismo: El estado '").append(t.estadoOrigen())
                               .append("' tiene más de una transición con el símbolo '").append(t.simbolo())
                               .append("'. Esto es ilegal en un AFD.\n");
                    } else {
                        transicionesVistas.add(firmaTransicion);
                    }
                }
            }
        }

        // Si el StringBuilder tiene texto, significa que recolectó errores
        if (errores.length() > 0) {
            return new ResultadoEvaluacion(false, "El autómata tiene errores de definición matemática:\n" + errores.toString());
        }

        // Si pasó todas las pruebas, es un autómata perfecto
        return new ResultadoEvaluacion(true, "Autómata validado correctamente. Estructura matemática impecable.");
    }

}
