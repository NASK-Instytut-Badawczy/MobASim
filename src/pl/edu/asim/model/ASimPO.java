package pl.edu.asim.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.persistence.annotations.CascadeOnDelete;

@Entity
@IdClass(ASimPOPK.class)
@CascadeOnDelete
public class ASimPO {

	@Id
	private String code;

	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@XmlTransient
	private ASimDO father;

	@Lob
	private String value;

	public void setCode(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setFather(ASimDO father) {
		this.father = father;
	}

	@XmlTransient
	public ASimDO getFather() {
		return father;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
