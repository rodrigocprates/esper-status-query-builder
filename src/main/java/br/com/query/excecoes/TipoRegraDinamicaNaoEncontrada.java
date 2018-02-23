package br.com.query.excecoes;

public class TipoRegraDinamicaNaoEncontrada extends RuntimeException {
    public TipoRegraDinamicaNaoEncontrada() {
        super("Tipo de Regra Dinâmica não encontrada.");
    }
}
