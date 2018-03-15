package br.com.query.query.builder;

import br.com.query.parser.RegraStatusQueryParser;
import br.com.query.parser.modelo.QueryStatus;
import br.com.query.regra.RegraDinamicaStatus;
import br.com.query.regra.query.clausula.TipoClausulaRegraDinamica;
import br.com.query.regra.query.tipo.QueryStatusEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class RegraStatusQueryParserTest {

    public static final String REGRA_GRUPO_BASE_WINDOWNAME_ = "_RegraGrupoBase_WINDOWNAME_";
    private RegraStatusQueryParser queryBuilder = new RegraStatusQueryParser();

    @Test
    public void deveGerarQueryWarningDeConjuntoANDCom2Condicoes() {
        // Given
        RegraDinamicaStatus regra = RegraDinamicaMock.mockRegraDinamicaStatus(QueryStatusEnum.WARNING);

        // When
        QueryStatus queryStatus = queryBuilder.criarQuery(REGRA_GRUPO_BASE_WINDOWNAME_,
                regra);

        // Then
        assertEquals("INSERT INTO _RegraGrupoBase_WINDOWNAME_ " +
                "SELECT 'warning' FROM _0RegraGrupoBase_UUID_123Result, IC01Result " +
                "WHERE (_0RegraGrupoBase_UUID_123Result.status NOT IN ('ERROR', 'WARNING')) " +
                "AND (IC01Result.doubleValue > 300);", queryStatus.getQueryGerada());
    }

    @Test
    public void deveGerarQueryWarningDeCondicao() {
        // Given
        RegraDinamicaStatus regraWarning = new RegraDinamicaStatus();
        regraWarning.setStatus(QueryStatusEnum.WARNING);
        regraWarning.setTipoClausula(TipoClausulaRegraDinamica.CONDICAO);
        regraWarning.setCondicao(RegraDinamicaMock.createConditional3());

        // When
        QueryStatus queryStatus = queryBuilder.criarQuery(REGRA_GRUPO_BASE_WINDOWNAME_, regraWarning);

        // Then
        assertEquals("INSERT INTO _RegraGrupoBase_WINDOWNAME_ " +
                "SELECT 'warning' FROM _0RegraGrupoBase_UUID_851574Result " +
                "WHERE (_0RegraGrupoBase_UUID_851574Result.status = 'ERROR');", queryStatus.getQueryGerada());
    }
}
