package de.jClipCorn.gui.frames.applyPatchFrame;

import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.xml.CCXMLElement;
import de.jClipCorn.util.xml.CCXMLException;

import javax.swing.*;
import java.util.List;

public class ActionVM {
	public final CCXMLElement XML;

	public final int State;

	public final int Ctr;
	public final String Type;
	public final String Description;

	public final List<ActionCommandVM> Commands;

	private ActionVM(CCXMLElement XML, int state, int ctr, String type, String description, List<ActionCommandVM> commands)
	{
		this.XML = XML;
		State = state;
		Ctr = ctr;
		Type = type;
		Description = description;
		Commands = commands;
	}

	public ActionVM(CCXMLElement xml, PatchExecState state) throws CCXMLException {
		this.XML         = xml;
		this.Ctr         = xml.getAttributeIntValueOrThrow("ctr");
		this.Type        = xml.getAttributeValueOrThrow("type");
		this.Description = xml.getAttributeValueOrThrow("description");
		this.State       = (Ctr <= state.LastSuccessfulCtr) ? 2 : 0;
		this.Commands    = xml.getAllChildren().map(ActionCommandVM::new).toList();
	}

	public Icon getStateIcon() {
		if (State == 0) return Resources.ICN_GENERIC_ORB_GRAY.get16x16();   // IN QUEUE
		if (State == 1) return Resources.ICN_GENERIC_ORB_ORANGE.get16x16(); // ACTIVE
		if (State == 2) return Resources.ICN_GENERIC_ORB_GREEN.get16x16();  // DONE

		return null;
	}

	public ActionVM WithState(int nstat)
	{
		return new ActionVM(XML, nstat, Ctr, Type, Description, Commands);
	}
}
