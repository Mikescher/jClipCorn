package de.jClipCorn.gui.actionTree;

public class CCTreeActionEvent {

	public final CCActionElement Source;
	public final String Command;
	public final ActionSource SourceType;

	public CCTreeActionEvent(CCActionElement src, String command, ActionSource type) {
		Source = src;
		Command = command;
		SourceType = type;
	}

}