package net.dec4234.httputils.core;

import java.net.http.HttpRequest;

@FunctionalInterface
public interface HttpRequestModifier {

	HttpRequest.Builder modifyRequest(HttpRequest.Builder starter);
}
