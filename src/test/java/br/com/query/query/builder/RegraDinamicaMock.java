package br.com.query.query.builder;

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

import java.util.Arrays;
import java.util.List;

public final class RegraDinamicaMock {

    public RegraDinamica mockRegraDinamica() {
        RegraDinamica regraDinamica = new RegraDinamica();
        regraDinamica.setWarning(mockRegraDinamicaStatus(QueryStatusEnum.GOOD));
        regraDinamica.setWarning(mockRegraDinamicaStatus(QueryStatusEnum.ERROR));
        regraDinamica.setWarning(mockRegraDinamicaStatus(QueryStatusEnum.WARNING));
        regraDinamica.setEventoBase(new Evento(Evento.TipoEventoCondicao.GRUPO, "ec4f75e0-d63d-4d25-83af-5707167a3927"));
        return regraDinamica;
    }

    public static RegraDinamicaStatus mockCustomRuleCondition() {
        RegraDinamicaStatus regraStatusDinamica = new RegraDinamicaStatus();
        regraStatusDinamica.setTipoClausula(TipoClausulaRegraDinamica.CONDICAO);
        regraStatusDinamica.setStatus(QueryStatusEnum.GOOD);
        regraStatusDinamica.setCondicao(createConditional1());
        return regraStatusDinamica;
    }

    public static List<ClausulaQuery> createClauses() {
        ClausulaQuery clausulaQuery1 = new ClausulaQuery();
        clausulaQuery1.setTipoClausula(TipoClausulaRegraDinamica.CONDICAO);
        clausulaQuery1.setCondicao(createConditional1());

        ClausulaQuery clausulaQuery2 = new ClausulaQuery();
        clausulaQuery2.setTipoClausula(TipoClausulaRegraDinamica.CONDICAO);
        clausulaQuery2.setCondicao(createConditional2());

        return Arrays.asList(clausulaQuery1, clausulaQuery2);
    }

    public static ClausulaQuery createClause1() {
        ClausulaConjuntoQuery q = new ClausulaConjuntoQuery();
        q.setTipoConjunto(TipoConjuntoQuery.AND);
        q.setClausulas(Arrays.asList(createClause1(), createClause2()));

        ClausulaQuery clausulaQuery = new ClausulaQuery();
        clausulaQuery.setTipoClausula(TipoClausulaRegraDinamica.CONJUNTO);
        clausulaQuery.setConjunto(q);
        return clausulaQuery;

    }

    public static ClausulaCondicaoQuery createConditional1() {
        ClausulaCondicaoQuery q = new ClausulaCondicaoQuery();
        q.setEvento(new Evento(Evento.TipoEventoCondicao.GRUPO, "UUID_123"));
        q.setCategoriaCondicao(CategoriaCondicaoQuery.STATUS_GRUPO);
        q.setOperador(TipoOperadorQuery.NAO_ESTA_CONTIDO_EM);
        q.setValorComparado(Arrays.asList(QueryStatusEnum.ERROR.toString(), QueryStatusEnum.WARNING.toString()));
        return q;
    }

    public static ClausulaCondicaoQuery createConditional2() {
        ClausulaCondicaoQuery q = new ClausulaCondicaoQuery();
        q.setEvento(new Evento(Evento.TipoEventoCondicao.IC, "IC01"));
        q.setCategoriaCondicao(CategoriaCondicaoQuery.CONDICAO_IC);
        q.setOperador(TipoOperadorQuery.MAIOR_QUE);
        q.setValorComparado(300);
        return q;
    }

    public static ClausulaQuery createClause2() {
        ClausulaQuery c = new ClausulaQuery();
        c.setTipoClausula(TipoClausulaRegraDinamica.CONDICAO);
        c.setCondicao(createConditional1());
        return c;
    }

    public static ClausulaCondicaoQuery createConditional3() {
        ClausulaCondicaoQuery q = new ClausulaCondicaoQuery();
        q.setEvento(new Evento(Evento.TipoEventoCondicao.GRUPO, "UUID_851574"));
        q.setCategoriaCondicao(CategoriaCondicaoQuery.STATUS_GRUPO);
        q.setOperador(TipoOperadorQuery.IGUAL_A);
        q.setValorComparado(QueryStatusEnum.ERROR);
        return q;
    }

    public static RegraDinamicaStatus mockRegraDinamicaStatus(QueryStatusEnum status) {
        RegraDinamicaStatus regraStatusDinamica = new RegraDinamicaStatus();
        regraStatusDinamica.setStatus(status);
        regraStatusDinamica.setTipoClausula(TipoClausulaRegraDinamica.CONJUNTO);
        regraStatusDinamica.setConjunto(createConjunto());
        return regraStatusDinamica;
    }

    private static ClausulaConjuntoQuery createConjunto() {
        ClausulaConjuntoQuery c = new ClausulaConjuntoQuery();
        c.setTipoConjunto(TipoConjuntoQuery.AND);
        c.setClausulas(createClauses());
        return c;
    }

}
