package br.com.query.regra;

import br.com.query.query.clausula.ClausulaQuery;
import br.com.query.query.conjunto.TipoConjuntoQuery;
import br.com.query.query.tipo.QueryStatusEnum;

import java.util.List;

public class RegraDinamicaStatus {

    private QueryStatusEnum status;
    private TipoClausulaRegraDinamica tipoClausula;
    private TipoConjuntoQuery tipoConjunto;
    private List<ClausulaQuery> clausulas;

    public TipoClausulaRegraDinamica getTipoClausula() {
        return tipoClausula;
    }

    public void setTipoClausula(TipoClausulaRegraDinamica tipoClausula) {
        this.tipoClausula = tipoClausula;
    }

    public TipoConjuntoQuery getTipoConjunto() {
        return tipoConjunto;
    }

    public void setTipoConjunto(TipoConjuntoQuery tipoConjunto) {
        this.tipoConjunto = tipoConjunto;
    }

    public List<ClausulaQuery> getClausulas() {
        return clausulas;
    }

    public void setClausulas(List<ClausulaQuery> clausulas) {
        this.clausulas = clausulas;
    }

    public QueryStatusEnum getStatus() {
        return status;
    }

    public void setStatus(QueryStatusEnum status) {
        this.status = status;
    }

}
