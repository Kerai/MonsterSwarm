package swarm.util;

import java.util.*;
import java.util.Map.Entry;
import java.util.regex.*;

public class StringUtils {
	
	public static String joinQuote(Iterable<String> tokens, String separator) {
		String result = "";
		boolean first = true;
		for(String s : tokens) {
			if(first) {
				first = false;
			} else {
				result += separator;
			}
			result += Pattern.quote(s);
		}
		return result;
	}
	
	public static String join(Iterable<String> tokens, String separator) {
		String result = "";
		boolean first = true;
		for(String s : tokens) {
			if(first) {
				first = false;
			} else {
				result += separator;
			}
			result += s;
		}
		return result;
	}
	
	public static String join(String[] arr, String separator) {
		String result = "";
		boolean first = true;
		for(String s : arr) {
			if(first) {
				first = false;
			} else {
				result += separator;
			}
			result += s;
		}
		return result;
	}
	
	public static String replace(String string, Map<String, String> tokens) {
		String pattern =  joinQuote(tokens.keySet(), "|");
		Pattern p = Pattern.compile(pattern);
		Matcher matcher = p.matcher(string);
		
		join(new String[] {}, null);
		StringBuffer sb = new StringBuffer();
		while(matcher.find()) {
		    matcher.appendReplacement(sb, tokens.get(matcher.group()));
		}
		matcher.appendTail(sb);

		return sb.toString();
	}
	
//	private static final Map<String, String> colors = new HashMap<>();
//	static {
//		String bold = ChatColor.BOLD.toString();
//		colors.put("&0", ChatColor.BLACK.toString() + bold);
//		colors.put("&1", ChatColor.DARK_BLUE.toString() + bold);
//		colors.put("&2", ChatColor.DARK_GREEN.toString() + bold);
//		colors.put("&3", ChatColor.DARK_AQUA.toString() + bold);
//		colors.put("&4", ChatColor.DARK_RED.toString() + bold);
//		colors.put("&5", ChatColor.DARK_PURPLE.toString() + bold);
//		colors.put("&6", ChatColor.GOLD.toString() + bold);
//		colors.put("&7", ChatColor.GRAY.toString() + bold);
//		colors.put("&8", ChatColor.DARK_GRAY.toString() + bold);
//		colors.put("&9", ChatColor.BLUE.toString() + bold);
//		colors.put("&a", ChatColor.GREEN.toString() + bold);
//		colors.put("&b", ChatColor.AQUA.toString() + bold);
//		colors.put("&c", ChatColor.RED.toString() + bold);
//		colors.put("&d", ChatColor.LIGHT_PURPLE.toString() + bold);
//		colors.put("&e", ChatColor.YELLOW.toString() + bold);
//		colors.put("&f", ChatColor.WHITE.toString() + bold);
//		colors.put("&k", ChatColor.MAGIC.toString() + bold);
//		//colors.put("&l", ChatColor.BOLD.toString());
//		colors.put("&m", ChatColor.STRIKETHROUGH.toString() + bold);
//		colors.put("&n", ChatColor.UNDERLINE.toString() + bold);
//		colors.put("&o", ChatColor.ITALIC.toString() + bold);
//		colors.put("&r", ChatColor.RESET.toString() + bold);
//	}
//	
//	public static String colorize(String string) {
//		if(string==null) return null;
//		return ChatColor.translateAlternateColorCodes('&', string);
//		//return replace(string, colors);
//	}
}
