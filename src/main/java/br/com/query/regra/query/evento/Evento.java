package br.com.query.regra.query.evento;

public class Evento {

    private TipoEventoCondicao tipo;
    private String id;

    public Evento(TipoEventoCondicao tipo, String id) {
        this.tipo = tipo;
        this.id = id;
    }

    public enum TipoEventoCondicao {
        GRUPO, IC
    }

    public TipoEventoCondicao getTipo() {
        return tipo;
    }

    public void setTipo(TipoEventoCondicao tipo) {
        this.tipo = tipo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
