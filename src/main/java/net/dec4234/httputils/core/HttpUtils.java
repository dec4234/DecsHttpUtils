package net.dec4234.httputils.core;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class HttpUtils {

	private Map<String, String> defaultHeaders;
	private boolean isDebugEnabled = false;

	public HttpUtils(Map<String, String> defaultHeaders) {
		this.defaultHeaders = defaultHeaders;
	}

	public void enableDebugMode(boolean debug) {
		isDebugEnabled = debug;
	}

	/**
	 * Send a GET url request to the url provided, returns a JsonObject of the response
	 */
	public JsonObject urlRequestGET(String url) {
		return getJsonObject(getStringResponse(getRequest(true, url, starter -> {
			starter.GET();
			return starter;
		})));
	}

	@Deprecated
	public JsonObject urlRequestGETOauth(String url) {
		return getJsonObject(getStringResponse(getRequest(true, url, starter -> {
			starter.GET();
			return starter;
		})));
	}

	public String urlRequestPOST(String url, String body) {
		if (body.isEmpty()) { body = "{\"message\": \"\",}"; }
		String finalBody = body;
		return getStringResponse(getRequest(true, url, starter -> {
			starter.setHeader("Content-Type", "application/json")
				   .POST(HttpRequest.BodyPublishers.ofString(finalBody));
			return starter;
		}));
	}

	@Deprecated
	public String urlRequestPOSTOauth(String url, String body) {

		if (body.isEmpty()) { body = "{\"message\": \"\",}"; }

		String finalBody = body;
		return getStringResponse(getRequest(true, url, starter -> {
			starter
				   .setHeader("Content-Type", "application/json")
				   .POST(HttpRequest.BodyPublishers.ofString(finalBody));

			return starter;
		}));
	}

	@Deprecated
	public JsonObject urlRequestPOSTOauth(String url, JsonObject body) {

		if (body.toString().isEmpty()) {
			body = new JsonObject();
			body.addProperty("message", "");
		}

		final String finalBody = body.toString();
		return getJsonObject(getStringResponse(getRequest(true, url, starter -> {
			starter
				   .setHeader("Content-Type", "application/json")
				   .POST(HttpRequest.BodyPublishers.ofString(finalBody));

			return starter;
		})));
	}

	private JsonObject getJsonObject(String stringResponse) {
		JsonObject jsonObject = new JsonParser().parse(stringResponse).getAsJsonObject();

		return jsonObject;
	}

	private String getStringResponse(HttpRequest httpRequest) {
		HttpClient httpClient = HttpClient.newHttpClient();
		try {
			String responseString = httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString()).thenApplyAsync(HttpResponse::body).get();

			if (isDebugEnabled) {
				System.out.println(httpRequest.method() + " " + httpRequest.uri().toString());
				System.out.println(responseString);
			}
			return responseString;
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

		return null;
	}

	private HttpRequest getRequest(boolean standardRequest, String url, HttpRequestModifier httpRequestModifier) {

		HttpRequest.Builder builder = httpRequestModifier.modifyRequest(HttpRequest.newBuilder());

		builder.uri(URI.create(url)).timeout(Duration.ofMinutes(3));

		if(standardRequest) {
			defaultHeaders.forEach((s, s2) -> {
				builder.setHeader(s, s2);
			});
		}

		return builder.build();
	}
}
