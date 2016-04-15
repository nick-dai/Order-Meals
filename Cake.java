public class Cake extends Product {
	public Cake(String name, double cost, int price) {
		setName(name);
		setCost(cost);
		setPrice(price);
	}
	public int getPrice(int s) {
		switch(s) {
			case 0:
				return price-50; // 6-inch.
			case 1:
				return price-25; // 8-inch.
			case 2:
				return price; // 10-inch.
			default:
				return 0;
		}
	}
}