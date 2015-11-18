package br.com.dbcorp.melhoreministerio.sinc;

import android.net.Uri;
import android.os.StrictMode;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class PHPConnection {

	private static final String url = "http://10.0.2.2:81/escolaWS";

	public enum HTTP_METHOD {
		GET, POST, PUT, DELETE;
	}
	
	private HttpURLConnection conn;
	private DataOutputStream wr;
	private String hash;
	private Map<String, Object> parameters;
	
	public PHPConnection(String urlComp, HTTP_METHOD method, String hash) throws IOException {
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());//premite acesso a net sincronamente na main thread.

		this.createConn(new URL(url + urlComp), method);
		
		this.parameters = new HashMap<String, Object>();
		this.hash = hash.replace("+", "%2B");

		if (method != HTTP_METHOD.GET && method != HTTP_METHOD.PUT) {
			conn.setDoOutput(true);
			this.wr = new DataOutputStream(conn.getOutputStream());
		}
	}
	
	public void setParameter(String key, Object value) throws UnsupportedEncodingException {
		if ("senha".equalsIgnoreCase(key)) {
			value = ((String) value).replace("+", "%2b");
			
		} else if (value instanceof String) {
			value = URLEncoder.encode((String) value, "UTF-8");
		}
		
		this.parameters.put(key, value);
	}
	
	public void connect() throws IOException {
		String urlParameters = "hash=";

		urlParameters += this.hash;

		for (String key : this.parameters.keySet()) {
			urlParameters += "&" + key + "=" + this.parameters.get(key);
		}

		if (this.wr != null) {
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

		} else {
			String url = this.conn.getURL().toString() + "?" + urlParameters;

			createConn(new URL(url), HTTP_METHOD.valueOf(this.conn.getRequestMethod()));
		}

		this.conn.connect();
	}
	
	public int getResponseCode() throws IOException {
		return this.conn.getResponseCode();
	}
	
	public String getErrorDetails() throws IOException {
		StringBuffer sb = new StringBuffer("HTTP code : ")
			.append(conn.getResponseCode()).append(" ");
		
		BufferedReader br = new BufferedReader(new InputStreamReader((this.conn.getErrorStream()), "UTF-8"));

		String temp = null;
		
		while ((temp = br.readLine()) != null) {
			sb.append(temp).append("\n");
		}

		return sb.toString();
	}
	
	public JSONObject getResponse() throws IOException {
		String response = this.getStringResponse();
		
		if (response != null && response.length() > 0) {
			try {
				return new JSONObject(response);

			} catch (JSONException ex) {
				return null;
			}
		}
		
		return null;
	}
	
	public String getStringResponse() throws IOException {
		StringBuffer sb = new StringBuffer();
		
		BufferedReader br = new BufferedReader(new InputStreamReader((this.conn.getInputStream()), "UTF-8"));
		 
		String temp;
		while ((temp = br.readLine()) != null) {
			sb.append(temp);
		}
		
		return sb.toString();
	}

	private void createConn(URL uri, HTTP_METHOD method) throws IOException {
		this.conn = (HttpURLConnection) uri.openConnection();
		this.conn.setRequestMethod(method.toString());
		this.conn.setRequestProperty("Accept-Charset", "UTF-8");
		this.conn.setRequestProperty("Accept", "application/json");
	}
}
