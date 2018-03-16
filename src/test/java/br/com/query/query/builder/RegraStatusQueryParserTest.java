package br.com.query.query.builder;

import br.com.query.parser.RegraStatusQueryParser;
import br.com.query.parser.modelo.QueryRegraDinamicaStatus;
import br.com.query.regra.RegraDinamicaStatus;
import br.com.query.regra.query.clausula.ClausulaQuery;
import br.com.query.regra.query.clausula.TipoClausulaRegraDinamica;
import br.com.query.regra.query.conjunto.ClausulaConjuntoQuery;
import br.com.query.regra.query.conjunto.TipoConjuntoQuery;
import br.com.query.regra.query.tipo.QueryStatusEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class RegraStatusQueryParserTest {

    public static final String REGRA_GRUPO_BASE_WINDOWNAME_ = "_RegraGrupoBase_WINDOWNAME_";
    private RegraStatusQueryParser queryParser = new RegraStatusQueryParser();

    @Test
    public void deveGerarQueryWarningDeConjuntoANDCom2Condicoes() {
        // Given
        RegraDinamicaStatus regra = RegraDinamicaMock.mockRegraDinamicaStatus(QueryStatusEnum.WARNING);

        // When
        QueryRegraDinamicaStatus queryRegraDinamicaStatus = queryParser.criarQuery(REGRA_GRUPO_BASE_WINDOWNAME_,
                regra);

        // Then
        assertEquals("INSERT INTO _RegraGrupoBase_WINDOWNAME_ " +
                "SELECT 'warning' FROM _0RegraGrupoBase_UUID_123Result, IC01Result " +
                "WHERE (_0RegraGrupoBase_UUID_123Result.status NOT IN ('ERROR', 'WARNING')) " +
                "AND (IC01Result.doubleValue > 300);", queryRegraDinamicaStatus.getQueryGerada());
    }

    @Test
    public void deveGerarQueryGoodDeConjuntoANDComCondicaoEConjuntoORComDuasCondicoes() {
        // Given
        RegraDinamicaStatus regra = new RegraDinamicaStatus();
        regra.setStatus(QueryStatusEnum.GOOD);
        regra.setTipoClausula(TipoClausulaRegraDinamica.CONJUNTO);

        ClausulaQuery clausulaCondicao = new ClausulaQuery();
        clausulaCondicao.setTipoClausula(TipoClausulaRegraDinamica.CONDICAO);
        clausulaCondicao.setCondicao(RegraDinamicaMock.createConditional1());

        ClausulaQuery clausulaConjuntoORComDuasCondicoes = new ClausulaQuery();
        clausulaConjuntoORComDuasCondicoes.setTipoClausula(TipoClausulaRegraDinamica.CONJUNTO);
        ClausulaConjuntoQuery conjuntoORCom2Condicoes = new ClausulaConjuntoQuery();
        conjuntoORCom2Condicoes.setTipoConjunto(TipoConjuntoQuery.OR);
        ClausulaQuery condicao1 = new ClausulaQuery(TipoClausulaRegraDinamica.CONDICAO, RegraDinamicaMock.createConditional1(), null);
        ClausulaQuery condicao2 = new ClausulaQuery(TipoClausulaRegraDinamica.CONDICAO, RegraDinamicaMock.createConditional2(), null);
        conjuntoORCom2Condicoes.setClausulas(Arrays.asList(condicao1, condicao2));
        clausulaConjuntoORComDuasCondicoes.setConjunto(conjuntoORCom2Condicoes);

        ClausulaConjuntoQuery conjuntoCom1CondicaoANDConjuntoCom2Condicoes = new ClausulaConjuntoQuery();
        conjuntoCom1CondicaoANDConjuntoCom2Condicoes.setTipoConjunto(TipoConjuntoQuery.AND);
        conjuntoCom1CondicaoANDConjuntoCom2Condicoes.setClausulas(Arrays.asList(clausulaCondicao, clausulaConjuntoORComDuasCondicoes));
        regra.setConjunto(conjuntoCom1CondicaoANDConjuntoCom2Condicoes);

        // When
        QueryRegraDinamicaStatus queryRegraDinamicaStatus = queryParser.criarQuery(REGRA_GRUPO_BASE_WINDOWNAME_, regra);

        // Then
        assertEquals("INSERT INTO _RegraGrupoBase_WINDOWNAME_ SELECT 'good' " +
                "FROM _0RegraGrupoBase_UUID_123Result, IC01Result " +
                "WHERE (_0RegraGrupoBase_UUID_123Result.status NOT IN ('ERROR', 'WARNING')) " +
                    "AND ((_0RegraGrupoBase_UUID_123Result.status NOT IN ('ERROR', 'WARNING')) " +
                    "OR (IC01Result.doubleValue > 300));", queryRegraDinamicaStatus.getQueryGerada());
    }

    @Test
    public void deveGerarQueryWarningDeCondicao() {
        // Given
        RegraDinamicaStatus regraWarning = new RegraDinamicaStatus();
        regraWarning.setStatus(QueryStatusEnum.WARNING);
        regraWarning.setTipoClausula(TipoClausulaRegraDinamica.CONDICAO);
        regraWarning.setCondicao(RegraDinamicaMock.createConditional3());

        // When
        QueryRegraDinamicaStatus queryRegraDinamicaStatus = queryParser.criarQuery(REGRA_GRUPO_BASE_WINDOWNAME_, regraWarning);

        // Then
        assertEquals("INSERT INTO _RegraGrupoBase_WINDOWNAME_ " +
                "SELECT 'warning' FROM _0RegraGrupoBase_UUID_851574Result " +
                "WHERE (_0RegraGrupoBase_UUID_851574Result.status = 'ERROR');", queryRegraDinamicaStatus.getQueryGerada());
    }
}
