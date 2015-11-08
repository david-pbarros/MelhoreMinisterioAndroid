package br.com.dbcorp.melhoreministerio.dto;

/**
 * Created by David on 08/11/2015.
 */
public enum TipoDesignacao {
    LEITURA("Leitura"), VISITA("Visita"), REVISITA("Revisita"), ESTUDO("Estudo");

    private String label;

    TipoDesignacao(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }
}
