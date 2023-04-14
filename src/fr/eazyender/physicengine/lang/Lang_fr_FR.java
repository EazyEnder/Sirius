package fr.eazyender.physicengine.lang;

import java.util.HashMap;
import java.util.Map;

public class Lang_fr_FR {
	
	public static final String title = "Français";
	private static Map<String,String> words = new HashMap<String, String>();
	
	public static String getTranslation(String ID) {
		if(!words.containsKey(ID)) return "ERREUR DE MAPPING";
		return words.get(ID);
	}
	
	public static void init() {
		words.put("QT_ERROR_NODE_OUTSIDE", "Erreur : Un noeud est en dehors de tous les QT => Supprimé");
		words.put("QT_ERROR_NODE_NOTINSUBTREES", "Erreur : Erreur : {1} noeuds qui sont dans le QuadTree {2} ne sont dans aucun sous arbres");
		words.put("QT_INFO_SUBDIVISE", "Sous division du QuadTree :");
		words.put("QT_ERROR_NODE_SPEED_LIMIT", "Erreur : Un noeud a une vitesse au dessus de la limite autorisée => Supprimé");
		
		words.put("CMD_DEBUGMOD_ENABLE", "Le mode de debug a été activé.");
		words.put("CMD_DEBUGMOD_DISABLE", "Le mode de debug a été désactivé.");
		words.put("CMD_SIMU_ALREADY_PAUSED", "La simulation est déjà en pause. Faites {1} resume ,pour relancer la simulation.");
		words.put("CMD_SIMU_PAUSE", "La simulation a été mise en pause.");
		words.put("CMD_SIMU_ALREADY_RESUMED", "La simulation est déjà en route. Faites {1} pause ,pour mettre en pause la simulation.");
		words.put("CMD_SIMU_RESUME", " La simulation a été remise en route.");
		words.put("CMD_CLEAR", "Tous les objets ont été supprimés.");
		words.put("CMD_SIMU_SPEED", "La vitesse de la simu a été changée à : {1}.");
		words.put("CMD_LANG_SWITCH", "La langue a été changé, si jamais elle est introuvable alors les textes seront en anglais.");
	}

}
