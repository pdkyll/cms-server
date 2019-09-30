package nl.menninga.menno.cms.util;

public class StringUrlUtils {

	public static String stringToFriendlyUrlPart(String urlPart) {
		return urlPart.replaceAll("[^a-zA-Z0-9\\s-]", "").replaceAll("\\s+", " ").replaceAll("\\s", "-");
	}
}
