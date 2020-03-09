package se.eyevinn.listenup.processor.util;

import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author Mattias Selin
 *
 */
public final class LocalVariableNamePicker {
	private final Set<String> usedNames = new HashSet<>();
	
	public void supplyUsedName(String name) {
		usedNames.add(name);
	}
	
	public String getSafeName(String suggestion, boolean addAsUsed) {
		String currentSuggestion = suggestion;
		int number = 0;
		while(usedNames.contains(currentSuggestion)) {
			currentSuggestion = suggestion+(number++);
		}
		if(addAsUsed) {
			usedNames.add(currentSuggestion);
		}
		return currentSuggestion;
	}
}
