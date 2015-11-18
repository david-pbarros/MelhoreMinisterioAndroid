package br.com.dbcorp.melhoreministerio.sinc;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.util.encoders.Base64;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.StringReader;
import java.net.URLDecoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Security;
import java.text.SimpleDateFormat;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import br.com.dbcorp.melhoreministerio.Sessao;
import br.com.dbcorp.melhoreministerio.dto.Avaliacao;
import br.com.dbcorp.melhoreministerio.dto.Designacao;
import br.com.dbcorp.melhoreministerio.dto.Estudo;
import br.com.dbcorp.melhoreministerio.dto.TipoDesignacao;
import br.com.dbcorp.melhoreministerio.dto.Usuario;

import static br.com.dbcorp.melhoreministerio.sinc.PHPConnection.HTTP_METHOD;

public class Sincronizador {

	private static final String chave = "LS0tLS1CRUdJTiBQVUJMSUMgS0VZLS0tLS0NCk1JR2ZNQTBHQ1NxR1NJYjNEUUVCQVFVQUE0R05BRENCaVFLQmdRQzNwbVVNVVEvNDRvN3h2TDJIUmhWUC8ycVYNCkEvTkRCRGZGdENrbFJldU1iTGNRa1k1UlVqU05JaFBZdlFpN3V3dG52NUdWZ1RaK1BreU55UmdPdnUvTGlhKysNCm4yeFJLMDhma05xdkxNR2trZFg0VWo5Q0V5U2hsNEFGRXZCeVpDTjFiOU52cGVWVzJ5dmY5eUl1eXVtUjV2SjgNCmxMbXVPSXZQZmpHTkkvUkJQd0lEQVFBQg0KLS0tLS1FTkQgUFVCTElDIEtFWS0tLS0t";

	private Sessao sessao;

	private Context contexto;
	private SharedPreferences preferences;
	
	private PublicKey chavePublica;
	//private Sincronismo ultimaSincronia;
	private String hash;
	private StringBuffer mensagensTela;
	
	public Sincronizador(Context contexto) {
		this.contexto = contexto;

		this.preferences = PreferenceManager.getDefaultSharedPreferences(this.contexto);

		this.sessao = Sessao.getInstance();
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
			this.obterChave();
			this.gerarHash();

			this.logins();
			this.estudos();
			this.designacoes();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public String logins() throws Exception {
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

				Usuario usuario = new Usuario();//this.gerenciador.obterUsuario(item.getString("id"));

				usuario.setBloqueado(item.getInt("bloqueado") == 1 ? true : false);
				usuario.setSenha(item.getString("senha"));
				usuario.setNome(URLDecoder.decode(item.getString("nome"), "UTF-8"));

				if (usuario.getIdonline() == null) {
					usuario.setIdonline(item.getString("id"));
					//this.gerenciador.salvar(usuario);

				} else {
					//this.gerenciador.atualizar(usuario);
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

				Estudo estudo = new Estudo();//this.gerenciador.obterEstudo(item.getInt("nrestudo"));

				estudo.setDescricao(URLDecoder.decode(item.getString("descricao"), "UTF-8"));

				if (estudo.getNrEstudo() == 0) {
					estudo.setNrEstudo(item.getInt("nrestudo"));
					//this.gerenciador.salvar(estudo);

				} else {
					//this.gerenciador.atualizar(estudo);
				}
			}
		} else if ("ERRO".equalsIgnoreCase(obj.getString("response"))) {
			return "\n" + obj.getString("mensagem");
		}

		return "";
	}

	private String designacoes() {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

			PHPConnection con = new PHPConnection("/service.php/mobile/designacao", HTTP_METHOD.GET, this.hash);
			con.connect();

			if (con.getResponseCode() != 200) {
				throw new RuntimeException(con.getErrorDetails());
			}

			JSONObject obj = con.getResponse();

			if ("ok".equalsIgnoreCase(obj.getString("response"))) {
				JSONArray array = obj.getJSONArray("itens");

				for (int i = 0; i < array.length(); i++) {
					JSONObject item = array.getJSONObject(i);

					Designacao designacao = new Designacao();//this.gerenciador.obterDesignacao(item.getString("id"));

					designacao.setData(sdf.parse(item.getString("data")));
					designacao.setFonte(URLDecoder.decode(item.getString("fonte"), "UTF-8"));
					designacao.setSala(item.getString("sala"));
					designacao.setAjudante(item.getString("ajudante"));
					designacao.setEstudante(item.getString("estudante"));
					designacao.setNrEstudo(item.getInt("nrestudo"));
					designacao.setStatus(Avaliacao.getByInitials(item.getString("status")));

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
						//this.gerenciador.salvar(estudo);

					} else {
						//this.gerenciador.atualizar(estudo);
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
	
	private void gerarHash() throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		StringBuffer hash = new StringBuffer(this.preferences.getString("nrCong", "")).append(";");

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
}
