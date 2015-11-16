package br.com.dbcorp.melhoreministerio.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {

	private static final String BANCO_DADOS = "EscolaMinisterio";
	private static int VERSAO = 100;
	
	public DataBaseHelper(Context context) {
		super(context, BANCO_DADOS, null, VERSAO);
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
                    "status CHAR," +
                    "estudante VARCHAR," +
                    "ajudante VARCHAR," +
                    "nrestudo NUMBER(10));");

        db.execSQL("CREATE TABLE estudo (" +
                "nrestudo NUMBER(10) PRIMARY KEY," +
                "idonline VARCHAR," +
                "descricao VARCHAR);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}