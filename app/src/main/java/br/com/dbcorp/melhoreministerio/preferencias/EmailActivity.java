package br.com.dbcorp.melhoreministerio.preferencias;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class EmailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"david_pbarros@hotmail.com"});
        emailIntent.setType("message/rfc822");
        startActivity(Intent.createChooser(emailIntent, "Email Client Chooser"));
    }
}
