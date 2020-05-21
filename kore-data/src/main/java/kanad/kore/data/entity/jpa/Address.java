package kanad.kore.data.entity.jpa;

import javax.persistence.*;

@Entity
public class Address extends AbstractEntity {
	private String addressLine1;
	private String addressLine2;
	private String city;
	private String state;
	private String pin;
	private String purpose;
	@Enumerated(EnumType.STRING)
	private AddressType addressType;  //ENUM
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "partyId", referencedColumnName = "id", nullable = false)
	private AbstractEntity party;

	public enum AddressType {
		PERMANENT("Permanent"),
		PRESENT("Present");
		
		private String textValue;
		
		private AddressType(String textValue) {
			this.textValue = textValue;
		}

		/**
		 * @return the textValue
		 */
		public String getTextValue() {
			return textValue;
		}

		/* (non-Javadoc)
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return getTextValue(); 
		}
		
		//enum from text value field
		public static AddressType fromTextValue(String tv){
			if(tv.equals("Permanent")){
				return AddressType.PERMANENT;
			}
			return AddressType.PRESENT;
		}
		
		//text value field from enum value string
		public static String fromEnumValue(String value){
			if(value.equals("PERMANENT")){
				return "Permanent";
			}
			return "Present";
		}
	}
	
	public Address() {
		// TODO Auto-generated constructor stub
	}

	public String getAddressLine1() {
		return addressLine1;
	}

	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	public String getAddressLine2() {
		return addressLine2;
	}

	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public AddressType getAddressType() {
		return addressType;
	}

	public void setAddressType(AddressType addressType) {
		this.addressType = addressType;
	}	
	
	public AbstractEntity getParty() {
		return party;
	}

	public void setParty(AbstractEntity party) {
		this.party = party;
	}
	
}
