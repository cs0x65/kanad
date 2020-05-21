package kanad.kore.data.entity.jpa;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;


@Entity
@Inheritance(strategy=InheritanceType.JOINED)
@DiscriminatorColumn(name="discType")
public abstract class AbstractEntity implements KEntity {
	/*
	 * Was AUTO earlier - hibernate uses TABLE gen strategy which is to simulate
	 * sequence using database table. Generally, hibernate creates
	 * hibernate_sequence table to simulates the sequence. But it has a
	 * performance bottleneck in that - it holds the row level lock on this
	 * table to get next_val. Row level lock is needed to make sure that no 2
	 * concurrent transactions are able to retrieve the next possible value at
	 * once to prevent dirty reads. Culprit is: select from .. update which holds
	 * the row level lock as evident from the following observation.
	 * Here's a sample o/p of: SHOW ENGINE INNODB STATUS\G
	 * query when tested the parking-discovery module/microservice with 50 concurrent requests:
	 * 
	 * ---TRANSACTION 123057, ACTIVE 45 sec
	 * starting index read mysql tables in use 1, locked 1 LOCK WAIT 2 lock
	 * struct(s), heap size 1136, 1 row lock(s) MySQL thread id 94, OS thread
	 * handle 140393856878336, query id 211701 localhost 127.0.0.1 psadmin
	 * Sending data select next_val as id_val from hibernate_sequence for update
	 * ------- TRX HAS BEEN WAITING 45 SEC FOR THIS LOCK TO BE GRANTED: RECORD
	 * LOCKS space id 1868 page no 3 n bits 72 index GEN_CLUST_INDEX of table
	 * `psmgmt`.`hibernate_sequence` trx id 123057 lock_mode X locks rec but
	 * not gap waiting Record lock, heap no 2 PHYSICAL RECORD: n_fields 4;
	 * compact format; info bits 0 0: len 6; hex 000000000c02; asc ;; 1: len 6;
	 * hex 00000001e086; asc ;; 2: len 7; hex 7100000232057a; asc q 2 z;; 3: len
	 * 8; hex 80000000000005c0; asc ;;
	 * 
	 * ------------------
	 */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
	@OneToOne(mappedBy="entity", fetch=FetchType.EAGER)
	private SecureID secureID;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date createdOn;

	@Column(nullable = true)
	private String createdBy;
	
	//Make sure you have mysql 5.7+ if default value needs to be CURRENT_TIMESTAMP; older versions don't allow
	//2 columns in same table to have CURRENT_TIMESTAMP as default value
	@Temporal(TemporalType.TIMESTAMP)
	//@Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@Column(columnDefinition = "TIMESTAMP null")
    private Date updatedOn = new Date();

    @Column(nullable = true)
	private String updatedBy;
	
    @Column(nullable = false, columnDefinition="TINYINT DEFAULT 0")
    private int deleted;
    
    @Column(nullable = false)
    private int orgId;
    
    @OneToMany(mappedBy="owner", fetch=FetchType.LAZY)
	private Collection<Remark> remarks;
    
    public AbstractEntity(){
    	
    }
    
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public SecureID getSecureID() {
		return secureID;
	}
	
	/**
	 * 
	 * @param secureID
	 * This method shall only be used while merging an existing entity into an updated one.
	 * In such a scenario, one shall set secure id on the updated one and merge.
	 */
	public void setSecureID(SecureID secureID) {
		this.secureID = secureID;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public int isDeleted() {
		return deleted;
	}

	public void setDeleted(int deleted) {
		this.deleted = deleted;
	}

	public int getOrgId() {
		return orgId;
	}

	public void setOrgId(int orgId) {
		this.orgId = orgId;
	}

	public Collection<Remark> getRemarks() {
		return remarks;
	}

	public void setRemarks(Collection<Remark> remarks) {
		this.remarks = remarks;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}
}
