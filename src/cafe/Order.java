package cafe;

import java.util.ArrayList;

public class Order {
    private ArrayList<Drink> drinks;
    private ArrayList<Pastry> pastries;
    private double tipPercentage;

    public Order() {
        this.drinks = new ArrayList<>();
        this.pastries = new ArrayList<>();
        this.tipPercentage = 0.0;
    }

    public void setTipPercentage(double tipPercentage) {
        this.tipPercentage = tipPercentage;
    }

    public void addDrink(Drink drink) {
        if (drink != null) {
            drinks.add(drink);
        }
    }

    public void addPastry(Pastry pastry) {
        if (pastry != null) {
            pastries.add(pastry);
        }
    }

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

    public double calculateTip() {
        return calculateSubtotal() * tipPercentage;
    }

    public double calculateTotal() {
        return calculateSubtotal() + calculateTip();
    }

    public String generateReceipt() {
        StringBuilder sb = new StringBuilder();
        sb.append("====== BEAN THERE CAFE ======\n\n");

        if (!drinks.isEmpty()) {
            sb.append("--- DRINKS ---\n");
            for (Drink d : drinks) {
                sb.append(d.toString()).append("\n");
            }
            sb.append("\n");
        }

        if (!pastries.isEmpty()) {
            sb.append("--- PASTRIES ---\n");
            for (Pastry p : pastries) {
                sb.append(p.toString()).append("\n");
            }
            sb.append("\n");
        }

        sb.append("-----------------------------\n");
        sb.append(String.format("Subtotal: $%.2f\n", calculateSubtotal()));
        sb.append(String.format("Tip (%.0f%%): $%.2f\n", tipPercentage * 100, calculateTip()));
        sb.append(String.format("TOTAL:    $%.2f\n", calculateTotal()));
        sb.append("=============================");

        return sb.toString();
    }
}
