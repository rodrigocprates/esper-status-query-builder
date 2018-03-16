package br.com.query.parser;

import br.com.query.excecoes.RegraDinamicaQueryParserException;
import br.com.query.parser.modelo.QueryRegraDinamicaStatus;
import br.com.query.regra.RegraDinamicaStatus;
import br.com.query.regra.query.clausula.ClausulaQuery;
import br.com.query.regra.query.clausula.TipoClausulaRegraDinamica;
import br.com.query.regra.query.condicao.CategoriaCondicaoQuery;
import br.com.query.regra.query.condicao.ClausulaCondicaoQuery;
import br.com.query.regra.query.conjunto.ClausulaConjuntoQuery;
import br.com.query.regra.query.evento.Evento;
import br.com.query.regra.query.tipo.TipoOperadorQuery;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class RegraStatusQueryParser {

    public static final String ESPACO = " ";
    public static final String PARENTESES_ESQUERDA = "(";
    public static final String PARENTESES_DIREITA = ")";
    public static final String STATUS_ATTRIBUTE = ".status";
    public static final String DOUBLE_VALUE_ATTRIBUTE = ".doubleValue";
    public static final String SINGLE_QUOT_MARK = "'";
    public static final String COMMA_IN_NOT_IN_CLAUSE = ", ";

    private QueryRegraDinamicaStatus queryRegraDinamicaStatus;

    public QueryRegraDinamicaStatus criarQuery(String nomeJanela, RegraDinamicaStatus regraStatusDinamica) {
        queryRegraDinamicaStatus = new QueryRegraDinamicaStatus();

        String queryGerada = new StringBuilder()
                .append(gerarInsertNaWindow(nomeJanela))
                .append(gerarSelect(regraStatusDinamica))
                .append(gerarFromDoSelect(regraStatusDinamica))
                .append(gerarWhereDoSelect(regraStatusDinamica))
                .append(";")
                .toString();

        if (CollectionUtils.isEmpty(queryRegraDinamicaStatus.getErros()))
            queryRegraDinamicaStatus.setQueryGerada(queryGerada);

        return queryRegraDinamicaStatus;
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
                throw new RegraDinamicaQueryParserException("Só é possível criar cláusulas de CONJUNTO ou CONDIÇÃO.");
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
                .flatMap(c -> {
                    if (TipoClausulaRegraDinamica.CONDICAO.equals(c.getTipoClausula()))
                        return asList(extrairNomeEventoCondicao(c.getCondicao())).stream();
                    else if (TipoClausulaRegraDinamica.CONJUNTO.equals(c.getTipoClausula()))
                        return extrairNomesEventosConjunto(c.getConjunto().getClausulas()).stream();

                    return Collections.<String>emptyList().stream();
                })
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.joining(", "));
    }

    private List<String> extrairNomesEventosConjunto(List<ClausulaQuery> clausulas) {
        return clausulas
                .stream()
                .flatMap(c -> {
                    if (TipoClausulaRegraDinamica.CONDICAO.equals(c.getTipoClausula()))
                        return asList(extrairNomeEventoCondicao(c.getCondicao())).stream();
                    else if (TipoClausulaRegraDinamica.CONJUNTO.equals(c.getTipoClausula()))
                        return extrairNomesEventosConjunto(c.getConjunto().getClausulas()).stream();

                    return Collections.<String>emptyList().stream();
                })
                .collect(Collectors.toList());
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

        throw new RegraDinamicaQueryParserException("É necessário inserir uma única cláusula do tipo 'CONDICAO' para gerar a query.");
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

        throw new RegraDinamicaQueryParserException("There's no Query Clause implementation class found.");
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
                return formatarListaParaClausulaInNotIn(condicaoClausula.getValorComparado());

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
            return "_0RegraGrupoBase_" + condicao.getEvento().getId().replaceAll("-", "_") + "Result"; // TODO centralizar keywords
        else if (Evento.TipoEventoCondicao.IC.equals(condicao.getEvento().getTipo())) {
            return condicao.getEvento().getId() + "Result"; // TODO centralizar keywords no motor
        }
        return null;
    }

    private String formatarAtributoEvento(ClausulaCondicaoQuery condicao) {
        if (CategoriaCondicaoQuery.STATUS_GRUPO.equals(condicao.getCategoriaCondicao()) || CategoriaCondicaoQuery.STATUS_IC.equals(condicao.getCategoriaCondicao()))
            return STATUS_ATTRIBUTE;
        else if (CategoriaCondicaoQuery.VALOR_IC.equals(condicao.getCategoriaCondicao()))
            return DOUBLE_VALUE_ATTRIBUTE;

        return null;
    }

    private String formatarListaParaClausulaInNotIn(Object match) {
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
