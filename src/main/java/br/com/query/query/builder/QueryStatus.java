package br.com.query.query.builder;

import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class QueryStatus {

    private String queryGerada;
    private List<String> erros;

    public QueryStatus() {
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
