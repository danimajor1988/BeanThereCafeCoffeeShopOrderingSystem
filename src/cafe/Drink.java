package cafe;

/**
 * Represents a single customizable drink (coffee or tea) on an order,
 * including size, temperature, milk, syrup, cold foam, toppings, and
 * quantity. Responsible for calculating its own unit price and item total.
 *
 * @author Danielle Major
 */
public class Drink {

    /** Top-level menu category: "Coffee" or "Tea". */
    private String drinkCategory;

    /** Specific drink on the menu (e.g. "Latte", "Matcha", "Green Tea"). */
    private String drinkName;

    /** Cup size: "Small" (base price), "Medium" (+$0.50), or "Large" (+$1.00). */
    private String size;

    /** Serving temperature: "Hot" (base), "Iced" (+$0.50), or "Frozen" (+$1.00). */
    private String temperature;

    /** Milk choice: "None", "Whole", "Oat" (+$0.75), "Almond", or "Soy". */
    private String milk;

    /** Number of extra espresso shots (+$1.00 each). */
    private int extraShots;

    /** Syrup flavor, or "None". Any syrup adds +$0.70. */
    private String syrup;

    /** Cold foam flavor, or "None". Any cold foam adds +$1.00. */
    private String coldFoam;

    /** Whether whipped cream was added (+$0.50). */
    private boolean whippedCream;

    /** Whether sprinkles were added (free, but only allowed on coffee/matcha). */
    private boolean sprinkles;

    /** How many of this exact drink the customer ordered. */
    private int quantity;

    /** Calculated price of a single drink with all customizations applied. */
    private double unitPrice;

    /**
     * Creates a fully customized drink and immediately calculates its unit price.
     * Sprinkles are only kept if the drink is coffee or matcha; they are
     * silently dropped for other teas.
     *
     * @param drinkCategory "Coffee" or "Tea"
     * @param drinkName     specific menu drink name
     * @param size          "Small", "Medium", or "Large"
     * @param temperature   "Hot", "Iced", or "Frozen"
     * @param milk          milk choice, or "None"
     * @param extraShots    number of extra espresso shots
     * @param syrup         syrup flavor, or "None"
     * @param coldFoam      cold foam flavor, or "None"
     * @param whippedCream  true to add whipped cream
     * @param sprinkles     true to add sprinkles (coffee/matcha only)
     * @param quantity      number of drinks ordered (expected to be >= 1)
     */
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
        // Sprinkles are only valid on coffee or matcha drinks; ignore them otherwise.
        boolean isCoffeeOrMatcha = "Coffee".equalsIgnoreCase(drinkCategory) || "Matcha".equalsIgnoreCase(drinkCategory);
        this.sprinkles = isCoffeeOrMatcha && sprinkles;
        this.quantity = quantity;
        // Lock in the unit price as soon as the drink is built.
        this.unitPrice = calculatePrice();
    }

    /**
     * Calculates (and caches) the price of a single drink by starting from
     * the category base price and adding every applicable upcharge:
     * <ul>
     *   <li>Base: $3.00 coffee/matcha, $2.00 tea</li>
     *   <li>Size: Medium +$0.50, Large +$1.00</li>
     *   <li>Temperature: Iced +$0.50, Frozen +$1.00</li>
     *   <li>Oat milk +$0.75</li>
     *   <li>Extra shots +$1.00 each</li>
     *   <li>Any syrup +$0.70, any cold foam +$1.00</li>
     *   <li>Whipped cream +$0.50; sprinkles free</li>
     * </ul>
     *
     * @return the unit price for one drink with all customizations
     */
    public double calculatePrice() {
        double price = 3.00; // Base price for Coffee and Matcha

        // Teas start from a lower base price.
        if ("Tea".equalsIgnoreCase(drinkCategory)) {
            price = 2.00;
        }

        // Size adjustments (Small is the base price).
        if ("Medium".equalsIgnoreCase(size)) {
            price += 0.50;
        } else if ("Large".equalsIgnoreCase(size)) {
            price += 1.00;
        }

        // Temperature adjustments (Hot is the base price).
        if ("Iced".equalsIgnoreCase(temperature)) {
            price += 0.50;
        } else if ("Frozen".equalsIgnoreCase(temperature)) {
            price += 1.00;
        }

        // Oat milk is the only milk with an upcharge.
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

    /**
     * Calculates the total cost for this line item.
     *
     * @return unit price multiplied by quantity
     */
    public double getItemTotal() {
        return unitPrice * quantity;
    }

    /**
     * Returns the specific drink name (e.g. "Latte").
     *
     * @return the drink name
     */
    public String getDrinkName() {
        return drinkName;
    }

    /**
     * Returns the menu category of this drink.
     *
     * @return "Coffee" or "Tea"
     */
    public String getDrinkCategory() {
        return drinkCategory;
    }

    /**
     * Returns the serving temperature of this drink.
     *
     * @return "Hot", "Iced", or "Frozen"
     */
    public String getTemperature() {
        return temperature;
    }

    /**
     * Builds the multi-line receipt description for this drink: a summary
     * line (quantity, size, temperature, name, line total) followed by an
     * indented breakdown of the base price and each upcharge applied.
     *
     * @return a multi-line, receipt-ready description of this drink
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        double basePrice = "Tea".equalsIgnoreCase(drinkCategory) ? 2.00 : 3.00;
        // Summary line, e.g. "2x Large Iced Latte - $11.00".
        sb.append(String.format("%dx %s %s %s - $%.2f",
                quantity, size, temperature, drinkName, getItemTotal()));
        // Itemized price breakdown: only list the upcharges that actually apply.
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
