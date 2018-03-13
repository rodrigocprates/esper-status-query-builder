package br.com.query.app;

import br.com.query.modelo.QueriesRegraDinamica;
import br.com.query.query.builder.RegraStatusQueryBuilder;
import br.com.query.query.clausula.ClausulaQuery;
import br.com.query.query.condicao.CategoriaCondicaoQuery;
import br.com.query.query.condicao.ClausulaCondicaoQuery;
import br.com.query.query.conjunto.ClausulaConjuntoQuery;
import br.com.query.query.conjunto.TipoConjuntoQuery;
import br.com.query.query.evento.Evento;
import br.com.query.query.tipo.QueryStatusEnum;
import br.com.query.query.tipo.TipoOperadorQuery;
import br.com.query.regra.RegraDinamica;
import br.com.query.regra.RegraDinamicaStatus;
import br.com.query.query.clausula.TipoClausulaRegraDinamica;
import br.com.query.service.RegraDinamicaService;

import java.util.Arrays;
import java.util.List;

public class App {

    public static void main(String[] args) {

        RegraDinamicaService regraDinamicaService = new RegraDinamicaService(new RegraStatusQueryBuilder());
        RegraDinamica regraDinamica = new RegraDinamica();
        regraDinamica.setWarning(mockRegraDinamicaStatus(QueryStatusEnum.WARNING));
        regraDinamica.setGood(mockRegraDinamicaStatus(QueryStatusEnum.GOOD));
        regraDinamica.setError(mockRegraDinamicaStatus(QueryStatusEnum.ERROR));
        regraDinamica.setEventoBase(new Evento(Evento.TipoEventoCondicao.GRUPO, "ec4f75e0-d63d-4d25-83af-5707167a3927"));
        QueriesRegraDinamica queriesRegraDinamica = regraDinamicaService.gerarQueries(regraDinamica);

        System.out.println(queriesRegraDinamica);

        //System.out.println(RegraStatusQueryBuilder.criarQuery("regra_base", regraStatusDinamica));
        //System.out.println(RegraStatusQueryBuilder.criarQuery("regra_base2", mockCustomRuleCondition()));
    }

    private static RegraDinamicaStatus mockCustomRuleCondition() {
        RegraDinamicaStatus regraStatusDinamica = new RegraDinamicaStatus();
        regraStatusDinamica.setTipoClausula(TipoClausulaRegraDinamica.CONDICAO);
        regraStatusDinamica.setStatus(QueryStatusEnum.GOOD);
        regraStatusDinamica.setCondicao(createConditional1());
        return regraStatusDinamica;
    }

    private static List<ClausulaQuery> createClauses() {
        ClausulaQuery clausulaQuery = new ClausulaQuery();
        clausulaQuery.setTipoClausula(TipoClausulaRegraDinamica.CONDICAO);
        clausulaQuery.setCondicao(createConditional3());
        return Arrays.asList(clausulaQuery);
    }

    private static ClausulaCondicaoQuery createConditional1() {
        ClausulaCondicaoQuery q = new ClausulaCondicaoQuery();
        q.setEvento(new Evento(Evento.TipoEventoCondicao.GRUPO, "UUID_123"));
        q.setCategoriaCondicao(CategoriaCondicaoQuery.STATUS_GRUPO);
        q.setOperador(TipoOperadorQuery.NAO_ESTA_CONTIDO_EM);
        q.setValorComparado(Arrays.asList(QueryStatusEnum.ERROR.toString(), QueryStatusEnum.WARNING.toString()));
        return q;
    }

    private static ClausulaCondicaoQuery createConditional2() {
        ClausulaCondicaoQuery q = new ClausulaCondicaoQuery();
        q.setEvento(new Evento(Evento.TipoEventoCondicao.IC, "IC01"));
        q.setCategoriaCondicao(CategoriaCondicaoQuery.CONDICAO_IC);
        q.setOperador(TipoOperadorQuery.MAIOR_QUE);
        q.setValorComparado(300);
        return q;
    }

    private static ClausulaCondicaoQuery createConditional3() {
        ClausulaCondicaoQuery q = new ClausulaCondicaoQuery();
        q.setEvento(new Evento(Evento.TipoEventoCondicao.GRUPO, "UUID_851574"));
        q.setCategoriaCondicao(CategoriaCondicaoQuery.STATUS_GRUPO);
        q.setOperador(TipoOperadorQuery.IGUAL_A);
        q.setValorComparado(QueryStatusEnum.ERROR);
        return q;
    }

    private static RegraDinamicaStatus mockRegraDinamicaStatus(QueryStatusEnum status) {
        RegraDinamicaStatus regraStatusDinamica = new RegraDinamicaStatus();
        regraStatusDinamica.setStatus(status);
        regraStatusDinamica.setTipoClausula(TipoClausulaRegraDinamica.CONJUNTO);
        regraStatusDinamica.setConjunto(createConjunto());
        return regraStatusDinamica;
    }

    private static ClausulaConjuntoQuery createConjunto() {

        ClausulaConjuntoQuery clausulaConjuntoQuery = new ClausulaConjuntoQuery();
        clausulaConjuntoQuery.setClausulas(createClauses());
        clausulaConjuntoQuery.setTipoConjunto(TipoConjuntoQuery.AND);
        return clausulaConjuntoQuery;


    }

}
