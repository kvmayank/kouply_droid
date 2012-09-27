package com.ourlittlegame.utilities;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ProtocolException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;

public class HttpUtils {

	private String responseText;
	private int responseCode;

	public void doGet(String url) {
		Reader reader = null;
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet();
		try {
			request.setURI(new URI(url));
			HttpResponse response = client.execute(request);
			reader = new InputStreamReader(response.getEntity().getContent());
			StringBuffer sb = new StringBuffer("");
			char[] tmp = new char[1024];
			int l;
			while ((l = reader.read(tmp)) != -1) {
				sb.append(tmp, 0, l);
			}

			this.setResponseText(sb.toString());
			this.setResponseCode(response.getStatusLine().getStatusCode());

		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (reader != null)
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}

	public void doPost(String url, List<NameValuePair> nameValuePairs) {
		Reader reader = null;
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		try {

			client.setRedirectHandler(new RedirectHandler() {
				public URI getLocationURI(HttpResponse response,
						HttpContext context) throws ProtocolException {
					return null;
				}

				public boolean isRedirectRequested(HttpResponse response,
						HttpContext context) {
					return false;
				}
			});

			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = client.execute(post);
			reader = new InputStreamReader(response.getEntity().getContent());
			StringBuffer sb = new StringBuffer("");
			char[] tmp = new char[1024];
			int l;
			while ((l = reader.read(tmp)) != -1) {
				sb.append(tmp, 0, l);
			}
			this.setResponseText(sb.toString());
			this.setResponseCode(response.getStatusLine().getStatusCode());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (reader != null)
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}

	public static String doPost(String url, String postData) {
		Reader reader = null;
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		try {
			post.setEntity(new StringEntity(postData));
			HttpResponse response = client.execute(post);
			reader = new InputStreamReader(response.getEntity().getContent());
			StringBuffer sb = new StringBuffer("");
			char[] tmp = new char[1024];
			int l;
			while ((l = reader.read(tmp)) != -1) {
				sb.append(tmp, 0, l);
			}
			return sb.toString();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (reader != null)
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}

		return "";
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
		MiscUtils.println("Response Code: " + responseCode);
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseText(String responseText) {
		this.responseText = responseText;
		MiscUtils.println("Response: " + responseText);
	}

	public String getResponseText() {
		return responseText;
	}
}
