package kanad.kore.data.dao.jpa.impl;

import kanad.kore.data.entity.jpa.AbstractEntity;
import org.apache.logging.log4j.LogManager;

import javax.persistence.Query;

public class SupplementaryDAOImpl extends DefaultJpaDaoImpl<AbstractEntity> {
	private static final String SEC_ID_GEN_TRIGGER = "sec_id_gen_trigger";
	//name of the table on which the above trigger is created.
	private static final String TABLE_NAME = "AbstractEntity";
	
	public SupplementaryDAOImpl() {
		this(AbstractEntity.class);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param clazz
	 */
	public SupplementaryDAOImpl(Class<AbstractEntity> clazz) {
		super(clazz);
		// TODO Auto-generated constructor stub
	}

	/**
	 * If the trigger already exists, then it's a no-op else it creates a new sec id generation trigger.
	 */
	public void createSecureIDTrigger(){
		if(secureIDTriggerExists()){
			LogManager.getLogger().info(SEC_ID_GEN_TRIGGER+"exists! Nothing to do.");
			return;
		}
		
		String triggerSQL =
				"CREATE TRIGGER "+ SEC_ID_GEN_TRIGGER +" AFTER INSERT ON AbstractEntity "
				+ "FOR EACH ROW "
				+ "INSERT INTO SecureID(entityId, sekId) VALUES(NEW.id, sha2(CONCAT(NEW.discType,'_',NEW.id),256))";
		Query query =  entityManager.createNativeQuery(triggerSQL);
		try{
			beginTransaction();
			int status = query.executeUpdate();
			commitTransaction();
			LogManager.getLogger().info("CreateSecureIDTrigger status = "+status);
		}catch(Exception e){
			LogManager.getLogger().error("CreateSecureIDTrigger failed- unable to commit transaction");
			LogManager.getLogger().error("Exception: "+e);
			LogManager.getLogger().error("Rolling back..");
			rollbackTransaction();
			LogManager.getLogger().error("Done!");
		}
	}
	
	
	private boolean secureIDTriggerExists(){
		LogManager.getLogger().info("Checking if "+SEC_ID_GEN_TRIGGER+" exists...");
		//Show triggers on Entity table
		String triggerSQL =  "SHOW TRIGGERS LIKE '"+ TABLE_NAME+ "'";
		Query query =  entityManager.createNativeQuery(triggerSQL);
		//Only 1 trigger currently, so this check will suffice for now.
		return query.getResultList().size() == 1;
	}
}
