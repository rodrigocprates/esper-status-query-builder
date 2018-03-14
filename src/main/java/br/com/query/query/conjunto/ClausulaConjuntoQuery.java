package br.com.query.query.conjunto;

import br.com.query.query.clausula.ClausulaQuery;

import java.util.ArrayList;
import java.util.List;

public class ClausulaConjuntoQuery {

    private TipoConjuntoQuery tipoConjunto;
    private List<ClausulaQuery> clausulas = new ArrayList<>();

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
}
