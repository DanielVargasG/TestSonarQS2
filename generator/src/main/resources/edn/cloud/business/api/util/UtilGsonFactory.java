package edn.cloud.business.api.util;

import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class UtilGsonFactory {

	private static final UtilGsonFactory INSTANCE = new UtilGsonFactory();

	public static UtilGsonFactory getInstance() {
		return INSTANCE;
	}

	private UtilGsonFactory() {
	}

	public Gson createDefaultGson() {
		return createaDefaultJsonBuilder().create();
	}

	public Gson createAnnotatedGson() {
		GsonBuilder gb = createaDefaultJsonBuilder();
		return gb.excludeFieldsWithoutExposeAnnotation().create();
	}

	private GsonBuilder createaDefaultJsonBuilder() {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Date.class, new UtilDateTimeAdapter());
		gsonBuilder.setPrettyPrinting();
		return gsonBuilder;
	}

}
