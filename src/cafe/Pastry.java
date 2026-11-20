package cafe;

/**
 * Represents a pastry line item on an order, with its name, quantity,
 * and unit price, and computes the item total.
 *
 * @author Danielle Major
 */
public class Pastry {

    /** Display name of the pastry (e.g. "Croissant", "Bagel"). */
    private String pastryName;

    /** How many of this pastry the customer ordered. */
    private int quantity;

    /** Price of a single pastry, in dollars. */
    private double unitPrice;

    /**
     * Creates a pastry line item.
     *
     * @param pastryName display name of the pastry
     * @param quantity   number of pastries ordered (expected to be >= 1)
     * @param unitPrice  price per pastry, in dollars
     */
    public Pastry(String pastryName, int quantity, double unitPrice) {
        this.pastryName = pastryName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
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
     * Returns the pastry's display name.
     *
     * @return the pastry name
     */
    public String getName() {
        return pastryName;
    }

    /**
     * Formats this line item for the receipt,
     * e.g. {@code "2x Bagel - $2.00 ($1.00 each)"}.
     *
     * @return a one-line receipt description of this pastry
     */
    @Override
    public String toString() {
        return String.format("%dx %s - $%.2f ($%.2f each)",
                quantity, pastryName, getItemTotal(), unitPrice);
    }
}
