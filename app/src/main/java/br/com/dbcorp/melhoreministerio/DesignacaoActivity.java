package br.com.dbcorp.melhoreministerio;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.dbcorp.melhoreministerio.dto.Designacao;
import br.com.dbcorp.melhoreministerio.dto.TipoDesignacao;

public class DesignacaoActivity extends AppCompatActivity {

    private Designacao designacao;

    private TextView lbEstudante;
    private TextView lbAjudante;
    private TextView lbFonte;
    private TextView txTempoDef;
    private Spinner spAvaliacao;
    private MediaPlayer player;
    private SharedPreferences preferences;

    private int segundos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_designacao);

        //TODO: remover instancia
        this.designacao= new Designacao();
        this.designacao.setTipoDesignacao(TipoDesignacao.LEITURA);
        this.designacao.setTempo("00:00");
        this.designacao.setStatus("V");
        this.designacao.setEstudante("Fulano Da Silva");
        this.designacao.setAjudante("Sicrano Junior");
        this.designacao.setFonte("IgT p.99");
        this.designacao.setData(new Date());

        //TODO:ajustar this.designacao = (Designacao) this.getIntent().getExtras().getSerializable("designacao");

        ((TextView)findViewById(R.id.lbTitle)).setText(new SimpleDateFormat("dd/MM/yyyy").format(this.designacao.getData()) + " - " + designacao.getTipoDesignacao());

        this.lbEstudante = (TextView) findViewById(R.id.lbEstudante);
        this.lbAjudante = (TextView) findViewById(R.id.lbAjudante);
        this.lbFonte = (TextView) findViewById(R.id.lbFonte);
        this.txTempoDef = (TextView) findViewById(R.id.txTempoDef);
        this.spAvaliacao = (Spinner) findViewById(R.id.spAvaliacao);

        this.preferences = PreferenceManager.getDefaultSharedPreferences(this);

        this.setCamposTela();
        this.setPlayer();
        this.setTempoMaximo();

        this.player.start();
    }

    public void back(View view) {
        this.finish();
    }

    @Override
    public void finish() {
        super.finish();

        if (player.isPlaying()) {
            this.player.stop();
        }

        this.player.release();
    }

    private void setCamposTela() {
        this.lbEstudante.setText(this.designacao.getEstudante());
        this.lbAjudante.setText(this.designacao.getAjudante());
        this.lbFonte.setText(this.designacao.getFonte());

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.tipo_avaliacao, android.R.layout.simple_spinner_dropdown_item);
        this.spAvaliacao.setAdapter(adapter);
    }

    private void setPlayer() {
        if (!this.preferences.getBoolean("som_pers", false)) {
            this.player = MediaPlayer.create(this, R.raw.bell);

        } else {
            String path = this.preferences.getString("alarm", "default ringtone");
            Uri uri = Uri.parse(path);

            try {
                this.player = new MediaPlayer();

                ContentResolver contentResolver = this.getContentResolver();
                String type = MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri));

                if ("MP3".equalsIgnoreCase(type)) {
                    //get resource uri from content uri
                    Uri contentUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, ContentUris.parseId(uri));
                    this.player.setDataSource(getApplicationContext(), contentUri);

                } else {
                    this.player.setDataSource(this, uri);
                }

                this.player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                this.player.prepare();

            } catch (Exception e) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
                this.player = MediaPlayer.create(this, R.raw.bell);
            }
        }

        this.player.setLooping(true);
    }

    private void setTempoMaximo() {
        switch (this.designacao.getTipoDesignacao()) {
            case LEITURA:
                this.preparaTempoDesignado(this.preferences.getString("leitura_time", "0:0"));
                break;
            case VISITA:
                this.preparaTempoDesignado(this.preferences.getString("visita_time", "0:0"));
                break;
            case REVISITA:
                this.preparaTempoDesignado(this.preferences.getString("revisita_time", "0:0"));
                break;
            case ESTUDO:
                this.preparaTempoDesignado(this.preferences.getString("estudo_time", "0:0"));
                break;
        }
    }

    private void preparaTempoDesignado(String tempo) {
        String[] temp = tempo.split(":");

        this.txTempoDef.setText(this.leftZeros(temp[0]) + ":" + this.leftZeros(temp[1]));

        this.setSegundos(temp[0], temp[1]);
    }

    private String leftZeros(String value) {
        while (value.length() < 2) {
            value = "0" + value;
        }

        return value;
    }

    private void setSegundos(String minutos, String segundos) {
        if (segundos == null || "".equals(segundos)) {
            segundos = "0";
        }

        this.segundos = Integer.parseInt(segundos);

        if (minutos == null || "".equals(minutos)) {
            minutos = "0";
        }

        int minutosInt = Integer.parseInt(minutos);

        this.segundos += (minutosInt * 60);
    }
}
