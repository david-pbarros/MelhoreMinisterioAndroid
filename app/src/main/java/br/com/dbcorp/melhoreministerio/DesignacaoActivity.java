package br.com.dbcorp.melhoreministerio;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.dbcorp.melhoreministerio.dto.Designacao;

public class DesignacaoActivity extends AppCompatActivity {

    private Designacao designacao;

    private TextView lbEstudante;
    private TextView lbAjudante;
    private TextView lbTema;
    private TextView lbFonte;
    private Spinner spAvaliacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_designacao);

        //TODO: remover instancia
        this.designacao= new Designacao();
        this.designacao.setTipoDesignacao("Leitura");
        this.designacao.setTempo("00:00");
        this.designacao.setStatus("V");
        this.designacao.setEstudante("Fulano Da Silva");
        this.designacao.setAjudante("Sicrano Junior");
        this.designacao.setTema("Como fazer a lista android");
        this.designacao.setFonte("IgT p.99");
        this.designacao.setData(new Date());

        //TODO:ajustar this.designacao = (Designacao) this.getIntent().getExtras().getSerializable("designacao");

        ((TextView)findViewById(R.id.lbTitle)).setText(new SimpleDateFormat("dd/MM/yyyy").format(this.designacao.getData()) + " - " + designacao.getTipoDesignacao());

        this.lbEstudante = (TextView) findViewById(R.id.lbEstudante);
        this.lbAjudante = (TextView) findViewById(R.id.lbAjudante);
        this.lbTema = (TextView) findViewById(R.id.lbTema);
        this.lbFonte = (TextView) findViewById(R.id.lbFonte);
        this.spAvaliacao = (Spinner) findViewById(R.id.spAvaliacao);

        this.setCamposTela();
    }

    public void back(View view) {
        this.finish();
    }

    private void setCamposTela() {
        this.lbEstudante.setText(this.designacao.getEstudante());
        this.lbAjudante.setText(this.designacao.getAjudante());
        this.lbTema.setText(this.designacao.getTema());
        this.lbFonte.setText(this.designacao.getFonte());

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.tipo_avaliacao, android.R.layout.simple_spinner_dropdown_item);
        this.spAvaliacao.setAdapter(adapter);
    }

}
