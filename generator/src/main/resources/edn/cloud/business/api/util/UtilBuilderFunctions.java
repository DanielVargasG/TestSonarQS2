package edn.cloud.business.api.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class UtilBuilderFunctions {

	private static Map<String, Locale> localeMap;

	public static void initCountryCodeMapping() {
		String[] countries = Locale.getISOCountries();
		localeMap = new HashMap<String, Locale>(countries.length);
		for (String country : countries) {
			Locale locale = new Locale("", country);
			localeMap.put(locale.getISO3Country().toUpperCase(), locale);
		}
	}

	public static String iso3CountryCodeToIso2CountryCode(String iso3CountryCode) {
		if (iso3CountryCode != "") {
			return localeMap.get(iso3CountryCode).getCountry();
		} else {
			return "GB";
		}
	}

	public static String iso3CountryCodeToIso2CountryCodeLowerCase(String iso3CountryCode) {
		if (iso3CountryCode != "") {
			return localeMap.get(iso3CountryCode).getCountry().toLowerCase();
		} else {
			return "gb";
		}
	}

	/**
	 * Función que elimina acentos y caracteres especiales de una cadena de texto.
	 * 
	 * @param input
	 * @return cadena de texto limpia de acentos y caracteres especiales.
	 */
	public static String normalizeStrintAscii(String input) {

		// input = escapeHtml(input);
		if (input != null && input.length() > 200) {
			input = input.substring(0, 200);
		}
		// Descomposición canónica
		// String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
		// Nos quedamos únicamente con los caracteres ASCII
		// Pattern pattern = Pattern.compile("\\p{ASCII}+");
		Pattern pattern = Pattern.compile("[^a-zA-Z0-9\\s]*");

		return pattern.matcher(input).replaceAll("");
	}
}
