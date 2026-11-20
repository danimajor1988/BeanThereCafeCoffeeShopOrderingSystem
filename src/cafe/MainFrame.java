package cafe;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Image;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

public class MainFrame extends JFrame {
    private Order currentOrder;

    private JComboBox<String> cmbCategory;
    private JComboBox<String> cmbDrinkName;
    private JComboBox<String> cmbMilk;
    private JComboBox<String> cmbSyrup;
    private JComboBox<String> cmbColdFoam;
    private JSpinner spnExtraShots;
    private JSpinner spnDrinkQty;
    private JRadioButton sizeSmall;
    private JRadioButton sizeMedium;
    private JRadioButton sizeLarge;
    private ButtonGroup sizeGroup;
    private ButtonGroup temperatureGroup;
    private JCheckBox chkWhippedCream;
    private JCheckBox chkSprinkles;
    private JLabel lblLiveDrinkPrice;

    private JComboBox<String> cmbPastry;
    private JSpinner spnPastryQty;
    private JLabel lblPastryTotal;

    private ButtonGroup tipGroup;
    private JPanel pnlReceiptItems;
    private JLabel lblSubtotal;
    private JLabel lblTip;
    private JLabel lblTotal;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> new MainFrame().setVisible(true));
    }

    public MainFrame() {
        currentOrder = new Order();
        setTitle("Bean There Cafe - Coffee, Tea & Pastry Ordering System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(980, 720));
        setSize(1280, 900);
        setLocationRelativeTo(null);

        JPanel content = new JPanel(new BorderLayout(12, 12));
        content.setBorder(new EmptyBorder(14, 18, 14, 18));
        setContentPane(content);

        JLabel heading = new JLabel(
                "<html><div style='text-align:center'><h1>BEAN THERE CAFE</h1>"
                + "Coffee, Tea &amp; Pastry Ordering System</div></html>",
                SwingConstants.CENTER);
        heading.setBorder(new EmptyBorder(0, 0, 12, 0));
        content.add(heading, BorderLayout.NORTH);

        JPanel leftColumn = buildSelectionPanel();
        JPanel receipt = buildReceiptPanel();
        JSplitPane columns = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(leftColumn), receipt);
        columns.setResizeWeight(0.52);
        columns.setDividerLocation(0.52);
        columns.setBorder(null);
        content.add(columns, BorderLayout.CENTER);

        connectEvents();
        updateDrinksForCategory();
        updatePastryTotal();
        refreshReceiptPreview();
    }

    private JPanel buildSelectionPanel() {
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBorder(new EmptyBorder(0, 0, 0, 8));

        JPanel drinkForm = newFormPanel();
        cmbCategory = new JComboBox<>(new String[]{"Coffee", "Matcha", "Tea"});
        cmbDrinkName = new JComboBox<>();
        sizeGroup = new ButtonGroup();
        JPanel sizes = new JPanel();
        sizeSmall = option("Small", "Small", sizeGroup, true);
        sizeMedium = option("Medium", "Medium", sizeGroup, false);
        sizeLarge = option("Large", "Large", sizeGroup, false);
        sizes.add(sizeSmall);
        sizes.add(sizeMedium);
        sizes.add(sizeLarge);
        addFormRow(drinkForm, 0, "Category", cmbCategory);
        addFormRow(drinkForm, 1, "Drink", cmbDrinkName);
        addFormRow(drinkForm, 2, "Size", sizes);
        left.add(section("DRINK SELECTION", drinkForm));

        JPanel customizeForm = newFormPanel();
        temperatureGroup = new ButtonGroup();
        JPanel temperatures = new JPanel();
        JRadioButton hot = option("Hot", "Hot", temperatureGroup, true);
        JRadioButton iced = option("Iced +$0.50", "Iced", temperatureGroup, false);
        JRadioButton frozen = option("Frozen +$1.00", "Frozen", temperatureGroup, false);
        temperatures.add(hot);
        temperatures.add(iced);
        temperatures.add(frozen);

        cmbMilk = new JComboBox<>(new String[]{"None", "Whole", "Oat", "Almond", "Soy"});
        spnExtraShots = new JSpinner(new SpinnerNumberModel(0, 0, 5, 1));
        cmbSyrup = new JComboBox<>(new String[]{
            "None", "Vanilla", "Caramel", "Mocha", "Brown Sugar",
            "Blueberry", "Strawberry", "Pistachio"
        });
        cmbColdFoam = new JComboBox<>(new String[]{
            "None", "Cookie Butter", "Vanilla", "Marshmallow",
            "Caramel", "Brown Sugar", "Pumpkin"
        });
        chkWhippedCream = new JCheckBox("Whipped cream +$0.50");
        chkSprinkles = new JCheckBox("Sprinkles (free with coffee/matcha)");
        spnDrinkQty = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
        lblLiveDrinkPrice = new JLabel("$3.00 each");
        lblLiveDrinkPrice.setFont(lblLiveDrinkPrice.getFont().deriveFont(Font.BOLD));

        addFormRow(customizeForm, 0, "Temperature", temperatures);
        addFormRow(customizeForm, 1, "Milk (oat +$0.75)", cmbMilk);
        addFormRow(customizeForm, 2, "Extra shots (+$1.00 each)", spnExtraShots);
        addFormRow(customizeForm, 3, "Syrup (+$0.70)", cmbSyrup);
        addFormRow(customizeForm, 4, "Cold foam (+$1.00)", cmbColdFoam);
        addFormRow(customizeForm, 5, "Toppings", chkWhippedCream);
        addFormRow(customizeForm, 6, "", chkSprinkles);
        addFormRow(customizeForm, 7, "Drink quantity", spnDrinkQty);
        addFormRow(customizeForm, 8, "Current drink price", lblLiveDrinkPrice);
        left.add(section("DRINK CUSTOMIZATION", customizeForm));

        JPanel pastryForm = newFormPanel();
        cmbPastry = new JComboBox<>(new String[]{
            "Croissant", "Chocolate Chip Cookie", "Cinnamon Roll", "Bagel"
        });
        spnPastryQty = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
        lblPastryTotal = new JLabel("$2.50");
        addFormRow(pastryForm, 0, "Pastry", cmbPastry);
        addFormRow(pastryForm, 1, "Quantity", spnPastryQty);
        addFormRow(pastryForm, 2, "Pastry total", lblPastryTotal);
        JButton addPastry = new JButton("Add pastry to order");
        addWideRow(pastryForm, 3, addPastry);
        left.add(section("PASTRY SELECTION", pastryForm));

        JPanel tipForm = newFormPanel();
        JPanel tips = new JPanel();
        tipGroup = new ButtonGroup();
        JRadioButton noTip = option("No tip", "0", tipGroup, true);
        JRadioButton tenTip = option("10%", "10", tipGroup, false);
        JRadioButton fifteenTip = option("15%", "15", tipGroup, false);
        JRadioButton twentyTip = option("20%", "20", tipGroup, false);
        tips.add(noTip);
        tips.add(tenTip);
        tips.add(fifteenTip);
        tips.add(twentyTip);
        addFormRow(tipForm, 0, "Tip applies to subtotal", tips);
        left.add(section("ORDER TIP", tipForm));

        JPanel actions = new JPanel(new GridLayout(2, 2, 8, 8));
        JButton calculate = new JButton("Calculate drink price");
        JButton addDrink = new JButton("Add drink to order");
        JButton checkout = new JButton("Checkout");
        JButton clear = new JButton("Clear order");
        actions.add(calculate);
        actions.add(addDrink);
        actions.add(checkout);
        actions.add(clear);
        left.add(section("ACTIONS", actions));

        calculate.addActionListener(e -> calculateDrinkPrice());
        addDrink.addActionListener(e -> addDrinkToOrder());
        addPastry.addActionListener(e -> addPastryToOrder());
        checkout.addActionListener(e -> checkout());
        clear.addActionListener(e -> clearOrder());
        return left;
    }

    private JPanel buildReceiptPanel() {
        JPanel receipt = new JPanel(new BorderLayout(8, 8));
        receipt.setBorder(BorderFactory.createTitledBorder("ITEMIZED RECEIPT"));

        JPanel body = new JPanel(new BorderLayout(8, 8));
        body.setBorder(new EmptyBorder(8, 10, 8, 10));
        JLabel receiptTitle = new JLabel("RECEIPT");
        receiptTitle.setFont(receiptTitle.getFont().deriveFont(Font.BOLD, 22f));
        body.add(receiptTitle, BorderLayout.NORTH);

        pnlReceiptItems = new JPanel();
        pnlReceiptItems.setLayout(new BoxLayout(pnlReceiptItems, BoxLayout.Y_AXIS));
        pnlReceiptItems.setBorder(new EmptyBorder(4, 0, 4, 0));
        pnlReceiptItems.add(new JLabel("Your order is empty."));
        body.add(new JScrollPane(pnlReceiptItems), BorderLayout.CENTER);

        JPanel totals = new JPanel(new GridBagLayout());
        lblSubtotal = new JLabel("$0.00");
        lblTip = new JLabel("$0.00");
        lblTotal = new JLabel("$0.00");
        lblTotal.setFont(lblTotal.getFont().deriveFont(Font.BOLD, 18f));
        addTotalRow(totals, 0, "Subtotal", lblSubtotal);
        addTotalRow(totals, 1, "Tip", lblTip);
        addTotalRow(totals, 2, "TOTAL", lblTotal);
        body.add(totals, BorderLayout.SOUTH);
        receipt.add(body, BorderLayout.CENTER);
        return receipt;
    }

    private void connectEvents() {
        cmbCategory.addActionListener(e -> updateDrinksForCategory());
        cmbDrinkName.addActionListener(e -> calculateDrinkPrice());
        cmbMilk.addActionListener(e -> calculateDrinkPrice());
        cmbSyrup.addActionListener(e -> calculateDrinkPrice());
        cmbColdFoam.addActionListener(e -> calculateDrinkPrice());
        spnExtraShots.addChangeListener(e -> calculateDrinkPrice());
        spnDrinkQty.addChangeListener(e -> calculateDrinkPrice());
        sizeSmall.addActionListener(e -> calculateDrinkPrice());
        sizeMedium.addActionListener(e -> calculateDrinkPrice());
        sizeLarge.addActionListener(e -> calculateDrinkPrice());
        for (java.awt.Component component : getTemperatureOptions()) {
            ((JRadioButton) component).addActionListener(e -> calculateDrinkPrice());
        }
        chkWhippedCream.addActionListener(e -> calculateDrinkPrice());
        chkSprinkles.addActionListener(e -> calculateDrinkPrice());
        cmbPastry.addActionListener(e -> updatePastryTotal());
        spnPastryQty.addChangeListener(e -> updatePastryTotal());
        java.util.Enumeration<?> tips = tipGroup.getElements();
        while (tips.hasMoreElements()) {
            JRadioButton tip = (JRadioButton) tips.nextElement();
            tip.addActionListener(e -> {
                applySelectedTip();
                refreshReceiptPreview();
            });
        }
    }

    private java.awt.Component[] getTemperatureOptions() {
        java.util.Enumeration<?> options = temperatureGroup.getElements();
        java.util.ArrayList<java.awt.Component> components = new java.util.ArrayList<>();
        while (options.hasMoreElements()) {
            components.add((java.awt.Component) options.nextElement());
        }
        return components.toArray(new java.awt.Component[0]);
    }

    private void updateDrinksForCategory() {
        String category = (String) cmbCategory.getSelectedItem();
        cmbDrinkName.removeAllItems();
        if ("Coffee".equalsIgnoreCase(category)) {
            for (String drink : new String[]{"Latte", "Espresso", "Doppio", "Ristretto", "Lungo"}) {
                cmbDrinkName.addItem(drink);
            }
        } else if ("Matcha".equalsIgnoreCase(category)) {
            cmbDrinkName.addItem("Matcha");
        } else if ("Tea".equalsIgnoreCase(category)) {
            cmbDrinkName.addItem("Black Tea");
            cmbDrinkName.addItem("Green Tea");
        }
        boolean sprinklesAllowed = !"Tea".equalsIgnoreCase(category);
        chkSprinkles.setEnabled(sprinklesAllowed);
        if (!sprinklesAllowed) chkSprinkles.setSelected(false);
        calculateDrinkPrice();
    }

    public void calculateDrinkPrice() {
        if (lblLiveDrinkPrice == null || cmbDrinkName.getSelectedItem() == null) return;
        Drink preview = buildCurrentDrinkFromUI();
        lblLiveDrinkPrice.setText(String.format("$%.2f each", preview.calculatePrice()));
    }

    public void addDrinkToOrder() {
        Drink drink = buildCurrentDrinkFromUI();
        currentOrder.addDrink(drink);
        addReceiptItem(drink.toString(), drink.getItemTotal(),
                getDrinkImageFile((String) cmbCategory.getSelectedItem(),
                        (String) cmbDrinkName.getSelectedItem(),
                        selected(temperatureGroup, "Hot"),
                        (String) cmbColdFoam.getSelectedItem()));
        refreshReceiptPreview();
    }

    public void addPastryToOrder() {
        String name = (String) cmbPastry.getSelectedItem();
        int quantity = (Integer) spnPastryQty.getValue();
        double unitPrice = getPastryPrice(name);
        Pastry pastry = new Pastry(name, quantity, unitPrice);
        currentOrder.addPastry(pastry);
        addReceiptItem(pastry.toString(), pastry.getItemTotal(), getPastryImageFile(name));
        refreshReceiptPreview();
    }

    public void checkout() {
        applySelectedTip();
        JTextArea receiptText = new JTextArea(currentOrder.generateReceipt(), 18, 38);
        receiptText.setEditable(false);
        receiptText.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JOptionPane.showMessageDialog(this, new JScrollPane(receiptText),
                "Order Complete", JOptionPane.INFORMATION_MESSAGE);
    }

    public void clearOrder() {
        currentOrder = new Order();
        cmbCategory.setSelectedIndex(0);
        cmbDrinkName.setSelectedIndex(0);
        sizeSmall.setSelected(true);
        selectOption(temperatureGroup, "Hot");
        cmbMilk.setSelectedItem("None");
        spnExtraShots.setValue(0);
        cmbSyrup.setSelectedIndex(0);
        cmbColdFoam.setSelectedIndex(0);
        chkWhippedCream.setSelected(false);
        chkSprinkles.setSelected(false);
        spnDrinkQty.setValue(1);
        cmbPastry.setSelectedIndex(0);
        spnPastryQty.setValue(1);
        selectOption(tipGroup, "0");
        pnlReceiptItems.removeAll();
        pnlReceiptItems.add(new JLabel("Your order is empty."));
        pnlReceiptItems.revalidate();
        pnlReceiptItems.repaint();
        applySelectedTip();
        calculateDrinkPrice();
        updatePastryTotal();
        refreshReceiptPreview();
    }

    private Drink buildCurrentDrinkFromUI() {
        return new Drink(
                (String) cmbCategory.getSelectedItem(),
                (String) cmbDrinkName.getSelectedItem(),
                selected(sizeGroup, "Small"),
                selected(temperatureGroup, "Hot"),
                (String) cmbMilk.getSelectedItem(),
                (Integer) spnExtraShots.getValue(),
                (String) cmbSyrup.getSelectedItem(),
                (String) cmbColdFoam.getSelectedItem(),
                chkWhippedCream.isSelected(),
                chkSprinkles.isSelected(),
                (Integer) spnDrinkQty.getValue());
    }

    private String selected(ButtonGroup group, String fallback) {
        return group.getSelection() == null ? fallback : group.getSelection().getActionCommand();
    }

    private void selectOption(ButtonGroup group, String value) {
        java.util.Enumeration<?> options = group.getElements();
        while (options.hasMoreElements()) {
            JRadioButton option = (JRadioButton) options.nextElement();
            if (value.equals(option.getActionCommand())) {
                option.setSelected(true);
                return;
            }
        }
    }

    private void applySelectedTip() {
        if (tipGroup != null && tipGroup.getSelection() != null) {
            currentOrder.setTipPercentage(
                    Integer.parseInt(tipGroup.getSelection().getActionCommand()) / 100.0);
        }
    }

    private void refreshReceiptPreview() {
        if (currentOrder == null || lblSubtotal == null) return;
        applySelectedTip();
        lblSubtotal.setText(String.format("$%.2f", currentOrder.calculateSubtotal()));
        lblTip.setText(String.format("$%.2f", currentOrder.calculateTip()));
        lblTotal.setText(String.format("$%.2f", currentOrder.calculateTotal()));
    }

    private void addReceiptItem(String details, double total, String imageFile) {
        if (pnlReceiptItems.getComponentCount() == 1
                && pnlReceiptItems.getComponent(0) instanceof JLabel
                && "Your order is empty.".equals(((JLabel) pnlReceiptItems.getComponent(0)).getText())) {
            pnlReceiptItems.removeAll();
        }
        JPanel row = new JPanel(new BorderLayout(10, 4));
        row.setBorder(new MatteBorder(0, 0, 1, 0, java.awt.Color.LIGHT_GRAY));
        row.setAlignmentX(LEFT_ALIGNMENT);
        JLabel image = new JLabel();
        image.setHorizontalAlignment(SwingConstants.CENTER);
        image.setPreferredSize(new Dimension(82, 82));
        setScaledIcon(image, "/images/" + imageFile, "Item");
        JLabel description = new JLabel("<html>" + details.replace("\n", "<br>") + "</html>");
        JLabel amount = new JLabel(String.format("$%.2f", total), SwingConstants.RIGHT);
        amount.setFont(amount.getFont().deriveFont(Font.BOLD, 16f));
        row.add(image, BorderLayout.WEST);
        row.add(description, BorderLayout.CENTER);
        row.add(amount, BorderLayout.EAST);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        pnlReceiptItems.add(row);
        pnlReceiptItems.add(Box.createVerticalStrut(6));
        pnlReceiptItems.revalidate();
        pnlReceiptItems.repaint();
    }

    private void updatePastryTotal() {
        if (lblPastryTotal == null) return;
        String name = (String) cmbPastry.getSelectedItem();
        int quantity = (Integer) spnPastryQty.getValue();
        lblPastryTotal.setText(String.format("$%.2f", getPastryPrice(name) * quantity));
    }

    private double getPastryPrice(String name) {
        if ("Chocolate Chip Cookie".equals(name) || "Bagel".equals(name)) return 1.00;
        return 2.50;
    }

    private String getDrinkImageFile(String category, String drink, String temperature, String coldFoam) {
        if (!"None".equalsIgnoreCase(coldFoam)) return "cold_foam.jpg";
        if ("Matcha".equalsIgnoreCase(category)) {
            return "Hot".equalsIgnoreCase(temperature) ? "hot_matcha.jpg" : "matcha.jpg";
        }
        if ("Green Tea".equalsIgnoreCase(drink)) return "green_tea.jpg";
        if ("Black Tea".equalsIgnoreCase(drink)) return "black_tea.jpg";
        return "Hot".equalsIgnoreCase(temperature) ? "hot_coffee.jpg" : "cold_coffee.jpg";
    }

    private String getPastryImageFile(String pastry) {
        if ("Chocolate Chip Cookie".equals(pastry)) return "choco_cookie.jpg";
        if ("Cinnamon Roll".equals(pastry)) return "cinnamon_rolls.jpg";
        if ("Bagel".equals(pastry)) return "bagel.jpg";
        return "croissamt.jpg";
    }

    private void setScaledIcon(JLabel label, String resourcePath, String fallbackText) {
        URL imageUrl = getClass().getResource(resourcePath);
        if (imageUrl == null) {
            label.setText(fallbackText);
            return;
        }
        ImageIcon icon = new ImageIcon(imageUrl);
        Image scaled = icon.getImage().getScaledInstance(72, 72, Image.SCALE_SMOOTH);
        label.setIcon(new ImageIcon(scaled));
        label.setText("");
    }

    private JPanel newFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(8, 10, 8, 10));
        return panel;
    }

    private JPanel section(String title, JPanel content) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.add(content, BorderLayout.CENTER);
        panel.setAlignmentX(LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getPreferredSize().height + 40));
        return panel;
    }

    private void addFormRow(JPanel panel, int row, String label, java.awt.Component component) {
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = row;
        labelConstraints.anchor = GridBagConstraints.WEST;
        labelConstraints.insets = new Insets(5, 4, 5, 12);
        panel.add(new JLabel(label), labelConstraints);

        GridBagConstraints componentConstraints = new GridBagConstraints();
        componentConstraints.gridx = 1;
        componentConstraints.gridy = row;
        componentConstraints.weightx = 1;
        componentConstraints.fill = GridBagConstraints.HORIZONTAL;
        componentConstraints.insets = new Insets(5, 4, 5, 4);
        panel.add(component, componentConstraints);
    }

    private void addWideRow(JPanel panel, int row, java.awt.Component component) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.gridwidth = 2;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(6, 4, 5, 4);
        panel.add(component, constraints);
    }

    private JRadioButton option(String text, String value, ButtonGroup group, boolean selected) {
        JRadioButton button = new JRadioButton(text, selected);
        button.setActionCommand(value);
        group.add(button);
        return button;
    }

    private void addTotalRow(JPanel panel, int row, String label, JLabel amount) {
        GridBagConstraints text = new GridBagConstraints();
        text.gridx = 0;
        text.gridy = row;
        text.weightx = 1;
        text.anchor = GridBagConstraints.WEST;
        text.insets = new Insets(7, 4, 7, 20);
        panel.add(new JLabel(label), text);

        GridBagConstraints value = new GridBagConstraints();
        value.gridx = 1;
        value.gridy = row;
        value.anchor = GridBagConstraints.EAST;
        value.insets = new Insets(7, 4, 7, 4);
        panel.add(amount, value);
    }
}
