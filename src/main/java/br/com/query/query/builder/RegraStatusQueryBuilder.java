package br.com.query.query.builder;

import br.com.query.excecoes.RegraDinamicaQueryBuilderException;
import br.com.query.excecoes.TipoRegraDinamicaNaoEncontrada;
import br.com.query.query.clausula.ClausulaQuery;
import br.com.query.query.condicao.CategoriaCondicaoQuery;
import br.com.query.query.condicao.ClausulaCondicaoQuery;
import br.com.query.query.conjunto.ClausulaConjuntoQuery;
import br.com.query.query.evento.Evento;
import br.com.query.query.tipo.TipoOperadorQuery;
import br.com.query.regra.RegraDinamicaStatus;
import br.com.query.query.clausula.TipoClausulaRegraDinamica;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RegraStatusQueryBuilder {

    public static final String ESPACO = " ";
    public static final String PARENTESES_ESQUERDA = "(";
    public static final String PARENTESES_DIREITA = ")";
    public static final String STATUS_ATTRIBUTE = ".status";
    public static final String DOUBLE_VALUE_ATTRIBUTE = ".doubleValue";
    public static final String SINGLE_QUOT_MARK = "'";
    public static final String COMMA_IN_NOT_IN_CLAUSE = ", ";

    private QueryStatus queryStatus;

    public QueryStatus criarQuery(String nomeJanela, RegraDinamicaStatus regraStatusDinamica) {
        validarDadosEntrada(nomeJanela, regraStatusDinamica);

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
        // TODO Externalizar para um Validator

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
        String eventos = regraStatusDinamica
                .getClausulas()
                .stream()
                .map(c -> {
                    if (c instanceof ClausulaCondicaoQuery)
                        return extrairEventoCondicao((ClausulaCondicaoQuery) c);
                    else if (c instanceof ClausulaConjuntoQuery)
                        return extrairEventoClausula(((ClausulaConjuntoQuery) c).getClauses());

                    return null;
                })
                .filter(Objects::nonNull) // TODO otimizar filter, distinct
                .distinct()
                .collect(Collectors.joining(", "));

        return new StringBuilder("FROM ").append(eventos).append(ESPACO).toString();
    }

    private String extrairEventoClausula(List<ClausulaQuery> clauses) {
        return clauses
                .stream()
                .map(c -> {
                    if (c instanceof ClausulaCondicaoQuery)
                        return extrairEventoCondicao((ClausulaCondicaoQuery) c);
                    else if (c instanceof ClausulaConjuntoQuery)
                        return extrairEventoClausula(((ClausulaConjuntoQuery) c).getClauses());
                    return null;
                }).collect(Collectors.joining(", "));
    }

    private String extrairEventoCondicao(ClausulaCondicaoQuery condicao) {
        return formatarNomeEvento(condicao);
    }

    private String gerarSelect(RegraDinamicaStatus regraStatusDinamica) {
        return "SELECT '" + regraStatusDinamica.getStatus().toString().toLowerCase() + "' ";
    }

    private String gerarQueryCondicao(RegraDinamicaStatus regraStatusDinamica) {
        if (CollectionUtils.isEmpty(regraStatusDinamica.getClausulas()))
            throw new RegraDinamicaQueryBuilderException("É necessário inserir somente uma condição dentro da lista de cláusulas.");

        if (TipoClausulaRegraDinamica.CONDICAO.equals(regraStatusDinamica.getTipoClausula())
                && (regraStatusDinamica.getClausulas().get(0) instanceof ClausulaCondicaoQuery)) {
            ClausulaCondicaoQuery unicaCondicao = (ClausulaCondicaoQuery) regraStatusDinamica.getClausulas().get(0);
            return criarClausulaCondicao(unicaCondicao);
        }

        throw new RegraDinamicaQueryBuilderException("É necessário inserir uma única cláusula do tipo 'CONDICAO' para gerar a query.");
    }

    private String gerarQueryConjunto(RegraDinamicaStatus regraStatusDinamica) {
        return regraStatusDinamica
                .getClausulas()
                .stream()
                .map(clause -> criarClausula(clause))
                .filter(Objects::nonNull)
                .collect(Collectors.joining(ESPACO + regraStatusDinamica.getTipoConjunto() + ESPACO));
    }

    private String criarClausula(ClausulaQuery clause) {
        if (clause == null)
            return null;

        if (clause instanceof ClausulaConjuntoQuery)
            return criarClausulaConjunto(clause);
        else if (clause instanceof ClausulaCondicaoQuery)
            return criarClausulaCondicao(clause);

        throw new RegraDinamicaQueryBuilderException("There's no Query Clause implementation class found.");
    }

    private String criarClausulaConjunto(ClausulaQuery clause) {
        ClausulaConjuntoQuery agreggationClause = (ClausulaConjuntoQuery) clause;
        StringBuilder aggregationQuery = new StringBuilder();

        String innerClauseString = agreggationClause.getClauses()
                .stream()
                .map(c -> { // TODO pensar em criar a Factory
                    if (c instanceof ClausulaCondicaoQuery)
                        return criarClausulaCondicao(c);
                    else if (c instanceof ClausulaConjuntoQuery)
                        return criarClausulaConjunto(c);
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.joining(ESPACO + agreggationClause.getType() + ESPACO));

        aggregationQuery.append(PARENTESES_ESQUERDA).append(innerClauseString).append(PARENTESES_DIREITA);
        return aggregationQuery.toString();
    }

    private String criarClausulaCondicao(ClausulaQuery clause) {
        if (clause == null)
            return null;

        ClausulaCondicaoQuery condicaoClausula = ((ClausulaCondicaoQuery) clause);
        StringBuilder sb = new StringBuilder();

        if (condicaoClausula != null)
            sb.append(PARENTESES_ESQUERDA)
                    .append(formatarNomeEventoComAtributo(condicaoClausula))
                    .append(ESPACO)
                    .append(condicaoClausula.getOperador() == null ? null : condicaoClausula.getOperador().getSimbolo())
                    .append(ESPACO)
                    .append(formatarValorComparado(condicaoClausula))
                    .append(PARENTESES_DIREITA);

        return sb.toString();
    }

    private String formatarValorComparado(ClausulaCondicaoQuery condicaoClausula) {
        if (CategoriaCondicaoQuery.STATUS_GRUPO.equals(condicaoClausula.getCategoriaCondicao()) || CategoriaCondicaoQuery.STATUS_IC.equals(condicaoClausula.getCategoriaCondicao())) {

            if (TipoOperadorQuery.ESTA_CONTIDO_EM.equals(condicaoClausula.getOperador()) || TipoOperadorQuery.NAO_ESTA_CONTIDO_EM.equals(condicaoClausula.getOperador()))
                return formatarListaParaClausulaIn(condicaoClausula.getValorComparado());

            return SINGLE_QUOT_MARK + condicaoClausula.getValorComparado() + SINGLE_QUOT_MARK;
        }

        return condicaoClausula.getValorComparado().toString();
    }

    private String formatarNomeEventoComAtributo(ClausulaCondicaoQuery condicao) {
        Evento.TipoEventoCondicao tipo = condicao.getEvento().getTipo();

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

            if (!CollectionUtils.isEmpty(conditionsList)) {
                return conditionsList
                        .stream()
                        .map(c -> SINGLE_QUOT_MARK + c + SINGLE_QUOT_MARK)
                        .collect(Collectors.joining(COMMA_IN_NOT_IN_CLAUSE, PARENTESES_ESQUERDA, PARENTESES_DIREITA));
            }
        }

        return null;
    }
}
