package br.com.dbcorp.melhoreministerio.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Designacao implements Serializable {

    private int id;
    private TipoDesignacao tipoDesignacao;
    private String idOnline;
    private String tempo;
    private Avaliacao status;
    private String estudante;
    private String ajudante;
    private String fonte;
    private int nrEstudo;
    private Date data;
    private String sala;
    private Date dataAtualizacao;
    private String nmEstudo;

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

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public TipoDesignacao getTipoDesignacao() {
        return tipoDesignacao;
    }
    public void setTipoDesignacao(TipoDesignacao tipoDesignacao) {
        this.tipoDesignacao = tipoDesignacao;
    }

    public String getIdOnline() {
        return idOnline;
    }
    public void setIdOnline(String idOnline) {
        this.idOnline = idOnline;
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

    public String getSala() {
        return sala;
    }
    public void setSala(String sala) {
        this.sala = sala;
    }

    public Date getDataAtualizacao() {
        return dataAtualizacao;
    }
    public void setDataAtualizacao(Date dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    public void setNomeEstudo(String nomeEstudo) {
        this.nmEstudo = nomeEstudo;
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
        return nrEstudo + " - " + this.nmEstudo;
    }
}