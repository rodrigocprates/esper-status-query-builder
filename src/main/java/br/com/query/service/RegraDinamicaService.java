package br.com.query.service;

import br.com.query.query.builder.RegraStatusQueryBuilder;
import br.com.query.regra.RegraDinamica;

import java.util.ArrayList;
import java.util.List;

public class RegraDinamicaService {

    public List<String> gerarQueries(RegraDinamica regra) {

        List<String> queries = new ArrayList<>();

        String nomeJanela = criarNomeJanela(regra);

        queries.add(gerarQueryCriacaoJanela(nomeJanela));
        queries.add(RegraStatusQueryBuilder.criarQuery(nomeJanela, regra.getGood()));
        queries.add(RegraStatusQueryBuilder.criarQuery(nomeJanela, regra.getWarning()));
        queries.add(RegraStatusQueryBuilder.criarQuery(nomeJanela, regra.getError()));
        queries.add(gerarSelectMonitoringAction(nomeJanela));

        return queries;
    }

    private String gerarSelectMonitoringAction(String nomeJanela) {
        return "select status as monitoringAction from " + nomeJanela; // TODO centralizar no motor
    }

    private String gerarQueryCriacaoJanela(String nomeJanela) {
        return "create window " + nomeJanela + ".std:lastevent() as (status string)"; // TODO centralizar no motor
    }

    private String criarNomeJanela(RegraDinamica regra) {
        return "_0RegraGrupoBase_" + regra.getEventoBase().getId().replaceAll("-", "_") + "Result"; // TODO centralizar no motor
    }

}
