public abstract class Product {
	protected int price, size;
	protected double cost;
	protected String name;
	public Product() {
		setName("");
		setCost(0);
		setSize(0);
		setPrice(0);
	}
	public void setName(String n) {
		name = n;
	}
	public void setCost(double c) {
		cost = c;
	}
	public void setSize(int s) {
		size = s;
	}
	public void setPrice(int p) {
		price = p;
	}
	public String getName() {
		return name;
	}
	public double getCost() {
		return cost;
	}
	public abstract int getPrice(int s);
}