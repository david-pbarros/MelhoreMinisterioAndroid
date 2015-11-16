package br.com.dbcorp.melhoreministerio;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TableRow;

import org.bouncycastle.util.encoders.Base64;

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
        //TODO: ajustar login

        Usuario user = new Usuario();
        user.setNome("admin");

        try {
            user.setSenha(this.criptoSenha("1"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        this.sessao.setUsuarioLogado(user);

        if (this.lnCong.getVisibility() != View.GONE) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("nrCong", this.txCongregacao.getText().toString());
            editor.commit();
        }

        Sincronizador sinc  = new Sincronizador(this);
        sinc.login();



        startActivity(new Intent(this, MainActivity.class));

        finish();

        /*if (this.txNome.getText().toString().equals("a") && this.txSenha.getText().toString().equals("a")) {
            startActivity(new Intent(this, MainActivity.class));

            finish();


        } else {
            new DialogHelper(this)
                    .setTitle("Login", R.mipmap.ic_launcher)
                    .setMessage("Login Inválido!")
                    .setbutton("OK", ButtonType.NEUTRAL, null)
                    .show();
        }*/
    }

    public String criptoSenha(String senha) throws NoSuchAlgorithmException {
        MessageDigest mDigest = MessageDigest.getInstance("SHA1");
        byte[] result = mDigest.digest(senha.getBytes());

        return Base64.toBase64String(result);
    }
}
