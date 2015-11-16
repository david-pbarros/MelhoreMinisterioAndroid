package br.com.dbcorp.melhoreministerio;

import br.com.dbcorp.melhoreministerio.dto.Usuario;

/**
 * Created by david.barros on 16/11/2015.
 */
public class Sessao {

    private static Sessao sessao;

    private Usuario usuarioLogado;

    private Sessao() {
    }

    public static Sessao getInstance() {
        if (sessao == null) {
            sessao = new Sessao();
        }

        return sessao;
    }

    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }
    public void setUsuarioLogado(Usuario usuario) {
        this.usuarioLogado = usuario;
    }

}
