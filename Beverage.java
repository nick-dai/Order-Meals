public class Beverage extends Product {
	public Beverage(String name, double cost, int price) {
		setName(name);
		setCost(cost);
		setPrice(price);
	}
	public int getPrice(int s) {
		switch(s) {
			case 0:
				return price-10; // Small.
			case 1:
				return price-5; // Medium.
			case 2:
				return price; // Large.
			default:
				return 0;
		}
	}
}