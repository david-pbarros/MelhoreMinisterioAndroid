package br.com.dbcorp.melhoreministerio.sinc;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.util.encoders.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.StringReader;
import java.net.URLDecoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Security;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import br.com.dbcorp.melhoreministerio.Sessao;
import br.com.dbcorp.melhoreministerio.db.DataBaseHelper;
import br.com.dbcorp.melhoreministerio.dto.Avaliacao;
import br.com.dbcorp.melhoreministerio.dto.Designacao;
import br.com.dbcorp.melhoreministerio.dto.Estudo;
import br.com.dbcorp.melhoreministerio.dto.TipoDesignacao;
import br.com.dbcorp.melhoreministerio.dto.Usuario;

import static br.com.dbcorp.melhoreministerio.sinc.PHPConnection.HTTP_METHOD;

public class Sincronizador {

	private static final String chave = "LS0tLS1CRUdJTiBQVUJMSUMgS0VZLS0tLS0NCk1JR2ZNQTBHQ1NxR1NJYjNEUUVCQVFVQUE0R05BRENCaVFLQmdRQzNwbVVNVVEvNDRvN3h2TDJIUmhWUC8ycVYNCkEvTkRCRGZGdENrbFJldU1iTGNRa1k1UlVqU05JaFBZdlFpN3V3dG52NUdWZ1RaK1BreU55UmdPdnUvTGlhKysNCm4yeFJLMDhma05xdkxNR2trZFg0VWo5Q0V5U2hsNEFGRXZCeVpDTjFiOU52cGVWVzJ5dmY5eUl1eXVtUjV2SjgNCmxMbXVPSXZQZmpHTkkvUkJQd0lEQVFBQg0KLS0tLS1FTkQgUFVCTElDIEtFWS0tLS0t";

	public static int TIPO = 2;

	private Sessao sessao;
	private DataBaseHelper dbHelper;

	private Context contexto;
	private SharedPreferences preferences;
	
	private PublicKey chavePublica;
	private String hash;
	private StringBuffer mensagensTela;

	public Sincronizador(Context contexto, DataBaseHelper dbHelper) {
		this.contexto = contexto;

		this.preferences = PreferenceManager.getDefaultSharedPreferences(this.contexto);

		this.sessao = Sessao.getInstance();


		this.dbHelper = dbHelper;
	}



