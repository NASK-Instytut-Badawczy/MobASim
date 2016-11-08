package pl.edu.asim.proc;

public class TLCard {

	TLProcess father;
	TLProcess[] children;
	
	public TLCard(int childrenCount) {
		children = new TLProcess[childrenCount];
	}

	public TLProcess getFather() {
		return father;
	}

	public void setFather(TLProcess father) {
		this.father = father;
	}

	public TLProcess[] getChildren() {
		return children;
	}

	public void setChildren(TLProcess[] children) {
		this.children = children;
	}
	
}
