package br.com.query.query.conjunto;

import br.com.query.query.clausula.ClausulaQuery;

import java.util.List;

public class ClausulaConjuntoQuery implements ClausulaQuery {

    private TipoConjuntoQuery type;
    private List<ClausulaQuery> clauses;

    public List<ClausulaQuery> getClauses() {
        return clauses;
    }

    public void setClauses(List<ClausulaQuery> clauses) {
        this.clauses = clauses;
    }

    public TipoConjuntoQuery getType() {
        return type;
    }

    public void setType(TipoConjuntoQuery type) {
        this.type = type;
    }
}
