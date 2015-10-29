package br.com.dbcorp.melhoreministerio;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import br.com.dbcorp.melhoreministerio.dto.Designacao;

public class MainActivity extends AppCompatActivity implements View.OnLongClickListener, DialogInterface.OnClickListener, AdapterView.OnItemClickListener {

    private List<Date> datas;
    private List<Designacao> designacoes;
    private int index;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    private static final String de[] = {"tipoDesignacao", "tempo", "status", "estudante", "ajudante", "tema", "fonte"};
    private static final int para[] = {R.id.tipoDesignacao, R.id.tempo, R.id.status, R.id.estudante, R.id.ajudante, R.id.tema, R.id.fonte};

    private ListView listaDesignacoes;
    private TextView txData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.listaDesignacoes = (ListView) findViewById(R.id.designacaoList);
        this.listaDesignacoes.setOnItemClickListener(this);

        this.txData = (TextView) findViewById(R.id.lbData);
        this.txData.setOnLongClickListener(this);

        this.carregaDatas();
        this.setDesignacoes();
    }

    private void carregaDatas() {
        long oneDay = 86400000;
        this.datas = new ArrayList<>();

        //TODO:substiruir linhas abaixo por vindas do banco
        try {
            this.datas.add(sdf.parse("01/10/2015"));
            this.datas.add(sdf.parse("08/10/2015"));
            this.datas.add(sdf.parse("15/10/2015"));
            this.datas.add(sdf.parse("22/10/2015"));
            this.datas.add(sdf.parse("29/10/2015"));

        } catch (Exception e) {
            //erro
        }

        long dtAtual = new Date().getTime() / oneDay;

        for (Date item : this.datas) {
            long data = item.getTime() / oneDay;

            if (data >= dtAtual) {
                this.txData.setText(sdf.format(item));
                this.index = this.datas.indexOf(item);
                break;
            }
        }
    }

    private void setDesignacoes() {
        SimpleAdapter adapter = new SimpleAdapter(this, this.itensDesignacao(), R.layout.list_designacao, de, para);

        this.listaDesignacoes.setAdapter(adapter);
    }

    private List<Map<String, Object>> itensDesignacao() {
        this.designacoes = new ArrayList<>();

        List<Map<String, Object>> designacoesMap = new ArrayList<>();

        //TODO:substiruir linhas abaixo por vindas do banco
        Designacao d = new Designacao();
        d.setTipoDesignacao("Leitura");
        d.setTempo("00:00");
        d.setStatus("V");
        d.setEstudante("Fulano Da Silva" + this.index);
        d.setAjudante("Sicrano Junior");
        d.setTema("Como fazer a lista android");
        d.setFonte("IgT p.99");
        d.setData(this.datas.get(this.index));
        designacoes.add(d);

        d = new Designacao();
        d.setTipoDesignacao("Visita");
        d.setTempo("00:00");
        d.setStatus("V");
        d.setEstudante("Fulano Da Silva" + this.index);
        d.setAjudante("Sicrano Junior");
        d.setTema("Como fazer a lista android");
        d.setFonte("IgT p.99");
        d.setData(this.datas.get(this.index));
        designacoes.add(d);

        d = new Designacao();
        d.setTipoDesignacao("Revisita");
        d.setTempo("00:00");
        d.setStatus("V");
        d.setEstudante("Fulano Da Silva" + this.index);
        d.setAjudante("Sicrano Junior");
        d.setTema("Como fazer a lista android");
        d.setFonte("IgT p.99");
        d.setData(this.datas.get(this.index));
        designacoes.add(d);

        d = new Designacao();
        d.setTipoDesignacao("Estudo");
        d.setTempo("00:00");
        d.setStatus("V");
        d.setEstudante("Fulano Da Silva" + this.index);
        d.setAjudante("Sicrano Junior");
        d.setTema("Como fazer a lista android");
        d.setFonte("IgT p.99");
        d.setData(this.datas.get(this.index));
        designacoes.add(d);
        //fim de linhas a substituir

        for (Designacao designacao : this.designacoes) {
            designacoesMap.add(designacao.toMap());
        }

        return designacoesMap;
    }

    public void next(View view) {
       if (index + 1 < this.datas.size()) {
           this.txData.setText(sdf.format(this.datas.get(++index)));
           this.setDesignacoes();
       }
    }

    public void previous(View view) {
        if (index - 1 >= 0) {
            this.txData.setText(sdf.format(this.datas.get(--index)));
            this.setDesignacoes();
        }
    }


    @Override
    public boolean onLongClick(View v) {
        List<String> datas = new ArrayList<>();
        datas.add("01/10/2015");
        datas.add("08/10/2015");
        datas.add("15/10/2015");
        datas.add("22/10/2015");
        datas.add("29/10/2015");

        new DialogHelper(this)
                .setTitle("Login", R.mipmap.ic_launcher, R.color.text_default)
                .setItens(datas, this)
                .show();

        return true;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        this.index = which;

        this.txData.setText(sdf.format(this.datas.get(this.index)));
        this.setDesignacoes();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent editar = new Intent(this, DesignacaoActivity.class);
        editar.putExtra("designacao", this.designacoes.get(position));
        startActivity(editar);
    }
}