	public boolean login() {
		try {
			this.obterChave();
			this.gerarHash();
			
			PHPConnection con = new PHPConnection("/service.php/mobile/logon", HTTP_METHOD.POST, this.hash);
			con.connect();
			
			if (con.getResponseCode() != 200) {
				throw new RuntimeException(con.getErrorDetails());
			}
			
			JSONObject obj = con.getResponse();
			
			return "OK".equalsIgnoreCase(obj.getString("response"));
			
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	public void sincronismoGeral() {
		try {
			Sincronizador.TIPO = 1;

			this.obterChave();
			this.gerarHash();

			this.logins();
			this.estudos();
			this.designacoes();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private String logins() throws Exception {
		PHPConnection con = new PHPConnection("/service.php/mobile/usuarios", HTTP_METHOD.GET, this.hash);
		con.connect();

		if (con.getResponseCode() != 200) {
			throw new RuntimeException(con.getErrorDetails());
		}

		JSONObject obj = con.getResponse();

		if ("ok".equalsIgnoreCase(obj.getString("response"))) {
			JSONArray array = obj.getJSONArray("itens");

			for (int i = 0; i < array.length(); i++) {
				JSONObject item = array.getJSONObject(i);

				Usuario usuario = this.dbHelper.obterUsuario(item.getString("id"));

				usuario.setBloqueado(item.getInt("bloqueado") == 1 ? true : false);
				usuario.setSenha(item.getString("senha"));
				usuario.setNome(URLDecoder.decode(item.getString("nome"), "UTF-8"));

				if (usuario.getIdonline() == null) {
					usuario.setIdonline(item.getString("id"));
					this.dbHelper.insereUsuario(usuario);

				} else {
					this.dbHelper.atualizaUsuario(usuario);
				}
			}
		} else if ("ERRO".equalsIgnoreCase(obj.getString("response"))) {
			return "\n" + obj.getString("mensagem");
		}

		return "";
	}

	private String estudos() throws Exception {
		PHPConnection con = new PHPConnection("/service.php/mobile/estudo", HTTP_METHOD.GET, this.hash);
		con.connect();

		if (con.getResponseCode() != 200) {
			throw new RuntimeException(con.getErrorDetails());
		}

		JSONObject obj = con.getResponse();

		if ("ok".equalsIgnoreCase(obj.getString("response"))) {
			JSONArray array = obj.getJSONArray("itens");

			for (int i = 0; i < array.length(); i++) {
				JSONObject item = array.getJSONObject(i);

				Estudo estudo = this.dbHelper.obterEstudo(item.getInt("nrestudo"));

				estudo.setDescricao(URLDecoder.decode(item.getString("descricao"), "UTF-8"));

				if (estudo.getNrEstudo() == 0) {
					estudo.setNrEstudo(item.getInt("nrestudo"));
					this.dbHelper.insereEstudo(estudo);

				} else {
					this.dbHelper.atualizaEstudo(estudo);
				}
			}
		} else if ("ERRO".equalsIgnoreCase(obj.getString("response"))) {
			return "\n" + obj.getString("mensagem");
		}

		return "";
	}

	public String designacoes() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		try {
			if (this.hash == null) {
				this.obterChave();
				this.gerarHash();
			}

			Calendar cd = Calendar.getInstance();
			cd.add(Calendar.WEEK_OF_YEAR, -2);

			this.dbHelper.removeDesignacoesPorData(cd.getTime());

			PHPConnection con = new PHPConnection("/service.php/mobile/designacao", HTTP_METHOD.GET, this.hash);
			con.setParameter("data_ultima", sdf.format(cd.getTime()));
			con.connect();

			if (con.getResponseCode() != 200) {
				throw new RuntimeException(con.getErrorDetails());
			}

			JSONObject obj = con.getResponse();

			if ("ok".equalsIgnoreCase(obj.getString("response"))) {
				JSONArray array = obj.getJSONArray("itens");

				for (int i = 0; i < array.length(); i++) {
					JSONObject item = array.getJSONObject(i);

					Designacao designacao = this.dbHelper.obterDesignacao(item.getString("id"));

					designacao.setData(sdf.parse(item.getString("data")));
					designacao.setFonte(URLDecoder.decode(item.getString("fonte"), "UTF-8"));
					designacao.setSala(item.getString("sala"));
					designacao.setAjudante(URLDecoder.decode(item.getString("ajudante"), "UTF-8"));
					designacao.setEstudante(URLDecoder.decode(item.getString("estudante"), "UTF-8"));
					designacao.setNrEstudo(item.getInt("nrestudo"));


					switch (item.getInt("numero")) {
						case 1:
							designacao.setTipoDesignacao(TipoDesignacao.LEITURA);
							break;
						case 2:
							designacao.setTipoDesignacao(TipoDesignacao.VISITA);
							break;
						case 3:
							designacao.setTipoDesignacao(TipoDesignacao.REVISITA);
							break;
						case 4:
							designacao.setTipoDesignacao(TipoDesignacao.ESTUDO);
							break;
					}

					if (designacao.getIdOnline() == null) {
						designacao.setIdOnline(item.getString("id"));

						this.dadosDesignacao(designacao, item, sdf2);
						this.dbHelper.insereDesignacao(designacao);

					} else {
						if (designacao.getDataAtualizacao().after(sdf2.parse(item.getString("dtultimaatualiza")))) {
							this.atualizaDesignacao(designacao);

						} else {
							this.dadosDesignacao(designacao, item, sdf2);
							this.dbHelper.atualizaDesignacao(designacao);
						}
					}
				}
			} else if ("ERRO".equalsIgnoreCase(obj.getString("response"))) {
				return "\n" + obj.getString("mensagem");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return "";
	}
	
	private void obterChave() throws IOException {
		if (Security.getProvider("BC") == null) {
		    Security.addProvider(new BouncyCastleProvider());
		}
		
		byte[] hash = Base64.decode(chave);

		String keyString = new String(hash, "UTF-8");

		try {
            PEMReader pemReader = new PEMReader(new StringReader(keyString));

            this.chavePublica = (PublicKey) pemReader.readObject();

            pemReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

	public String atualizaDesignacao(Designacao designacao) {
		try {
			if (this.hash == null) {
				this.obterChave();
				this.gerarHash();
			}

			PHPConnection con = new PHPConnection("/service.php/mobile/designacao", HTTP_METHOD.POST, this.hash);
			con.setParameter("id", designacao.getIdOnline());
			con.setParameter("status", designacao.getStatus().getSigla());
			con.setParameter("tempo", designacao.getTempo());
			con.connect();

			if (con.getResponseCode() != 200) {
				throw new RuntimeException(con.getErrorDetails());
			}

			JSONObject obj = con.getResponse();

			return "OK".equalsIgnoreCase(obj.getString("response")) ? "" : obj.getString("mensagem");

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return "";
	}
	
	private void gerarHash() throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		StringBuilder hash = new StringBuilder(this.preferences.getString("nrCong", "")).append(";");

		if (this.sessao.getUsuarioLogado() != null) {
			hash.append(this.sessao.getUsuarioLogado().getNome()).append(";")
					.append(this.sessao.getUsuarioLogado().getSenha()).append(";");

		} else {
			hash.append(";").append(";");
		}
		
		hash.append(100);
		
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC");
		cipher.init(Cipher.ENCRYPT_MODE, this.chavePublica);
		
		byte[] ciphered = cipher.doFinal(hash.toString().getBytes());
		
		this.hash = android.util.Base64.encodeToString(ciphered, android.util.Base64.NO_WRAP);
	}

	private void dadosDesignacao(Designacao designacao, JSONObject item, SimpleDateFormat sdf) throws JSONException, ParseException {
		designacao.setStatus(Avaliacao.getByInitials(item.getString("status")));
		designacao.setTempo(item.getString("tempo"));
		designacao.setDataAtualizacao(sdf.parse(item.getString("dtultimaatualiza")));
	}
}
