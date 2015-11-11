package br.com.dbcorp.melhoreministerio;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.dbcorp.melhoreministerio.dto.Avaliacao;
import br.com.dbcorp.melhoreministerio.dto.Designacao;
import br.com.dbcorp.melhoreministerio.dto.TipoDesignacao;

public class DesignacaoActivity extends AppCompatActivity {

    private Designacao designacao;

    private TextView lbEstudante;
    private TextView lbAjudante;
    private TextView lbFonte;
    private TextView lbEstudo;
    private TextView txTempoDef;
    private TextView txMinCrono;
    private TextView txSecCrono;
    private TextView txTempoCor;
    private Spinner spAvaliacao;
    private ImageView imgMute;
    private ImageView btStart;

    private Animation animation;
    private MediaPlayer player;
    private SharedPreferences preferences;

    private int padrao;
    private int ini;
    private int warn;
    private int outTime;

    private static final String de[] = {"imagem", "descricao"};
    private static final int para[] = {R.id.imgStatus, R.id.txStatus};

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
        this.lbEstudo = (TextView) findViewById(R.id.lbEstudo);
        this.txTempoDef = (TextView) findViewById(R.id.txTempoDef);
        this.txMinCrono = (TextView) findViewById(R.id.minCrono);
        this.txSecCrono = (TextView) findViewById(R.id.secCrono);
        this.txTempoCor = (TextView) findViewById(R.id.txTempoCor);
        this.spAvaliacao = (Spinner) findViewById(R.id.spAvaliacao);
        this.imgMute = (ImageView) findViewById(R.id.imgMute);
        this.btStart = (ImageView) findViewById(R.id.btStart);

        this.animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.resize);

        this.preferences = PreferenceManager.getDefaultSharedPreferences(this);

        this.padrao = ContextCompat.getColor(this, R.color.text_default);
        this.ini = ContextCompat.getColor(this, R.color.defTime);
        this.warn = ContextCompat.getColor(this, R.color.warnTime);
        this.outTime = ContextCompat.getColor(this, R.color.outTime);

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
        this.designacao.setStatus(Avaliacao.values()[this.spAvaliacao.getSelectedItemPosition()]);

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
            this.btStart.setImageResource(R.drawable.stop);

            this.setTempoOcorStyle(this.ini);
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

        this.setTempoOcorStyle(this.padrao);
    }

    public void mute(View view) {
        this.mudo = !this.mudo;

        if (mudo) {
            this.imgMute.setImageResource(R.drawable.mute);

        } else {
            this.imgMute.setImageResource(R.drawable.un_mute);
        }
    }

    private void stopCron() {
        this.started = false;

        if (this.player.isPlaying()) {
            this.player.stop();
        }

        this.btStart.setImageResource(R.drawable.start);
    }

    private void setComboAvaliacao() {
        List<Map<String, Object>> avaliacoes = new ArrayList<>();

        for (int i = 0; i < Avaliacao.values().length; i++) {
            Map<String, Object> opcao = new HashMap<>();
            opcao.put("imagem", Designacao.getIconCode(Avaliacao.values()[i]));
            opcao.put("descricao", Avaliacao.values()[i].getLabel());

            avaliacoes.add(opcao);
        }

        SimpleAdapter adapter = new SimpleAdapter(this, avaliacoes, R.layout.list_avaliacao, de, para);
        this.spAvaliacao.setAdapter(adapter);
    }

    private void setCamposTela() {
        this.setComboAvaliacao();

        this.lbEstudante.setText(this.designacao.getEstudante());
        this.lbAjudante.setText(this.designacao.getAjudante());
        this.lbEstudo.setText(this.designacao.getEstudo());
        this.lbFonte.setText(this.designacao.getFonte());
        this.spAvaliacao.setSelection(this.designacao.getStatus().ordinal());
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
        if (this.minutos == 0 && this.segundos > 20 && this.txTempoCor.getCurrentTextColor() != this.warn) {
            this.setTempoOcorStyle(this.warn);

        } else if (this.minutos == 0 && this.segundos <= 20) {
            this.setTempoOcorStyle(this.outTime);
        }

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

    private void setTempoOcorStyle(int color) {
        this.txTempoCor.startAnimation(this.animation);

        this.txTempoCor.setTextColor(color);
    }
}