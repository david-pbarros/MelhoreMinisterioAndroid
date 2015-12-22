package br.com.dbcorp.melhoreministerio.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.dbcorp.melhoreministerio.Sessao;
import br.com.dbcorp.melhoreministerio.dto.Avaliacao;
import br.com.dbcorp.melhoreministerio.dto.Designacao;
import br.com.dbcorp.melhoreministerio.dto.Estudo;
import br.com.dbcorp.melhoreministerio.dto.TipoDesignacao;
import br.com.dbcorp.melhoreministerio.dto.Usuario;

public class DataBaseHelper extends SQLiteOpenHelper implements Serializable {

	public DataBaseHelper(Context context) {
		super(context, "EscolaMinisterio", null, 100);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE usuario (" +
			       "_id INTEGER PRIMARY KEY," +
			       "idonline VARCHAR," +
			       "nome VARCHAR," +
			       "senha VARCHAR," +
                   "bloqueado NUMBER(1));");

        db.execSQL("CREATE TABLE designacao (" +
                    "_id INTEGER PRIMARY KEY," +
                    "idonline VARCHAR," +
                    "data NUMBER(19)," +
                    "fonte VARCHAR," +
                    "tipo NUMBER(1)," +
                    "sala VARCHAR," +
                    "status VARCHAR," +
                    "estudante VARCHAR," +
                    "ajudante VARCHAR," +
                    "tempo VARCHAR," +
                    "dataatualizacao NUMBER(19)," +
                    "nrestudo NUMBER(10));");

        db.execSQL("CREATE TABLE estudo (" +
                "nrestudo NUMBER(10) PRIMARY KEY," +
                "descricao VARCHAR);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

    public void insereUsuario(Usuario usuario) {
        ContentValues values = this.usuarioValues(usuario);

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert("usuario", null, values);
        db.close();
    }

    public void atualizaUsuario(Usuario usuario) {
        ContentValues values = this.usuarioValues(usuario);

        SQLiteDatabase db = this.getWritableDatabase();

        db.update("usuario", values, "_id = ?", new String[]{Integer.toString(usuario.getId())});
        db.close();
    }

    public boolean existeRegistros() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT _id FROM designacao", null);

        boolean existe = cursor.getCount() > 0;

        cursor.close();

        return existe;
    }

    public Usuario obterUsuario(String idOnline) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] values = {idOnline};

