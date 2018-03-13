package br.com.query.service;

import br.com.query.modelo.QueriesRegraDinamica;
import br.com.query.query.builder.RegraStatusQueryBuilder;
import br.com.query.regra.RegraDinamica;

import java.util.ArrayList;
import java.util.List;
public class RegraDinamicaService {

    private RegraStatusQueryBuilder queryBuilder;

    public RegraDinamicaService(RegraStatusQueryBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
    }

    public QueriesRegraDinamica gerarQueries(RegraDinamica regra) {
        QueriesRegraDinamica queries = new QueriesRegraDinamica();

        String nomeJanela = criarNomeJanela(regra);

        // TODO ver como montar objeto com error, se faz tudo no service ou cria no obj um Map chave(good, warning, window, etc) e list de erros de cada?
        queries.setQueryCriacaoJanela(gerarQueryCriacaoJanela(nomeJanela));
        queries.setQueryGood(queryBuilder.criarQuery(nomeJanela, regra.getGood()).getQueryGerada());
        queries.setQueryWarning(queryBuilder.criarQuery(nomeJanela, regra.getWarning()).getQueryGerada());
        queries.setQueryError(queryBuilder.criarQuery(nomeJanela, regra.getError()).getQueryGerada());
        queries.setQueryStatus(gerarSelectMonitoringAction(nomeJanela));

        return queries;
    }

    private String gerarSelectMonitoringAction(String nomeJanela) {
        return new StringBuilder()
                .append("select status as monitoringAction from ") // TODO centralizar keywords no motor
                .append(nomeJanela)
                .append(";").toString();
    }

    private String gerarQueryCriacaoJanela(String nomeJanela) {
        return new StringBuilder()
                .append("create window ") // TODO centralizar keywords no motor
                .append(nomeJanela)
                .append(".std:lastevent() as (status string);").toString();
    }

    private String criarNomeJanela(RegraDinamica regra) {
        return new StringBuilder()
                .append("_0RegraGrupoBase_") // TODO centralizar keywords no motor
                .append(regra.getEventoBase().getId().replaceAll("-", "_"))
                .append("Result").toString();
    }

}
