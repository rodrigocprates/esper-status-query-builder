package br.com.query.query.builder;

import br.com.query.excecoes.RegraDinamicaQueryBuilderException;
import br.com.query.query.tipo.QueryStatusEnum;
import br.com.query.regra.RegraDinamicaStatus;
import br.com.query.regra.TipoClausulaRegraDinamica;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(JUnit4.class)
public class RegraStatusQueryBuilderTest {

    public static final String REGRA_GRUPO_BASE_WINDOWNAME_ = "_RegraGrupoBase_WINDOWNAME_";

    /**
     Cenários de teste:
        validar se eventos no "from" aparecem mais de uma vez
        validar sempre que claúsulas tenham no mínimo 2 conjuntos/codicoes

     */

    @Test
    public void deveGerarQueryWarning() {
        String queryGerada = RegraStatusQueryBuilder.criarQuery(REGRA_GRUPO_BASE_WINDOWNAME_,
                RegraDinamicaMock.mockRegraDinamicaStatus(QueryStatusEnum.WARNING));

        assertEquals("INSERT INTO _RegraGrupoBase_WINDOWNAME_ " +
                "SELECT 'warning' " +
                "FROM _0RegraGrupoBase_UUID_123Result, IC01Result, _0RegraGrupoBase_UUID_851574Result " +
                "WHERE ((_0RegraGrupoBase_UUID_123Result.status NOT IN ('ERROR', 'WARNING')) " +
                    "AND (IC01Result.doubleValue > 300)) " +
                "OR (_0RegraGrupoBase_UUID_851574Result.status = 'ERROR')", queryGerada);
    }

    @Test
    public void deveLancarExceptionAoCriarRegraDeConjuntoComSomenteUmaClausula() {
        RegraDinamicaStatus regraComUmaClausula = RegraDinamicaMock.mockRegraDinamicaStatus(QueryStatusEnum.GOOD);
        regraComUmaClausula.setTipoClausula(TipoClausulaRegraDinamica.CONDICAO);
        try {
            RegraStatusQueryBuilder.criarQuery(REGRA_GRUPO_BASE_WINDOWNAME_, regraComUmaClausula);
            fail();
        } catch(RegraDinamicaQueryBuilderException e) {
            assertEquals("Para criar uma regra com uma única " +
                    "condição é necessário informar somente uma cláusula.", e.getMessage());
        }
    }

    @Test
    public void deveLancarExceptionAoCriarRegraDeCondicaoComMaisDeUmaClausula() {
        RegraDinamicaStatus regraComUmaClausula = RegraDinamicaMock.mockRegraDinamicaStatus(QueryStatusEnum.GOOD);
        regraComUmaClausula.setClausulas(Arrays.asList(RegraDinamicaMock.createClause1()));

        try {
            RegraStatusQueryBuilder.criarQuery(REGRA_GRUPO_BASE_WINDOWNAME_, regraComUmaClausula);
            fail();
        } catch(RegraDinamicaQueryBuilderException e) {
            assertEquals("Para criar uma regra com " +
                    "conjunto OR é necessário informar no mínimo duas cláusulas.", e.getMessage());
        }
    }

    @Test
    public void deveLancarExcecaoSeNomeJanelaVazio() {
        try {
            RegraStatusQueryBuilder.criarQuery(null, null);
            fail();
        } catch (RegraDinamicaQueryBuilderException e) {
            assertEquals("Nome da janela não pode ser vazio.", e.getMessage());
        }
    }

    @Test
    public void deveLancarExcecaoSeRegraVazia() {
        try {
            RegraStatusQueryBuilder.criarQuery(REGRA_GRUPO_BASE_WINDOWNAME_, new RegraDinamicaStatus());
            fail();
        } catch (RegraDinamicaQueryBuilderException e) {
            assertEquals("É necessário adicionar ao menos uma cláusula para criar uma regra.", e.getMessage());
        }
    }

}
