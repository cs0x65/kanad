package kanad.kore.data.entity.jpa;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


@Entity
public class Remark extends AbstractEntity{
	//maintain the history of remarks for an entity/event/operation.
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ownerId", referencedColumnName="id")
	private AbstractEntity owner;
	
	//comma separated tuples.
	//making text coz column may need substantial space depending on the number of labels defined for the entity.
	@Column(columnDefinition="TEXT")
	private String labels;
	
	@Transient
	private List<Label> labelList;
	
	private String value;

	public Remark() {
		// TODO Auto-generated constructor stub
	}

	public AbstractEntity getOwner() {
		return owner;
	}

	public void setOwner(AbstractEntity owner) {
		this.owner = owner;
	}
	
	
	/**
	 * @return the labels
	 */
	public String getLabels() {
		return labels;
	}

	/**
	 * @param labels the labels to set
	 */
	public void setLabels(String labels) {
		this.labels = labels;
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * 
	 * @return the List of labels constructed from the current labels string.
	 * Note that this label list is constructed by scanning the labels string and this is done only once in the lifetime of the Remark object.
	 * Once constructed, subsequent calls to this method will retrun this pre-generated/cached labels list.
	 * This means once constructed, number of labels on the remark remain unchanged, though their attributes might change.
	 */
	public List<Label> getLabelsAsList() {
		if(labelList != null){
			return labelList;
		}
		
		labelList = new ArrayList<>();
		if(labels.contains(",")){
			String[] labelArray = labels.split(",");
			for(String label : labelArray){
				labelList.add(buildLabel(label));
			}
		}else if(labels.contains(";")){
			labelList.add(buildLabel(labels));
		}
			
		return labelList;
	}

	/**
	 * 
	 * @param labels the list of labels for this remark.
	 * This method processes the given label list and updates the labels string corresponding to this remark.
	 * Note that ultimately, it's the labels string that will be persisted to the DB.
	 * List<Label> is a handy and more organized way to refer to the remark labels in the code. 
	 */
	public void setLabelsAsList(List<Label> labels) {
		if(labels == null){
			//This means just rebuild the labels string from the updated list contents
			labels = this.labelList;
		}
		
		StringBuffer labelsCSV = new StringBuffer();
		for(Iterator<Label> iterator = labels.iterator(); iterator.hasNext();){
			Label label = iterator.next();
			labelsCSV.append(label.getType());
			labelsCSV.append(";");
			labelsCSV.append(label.getName());
			labelsCSV.append(";");
			labelsCSV.append(label.getValue());
			labelsCSV.append(";");
			labelsCSV.append(label.getId());
			labelsCSV.append(",");
		}
		labelsCSV.deleteCharAt(labelsCSV.length()-1);//remove last comma
		this.labels = labelsCSV.toString();
	}
	
	
	public void refreshLabelsString(){
		setLabelsAsList(null);
	}

	//Helpers
	private Label buildLabel(String labelString){
		Label label = new Label();
		if(labelString.contains(";")){
			String[] parts = labelString.split(";");
			if(parts.length == 4){
				label.setType(parts[0]);
				label.setName(parts[1]);
				label.setValue(parts[2]);
				label.setId(parts[3]);
			}
		}
		return label;
	}
}
