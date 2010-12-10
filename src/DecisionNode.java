
public class DecisionNode implements Node{
	
	private int attribute;
	private Node left;
	private Node right;
	
	public DecisionNode(int attribute, Node left, Node right){
		this.attribute = attribute;
		this.left = left;
		this.right = right;
	}
	
	public int getAttribute(){
		return this.attribute;
	}
	
	public Node getLeft(){
		return this.left;
	}
	
	public Node getRight(){
		return this.right;
	}
	
	public String toString(){
		return this.attribute + "";
	}
}
