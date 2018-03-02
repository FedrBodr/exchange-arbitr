package ru.fedrbodr.exchangearbitr.utils;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class JsonObjectUtils {

	public static JSONObject getNewJsonObject(String startUrl) throws IOException {
		return new JSONObject(IOUtils.toString(new URL(startUrl), StandardCharsets.UTF_8));
	}

	public static JSONObject getNewJsonObject(String startUrl, Proxy proxy) throws IOException {
		URL url = new URL(startUrl);
		return new JSONObject(IOUtils.toString(url.openConnection(proxy).getInputStream(), StandardCharsets.UTF_8));
	}
}
