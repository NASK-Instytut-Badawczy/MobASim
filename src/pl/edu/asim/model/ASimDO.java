package pl.edu.asim.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.persistence.annotations.CascadeOnDelete;

@XmlRootElement
@Entity
@CascadeOnDelete
// @NamedQueries({
// @NamedQuery(name = "getChildren2", query =
// "select e from ASimDO e where e.father = :id"),
// @NamedQuery(name = "getProperties2", query =
// "select e from ASimPO e where e.father = :id") })
public class ASimDO implements java.lang.Comparable<ASimDO> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@XmlTransient
	private ASimDO father;

	private String name;

	private String type;

	private int inTreeOrder;
	// , cascade = CascadeType.REMOVE
	@OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, cascade = { CascadeType.ALL })
	@CascadeOnDelete
	@OrderBy("inTreeOrder")
	private List<ASimDO> children = new ArrayList<ASimDO>();

	@OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, cascade = { CascadeType.ALL })
	@CascadeOnDelete
	private List<ASimPO> properties = new ArrayList<ASimPO>();

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setFather(ASimDO father) {
		this.father = father;
	}

	@XmlTransient
	public ASimDO getFather() {
		return father;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setProperties(List<ASimPO> properties) {
		this.properties = properties;
	}

	public List<ASimPO> getProperties() {
		return properties;
	}

	public void setChildren(List<ASimDO> children) {
		this.children = children;
	}

	public List<ASimDO> getChildren() {
		return children;
	}

	public List<ASimDO> getChildren2() {
		return ASimElementManager.getInstance().getElementListByFather(this);
	}

	public int getOrder() {
		return inTreeOrder;
	}

	public void setOrder(int i) {
		inTreeOrder = i;
	}

	@Override
	public int compareTo(ASimDO o) {
		if (this.getOrder() < o.getOrder())
			return -1;
		else if (this.getOrder() > o.getOrder())
			return 1;
		else {
			return this.getName().compareTo(o.getName());
		}
	}

}
