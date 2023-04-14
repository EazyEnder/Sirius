package fr.eazyender.physicengine.lang;

public class LangManager {
	
	public static String lang = "fr_FR";
	
	public static void init() {
		Lang_fr_FR.init();
	}
	
	public static String getText(String ID) {
		switch(lang) {
		case "fr_FR": return Lang_fr_FR.getTranslation(ID);
		default: return Lang_fr_FR.getTranslation(ID);
		}
	}
	
	public static String getText(String ID, String[] args) {
		
		String result = "";
		
		switch(lang) {
		case "fr_FR": result = Lang_fr_FR.getTranslation(ID);
		default: result = Lang_fr_FR.getTranslation(ID);
		}
		
		for (int i = 0; i < args.length; i++) {
			result = result.replace("{"+(i+1)+"}",args[i]);
		}
		
		return result;
	}

}
