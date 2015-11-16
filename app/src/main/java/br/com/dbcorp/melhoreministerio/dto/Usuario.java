package br.com.dbcorp.melhoreministerio.dto;

import java.io.Serializable;

/**
 * Created by david.barros on 16/11/2015.
 */
public class Usuario implements Serializable {

    private int id;
    private String idonline;
    private String nome;
    private String senha;
    private boolean bloqueado;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getIdonline() {
        return idonline;
    }
    public void setIdonline(String idonline) {
        this.idonline = idonline;
    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSenha() {
        return senha;
    }
    public void setSenha(String senha) {
        this.senha = senha;
    }

    public boolean isBloqueado() {
        return bloqueado;
    }
    public void setBloqueado(boolean bloqueado) {
        this.bloqueado = bloqueado;
    }
}
