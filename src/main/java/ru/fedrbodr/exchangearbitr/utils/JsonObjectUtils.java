package ru.fedrbodr.exchangearbitr.utils;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;

public class JsonObjectUtils {

	public static JSONObject getNewJsonObject(String startUrl) throws IOException {
		return new JSONObject(IOUtils.toString(new URL(startUrl), Charset.forName("UTF-8")));
	}
}
