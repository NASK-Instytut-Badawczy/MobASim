package pl.edu.asim.model;

import java.io.Serializable;

public class ASimPOPK implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4727478267149505968L;

	private Long father;
	
	private String code;

	public Long getFather() {
		return father;
	}

	public void setFather(Long father) {
		this.father = father;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	 public int hashCode() {
	    return code.hashCode()+father.hashCode();
	 }

	@Override
   public boolean equals(Object o) {
       if(o instanceof ASimPOPK){
       	ASimPOPK other = (ASimPOPK) o;
           return code.equals(other.getCode()) && father.equals(other.father);
       }

       return false;
   }

}
