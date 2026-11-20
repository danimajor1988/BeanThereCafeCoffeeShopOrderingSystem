package cafe;

import java.util.ArrayList;

/**
 * Represents a customer's order: the collection of drinks and pastries,
 * the selected tip percentage, and the subtotal/tip/total calculations.
 * Also generates the final itemized text receipt shown at checkout.
 *
 * @author Danielle Major
 */
public class Order {

    /** All drinks the customer has added to this order. */
    private ArrayList<Drink> drinks;

    /** All pastries the customer has added to this order. */
    private ArrayList<Pastry> pastries;

    /** Tip as a fraction of the subtotal (e.g. 0.15 for a 15% tip). */
    private double tipPercentage;

    /**
     * Creates a new, empty order with no items and no tip.
     */
    public Order() {
        this.drinks = new ArrayList<>();
        this.pastries = new ArrayList<>();
        this.tipPercentage = 0.0;
    }

    /**
     * Sets the tip rate applied to the subtotal.
     *
     * @param tipPercentage tip as a fraction (e.g. 0.20 for 20%)
     */
    public void setTipPercentage(double tipPercentage) {
        this.tipPercentage = tipPercentage;
    }

    /**
     * Adds a drink to the order. Null drinks are ignored.
     *
     * @param drink the drink to add
     */
    public void addDrink(Drink drink) {
        if (drink != null) {
            drinks.add(drink);
        }
    }

    /**
     * Adds a pastry to the order. Null pastries are ignored.
     *
     * @param pastry the pastry to add
     */
    public void addPastry(Pastry pastry) {
        if (pastry != null) {
            pastries.add(pastry);
        }
    }

    /**
     * Removes the drink at the given position. Out-of-range
     * indexes are ignored rather than throwing an exception.
     *
     * @param index position of the drink in the order list
     */
    public void removeDrink(int index) {
        if (index >= 0 && index < drinks.size()) {
            drinks.remove(index);
        }
    }

    /**
     * Removes the pastry at the given position. Out-of-range
     * indexes are ignored rather than throwing an exception.
     *
     * @param index position of the pastry in the order list
     */
    public void removePastry(int index) {
        if (index >= 0 && index < pastries.size()) {
            pastries.remove(index);
        }
    }

    /**
     * Returns the number of drink line items on the order.
     *
     * @return the drink count
     */
    public int getDrinkCount() {
        return drinks.size();
    }

    /**
     * Returns the number of pastry line items on the order.
     *
     * @return the pastry count
     */
    public int getPastryCount() {
        return pastries.size();
    }

    /**
     * Returns the live list of drinks on this order.
     *
     * @return the drinks list
     */
    public ArrayList<Drink> getDrinks() {
        return drinks;
    }

    /**
     * Returns the live list of pastries on this order.
     *
     * @return the pastries list
     */
    public ArrayList<Pastry> getPastries() {
        return pastries;
    }

    /**
     * Sums the line totals of every drink and pastry on the order.
     *
     * @return the pre-tip subtotal in dollars
     */
    public double calculateSubtotal() {
        double subtotal = 0.0;
        for (Drink d : drinks) {
            subtotal += d.getItemTotal();
        }
        for (Pastry p : pastries) {
            subtotal += p.getItemTotal();
        }
        return subtotal;
    }

    /**
     * Calculates the tip amount from the current subtotal and tip rate.
     *
     * @return the tip in dollars
     */
    public double calculateTip() {
        return calculateSubtotal() * tipPercentage;
    }

    /**
     * Calculates the grand total the customer owes.
     *
     * @return subtotal plus tip, in dollars
     */
    public double calculateTotal() {
        return calculateSubtotal() + calculateTip();
    }

    /**
     * Builds the full plain-text receipt shown at checkout: a header,
     * a DRINKS section and a PASTRIES section (each omitted when empty),
     * and the subtotal / tip / total footer.
     *
     * @return the complete multi-line receipt text
     */
    public String generateReceipt() {
        StringBuilder sb = new StringBuilder();
        sb.append("====== BEAN THERE CAFE ======\n\n");

        // Drinks section - only printed if the order has at least one drink.
        if (!drinks.isEmpty()) {
            sb.append("--- DRINKS ---\n");
            for (Drink d : drinks) {
                sb.append(d.toString()).append("\n");
            }
            sb.append("\n");
        }

        // Pastries section - only printed if the order has at least one pastry.
        if (!pastries.isEmpty()) {
            sb.append("--- PASTRIES ---\n");
            for (Pastry p : pastries) {
                sb.append(p.toString()).append("\n");
            }
            sb.append("\n");
        }

        // Money footer: subtotal, tip (shown as a percentage), and grand total.
        sb.append("-----------------------------\n");
        sb.append(String.format("Subtotal: $%.2f\n", calculateSubtotal()));
        sb.append(String.format("Tip (%.0f%%): $%.2f\n", tipPercentage * 100, calculateTip()));
        sb.append(String.format("TOTAL:    $%.2f\n", calculateTotal()));
        sb.append("=============================");

        return sb.toString();
    }
}
