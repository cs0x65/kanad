package kanad.kore.data.entity.jpa;

import kanad.kore.data.entity.KEntity;

import javax.persistence.*;

@Entity
/**
 * Refer: generating_secure_ids_for_the_entities_for_use_in_the_web_services or
 * SupplementaryDAOImpl.createSecureIDTrigger() for more details.
 * All the setters methods shall only be used while merging the owning entity of this secure id instance.
 */
public class SecureID implements KEntity {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="entityId", referencedColumnName="id")
	private AbstractEntity entity;
	
	@Column(columnDefinition="varchar(256)")
	private String sekId;
	
	
	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the entity
	 */
	public AbstractEntity getEntity() {
		return entity;
	}

	/**
	 * @param entity the entity to set
	 */
	public void setEntity(AbstractEntity entity) {
		this.entity = entity;
	}

	public String getSekId() {
		return sekId;
	}
	
	public void setSekId(String sekId) {
		this.sekId = sekId;
	}
}
