package pl.edu.asim.interfaces;

import java.io.Serializable;

import pl.edu.asim.util.PropertyType;

public interface Property {

	public String getName();
	
	public Serializable getValue();
	
	public PropertyType getType();
}
