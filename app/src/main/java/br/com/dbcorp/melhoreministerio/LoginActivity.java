package br.com.dbcorp.melhoreministerio;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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
import br.com.dbcorp.melhoreministerio.db.DataBaseHelper;
import br.com.dbcorp.melhoreministerio.dto.Usuario;
import br.com.dbcorp.melhoreministerio.sinc.Sincronizador;


public class LoginActivity extends AppCompatActivity {

    private Sessao sessao;
    private DataBaseHelper dbHelper;

    private ProgressDialog dialog;
    private EditText txNome;
    private EditText txSenha;
    private EditText txCongregacao;
    private TableRow lnCong;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.dbHelper = new DataBaseHelper(this);

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

    @Override
    public void finish() {
        this.dbHelper.close();
        super.finish();
    }

    public void login(View view) {
        this.dialog = new ProgressDialog(LoginActivity.this);
        this.dialog.show();

        Usuario user = new Usuario();
        user.setNome("admin");

        try {
            user.setSenha(this.criptoSenha("adminEscola"));
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
            editor.apply();
        }

        new LoginSinc().execute();
    }

    private String criptoSenha(String senha) throws NoSuchAlgorithmException {
        MessageDigest mDigest = MessageDigest.getInstance("SHA1");
        byte[] result = mDigest.digest(senha.getBytes());

        return android.util.Base64.encodeToString(result, Base64.NO_WRAP);
    }

    private class LoginSinc extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Sincronizador sinc = new Sincronizador(LoginActivity.this, LoginActivity.this.dbHelper);

            if (sinc.login()) {
                this.proximo(sinc);

            } else {
                if (LoginActivity.this.dbHelper.logon()) {
                    LoginActivity.this.startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    LoginActivity.this.finish();

                } else {
                    LoginActivity.this.dialog.dismiss();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new DialogHelper(LoginActivity.this)
                                    .setTitle("Login", R.mipmap.ic_launcher)
                                    .setMessage("Login Inválido!")
                                    .setbutton("OK", DialogHelper.ButtonType.NEUTRAL, null)
                                    .show();
                        }
                    });
                }
            }

            return null;
        }

        private void proximo(Sincronizador sinc) {
            this.sincroniza(sinc);

            LoginActivity.this.startActivity(new Intent(LoginActivity.this, MainActivity.class));

            LoginActivity.this.dialog.dismiss();

            LoginActivity.this.finish();
        }

        private void sincroniza(Sincronizador sinc) {
            if (!LoginActivity.this.dbHelper.existeRegistros()) {
                sinc.sincronismoGeral();

            } else {
                sinc.designacoes();
            }
        }
    }
}
