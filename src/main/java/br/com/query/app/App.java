package br.com.query.app;

import br.com.query.parser.RegraStatusQueryParser;
import br.com.query.parser.modelo.QueriesRegraDinamica;
import br.com.query.regra.RegraDinamica;
import br.com.query.regra.RegraDinamicaStatus;
import br.com.query.regra.query.clausula.ClausulaQuery;
import br.com.query.regra.query.clausula.TipoClausulaRegraDinamica;
import br.com.query.regra.query.condicao.CategoriaCondicaoQuery;
import br.com.query.regra.query.condicao.ClausulaCondicaoQuery;
import br.com.query.regra.query.conjunto.ClausulaConjuntoQuery;
import br.com.query.regra.query.conjunto.TipoConjuntoQuery;
import br.com.query.regra.query.evento.Evento;
import br.com.query.regra.query.tipo.QueryStatusEnum;
import br.com.query.regra.query.tipo.TipoOperadorQuery;
import br.com.query.service.RegraDinamicaService;

import java.util.Arrays;
import java.util.List;

public class App {

    public static void main(String[] args) {

        RegraDinamicaService regraDinamicaService = new RegraDinamicaService(new RegraStatusQueryParser());
        RegraDinamica regraDinamica = new RegraDinamica();
        regraDinamica.setWarning(mockRegraDinamicaStatus(QueryStatusEnum.WARNING));
        regraDinamica.setGood(mockRegraDinamicaStatus(QueryStatusEnum.GOOD));
        regraDinamica.setError(mockRegraDinamicaStatus(QueryStatusEnum.ERROR));
        regraDinamica.setEventoBase(new Evento(Evento.TipoEventoCondicao.GRUPO, "ec4f75e0-d63d-4d25-83af-5707167a3927"));
        QueriesRegraDinamica queriesRegraDinamica = regraDinamicaService.gerarQueries(regraDinamica);

        System.out.println(queriesRegraDinamica);
    }


    private static List<ClausulaQuery> createClauses() {
        ClausulaQuery clausulaQuery1 = new ClausulaQuery();
        clausulaQuery1.setTipoClausula(TipoClausulaRegraDinamica.CONDICAO);
        clausulaQuery1.setCondicao(createConditional3());

        ClausulaQuery clausulaQuery2 = new ClausulaQuery();
        clausulaQuery2.setTipoClausula(TipoClausulaRegraDinamica.CONDICAO);
        clausulaQuery2.setCondicao(createConditional2());

        ClausulaQuery clausulaQuery3 = new ClausulaQuery();
        clausulaQuery3.setTipoClausula(TipoClausulaRegraDinamica.CONDICAO);
        clausulaQuery3.setCondicao(createConditional1());

        return Arrays.asList(clausulaQuery1, clausulaQuery2, clausulaQuery3, clausulaQuery3);
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
        q.setCategoriaCondicao(CategoriaCondicaoQuery.VALOR_IC);
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
