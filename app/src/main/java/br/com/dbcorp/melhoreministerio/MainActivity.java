package br.com.dbcorp.melhoreministerio;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import br.com.dbcorp.melhoreministerio.dto.Designacao;
import br.com.dbcorp.melhoreministerio.dto.TipoDesignacao;
import br.com.dbcorp.melhoreministerio.preferencias.PreferenciasActivity;

public class MainActivity extends AppCompatActivity implements View.OnLongClickListener, DialogInterface.OnClickListener, AdapterView.OnItemClickListener {

    //TODO: remover
    private static List<Designacao> temp = new ArrayList<>();
    static{
        //TODO:substiruir linhas abaixo por vindas do banco
        Designacao d = new Designacao();
        d.setTipoDesignacao(TipoDesignacao.LEITURA);
        d.setTempo("01:40");
        d.setStatus("P");
        d.setEstudante("Fulano Da Silva");
        d.setAjudante("Sicrano Junior");
        d.setFonte("IgT p.99");
        d.setData(new Date());
        temp.add(d);

        d = new Designacao();
        d.setTipoDesignacao(TipoDesignacao.VISITA);
        d.setTempo("00:00");
        d.setStatus("V");
        d.setEstudante("Fulano Da Silva");
        d.setAjudante("Sicrano Junior");
        d.setFonte("IgT p.99");
        d.setData(new Date());
        temp.add(d);

        d = new Designacao();
        d.setTipoDesignacao(TipoDesignacao.REVISITA);
        d.setTempo("00:00");
        d.setStatus("V");
        d.setEstudante("Fulano Da Silva");
        d.setAjudante("Sicrano Junior");
        d.setFonte("IgT p.99");
        d.setData(new Date());
        temp.add(d);

        d = new Designacao();
        d.setTipoDesignacao(TipoDesignacao.ESTUDO);
        d.setTempo("00:00");
        d.setStatus("V");
        d.setEstudante("Fulano Da Silva");
        d.setAjudante("Sicrano Junior");
        d.setFonte("IgT p.99");
        d.setData(new Date());
        temp.add(d);
    }

    private List<Date> datas;
    private List<Designacao> designacoes;
    private int index;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    private static final String de[] = {"tipoDesignacao", "tempo", "status", "estudante", "ajudante", "fonte"};
    private static final int para[] = {R.id.tipoDesignacao, R.id.tempo, R.id.status, R.id.estudante, R.id.ajudante, R.id.fonte};

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

        ImageView btOption = (ImageView) findViewById(R.id.btOpt);

        if (ViewConfiguration.get(this).hasPermanentMenuKey()) {
            (btOption).setVisibility(View.INVISIBLE);

        } else {
            registerForContextMenu(btOption);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.option) {
            this.openOptions();
            return true;

        } else if (item.getItemId() == R.id.sinc) {

            return true;
        }

        return false;
    }

    private void carregaDatas() {
        long oneDay = 86400000;
        this.datas = new ArrayList<>();

        //TODO:substiruir linhas abaixo por vindas do banco
        try {
            this.datas.add(sdf.parse("05/11/2015"));
            this.datas.add(sdf.parse("12/11/2015"));
            this.datas.add(sdf.parse("19/11/2015"));
            this.datas.add(sdf.parse("26/11/2015"));

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

    public void options(View view) {
        openContextMenu(view);
    }

    @Override
    public boolean onLongClick(View v) {
        List<String> datas = new ArrayList<>();
        datas.add("05/11/2015");
        datas.add("12/11/2015");
        datas.add("19/11/2015");
        datas.add("26/11/2015");

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
        startActivityForResult(editar, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 1) {
            if (data.hasExtra("designacao")) {
                //TODO: melhorar lógica

                Designacao d1 = (Designacao) data.getExtras().getSerializable("designacao");
                Designacao d = this.designacoes.get(this.designacoes.indexOf(d1));


                d.setTempo(d1.getTempo());
                d.setStatus(d1.getStatus());

                this.setDesignacoes();
            }
        }
    }

    private void setDesignacoes() {
        SimpleAdapter adapter = new SimpleAdapter(this, this.itensDesignacao(), R.layout.list_designacao, de, para);

        this.listaDesignacoes.setAdapter(adapter);
    }

    private List<Map<String, Object>> itensDesignacao() {
        //buscar do banco
        this.designacoes = new ArrayList<>();
        this.designacoes.addAll(temp);

        List<Map<String, Object>> designacoesMap = new ArrayList<>();

        for (Designacao designacao : this.designacoes) {
            designacoesMap.add(designacao.toMap());
        }

        return designacoesMap;
    }

    private void openOptions() {
        startActivity(new Intent(this, PreferenciasActivity.class));
    }
}