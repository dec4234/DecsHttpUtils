package net.dec4234.httputils.core;

import com.google.gson.JsonObject;

@FunctionalInterface
public interface JsonObjectModifier {

	JsonObject modify(JsonObject source);
}
