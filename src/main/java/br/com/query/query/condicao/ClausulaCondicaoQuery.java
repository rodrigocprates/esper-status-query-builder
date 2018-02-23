package br.com.query.query.condicao;

import br.com.query.query.clausula.ClausulaQuery;
import br.com.query.query.evento.Evento;
import br.com.query.query.tipo.TipoOperadorQuery;

public class ClausulaCondicaoQuery implements ClausulaQuery {

    private Evento evento;
    private TipoOperadorQuery operador;
    private Object valorComparado;
    private CategoriaCondicaoQuery categoriaCondicao;
    private String nomeEventoFormatado;

    public TipoOperadorQuery getOperador() {
        return operador;
    }

    public void setOperador(TipoOperadorQuery operador) {
        this.operador = operador;
    }

    public Object getValorComparado() {
        return valorComparado;
    }

    public void setValorComparado(Object valorComparado) {
        this.valorComparado = valorComparado;
    }

    public CategoriaCondicaoQuery getCategoriaCondicao() {
        return categoriaCondicao;
    }

    public void setCategoriaCondicao(CategoriaCondicaoQuery categoriaCondicao) {
        this.categoriaCondicao = categoriaCondicao;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    public String getNomeEventoFormatado() {
        return nomeEventoFormatado;
    }

    public void setNomeEventoFormatado(String nomeEventoFormatado) {
        this.nomeEventoFormatado = nomeEventoFormatado;
    }
}
