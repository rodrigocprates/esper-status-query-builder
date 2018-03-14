package br.com.query.parser;

import br.com.query.excecoes.RegraDinamicaQueryBuilderException;
import br.com.query.parser.modelo.QueryStatus;
import br.com.query.regra.RegraDinamicaStatus;
import br.com.query.regra.query.clausula.ClausulaQuery;
import br.com.query.regra.query.clausula.TipoClausulaRegraDinamica;
import br.com.query.regra.query.condicao.CategoriaCondicaoQuery;
import br.com.query.regra.query.condicao.ClausulaCondicaoQuery;
import br.com.query.regra.query.conjunto.ClausulaConjuntoQuery;
import br.com.query.regra.query.evento.Evento;
import br.com.query.regra.query.tipo.TipoOperadorQuery;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RegraStatusQueryParser {

    public static final String ESPACO = " ";
    public static final String PARENTESES_ESQUERDA = "(";
    public static final String PARENTESES_DIREITA = ")";
    public static final String STATUS_ATTRIBUTE = ".status";
    public static final String DOUBLE_VALUE_ATTRIBUTE = ".doubleValue";
    public static final String SINGLE_QUOT_MARK = "'";
    public static final String COMMA_IN_NOT_IN_CLAUSE = ", ";

    private QueryStatus queryStatus;

    public QueryStatus criarQuery(String nomeJanela, RegraDinamicaStatus regraStatusDinamica) {
        //validarDadosEntrada(nomeJanela, regraStatusDinamica); // TODO quais validações ?

        queryStatus = new QueryStatus();

        String queryGerada = new StringBuilder()
                .append(gerarInsertNaWindow(nomeJanela))
                .append(gerarSelect(regraStatusDinamica))
                .append(gerarFromDoSelect(regraStatusDinamica))
                .append(gerarWhereDoSelect(regraStatusDinamica))
                .append(";")
                .toString();

        if (CollectionUtils.isEmpty(queryStatus.getErros()))
            queryStatus.setQueryGerada(queryGerada);

        return queryStatus;
    }

    private void validarDadosEntrada(String nomeJanela, RegraDinamicaStatus regraStatusDinamica) {
/**        // TODO Externalizar para um Validator

 if (nomeJanela == null)
 throw new RegraDinamicaQueryBuilderException("Nome da janela não pode ser vazio.");

 if (regraStatusDinamica == null || CollectionUtils.isEmpty(regraStatusDinamica.getClausulas()))
 throw new RegraDinamicaQueryBuilderException("É necessário adicionar ao menos uma cláusula para criar uma regra.");

 if (regraStatusDinamica.getTipoClausula().equals(TipoClausulaRegraDinamica.CONJUNTO)
 && regraStatusDinamica.getClausulas().size() < 2) {
 throw new RegraDinamicaQueryBuilderException(String.format("Para criar uma regra com conjunto %s é necessário informar no mínimo duas cláusulas.", regraStatusDinamica.getTipoConjunto()));
 } else if (regraStatusDinamica.getTipoClausula().equals(TipoClausulaRegraDinamica.CONDICAO)
 && regraStatusDinamica.getClausulas().size() > 1)
 throw new RegraDinamicaQueryBuilderException(String.format("Para criar uma regra com uma única condição é necessário informar somente uma cláusula.", regraStatusDinamica.getTipoConjunto()));
 **/

        // TODO validar todos dados de entrada do objeto de regra - ver se usa @Valid do pra camada de API
    }


    private String gerarInsertNaWindow(String regraBase) {
        return "INSERT INTO " + regraBase + ESPACO;
    }

    private String gerarWhereDoSelect(RegraDinamicaStatus regraStatusDinamica) {
        StringBuilder whereSelect = new StringBuilder("WHERE ");

        switch (regraStatusDinamica.getTipoClausula()) {
            case CONDICAO:
                whereSelect.append(gerarQueryCondicao(regraStatusDinamica));
                break;
            case CONJUNTO:
                whereSelect.append(gerarQueryConjunto(regraStatusDinamica));
                break;
            default:
                throw new RegraDinamicaQueryBuilderException("Só é possível criar cláusulas de CONJUNTO ou CONDIÇÃO.");
        }

        return whereSelect.toString();
    }

    private String gerarFromDoSelect(RegraDinamicaStatus regraStatusDinamica) {
        String eventosSeparadoPorVirgula = "";

        if (TipoClausulaRegraDinamica.CONDICAO.equals(regraStatusDinamica.getTipoClausula()))
            eventosSeparadoPorVirgula = mapearEventosDoFromQuandoCondicao(regraStatusDinamica.getCondicao());
        else if (TipoClausulaRegraDinamica.CONJUNTO.equals(regraStatusDinamica.getTipoClausula()))
            eventosSeparadoPorVirgula = mapearEventosDoFromQuandoConjunto(regraStatusDinamica.getConjunto());

        return new StringBuilder("FROM ").append(eventosSeparadoPorVirgula).append(ESPACO).toString();
    }

    private String mapearEventosDoFromQuandoCondicao(ClausulaCondicaoQuery condicao) {
        return extrairNomeEventoCondicao(condicao);
    }

    private String mapearEventosDoFromQuandoConjunto(ClausulaConjuntoQuery conjunto) {
        return conjunto
                .getClausulas()
                .stream()
                .map(c -> {
                    if (TipoClausulaRegraDinamica.CONDICAO.equals(c.getTipoClausula()))
                        return extrairNomeEventoCondicao(c.getCondicao());
                    else if (TipoClausulaRegraDinamica.CONJUNTO.equals(c.getTipoClausula()))
                        return extrairNomeEventoConjunto(c.getConjunto().getClausulas());

                    return null;
                })
                .filter(Objects::nonNull) // TODO otimizar filter, distinct
                .distinct()
                .collect(Collectors.joining(", "));
    }

    private String extrairNomeEventoConjunto(List<ClausulaQuery> clausulas) {
        return clausulas
                .stream()
                .map(c -> {
                    if (TipoClausulaRegraDinamica.CONDICAO.equals(c.getTipoClausula()))
                        return extrairNomeEventoCondicao(c.getCondicao());
                    else if (TipoClausulaRegraDinamica.CONJUNTO.equals(c.getTipoClausula()))
                        return extrairNomeEventoConjunto(c.getConjunto().getClausulas());
                    return null;
                }).collect(Collectors.joining(", "));
    }

    private String extrairNomeEventoCondicao(ClausulaCondicaoQuery condicao) {
        return formatarNomeEvento(condicao);
    }

    private String gerarSelect(RegraDinamicaStatus regraStatusDinamica) {
        return "SELECT '" + regraStatusDinamica.getStatus().toString().toLowerCase() + "' ";
    }

    private String gerarQueryCondicao(RegraDinamicaStatus regraStatusDinamica) {
        if (TipoClausulaRegraDinamica.CONDICAO.equals(regraStatusDinamica.getTipoClausula())
                && (regraStatusDinamica.getCondicao() != null)) {
            return criarClausulaCondicao(regraStatusDinamica.getCondicao());
        }

        throw new RegraDinamicaQueryBuilderException("É necessário inserir uma única cláusula do tipo 'CONDICAO' para gerar a query.");
    }

    private String gerarQueryConjunto(RegraDinamicaStatus regraStatusDinamica) {
        return regraStatusDinamica
                .getConjunto()
                .getClausulas()
                .stream()
                .map(clause -> criarClausula(clause))
                .filter(Objects::nonNull)
                .collect(Collectors.joining(ESPACO + regraStatusDinamica.getConjunto().getTipoConjunto() + ESPACO));
    }

    private String criarClausula(ClausulaQuery clausula) {
        if (clausula == null)
            return null;

        if (TipoClausulaRegraDinamica.CONJUNTO.equals(clausula.getTipoClausula()))
            return criarClausulaConjunto(clausula.getConjunto());
        else if (TipoClausulaRegraDinamica.CONDICAO.equals(clausula.getTipoClausula()))
            return criarClausulaCondicao(clausula.getCondicao());

        throw new RegraDinamicaQueryBuilderException("There's no Query Clause implementation class found.");
    }

    private String criarClausulaConjunto(ClausulaConjuntoQuery conjunto) {
        StringBuilder aggregationQuery = new StringBuilder();

        String innerClauseString = conjunto.getClausulas()
                .stream()
                .map(c -> {
                    if (TipoClausulaRegraDinamica.CONDICAO.equals(c.getTipoClausula()))
                        return criarClausulaCondicao(c.getCondicao());
                    else if (TipoClausulaRegraDinamica.CONJUNTO.equals(c.getTipoClausula()))
                        return criarClausulaConjunto(c.getConjunto());
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.joining(ESPACO + conjunto.getTipoConjunto() + ESPACO));

        aggregationQuery.append(PARENTESES_ESQUERDA).append(innerClauseString).append(PARENTESES_DIREITA);
        return aggregationQuery.toString();
    }

    private String criarClausulaCondicao(ClausulaCondicaoQuery condicao) {
        if (condicao == null)
            return null;

        StringBuilder sb = new StringBuilder();

        if (condicao != null)
            sb.append(PARENTESES_ESQUERDA)
                    .append(formatarNomeEventoComAtributo(condicao))
                    .append(ESPACO)
                    .append(condicao.getOperador() == null ? null : condicao.getOperador().getSimbolo())
                    .append(ESPACO)
                    .append(formatarValorComparado(condicao))
                    .append(PARENTESES_DIREITA);

        return sb.toString();
    }

    private String formatarValorComparado(ClausulaCondicaoQuery condicaoClausula) {
        if (CategoriaCondicaoQuery.STATUS_GRUPO.equals(condicaoClausula.getCategoriaCondicao())
                || CategoriaCondicaoQuery.STATUS_IC.equals(condicaoClausula.getCategoriaCondicao())) {

            if (TipoOperadorQuery.ESTA_CONTIDO_EM.equals(condicaoClausula.getOperador()) || TipoOperadorQuery.NAO_ESTA_CONTIDO_EM.equals(condicaoClausula.getOperador()))
                return formatarListaParaClausulaIn(condicaoClausula.getValorComparado());

            return SINGLE_QUOT_MARK + condicaoClausula.getValorComparado() + SINGLE_QUOT_MARK;
        }

        return condicaoClausula.getValorComparado().toString();
    }

    private String formatarNomeEventoComAtributo(ClausulaCondicaoQuery condicao) {
        StringBuilder nomeEventoFormatado = new StringBuilder();

        return nomeEventoFormatado
                .append(formatarNomeEvento(condicao))
                .append(formatarAtributoEvento(condicao))
                .toString();
    }

    private String formatarNomeEvento(ClausulaCondicaoQuery condicao) {
        if (Evento.TipoEventoCondicao.GRUPO.equals(condicao.getEvento().getTipo()))
            return "_0RegraGrupoBase_" + condicao.getEvento().getId().replaceAll("-", "_") + "Result"; // TODO quando levar pro Motor montar esse nome usando classe centralizada
        else if (Evento.TipoEventoCondicao.IC.equals(condicao.getEvento().getTipo())) {
            return condicao.getEvento().getId() + "Result"; // TODO usar classe centralizada do motor também
        }
        return null;
    }

    private String formatarAtributoEvento(ClausulaCondicaoQuery condicao) {
        if (CategoriaCondicaoQuery.STATUS_GRUPO.equals(condicao.getCategoriaCondicao()) || CategoriaCondicaoQuery.STATUS_IC.equals(condicao.getCategoriaCondicao()))
            return STATUS_ATTRIBUTE;
        else if (CategoriaCondicaoQuery.CONDICAO_IC.equals(condicao.getCategoriaCondicao()))
            return DOUBLE_VALUE_ATTRIBUTE;

        return null;
    }

    private String formatarListaParaClausulaIn(Object match) { // TODO ver se podera fazer in de não-string - IN (1, 2, 3)
        if (match instanceof List) {
            List<String> conditionsList = (List<String>) match;

            if (!CollectionUtils.isEmpty(conditionsList))
                return conditionsList
                        .stream()
                        .map(c -> SINGLE_QUOT_MARK + c + SINGLE_QUOT_MARK)
                        .collect(Collectors.joining(COMMA_IN_NOT_IN_CLAUSE, PARENTESES_ESQUERDA, PARENTESES_DIREITA));
        }
        return null;
    }
}
