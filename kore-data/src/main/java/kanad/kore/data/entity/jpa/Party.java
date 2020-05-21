package kanad.kore.data.entity.jpa;

import javax.persistence.*;
import java.util.Collection;

@MappedSuperclass
public class Party extends AbstractEntity {
	
	protected String name;
	
	@OneToMany(mappedBy="party", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch=FetchType.LAZY)
	protected Collection<Address> addresses;
	
	protected String emailId;
	
	@Embedded
	protected Phone phone;
	
	public Party() {
		// TODO Auto-generated constructor stub
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Collection<Address> getAddresses() {
		return addresses;
	}

	public void setAddresses(Collection<Address> addresses) {
		this.addresses = addresses;
	}

	public Phone getPhone() {
		return phone;
	}

	public void setPhone(Phone phone) {
		this.phone = phone;
	}
	
	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

}
