package kanad.kore.data.entity.jpa;

import javax.persistence.Embeddable;

@Embeddable
public class Phone {
	//primary
	private String mobile1;
	//secondary.
	private String mobile2;
	private String emergencyContact1MobNo;
	private String emergencyContactName1;
	private String relationWithEmergencyContact1; 
	private String emergencyContact2MobNo;
	private String emergencyContactName2;
	private String relationWithEmergencyContact2; 
	private String landline;
	
	public Phone() {
		// TODO Auto-generated constructor stub
	}

	public String getMobile1() {
		return mobile1;
	}

	public void setMobile1(String mobile1) {
		this.mobile1 = mobile1;
	}

	public String getMobile2() {
		return mobile2;
	}

	public void setMobile2(String mobile2) {
		this.mobile2 = mobile2;
	}

	public String getEmergencyContact1MobNo() {
		return emergencyContact1MobNo;
	}

	public void setEmergencyContact1MobNo(String emergencyContact1MobNo) {
		this.emergencyContact1MobNo = emergencyContact1MobNo;
	}

	public String getEmergencyContactName1() {
		return emergencyContactName1;
	}

	public void setEmergencyContactName1(String emergencyContactName1) {
		this.emergencyContactName1 = emergencyContactName1;
	}

	public String getRelationWithEmergencyContact1() {
		return relationWithEmergencyContact1;
	}

	public void setRelationWithEmergencyContact1(String relationWithEmergencyContact1) {
		this.relationWithEmergencyContact1 = relationWithEmergencyContact1;
	}

	public String getEmergencyContact2MobNo() {
		return emergencyContact2MobNo;
	}

	public void setEmergencyContact2MobNo(String emergencyContact2MobNo) {
		this.emergencyContact2MobNo = emergencyContact2MobNo;
	}

	public String getEmergencyContactName2() {
		return emergencyContactName2;
	}

	public void setEmergencyContactName2(String emergencyContactName2) {
		this.emergencyContactName2 = emergencyContactName2;
	}

	public String getRelationWithEmergencyContact2() {
		return relationWithEmergencyContact2;
	}

	public void setRelationWithEmergencyContact2(String relationWithEmergencyContact2) {
		this.relationWithEmergencyContact2 = relationWithEmergencyContact2;
	}

	public String getLandline() {
		return landline;
	}

	public void setLandline(String landline) {
		this.landline = landline;
	}
}
