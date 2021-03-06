package br.com.query.parser.modelo;

import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class QueryRegraDinamicaStatus {

    private String queryGerada;
    private List<String> erros;

    public QueryRegraDinamicaStatus() {
        this.erros = new ArrayList<>();
    }

    public Boolean contemErros() {
        return !CollectionUtils.isEmpty(erros);
    }

    public String getQueryGerada() {
        return queryGerada;
    }

    public void setQueryGerada(String queryGerada) {
        this.queryGerada = queryGerada;
    }

    public List<String> getErros() {
        return erros;
    }

    public void setErros(List<String> erros) {
        this.erros = erros;
    }
}
