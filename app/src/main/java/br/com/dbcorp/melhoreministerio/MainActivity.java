//https://www.google.com/design/icons/index.html#ic_notifications_off
package br.com.dbcorp.melhoreministerio;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
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

import br.com.dbcorp.melhoreministerio.dto.Avaliacao;
import br.com.dbcorp.melhoreministerio.dto.Designacao;
import br.com.dbcorp.melhoreministerio.dto.TipoDesignacao;
import br.com.dbcorp.melhoreministerio.preferencias.PreferenciasActivity;

public class MainActivity extends AppCompatActivity implements OnLongClickListener, OnClickListener, OnItemClickListener, OnMenuItemClickListener {

    //TODO: remover
    private static List<Designacao> temp = new ArrayList<>();
    static{
        //TODO:substiruir linhas abaixo por vindas do banco
        Designacao d = new Designacao();
        d.setTipoDesignacao(TipoDesignacao.LEITURA);
        d.setTempo("01:40");
        d.setStatus(Avaliacao.PASSOU);
        d.setEstudante("Fulano Da Silva");
        d.setAjudante("Sicrano Junior");
        d.setFonte("IgT p.99");
        d.setData(new Date());
        d.setNrEstudo(1);
        temp.add(d);

        d = new Designacao();
        d.setTipoDesignacao(TipoDesignacao.VISITA);
        d.setTempo("00:00");
        d.setStatus(Avaliacao.NAO_AVALIADO);
        d.setEstudante("Fulano Da Silva");
        d.setAjudante("Sicrano Junior");
        d.setFonte("IgT p.99");
        d.setData(new Date());
        d.setNrEstudo(2);
        temp.add(d);

        d = new Designacao();
        d.setTipoDesignacao(TipoDesignacao.REVISITA);
        d.setTempo("00:00");
        d.setStatus(Avaliacao.NAO_PASSOU);
        d.setEstudante("Fulano Da Silva");
        d.setAjudante("Sicrano Junior");
        d.setFonte("IgT p.99");
        d.setData(new Date());
        d.setNrEstudo(3);
        temp.add(d);

        d = new Designacao();
        d.setTipoDesignacao(TipoDesignacao.ESTUDO);
        d.setTempo("00:00");
        d.setStatus(Avaliacao.SUBSTITUIDO);
        d.setEstudante("Fulano Da Silva");
        d.setAjudante("Sicrano Junior");
        d.setFonte("IgT p.99");
        d.setData(new Date());
        d.setNrEstudo(4);
        temp.add(d);
    }

    private List<Date> datas;
    private List<Designacao> designacoes;
    private int index;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    private static final String de[] = {"tipoDesignacao", "tempo", "status", "estudante", "ajudante", "fonte", "estudo"};
    private static final int para[] = {R.id.tipoDesignacao, R.id.tempo, R.id.status, R.id.estudante, R.id.ajudante, R.id.fonte, R.id.estudo};

    private ListView listaDesignacoes;
    private TextView txData;
    private TextView txSala;
    private PopupMenu popup;

    private Animation clickAnimation;
    private SharedPreferences preferences;

    private String sala;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.listaDesignacoes = (ListView) findViewById(R.id.designacaoList);
        this.listaDesignacoes.setOnItemClickListener(this);

        this.txSala = (TextView) findViewById(R.id.txSala);
        this.txData = (TextView) findViewById(R.id.lbData);

        this.txData.setOnLongClickListener(this);

        this.preferences = PreferenceManager.getDefaultSharedPreferences(this);

        this.carregaDatas();
        this.setDesignacoes();

        ImageView btOption = (ImageView) findViewById(R.id.btOpt);

        this.clickAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.click);

        this.popup = new PopupMenu(this, btOption);
        this.popup.getMenuInflater().inflate(R.menu.menu_main, this.popup.getMenu());
        this.popup.setOnMenuItemClickListener(this);
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
       view.setAnimation(this.clickAnimation);
       if (index + 1 < this.datas.size()) {
           this.txData.setText(sdf.format(this.datas.get(++index)));
           this.setDesignacoes();
       }
    }

    public void previous(View view) {
        view.setAnimation(this.clickAnimation);
        if (index - 1 >= 0) {
            this.txData.setText(sdf.format(this.datas.get(--index)));
            this.setDesignacoes();
        }
    }

    public void options(View view) {
        view.setAnimation(this.clickAnimation);
        this.popup.show();
    }

    public void mudaSala(View view) {
        this.mudaSala();
    }

    //OnLongClickListener
    @Override
    public boolean onLongClick(View v) {
        v.setAnimation(this.clickAnimation);

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

    //OnClickListener
    @Override
    public void onClick(DialogInterface dialog, int which) {
        this.index = which;

        this.txData.setText(sdf.format(this.datas.get(this.index)));
        this.setDesignacoes();
    }

    //OnItemClickListener
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        view.setAnimation(this.clickAnimation);

        Intent editar = new Intent(this, DesignacaoActivity.class);
        editar.putExtra("designacao", this.designacoes.get(position));
        startActivityForResult(editar, 1);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_MENU){
            this.popup.show();
        }
        return super.onKeyDown(keyCode, event);
    }

    //OnMenuItemClickListener
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.option) {
            this.openOptions();

            return true;
        } else if (item.getItemId() == R.id.btSala) {
            this.mudaSala();
            return true;

        } else {
            return false;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("data", this.txData.getText().toString());
        outState.putInt("index", index);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        this.txData.setText(savedInstanceState.getString("data"));
        this.index = savedInstanceState.getInt("index");
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
        this.setSala();

        SimpleAdapter adapter = new SimpleAdapter(this, this.itensDesignacao(), R.layout.list_designacao, de, para);

        this.listaDesignacoes.setAdapter(adapter);
    }

    private void setSala() {
        String sala = null;

        if (this.sala == null) {
            if (!this.preferences.contains("sala")) {
                sala = "A";

            } else {
                sala = this.preferences.getString("sala", "A");
            }

        } else {
            sala = this.sala;
        }

        SharedPreferences.Editor editor = this.preferences.edit();
        editor.putString("sala", sala);
        editor.commit();

        this.txSala.setText("Sala " + sala);

        this.sala = sala;
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

    private void mudaSala() {
        this.sala = "A".equals(this.sala) ? "B" : "A";
        this.setDesignacoes();
    }
}