package br.com.query.regra;

import br.com.query.query.evento.Evento;

public class RegraDinamica {

    private Evento eventoBase;
    private RegraDinamicaStatus good;
    private RegraDinamicaStatus warning;
    private RegraDinamicaStatus error;

    public Evento getEventoBase() {
        return eventoBase;
    }

    public void setEventoBase(Evento eventoBase) {
        this.eventoBase = eventoBase;
    }

    public RegraDinamicaStatus getGood() {
        return good;
    }

    public void setGood(RegraDinamicaStatus good) {
        this.good = good;
    }

    public RegraDinamicaStatus getWarning() {
        return warning;
    }

    public void setWarning(RegraDinamicaStatus warning) {
        this.warning = warning;
    }

    public RegraDinamicaStatus getError() {
        return error;
    }

    public void setError(RegraDinamicaStatus error) {
        this.error = error;
    }
}
