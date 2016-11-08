package pl.edu.asim.gui.actions;

import pl.edu.asim.gui.fields.AttributeInterface;

public class FieldUpdateAction {

	AttributeInterface field;
	String type;
	String actionCommand;
	public AttributeInterface getField() {
		return field;
	}
	public void setField(AttributeInterface field) {
		this.field = field;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getActionCommand() {
		return actionCommand;
	}
	public void setActionCommand(String actionCommand) {
		this.actionCommand = actionCommand;
	}
	
}
