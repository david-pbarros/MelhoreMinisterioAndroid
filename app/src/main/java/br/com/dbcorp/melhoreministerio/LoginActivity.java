package br.com.dbcorp.melhoreministerio;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import br.com.dbcorp.melhoreministerio.DialogHelper.ButtonType;

public class LoginActivity extends AppCompatActivity {
    private EditText txNome;
    private EditText txSenha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.txNome = (EditText) findViewById(R.id.txNome);
        this.txSenha = (EditText) findViewById(R.id.txPass);
    }


    public void login(View view) {
        //TODO: ajustar login

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
}
