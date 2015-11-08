package br.com.dbcorp.melhoreministerio.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Designacao implements Serializable {

    private TipoDesignacao tipoDesignacao;
    private String tempo;
    private String status;
    private String estudante;
    private String ajudante;
    private String fonte;
    private Date data;

    public TipoDesignacao getTipoDesignacao() {
        return tipoDesignacao;
    }
    public void setTipoDesignacao(TipoDesignacao tipoDesignacao) {
        this.tipoDesignacao = tipoDesignacao;
    }

    public String getTempo() {
        return tempo;
    }
    public void setTempo(String tempo) {
        this.tempo = tempo;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getEstudante() {
        return estudante;
    }
    public void setEstudante(String estudante) {
        this.estudante = estudante;
    }

    public String getAjudante() {
        return ajudante;
    }
    public void setAjudante(String ajudante) {
        this.ajudante = ajudante;
    }

    public String getFonte() {
        return fonte;
    }
    public void setFonte(String fonte) {
        this.fonte = fonte;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> item = new HashMap<>();
        item.put("tipoDesignacao", tipoDesignacao.getLabel());
        item.put("tempo", tempo);
        item.put("status", status);
        item.put("estudante", "Estudante: " + estudante);
        item.put("ajudante", "Ajudante: " + ajudante);
        item.put("fonte", "Fonte: " + fonte);

        return item;
    }
}