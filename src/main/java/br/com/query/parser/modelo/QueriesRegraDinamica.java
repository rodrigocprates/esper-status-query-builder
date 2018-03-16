package br.com.query.parser.modelo;

public class QueriesRegraDinamica {

    private String queryCriacaoJanela;
    private String queryGood;
    private String queryWarning;
    private String queryError;
    private String queryStatus;

    public String getQueryCriacaoJanela() {
        return queryCriacaoJanela;
    }

    public void setQueryCriacaoJanela(String queryCriacaoJanela) {
        this.queryCriacaoJanela = queryCriacaoJanela;
    }

    public String getQueryGood() {
        return queryGood;
    }

    public void setQueryGood(String queryGood) {
        this.queryGood = queryGood;
    }

    public String getQueryWarning() {
        return queryWarning;
    }

    public void setQueryWarning(String queryWarning) {
        this.queryWarning = queryWarning;
    }

    public String getQueryError() {
        return queryError;
    }

    public void setQueryError(String queryError) {
        this.queryError = queryError;
    }

    public String getQueryStatus() {
        return queryStatus;
    }

    public void setQueryStatus(String queryStatus) {
        this.queryStatus = queryStatus;
    }

    @Override
    public String toString() {
        return "Queries Regra Dinamica: \n" +
                queryCriacaoJanela + "\n" +
                queryGood + "\n" +
                queryWarning + "\n" +
                queryError + "\n" +
                queryStatus + "\n";
    }
}
