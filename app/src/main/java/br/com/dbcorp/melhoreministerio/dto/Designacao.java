package br.com.dbcorp.melhoreministerio.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Designacao implements Serializable {

    private TipoDesignacao tipoDesignacao;
    private String tempo;
    private Avaliacao status;
    private String estudante;
    private String ajudante;
    private String fonte;
    private int nrEstudo;
    private Date data;
    private Map<Integer, String> estudos;

    {
        this.estudos = new HashMap<>();
        this.estudos.put(1, "Estudo a");
        this.estudos.put(2, "Estudo b");
        this.estudos.put(3, "Estudo c");
        this.estudos.put(4, "Estudo d");
    }

    public static int getIconCode(Avaliacao avaliacao) {
        switch (avaliacao) {
            case PASSOU:
                return android.R.drawable.presence_online;
            case NAO_PASSOU:
                return android.R.drawable.presence_busy;
            case SUBSTITUIDO:
                return android.R.drawable.stat_notify_sync;
            default:
                return android.R.drawable.presence_invisible;
        }
    }

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

    public Avaliacao getStatus() {
        return status;
    }
    public void setStatus(Avaliacao status) {
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

    public int getNrEstudo() {
        return nrEstudo;
    }
    public void setNrEstudo(int nrEstudo) {
        this.nrEstudo = nrEstudo;
    }

    @Override
    public int hashCode() {
        return data.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof  Designacao) {
            Designacao temp = (Designacao) o;

            return data.equals(temp.getData()) && tipoDesignacao == temp.getTipoDesignacao();
        }

        return false;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> item = new HashMap<>();
        item.put("tipoDesignacao", tipoDesignacao.getLabel());
        item.put("tempo", tempo);
        item.put("estudante", "Estudante: " + estudante);
        item.put("ajudante", "Ajudante: " + ajudante);
        item.put("fonte", "Fonte: " + fonte);
        item.put("status", Designacao.getIconCode(status));
        item.put("estudo", "Estudo: " + this.getEstudo());

        return item;
    }

    public String getEstudo() {
        return nrEstudo + " - " + this.estudos.get(nrEstudo);
    }
}