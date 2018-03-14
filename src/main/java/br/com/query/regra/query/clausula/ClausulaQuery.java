package br.com.query.regra.query.clausula;

import br.com.query.regra.query.condicao.ClausulaCondicaoQuery;
import br.com.query.regra.query.conjunto.ClausulaConjuntoQuery;

public class ClausulaQuery {

    private TipoClausulaRegraDinamica tipoClausula;
    private ClausulaCondicaoQuery condicao;
    private ClausulaConjuntoQuery conjunto;

    public TipoClausulaRegraDinamica getTipoClausula() {
        return tipoClausula;
    }

    public void setTipoClausula(TipoClausulaRegraDinamica tipoClausula) {
        this.tipoClausula = tipoClausula;
    }

    public ClausulaCondicaoQuery getCondicao() {
        return condicao;
    }

    public void setCondicao(ClausulaCondicaoQuery condicao) {
        this.condicao = condicao;
    }

    public ClausulaConjuntoQuery getConjunto() {
        return conjunto;
    }

    public void setConjunto(ClausulaConjuntoQuery conjunto) {
        this.conjunto = conjunto;
    }
}
