package kanad.kore.data.entity.jpa;

public class Label {
	/**
	 * The type is typically a simple class name (sans fully qualified package name) of the owner entity to which the
	 * property identified by 'name' belongs.
	 */
	private String type;
	/**
	 * name is the field/property name on the current label type.
	 */
	private String name;
	/**
	 * value is the actual value for the property/field identified by 'name' on current label type.
	 */
	private String value;
	
	/**
	 * The Secure ID of the owner entity of type 'type'.
	 * This will be typically needed while navigating to the corresponding owner entity/resource.
	 */
	private String id;
	
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
}
