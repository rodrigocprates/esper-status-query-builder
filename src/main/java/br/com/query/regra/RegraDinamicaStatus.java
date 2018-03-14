package br.com.query.regra;

import br.com.query.regra.query.clausula.TipoClausulaRegraDinamica;
import br.com.query.regra.query.condicao.ClausulaCondicaoQuery;
import br.com.query.regra.query.conjunto.ClausulaConjuntoQuery;
import br.com.query.regra.query.tipo.QueryStatusEnum;

public class RegraDinamicaStatus {

    private QueryStatusEnum status;

    private TipoClausulaRegraDinamica tipoClausula;

    private ClausulaConjuntoQuery conjunto;
    private ClausulaCondicaoQuery condicao;

    public QueryStatusEnum getStatus() {
        return status;
    }

    public void setStatus(QueryStatusEnum status) {
        this.status = status;
    }

    public TipoClausulaRegraDinamica getTipoClausula() {
        return tipoClausula;
    }

    public void setTipoClausula(TipoClausulaRegraDinamica tipoClausula) {
        this.tipoClausula = tipoClausula;
    }

    public ClausulaConjuntoQuery getConjunto() {
        return conjunto;
    }

    public void setConjunto(ClausulaConjuntoQuery conjunto) {
        this.conjunto = conjunto;
    }

    public ClausulaCondicaoQuery getCondicao() {
        return condicao;
    }

    public void setCondicao(ClausulaCondicaoQuery condicao) {
        this.condicao = condicao;
    }
}
