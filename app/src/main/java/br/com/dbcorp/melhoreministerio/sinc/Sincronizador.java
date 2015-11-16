package br.com.dbcorp.melhoreministerio.sinc;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.util.encoders.Base64;
import org.json.JSONObject;

import java.io.IOException;
import java.io.StringReader;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Sincronizador {


	private PublicKey chave;
	//private Sincronismo ultimaSincronia;
	private String hash;
	private StringBuffer mensagensTela;
	
	public void verificaSinc() {
		try {
			this.obterChave();
			this.gerarHash();
			
			PHPConnection con = new PHPConnection("/service.php/lastSinc", PHPConnection.HTTP_METHOD.GET, this.hash);
			con.connect();
			
			if (con.getResponseCode() != 200) {
				throw new RuntimeException(con.getErrorDetails());
			}
			
			JSONObject obj = con.getResponse();
			
			if (obj != null) {
				if ("existente".equalsIgnoreCase(obj.getString("response"))) {
					//LocalDateTime temp = LocalDateTime.parse(obj.getString("data"), br.com.dbcorp.escolaMinisterio.ui.Params.dateTimeFormate());
					
					//Params.propriedades().put("doSinc", this.gerenciador.pegarUltimo().getData().isBefore(temp));
					
				} else {
					//Params.propriedades().put("doSinc", true);
				}
			}
		} catch (Exception ex) {
			//Params.propriedades().put("doSinc", true);
		}
	}
	
	public void finalizaSinc(boolean hasErro) {
		try {
			this.obterChave();
			this.gerarHash();
			
			PHPConnection con = new PHPConnection("/service.php/lastSinc", PHPConnection.HTTP_METHOD.POST, this.hash);
			con.setParameter("status", hasErro ? "I" : "C");
			con.connect();
			
			if (con.getResponseCode() != 200) {
				throw new RuntimeException(con.getErrorDetails());
			}
		} catch (Exception ex) {
			//Params.propriedades().put("doSinc", true);
		}
	}
	
	public String versao() {
		String retorno = "";
		
		try {
			this.obterChave();
			this.gerarHash();
			
			PHPConnection con = new PHPConnection("/service.php/versao", PHPConnection.HTTP_METHOD.GET, this.hash);
			con.connect();
			
			if (con.getResponseCode() != 200) {
				throw new RuntimeException(con.getErrorDetails());
			}
			
			JSONObject obj = con.getResponse();
			
			if (obj != null) {
				int versao = (int) 0;//Params.propriedades().get("verionNumber");
				
				if (versao < obj.getInt("versao")) {
					retorno = "\nExiste nova vers�o do sistema para download.";
					
					String msg = obj.getString("msg");
					
					if (msg.length() > 0) {
						retorno += "\n" + msg;
					}
				}
			}
		} catch (Exception ex) {
			retorno =  "";
		}
		
		return retorno;
	}
	
	public void verificaVersao() {
		String retorno = this.versao();
		
		if (!"".equals(retorno)) {
			//JOptionPane.showMessageDialog(null, retorno, "Nova Vers�o", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	public boolean login() {
		try {
			this.obterChave();
			this.gerarHash();
			
			PHPConnection con = new PHPConnection("/service.php/logon", PHPConnection.HTTP_METHOD.POST, this.hash);
			con.connect();
			
			if (con.getResponseCode() != 200) {
				throw new RuntimeException(con.getErrorDetails());
			}
			
			JSONObject obj = con.getResponse();
			
			return "OK".equalsIgnoreCase(obj.getString("response"));
			
		} catch (Exception ex) {
			return false;
		}
	}
	
	/*public void sincronizarSeguranca() {
		Sincronismo sinc  = new Sincronismo();
		sinc.setSucesso(true);
		
		try {
			this.obterChave();
			this.gerarHash();
			
			this.ultimaSincronia = this.gerenciador.pegarUltimo();
			
			this.profile = new ProfileSinc(this.gerenciador, this.ultimaSincronia, this.hash);
			this.itemProfile = new ItemProfileSinc(this.gerenciador, this.ultimaSincronia, this.hash);
			this.usuario = new UsuarioSinc(this.gerenciador, this.ultimaSincronia, this.hash);
			
			this.refreshMsg("\nObtendo novos perfis...");
			if (!this.hasError("\nProblemas nos profiles recebidos:", this.profile.obterNovos())) {
				this.refreshMsg("\nObtendo novos itens de seguran�a...");
				if (!this.hasError("\nProblemas nos itens de seguran�a recebidos:", this.itemProfile.obterNovos())) {
					this.refreshMsg("\nObtendo novos usuarios...");
					this.hasError("\nProblemas nos usuarios recebidos:", this.usuario.obterNovos());
				}
			}
			
			this.refreshMsg("\nFim do sincronismo. Reinicie o sistema.");
			
		} catch (Exception ex) {
			String erro = "Erro inesperado durante o sincronismo de informa��es.";
			this.log.error(erro, ex);
			
			this.refreshMsg("\n" + erro+ " Consultar Log.");
			
			sinc.setSucesso(false);
		}
		
		sinc.setData(LocalDateTime.now());
		this.gerenciador.salvar(sinc);
	}*/
	
	public void sincronizar() {
		/*boolean hasErro = false;
		
		Sincronismo sinc = new Sincronismo();
		
		try {
			
			this.obterChave();
			this.gerarHash();
			
			this.gerenciador.apagarVelhos();
			this.ultimaSincronia = this.gerenciador.pegarUltimo();
			Sincronismo ultimoSeguranca = this.gerenciador.pegarUltimoSeguranca();

			this.mes = new MesSinc(this.gerenciador, this.ultimaSincronia, this.hash);
			this.ajudante = new AjudanteSinc(this.gerenciador, this.ultimaSincronia, this.hash);
			this.estudante = new EstudanteSinc(this.gerenciador, this.ultimaSincronia, this.hash);
			this.estudo = new EstudoSinc(this.gerenciador, this.ultimaSincronia, this.hash);
			this.profile = new ProfileSinc(this.gerenciador, ultimoSeguranca, this.hash);
			this.itemProfile = new ItemProfileSinc(this.gerenciador, ultimoSeguranca, this.hash);
			this.usuario = new UsuarioSinc(this.gerenciador, ultimoSeguranca, this.hash);
			this.semana = new SemanaSinc(this.gerenciador, this.ultimaSincronia, this.hash);
			this.designacao = new DesignacaoSinc(this.gerenciador, this.ultimaSincronia, this.hash);
			
			this.refreshMsg("\nDesfragmentando a base...");
			this.gerenciador.desfragmentarBase();

			hasErro = true;
			
			if (this.apagarLocal()) {
				if (this.apagaWeb()) {
					if (this.enviarNovos()) {
						if (this.atualizarWeb()) {
							if (this.obterNovos()) {
								hasErro = false;
								Params.propriedades().put("doSinc", false);
							}
						}
					}
				}
			}
			
			this.finalizaSinc(hasErro);
			
			this.refreshMsg("\nFim do sincronismo. Reinicie o sistema.");
			
		} catch (Exception ex) {
			String erro = "Erro inesperado durante o sincronismo de informa��es.";
			this.log.error(erro, ex);
			
			this.refreshMsg("\n" + erro + " Consultar Log.");
			
			hasErro = true;
		}
		
		sinc.setData(LocalDateTime.now());
		sinc.setSucesso(!hasErro);
		
		this.gerenciador.salvar(sinc);*/
	}
	
	private void obterChave() throws IOException {
		if (Security.getProvider("BC") == null) {
		    Security.addProvider(new BouncyCastleProvider());
		}
		
		byte[] hash = Base64.decode("chavepublica");

		String keyString = new String(hash, "UTF-8");
		
		 PEMReader pemReader= new PEMReader(new StringReader(keyString));
		 this.chave = (PublicKey) pemReader.readObject();
		 
		 pemReader.close();
	}
	
	private void gerarHash() throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		//StringBuffer hash = new StringBuffer(Params.propriedades().getProperty("congregacao")).append(";");
		
		/*if (this.gerenciador.getUser() != null) {
			hash.append(this.gerenciador.getUser().getNome()).append(";")
				.append(this.gerenciador.getUser().getSenha()).append(";");
		
		} else {
			hash.append(";").append(";");
		}
		
		hash.append(Params.propriedades().get("verionNumber"));*/
		
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC");
		cipher.init(Cipher.ENCRYPT_MODE, this.chave);
		
		byte[] ciphered = cipher.doFinal(hash.toString().getBytes());
		
		this.hash = Base64.toBase64String(ciphered);
	}
}
