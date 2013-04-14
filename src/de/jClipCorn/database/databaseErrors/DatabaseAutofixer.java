package de.jClipCorn.database.databaseErrors;

import java.util.ArrayList;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.util.DriveMap;
import de.jClipCorn.util.ProgressCallbackListener;

public class DatabaseAutofixer {
	public static boolean fixErrors(ArrayList<DatabaseError> list, ProgressCallbackListener pcl) {
		if (! DriveMap.isCreated()) {
			return false;
		}
		
		pcl.setMax(list.size());
		pcl.reset();
		
		boolean fullsuccess = true;
		
		for (int i = 0; i < list.size(); i++) {
			pcl.step();
			
			DatabaseError error = list.get(i);
			
			if (! error.isAutoFixable()) { // Test if this File is Fixable
				continue;
			}
			
			ArrayList<DatabaseError> errlist = getAllWithSameElement(list, error); // Test if all Errors with this File are Fixable
			boolean succ = true;
			for (int j = 0; j < errlist.size(); j++) {
				DatabaseError lerror = errlist.get(i);
				succ &= lerror.isAutoFixable();
			}
			if (! succ) {
				continue;
			}
			
			boolean succval = error.autoFix();
			
			if(! succval) {
				CCLog.addWarning(LocaleBundle.getFormattedString("CheckDatabaseDialog.Autofix.problem", error.getErrorString(), error.getElement1Name())); //$NON-NLS-1$
				fullsuccess = false;
			}
		}
		
		pcl.reset();
		
		return fullsuccess;
	}
	
	private static ArrayList<DatabaseError> getAllWithSameElement(ArrayList<DatabaseError> list, DatabaseError element) {
		ArrayList<DatabaseError> result = new ArrayList<>();
		
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).elementsEquals(element)) {
				result.add(list.get(i));
			}
		}
		
		return result;
	}
}
