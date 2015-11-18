package br.com.dbcorp.melhoreministerio;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.TableRow;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import br.com.dbcorp.melhoreministerio.DialogHelper.ButtonType;
import br.com.dbcorp.melhoreministerio.dto.Usuario;
import br.com.dbcorp.melhoreministerio.sinc.Sincronizador;


public class LoginActivity extends AppCompatActivity {

    private Sessao sessao;

    private EditText txNome;
    private EditText txSenha;
    private EditText txCongregacao;
    private TableRow lnCong;

    private SharedPreferences preferences;

    private boolean loginWebOK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.sessao = Sessao.getInstance();

        this.txNome = (EditText) findViewById(R.id.txNome);
        this.txSenha = (EditText) findViewById(R.id.txPass);
        this.txCongregacao = (EditText) findViewById(R.id.txCongregacao);
        this.lnCong = (TableRow) findViewById(R.id.lnCong);

        this.preferences = PreferenceManager.getDefaultSharedPreferences(this);


        if (!"".equals(this.preferences.getString("nrCong", ""))) {
            this.lnCong.setVisibility(View.GONE);
        }
    }


    public void login(View view) {
        Usuario user = new Usuario();
        user.setNome("admin");

        try {
            user.setSenha(this.criptoSenha("1"));
        } catch (Exception ex) {
            new DialogHelper(this)
                    .setTitle("Login", R.mipmap.ic_launcher)
                    .setMessage("Erro no processo de Login!")
                    .setbutton("OK", ButtonType.NEUTRAL, null)
                    .show();
        }

        this.sessao.setUsuarioLogado(user);

        if (this.lnCong.getVisibility() != View.GONE) {
            SharedPreferences.Editor editor = this.preferences.edit();
            editor.putString("nrCong", this.txCongregacao.getText().toString());
            editor.commit();
        }

        final Sincronizador sinc  = new Sincronizador(this);

        Thread login = new Thread(new Runnable() {
            @Override
            public void run() {
                loginWebOK = sinc.login();
            };
        });

        while (login.isAlive()) {
            //espera.......
        }

        if (loginWebOK) {
            this.proximo();

        } else {
            //TODO: login soh pela web, verificar login local antes do popup

            new DialogHelper(this)
                    .setTitle("Login", R.mipmap.ic_launcher)
                    .setMessage("Login Inválido!")
                    .setbutton("OK", ButtonType.NEUTRAL, null)
                    .show();
        }
    }

    private String criptoSenha(String senha) throws NoSuchAlgorithmException {
        MessageDigest mDigest = MessageDigest.getInstance("SHA1");
        byte[] result = mDigest.digest(senha.getBytes());

        return android.util.Base64.encodeToString(result, Base64.NO_WRAP);
    }

    private void proximo() {
        this.sincGeral();

        startActivity(new Intent(this, MainActivity.class));

        finish();
    }

    private void sincGeral() {
        if (this.preferences.getBoolean("sinc_ini", false)) {

        }
    }
}
