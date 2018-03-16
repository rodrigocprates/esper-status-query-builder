package br.com.query.service;

import br.com.query.parser.RegraStatusQueryParser;
import br.com.query.parser.modelo.QueriesRegraDinamica;
import br.com.query.regra.RegraDinamica;

public class RegraDinamicaService {

    private RegraStatusQueryParser queryParser;

    public RegraDinamicaService(RegraStatusQueryParser queryParser) {
        this.queryParser = queryParser;
    }

    public QueriesRegraDinamica gerarQueries(RegraDinamica regra) {
        QueriesRegraDinamica queries = new QueriesRegraDinamica();

        String nomeJanela = criarNomeJanela(regra);

        queries.setQueryCriacaoJanela(gerarQueryCriacaoJanela(nomeJanela));
        queries.setQueryGood(queryParser.criarQuery(nomeJanela, regra.getGood()).getQueryGerada());
        queries.setQueryWarning(queryParser.criarQuery(nomeJanela, regra.getWarning()).getQueryGerada());
        queries.setQueryError(queryParser.criarQuery(nomeJanela, regra.getError()).getQueryGerada());
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
