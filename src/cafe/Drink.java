package cafe;

public class Drink {
    private String drinkCategory;
    private String drinkName;
    private String size;
    private String temperature;
    private String milk;
    private int extraShots;
    private String syrup;
    private String coldFoam;
    private boolean whippedCream;
    private boolean sprinkles;
    private int quantity;
    private double unitPrice;

    public Drink(String drinkCategory, String drinkName, String size, String temperature,
                 String milk, int extraShots, String syrup, String coldFoam,
                 boolean whippedCream, boolean sprinkles, int quantity) {
        this.drinkCategory = drinkCategory;
        this.drinkName = drinkName;
        this.size = size;
        this.temperature = temperature;
        this.milk = milk;
        this.extraShots = extraShots;
        this.syrup = syrup;
        this.coldFoam = coldFoam;
        this.whippedCream = whippedCream;
        boolean isCoffeeOrMatcha = "Coffee".equalsIgnoreCase(drinkCategory) || "Matcha".equalsIgnoreCase(drinkCategory);
        this.sprinkles = isCoffeeOrMatcha && sprinkles;
        this.quantity = quantity;
        this.unitPrice = calculatePrice();
    }

    public double calculatePrice() {
        double price = 3.00; // Base price for Coffee and Matcha

        if ("Tea".equalsIgnoreCase(drinkCategory)) {
            price = 2.00;
        }

        // Size adjustments
        if ("Medium".equalsIgnoreCase(size)) {
            price += 0.50;
        } else if ("Large".equalsIgnoreCase(size)) {
            price += 1.00;
        }

        if ("Iced".equalsIgnoreCase(temperature)) {
            price += 0.50;
        } else if ("Frozen".equalsIgnoreCase(temperature)) {
            price += 1.00;
        }

        if ("Oat".equalsIgnoreCase(milk)) {
            price += 0.75;
        }

        // Extra shots (+$1.00 each)
        price += extraShots * 1.00;

        // Syrups (+$0.70)
        if (syrup != null && !"None".equalsIgnoreCase(syrup)) {
            price += 0.70;
        }

        // Cold Foams (+$1.00)
        if (coldFoam != null && !"None".equalsIgnoreCase(coldFoam)) {
            price += 1.00;
        }

        // Whipped Cream (+$0.50)
        if (whippedCream) {
            price += 0.50;
        }

        // Sprinkles are FREE ($0.00) with Coffee/Matcha
        this.unitPrice = price;
        return this.unitPrice;
    }

    public double getItemTotal() {
        return unitPrice * quantity;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        double basePrice = "Tea".equalsIgnoreCase(drinkCategory) ? 2.00 : 3.00;
        sb.append(String.format("%dx %s %s %s - $%.2f",
                quantity, size, temperature, drinkName, getItemTotal()));
        sb.append(String.format("\n   Base price: $%.2f", basePrice));
        if ("Medium".equalsIgnoreCase(size)) sb.append("\n   Medium size: +$0.50");
        if ("Large".equalsIgnoreCase(size)) sb.append("\n   Large size: +$1.00");
        if ("Iced".equalsIgnoreCase(temperature)) sb.append("\n   Iced: +$0.50");
        if ("Frozen".equalsIgnoreCase(temperature)) sb.append("\n   Frozen: +$1.00");
        sb.append("\n   Milk: ").append(milk);
        if ("Oat".equalsIgnoreCase(milk)) sb.append("\n   Oat milk: +$0.75");
        if (extraShots > 0) {
            sb.append(String.format("\n   %d extra shot(s): +$%.2f", extraShots, extraShots * 1.00));
        }
        if (syrup != null && !"None".equalsIgnoreCase(syrup)) {
            sb.append("\n   ").append(syrup).append(" syrup: +$0.70");
        }
        if (coldFoam != null && !"None".equalsIgnoreCase(coldFoam)) {
            sb.append("\n   ").append(coldFoam).append(" cold foam: +$1.00");
        }
        if (whippedCream) sb.append("\n   Whipped cream: +$0.50");
        if (sprinkles) sb.append("\n   Sprinkles: Free");
        return sb.toString();
    }
}
