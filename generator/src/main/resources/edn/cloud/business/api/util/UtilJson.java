package edn.cloud.business.api.util;

import java.io.IOException;
import java.io.StringReader;
import com.google.gson.stream.JsonReader;

public class UtilJson {

	private static final UtilJson INSTANCE = new UtilJson();

	public static UtilJson getInstance() {
		return INSTANCE;
	}
	
	private UtilJson() {
	}

	public JsonReader createJsonReader(String json) throws IOException {
		JsonReader reader = new JsonReader(new StringReader(json));
		return openJsonReader(reader);
	}

	public JsonReader openJsonReader(JsonReader reader) throws IOException {
		// Bypasses the top level element
		reader.beginObject();
		reader.nextName();
		return reader;
	}

	public JsonReader closeJsonReader(JsonReader reader) throws IOException {
		reader.endObject();
		reader.close();
		return reader;
	}

	public <T> T load(String json, JsonLoadingJob<T> job) throws IOException {
		JsonReader reader = createJsonReader(json);
		try {
			return job.loadFromJson(reader);
		} finally {
			closeJsonReader(reader);
		}
	}

	public interface JsonLoadingJob<T> {
		T loadFromJson(JsonReader reader);
	}
}
