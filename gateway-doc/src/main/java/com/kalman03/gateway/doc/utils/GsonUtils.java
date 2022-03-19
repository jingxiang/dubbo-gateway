package com.kalman03.gateway.doc.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author kalman03
 * @since 2022-03-18
 */
public class GsonUtils {

	static Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();

	public static String prettyJson(Object object) {
		return prettyGson.toJson(object);
	}
}
