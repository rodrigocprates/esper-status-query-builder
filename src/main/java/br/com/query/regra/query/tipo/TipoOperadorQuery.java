package br.com.query.regra.query.tipo;

public enum TipoOperadorQuery {

    IGUAL_A("="),
    DIFERENTE_DE("!="),
    MAIOR_QUE(">"),
    MENOR_QUE("<"),
    ESTA_CONTIDO_EM("IN"),
    NAO_ESTA_CONTIDO_EM("NOT IN");

    private final String simbolo;

    TipoOperadorQuery(String simbolo) {
        this.simbolo = simbolo;
    }

    public String getSimbolo() {
        return simbolo;
    }
}
