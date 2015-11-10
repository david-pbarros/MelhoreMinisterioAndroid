package br.com.dbcorp.melhoreministerio;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import br.com.dbcorp.melhoreministerio.dto.Avaliacao;
import br.com.dbcorp.melhoreministerio.dto.Designacao;
import br.com.dbcorp.melhoreministerio.dto.TipoDesignacao;

public class DesignacaoActivity extends AppCompatActivity {

    private Designacao designacao;

    private TextView lbEstudante;
    private TextView lbAjudante;
    private TextView lbFonte;
    private TextView txTempoDef;
    private TextView txMinCrono;
    private TextView txSecCrono;
    private TextView txTempoCor;
    private Spinner spAvaliacao;
    private MediaPlayer player;
    private SharedPreferences preferences;

    private int minutos;
    private int segundos;
    private int curMinutos;
    private int curSegundos;
    private boolean started;
    private boolean mudo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_designacao);

        this.designacao = (Designacao) this.getIntent().getExtras().getSerializable("designacao");

        ((TextView)findViewById(R.id.lbTitle)).setText(new SimpleDateFormat("dd/MM/yyyy").format(this.designacao.getData()) + " - " + designacao.getTipoDesignacao());

        this.lbEstudante = (TextView) findViewById(R.id.lbEstudante);
        this.lbAjudante = (TextView) findViewById(R.id.lbAjudante);
        this.lbFonte = (TextView) findViewById(R.id.lbFonte);
        this.txTempoDef = (TextView) findViewById(R.id.txTempoDef);
        this.txMinCrono = (TextView) findViewById(R.id.minCrono);
        this.txSecCrono = (TextView) findViewById(R.id.secCrono);
        this.txTempoCor = (TextView) findViewById(R.id.txTempoCor);
        this.spAvaliacao = (Spinner) findViewById(R.id.spAvaliacao);

        this.preferences = PreferenceManager.getDefaultSharedPreferences(this);

        this.setCamposTela();
        this.setPlayer();
        this.setTempoMaximo();
        this.txTempoCor.setText(this.designacao.getTempo());
    }

    public void back(View view) {
        this.finish();
    }

    @Override
    public void finish() {
        if (player.isPlaying()) {
            this.player.stop();
        }

        this.player.release();

        this.designacao.setTempo((String) this.txTempoCor.getText());
        this.designacao.setStatus(Avaliacao.values()[this.spAvaliacao.getSelectedItemPosition()].getSigla());

        Intent data = new Intent();
        data.putExtra("designacao", this.designacao);
        setResult(RESULT_OK, data);

        super.finish();
    }

    public void cronometro(View view) {
        if (this.started) {
            this.stopCron();

        } else {
            this.started = true;
            this.setCronometro().start();
            //TODO: modificar icone start
        }
    }

    public void reset(View view) {
        if (this.started) {
           this.stopCron();
        }

        this.curSegundos = 0;
        this.curMinutos = 0;

        this.setTempoCorrido(this.curMinutos, this.curSegundos);
        this.setTempoMaximo();
    }

    public void mute(View view) {
        this.mudo = !this.mudo;
        //TODO: modificar icone mudo
    }

    private void stopCron() {
        this.started = false;

        if (this.player.isPlaying()) {
            this.player.stop();
        }

        //TODO: modificar icone start
    }

    private void setCamposTela() {
        this.lbEstudante.setText(this.designacao.getEstudante());
        this.lbAjudante.setText(this.designacao.getAjudante());
        this.lbFonte.setText(this.designacao.getFonte());

        String[] status = new String[Avaliacao.values().length];

        for (int i = 0; i < Avaliacao.values().length; i++) {
            status[i] = Avaliacao.values()[i].getLabel();
        }

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_dropdown_item, status);
        this.spAvaliacao.setAdapter(adapter);

        this.spAvaliacao.setSelection(Avaliacao.getByInitials(this.designacao.getStatus()).ordinal());
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
                this.preparaTempoDesignado(this.preferences.getString("leitura_time", getResources().getString(R.string.tempo_df_leitura)));
                break;
            case VISITA:
                this.preparaTempoDesignado(this.preferences.getString("visita_time", getResources().getString(R.string.tempo_df_visita)));
                break;
            case REVISITA:
                this.preparaTempoDesignado(this.preferences.getString("revisita_time", getResources().getString(R.string.tempo_df_revisita)));
                break;
            case ESTUDO:
                this.preparaTempoDesignado(this.preferences.getString("estudo_time", getResources().getString(R.string.tempo_df_estudo)));
                break;
        }
    }

    private void preparaTempoDesignado(String tempo) {
        String[] temp = tempo.split(":");

        String segundos = this.leftZeros(temp[1]);
        String minutos = this.leftZeros(temp[0]);

        this.txTempoDef.setText(minutos + ":" + segundos);

        this.setTempo(temp[0], temp[1]);
        this.setTempoCronometro(minutos, segundos);
    }

    private void setTempoCronometro(String minutos, String segundos) {
        this.txMinCrono.setText(minutos);
        this.txSecCrono.setText(segundos);
    }

    private void setTempoCronometro(int minutos, int segundos) {
        this.txMinCrono.setText(leftZeros(Integer.toString(minutos)));
        this.txSecCrono.setText(leftZeros(Integer.toString(segundos)));
    }

    private void setTempoCorrido(int minutos, int segundos) {
        this.txTempoCor.setText(leftZeros(Integer.toString(minutos)) + ":" + leftZeros(Integer.toString(segundos)));
    }

    private String leftZeros(String value) {
        while (value.length() < 2) {
            value = "0" + value;
        }

        return value;
    }

    private void setTempo(String minutos, String segundos) {
        if (segundos == null || "".equals(segundos)) {
            segundos = "0";
        }

        this.segundos = Integer.parseInt(segundos);

        if (minutos == null || "".equals(minutos)) {
            minutos = "0";
        }

        this.minutos = Integer.parseInt(minutos);
    }

    private Thread setCronometro() {
        Thread cronometro = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int tempo = segundos + (minutos * 60);; tempo--) {
                    this.contagemRegressiva();
                    this.contagemProgressiva();

                    try {
                        Thread.sleep(1000);

                        if (!started) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            private void contagemRegressiva() {
                if (segundos == 0) {
                    segundos = 59;
                    minutos--;

                } else {
                    segundos--;
                }

                if (minutos == 0 && segundos == 0 && !mudo) {
                    player.start();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setTempoCronometro(minutos, segundos);
                    }
                });
            }

            private void contagemProgressiva() {
                if (curSegundos == 59) {
                    curSegundos = 0;
                    curMinutos++;

                } else {
                    curSegundos++;
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setTempoCorrido(curMinutos, curSegundos);
                    }
                });
            }
        });

        return cronometro;
    }
}