        Cursor cursor = db.rawQuery("SELECT _id, idonline, nome, senha, bloqueado FROM usuario WHERE idonline = ?", values);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            return this.preencheUsuario(cursor);

        } else {
            cursor.close();
            return new Usuario();
        }
    }

    public void insereEstudo(Estudo estudo) {
        ContentValues values = this.estudoValues(estudo);

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert("estudo", null, values);
        db.close();
    }

    public void atualizaEstudo(Estudo estudo) {
        ContentValues values = this.estudoValues(estudo);

        SQLiteDatabase db = this.getWritableDatabase();

        db.update("estudo", values, "nrestudo = ?", new String[]{Integer.toString(estudo.getNrEstudo())});
        db.close();
    }

    public Estudo obterEstudo(int nrEstudo) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] values = {Integer.toString(nrEstudo)};

        Cursor cursor = db.rawQuery("SELECT nrestudo, descricao FROM estudo WHERE nrestudo = ?", values);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            return this.preencheEstudo(cursor);

        } else {
            cursor.close();
            return new Estudo();
        }
    }

    public void insereDesignacao(Designacao designacao) {
        ContentValues values = this.designacaoValues(designacao);

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert("designacao", null, values);
        db.close();
    }

    public void atualizaDesignacao(Designacao designacao) {
        ContentValues values = this.designacaoValues(designacao);

        SQLiteDatabase db = this.getWritableDatabase();

        db.update("designacao", values, "_id = ?", new String[]{Integer.toString(designacao.getId())});
        db.close();
    }

    public void removeDesignacoesPorData(Date data) {
        SQLiteDatabase db = this.getWritableDatabase();

        String[] values = {Long.toString(data.getTime())};

        db.rawQuery("DELETE FROM designacao WHERE data <= ?", values);

        db.close();
    }

    public Designacao obterDesignacao(String idOnline) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] values = {idOnline};

        Cursor cursor = db.rawQuery("SELECT _id, idonline, data, fonte, tipo, sala, status, estudante, ajudante, tempo, dataatualizacao, nrestudo " +
                        "FROM designacao WHERE idonline = ?", values);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            Designacao des = this.preencheDesignacao(cursor);
            cursor.close();
            return des;

        } else {
            cursor.close();
            return new Designacao();
        }
    }

    public List<Date> datasDesignacoes() {
        List<Date> datas = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT DISTINCT(data) FROM designacao ORDER BY data", null);

        while (cursor.moveToNext()) {
            datas.add(new Date(cursor.getLong(0)));
        }

        cursor.close();

        return datas;
    }

    public List<Designacao> designacoesPorData(Date data, String sala) {
        List<Designacao> designacaos = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        String[] values = {Long.toString(data.getTime()), sala.replace("Sala ", "")};

        Cursor cursor = db.rawQuery("SELECT _id, idonline, data, fonte, tipo, sala, status, estudante, ajudante, tempo, dataatualizacao, d.nrestudo, e.descricao FROM designacao d " +
                "LEFT JOIN estudo e ON e.nrestudo = d.nrestudo WHERE data = ? AND sala = ?", values);

        while (cursor.moveToNext()) {
            Designacao des = this.preencheDesignacao(cursor);
            des.setNomeEstudo(cursor.getString(12));
            designacaos.add(des);
        }

        cursor.close();

        return designacaos;
    }

    public boolean logon() {
        Usuario usuario = Sessao.getInstance().getUsuarioLogado();

        SQLiteDatabase db = this.getReadableDatabase();

        String[] values = {usuario.getNome(), usuario.getSenha()};

        Cursor cursor = db.rawQuery("SELECT 1 FROM usuario WHERE nome = ? AND senha = ?", values);

        return cursor.getCount() > 0;
    }

    public void reset() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete("usuario", null, null);
        db.delete("designacao", null, null);

        db.close();
    };

    private ContentValues designacaoValues(Designacao designacao) {
        ContentValues values = new ContentValues();
        values.put("idonline", designacao.getIdOnline());
        values.put("data", designacao.getData().getTime());
        values.put("fonte", designacao.getFonte());
        values.put("tipo", designacao.getTipoDesignacao().ordinal());
        values.put("sala", designacao.getSala());
        values.put("status", designacao.getStatus().getSigla());
        values.put("estudante", designacao.getEstudante());
        values.put("ajudante", designacao.getAjudante());
        values.put("dataatualizacao", designacao.getDataAtualizacao().getTime());
        values.put("nrestudo", designacao.getNrEstudo());

        return values;
    }

    private ContentValues estudoValues(Estudo estudo) {
        ContentValues values = new ContentValues();
        values.put("nrestudo", estudo.getNrEstudo());
        values.put("descricao", estudo.getDescricao());

        return values;
    }

    private ContentValues usuarioValues(Usuario usuario) {
        ContentValues values = new ContentValues();
        values.put("idonline", usuario.getIdonline());
        values.put("nome", usuario.getNome());
        values.put("senha", usuario.getSenha());
        values.put("bloqueado", usuario.isBloqueado());

        return values;
    }

    private Designacao preencheDesignacao(Cursor cursor) {
        Designacao designacao = new Designacao();
        designacao.setId(cursor.getInt(0));
        designacao.setIdOnline(cursor.getString(1));
        designacao.setData(new Date(cursor.getLong(2)));
        designacao.setFonte(cursor.getString(3));
        designacao.setTipoDesignacao(TipoDesignacao.values()[cursor.getInt(4)]);
        designacao.setSala(cursor.getString(5));
        designacao.setStatus(Avaliacao.getByInitials(cursor.getString(6)));
        designacao.setEstudante(cursor.getString(7));
        designacao.setAjudante(cursor.getString(8));
        designacao.setDataAtualizacao(new Date(cursor.getLong(10)));
        designacao.setNrEstudo(cursor.getInt(11));

        String tempo = cursor.getString(9);
        designacao.setTempo(tempo == null || "".equals(tempo) ? "00:00" : tempo);

        return designacao;
    }

    private Usuario preencheUsuario(Cursor cursor) {
        Usuario usuario = new Usuario();
        usuario.setId(cursor.getInt(0));
        usuario.setIdonline(cursor.getString(1));
        usuario.setNome(cursor.getString(2));
        usuario.setSenha(cursor.getString(3));
        usuario.setBloqueado(cursor.getInt(4) == 1);

        cursor.close();

        return usuario;
    }

    private Estudo preencheEstudo(Cursor cursor) {
        Estudo estudo = new Estudo();
        estudo.setNrEstudo(cursor.getInt(0));
        estudo.setDescricao(cursor.getString(1));

        cursor.close();

        return estudo;
    }
}