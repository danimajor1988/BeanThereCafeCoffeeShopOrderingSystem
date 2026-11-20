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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Main application window for the Bean There Cafe ordering system.
 * The frame layout (drink selection/customization forms, pastry selection,
 * tip options, and itemized receipt panel) was designed with the Eclipse
 * WindowBuilder designer. Event handling and integration with the order
 * backend ({@link Order}, {@link Drink}, {@link Pastry}) are implemented
 * in the handler methods below the constructor.
 *
 * @author Nathaly Franco - GUI layout (WindowBuilder)
 * @author Danielle Major - event handlers and backend integration
 */
public class MainFrame extends JFrame {

    /** The order currently being built; replaced with a fresh Order on clear/checkout. */
    private Order currentOrder;

    /** Root content pane of the frame (managed by WindowBuilder). */
    private JPanel contentPane;

    // --- Drink selection controls ---
    /** Drink category selector: "Coffee" or "Tea". */
    private JComboBox<String> cmbCategory;
    /** Drink name selector; its items change when the category changes. */
    private JComboBox<String> cmbDrinkName;
    /** Milk choice selector (oat milk carries an upcharge). */
    private JComboBox<String> cmbMilk;
    /** Syrup flavor selector ("None" = no syrup). */
    private JComboBox<String> cmbSyrup;
    /** Cold foam flavor selector ("None" = no cold foam). */
    private JComboBox<String> cmbColdFoam;
    /** Number of extra espresso shots (0-5). */
    private JSpinner spnExtraShots;
    /** Quantity of the current drink to add (1-20). */
    private JSpinner spnDrinkQty;
    /** Size radio buttons; action commands hold the size names. */
    private JRadioButton sizeSmall;
    private JRadioButton sizeMedium;
    private JRadioButton sizeLarge;
    /** Groups the size radios so only one can be selected. */
    private ButtonGroup sizeGroup;
    /** Groups the Hot/Iced/Frozen radios; selection read via action command. */
    private ButtonGroup temperatureGroup;
    /** Whipped cream topping (+$0.50). */
    private JCheckBox chkWhippedCream;
    /** Sprinkles topping (free; disabled for tea drinks). */
    private JCheckBox chkSprinkles;
    /** Live per-drink price readout, updated as options change. */
    private JLabel lblLiveDrinkPrice;

    // --- Pastry selection controls ---
    /** Pastry selector. */
    private JComboBox<String> cmbPastry;
    /** Quantity of the selected pastry (1-20). */
    private JSpinner spnPastryQty;
    /** Live pastry line total (unit price x quantity). */
    private JLabel lblPastryTotal;

    // --- Tip and receipt controls ---
    /** Groups the tip radios; action commands hold the tip percentages. */
    private ButtonGroup tipGroup;
    /** Vertical list of receipt row panels, one per order item. */
    private JPanel pnlReceiptItems;
    /** Running subtotal readout. */
    private JLabel lblSubtotal;
    /** Running tip readout. */
    private JLabel lblTip;
    /** Running grand total readout. */
    private JLabel lblTotal;
    /** Per-row bookkeeping (item type, order index, row panel) used for item removal. */
    private ArrayList<Map<String, Object>> receiptItemMetadata;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    MainFrame frame = new MainFrame();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     * Layout built in the WindowBuilder designer (Nathaly Franco); the
     * listeners attached here delegate to the event handler methods below
     * (Danielle Major).
     */
    public MainFrame() {
        currentOrder = new Order();
        receiptItemMetadata = new ArrayList<Map<String, Object>>();
        setTitle("Bean There Cafe - Coffee, Tea & Pastry Ordering System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(980, 720));
        setBounds(100, 100, 1280, 900);
        setLocationRelativeTo(null);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(14, 18, 14, 18));
        contentPane.setLayout(new BorderLayout(12, 12));
        setContentPane(contentPane);

        // Centered cafe heading across the top of the window.
        JLabel lblHeading = new JLabel(
                "<html><div style='text-align:center'><h1>BEAN THERE CAFE</h1>"
                + "Coffee, Tea &amp; Pastry Ordering System</div></html>",
                SwingConstants.CENTER);
        lblHeading.setBorder(new EmptyBorder(0, 0, 12, 0));
        contentPane.add(lblHeading, BorderLayout.NORTH);

        // Left column: stacked titled sections (drink, customization, pastry, tip, actions).
        JPanel pnlLeftColumn = new JPanel();
        pnlLeftColumn.setBorder(new EmptyBorder(0, 0, 0, 8));
        pnlLeftColumn.setLayout(new BoxLayout(pnlLeftColumn, BoxLayout.Y_AXIS));

        // ----- DRINK SELECTION section: category, drink name, and size -----
        JPanel pnlDrinkForm = new JPanel();
        pnlDrinkForm.setBorder(new EmptyBorder(8, 10, 8, 10));
        pnlDrinkForm.setLayout(new GridBagLayout());

        JLabel lblCategory = new JLabel("Category");
        GridBagConstraints gbc_lblCategory = new GridBagConstraints();
        gbc_lblCategory.gridx = 0;
        gbc_lblCategory.gridy = 0;
        gbc_lblCategory.anchor = GridBagConstraints.WEST;
        gbc_lblCategory.insets = new Insets(5, 4, 5, 12);
        pnlDrinkForm.add(lblCategory, gbc_lblCategory);

        cmbCategory = new JComboBox<String>();
        cmbCategory.setModel(new DefaultComboBoxModel<String>(new String[] {"Coffee", "Tea"}));
        cmbCategory.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateDrinksForCategory();
            }
        });
        GridBagConstraints gbc_cmbCategory = new GridBagConstraints();
        gbc_cmbCategory.gridx = 1;
        gbc_cmbCategory.gridy = 0;
        gbc_cmbCategory.weightx = 1.0;
        gbc_cmbCategory.fill = GridBagConstraints.HORIZONTAL;
        gbc_cmbCategory.insets = new Insets(5, 4, 5, 4);
        pnlDrinkForm.add(cmbCategory, gbc_cmbCategory);

        JLabel lblDrink = new JLabel("Drink");
        GridBagConstraints gbc_lblDrink = new GridBagConstraints();
        gbc_lblDrink.gridx = 0;
        gbc_lblDrink.gridy = 1;
        gbc_lblDrink.anchor = GridBagConstraints.WEST;
        gbc_lblDrink.insets = new Insets(5, 4, 5, 12);
        pnlDrinkForm.add(lblDrink, gbc_lblDrink);

        cmbDrinkName = new JComboBox<String>();
        cmbDrinkName.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                calculateDrinkPrice();
            }
        });
        GridBagConstraints gbc_cmbDrinkName = new GridBagConstraints();
        gbc_cmbDrinkName.gridx = 1;
        gbc_cmbDrinkName.gridy = 1;
        gbc_cmbDrinkName.weightx = 1.0;
        gbc_cmbDrinkName.fill = GridBagConstraints.HORIZONTAL;
        gbc_cmbDrinkName.insets = new Insets(5, 4, 5, 4);
        pnlDrinkForm.add(cmbDrinkName, gbc_cmbDrinkName);

        JLabel lblSize = new JLabel("Size");
        GridBagConstraints gbc_lblSize = new GridBagConstraints();
        gbc_lblSize.gridx = 0;
        gbc_lblSize.gridy = 2;
        gbc_lblSize.anchor = GridBagConstraints.WEST;
        gbc_lblSize.insets = new Insets(5, 4, 5, 12);
        pnlDrinkForm.add(lblSize, gbc_lblSize);

        JPanel pnlSizes = new JPanel();

        sizeGroup = new ButtonGroup();

        sizeSmall = new JRadioButton("Small");
        sizeSmall.setSelected(true);
        sizeSmall.setActionCommand("Small");
        sizeSmall.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                calculateDrinkPrice();
            }
        });
        sizeGroup.add(sizeSmall);
        pnlSizes.add(sizeSmall);

        sizeMedium = new JRadioButton("Medium");
        sizeMedium.setActionCommand("Medium");
        sizeMedium.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                calculateDrinkPrice();
            }
        });
        sizeGroup.add(sizeMedium);
        pnlSizes.add(sizeMedium);

        sizeLarge = new JRadioButton("Large");
        sizeLarge.setActionCommand("Large");
        sizeLarge.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                calculateDrinkPrice();
            }
        });
        sizeGroup.add(sizeLarge);
        pnlSizes.add(sizeLarge);

        GridBagConstraints gbc_pnlSizes = new GridBagConstraints();
        gbc_pnlSizes.gridx = 1;
        gbc_pnlSizes.gridy = 2;
        gbc_pnlSizes.weightx = 1.0;
        gbc_pnlSizes.fill = GridBagConstraints.HORIZONTAL;
        gbc_pnlSizes.insets = new Insets(5, 4, 5, 4);
        pnlDrinkForm.add(pnlSizes, gbc_pnlSizes);

        JPanel pnlDrinkSection = new JPanel();
        pnlDrinkSection.setBorder(BorderFactory.createTitledBorder("DRINK SELECTION"));
        pnlDrinkSection.setLayout(new BorderLayout());
        pnlDrinkSection.add(pnlDrinkForm, BorderLayout.CENTER);
        pnlDrinkSection.setAlignmentX(LEFT_ALIGNMENT);
        pnlDrinkSection.setMaximumSize(new Dimension(Integer.MAX_VALUE, pnlDrinkSection.getPreferredSize().height + 40));
        pnlLeftColumn.add(pnlDrinkSection);

        // ----- DRINK CUSTOMIZATION section: temperature, milk, shots, syrup,
        // cold foam, toppings, quantity, live price, and the add button -----
        JPanel pnlCustomizeForm = new JPanel();
        pnlCustomizeForm.setBorder(new EmptyBorder(8, 10, 8, 10));
        pnlCustomizeForm.setLayout(new GridBagLayout());

        JLabel lblTemperature = new JLabel("Temperature");
        GridBagConstraints gbc_lblTemperature = new GridBagConstraints();
        gbc_lblTemperature.gridx = 0;
        gbc_lblTemperature.gridy = 0;
        gbc_lblTemperature.anchor = GridBagConstraints.WEST;
        gbc_lblTemperature.insets = new Insets(5, 4, 5, 12);
        pnlCustomizeForm.add(lblTemperature, gbc_lblTemperature);

        JPanel pnlTemperatures = new JPanel();

        temperatureGroup = new ButtonGroup();

        JRadioButton rdbtnHot = new JRadioButton("Hot");
        rdbtnHot.setSelected(true);
        rdbtnHot.setActionCommand("Hot");
        rdbtnHot.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                calculateDrinkPrice();
            }
        });
        temperatureGroup.add(rdbtnHot);
        pnlTemperatures.add(rdbtnHot);

        JRadioButton rdbtnIced = new JRadioButton("Iced +$0.50");
        rdbtnIced.setActionCommand("Iced");
        rdbtnIced.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                calculateDrinkPrice();
            }
        });
        temperatureGroup.add(rdbtnIced);
        pnlTemperatures.add(rdbtnIced);

        JRadioButton rdbtnFrozen = new JRadioButton("Frozen +$1.00");
        rdbtnFrozen.setActionCommand("Frozen");
        rdbtnFrozen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                calculateDrinkPrice();
            }
        });
        temperatureGroup.add(rdbtnFrozen);
        pnlTemperatures.add(rdbtnFrozen);

        GridBagConstraints gbc_pnlTemperatures = new GridBagConstraints();
        gbc_pnlTemperatures.gridx = 1;
        gbc_pnlTemperatures.gridy = 0;
        gbc_pnlTemperatures.weightx = 1.0;
        gbc_pnlTemperatures.fill = GridBagConstraints.HORIZONTAL;
        gbc_pnlTemperatures.insets = new Insets(5, 4, 5, 4);
        pnlCustomizeForm.add(pnlTemperatures, gbc_pnlTemperatures);

        JLabel lblMilk = new JLabel("Milk (oat +$0.75)");
        GridBagConstraints gbc_lblMilk = new GridBagConstraints();
        gbc_lblMilk.gridx = 0;
        gbc_lblMilk.gridy = 1;
        gbc_lblMilk.anchor = GridBagConstraints.WEST;
        gbc_lblMilk.insets = new Insets(5, 4, 5, 12);
        pnlCustomizeForm.add(lblMilk, gbc_lblMilk);

        cmbMilk = new JComboBox<String>();
        cmbMilk.setModel(new DefaultComboBoxModel<String>(new String[] {"None", "Whole", "Oat", "Almond", "Soy"}));
        cmbMilk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                calculateDrinkPrice();
            }
        });
        GridBagConstraints gbc_cmbMilk = new GridBagConstraints();
        gbc_cmbMilk.gridx = 1;
        gbc_cmbMilk.gridy = 1;
        gbc_cmbMilk.weightx = 1.0;
        gbc_cmbMilk.fill = GridBagConstraints.HORIZONTAL;
        gbc_cmbMilk.insets = new Insets(5, 4, 5, 4);
        pnlCustomizeForm.add(cmbMilk, gbc_cmbMilk);

        JLabel lblExtraShots = new JLabel("Extra shots (+$1.00 each)");
        GridBagConstraints gbc_lblExtraShots = new GridBagConstraints();
        gbc_lblExtraShots.gridx = 0;
        gbc_lblExtraShots.gridy = 2;
        gbc_lblExtraShots.anchor = GridBagConstraints.WEST;
        gbc_lblExtraShots.insets = new Insets(5, 4, 5, 12);
        pnlCustomizeForm.add(lblExtraShots, gbc_lblExtraShots);

        spnExtraShots = new JSpinner();
        spnExtraShots.setModel(new SpinnerNumberModel(0, 0, 5, 1));
        spnExtraShots.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                calculateDrinkPrice();
            }
        });
        GridBagConstraints gbc_spnExtraShots = new GridBagConstraints();
        gbc_spnExtraShots.gridx = 1;
        gbc_spnExtraShots.gridy = 2;
        gbc_spnExtraShots.weightx = 1.0;
        gbc_spnExtraShots.fill = GridBagConstraints.HORIZONTAL;
        gbc_spnExtraShots.insets = new Insets(5, 4, 5, 4);
        pnlCustomizeForm.add(spnExtraShots, gbc_spnExtraShots);

        JLabel lblSyrup = new JLabel("Syrup (+$0.70)");
        GridBagConstraints gbc_lblSyrup = new GridBagConstraints();
        gbc_lblSyrup.gridx = 0;
        gbc_lblSyrup.gridy = 3;
        gbc_lblSyrup.anchor = GridBagConstraints.WEST;
        gbc_lblSyrup.insets = new Insets(5, 4, 5, 12);
        pnlCustomizeForm.add(lblSyrup, gbc_lblSyrup);

        cmbSyrup = new JComboBox<String>();
        cmbSyrup.setModel(new DefaultComboBoxModel<String>(new String[] {
            "None", "Vanilla", "Caramel", "Mocha", "Brown Sugar",
            "Blueberry", "Strawberry", "Pistachio"
        }));
        cmbSyrup.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                calculateDrinkPrice();
            }
        });
        GridBagConstraints gbc_cmbSyrup = new GridBagConstraints();
        gbc_cmbSyrup.gridx = 1;
        gbc_cmbSyrup.gridy = 3;
        gbc_cmbSyrup.weightx = 1.0;
        gbc_cmbSyrup.fill = GridBagConstraints.HORIZONTAL;
        gbc_cmbSyrup.insets = new Insets(5, 4, 5, 4);
        pnlCustomizeForm.add(cmbSyrup, gbc_cmbSyrup);

        JLabel lblColdFoam = new JLabel("Cold foam (+$1.00)");
        GridBagConstraints gbc_lblColdFoam = new GridBagConstraints();
        gbc_lblColdFoam.gridx = 0;
        gbc_lblColdFoam.gridy = 4;
        gbc_lblColdFoam.anchor = GridBagConstraints.WEST;
        gbc_lblColdFoam.insets = new Insets(5, 4, 5, 12);
        pnlCustomizeForm.add(lblColdFoam, gbc_lblColdFoam);

        cmbColdFoam = new JComboBox<String>();
        cmbColdFoam.setModel(new DefaultComboBoxModel<String>(new String[] {
            "None", "Cookie Butter", "Vanilla", "Marshmallow",
            "Caramel", "Brown Sugar", "Pumpkin"
        }));
        cmbColdFoam.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                calculateDrinkPrice();
            }
        });
        GridBagConstraints gbc_cmbColdFoam = new GridBagConstraints();
        gbc_cmbColdFoam.gridx = 1;
        gbc_cmbColdFoam.gridy = 4;
        gbc_cmbColdFoam.weightx = 1.0;
        gbc_cmbColdFoam.fill = GridBagConstraints.HORIZONTAL;
        gbc_cmbColdFoam.insets = new Insets(5, 4, 5, 4);
        pnlCustomizeForm.add(cmbColdFoam, gbc_cmbColdFoam);

        JLabel lblToppings = new JLabel("Toppings");
        GridBagConstraints gbc_lblToppings = new GridBagConstraints();
        gbc_lblToppings.gridx = 0;
        gbc_lblToppings.gridy = 5;
        gbc_lblToppings.anchor = GridBagConstraints.WEST;
        gbc_lblToppings.insets = new Insets(5, 4, 5, 12);
        pnlCustomizeForm.add(lblToppings, gbc_lblToppings);

        chkWhippedCream = new JCheckBox("Whipped cream +$0.50");
        chkWhippedCream.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                calculateDrinkPrice();
            }
        });
        GridBagConstraints gbc_chkWhippedCream = new GridBagConstraints();
        gbc_chkWhippedCream.gridx = 1;
        gbc_chkWhippedCream.gridy = 5;
        gbc_chkWhippedCream.weightx = 1.0;
        gbc_chkWhippedCream.fill = GridBagConstraints.HORIZONTAL;
        gbc_chkWhippedCream.insets = new Insets(5, 4, 5, 4);
        pnlCustomizeForm.add(chkWhippedCream, gbc_chkWhippedCream);

        JLabel lblToppingsSpacer = new JLabel("");
        GridBagConstraints gbc_lblToppingsSpacer = new GridBagConstraints();
        gbc_lblToppingsSpacer.gridx = 0;
        gbc_lblToppingsSpacer.gridy = 6;
        gbc_lblToppingsSpacer.anchor = GridBagConstraints.WEST;
        gbc_lblToppingsSpacer.insets = new Insets(5, 4, 5, 12);
        pnlCustomizeForm.add(lblToppingsSpacer, gbc_lblToppingsSpacer);

        chkSprinkles = new JCheckBox("Sprinkles (free with coffee/matcha)");
        chkSprinkles.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                calculateDrinkPrice();
            }
        });
        GridBagConstraints gbc_chkSprinkles = new GridBagConstraints();
        gbc_chkSprinkles.gridx = 1;
        gbc_chkSprinkles.gridy = 6;
        gbc_chkSprinkles.weightx = 1.0;
        gbc_chkSprinkles.fill = GridBagConstraints.HORIZONTAL;
        gbc_chkSprinkles.insets = new Insets(5, 4, 5, 4);
        pnlCustomizeForm.add(chkSprinkles, gbc_chkSprinkles);

        JLabel lblDrinkQuantity = new JLabel("Drink quantity");
        GridBagConstraints gbc_lblDrinkQuantity = new GridBagConstraints();
        gbc_lblDrinkQuantity.gridx = 0;
        gbc_lblDrinkQuantity.gridy = 7;
        gbc_lblDrinkQuantity.anchor = GridBagConstraints.WEST;
        gbc_lblDrinkQuantity.insets = new Insets(5, 4, 5, 12);
        pnlCustomizeForm.add(lblDrinkQuantity, gbc_lblDrinkQuantity);

        spnDrinkQty = new JSpinner();
        spnDrinkQty.setModel(new SpinnerNumberModel(1, 1, 20, 1));
        spnDrinkQty.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                calculateDrinkPrice();
            }
        });
        GridBagConstraints gbc_spnDrinkQty = new GridBagConstraints();
        gbc_spnDrinkQty.gridx = 1;
        gbc_spnDrinkQty.gridy = 7;
        gbc_spnDrinkQty.weightx = 1.0;
        gbc_spnDrinkQty.fill = GridBagConstraints.HORIZONTAL;
        gbc_spnDrinkQty.insets = new Insets(5, 4, 5, 4);
        pnlCustomizeForm.add(spnDrinkQty, gbc_spnDrinkQty);

        JLabel lblCurrentDrinkPrice = new JLabel("Current drink price");
        GridBagConstraints gbc_lblCurrentDrinkPrice = new GridBagConstraints();
        gbc_lblCurrentDrinkPrice.gridx = 0;
        gbc_lblCurrentDrinkPrice.gridy = 8;
        gbc_lblCurrentDrinkPrice.anchor = GridBagConstraints.WEST;
        gbc_lblCurrentDrinkPrice.insets = new Insets(5, 4, 5, 12);
        pnlCustomizeForm.add(lblCurrentDrinkPrice, gbc_lblCurrentDrinkPrice);

        lblLiveDrinkPrice = new JLabel("$3.00 each");
        lblLiveDrinkPrice.setFont(lblLiveDrinkPrice.getFont().deriveFont(Font.BOLD));
        GridBagConstraints gbc_lblLiveDrinkPrice = new GridBagConstraints();
        gbc_lblLiveDrinkPrice.gridx = 1;
        gbc_lblLiveDrinkPrice.gridy = 8;
        gbc_lblLiveDrinkPrice.weightx = 1.0;
        gbc_lblLiveDrinkPrice.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblLiveDrinkPrice.insets = new Insets(5, 4, 5, 4);
        pnlCustomizeForm.add(lblLiveDrinkPrice, gbc_lblLiveDrinkPrice);

        JButton btnAddDrink = new JButton("Add drink to order");
        btnAddDrink.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addDrinkToOrder();
            }
        });
        GridBagConstraints gbc_btnAddDrink = new GridBagConstraints();
        gbc_btnAddDrink.gridx = 0;
        gbc_btnAddDrink.gridy = 9;
        gbc_btnAddDrink.gridwidth = 2;
        gbc_btnAddDrink.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnAddDrink.insets = new Insets(6, 4, 5, 4);
        pnlCustomizeForm.add(btnAddDrink, gbc_btnAddDrink);

        JPanel pnlCustomizeSection = new JPanel();
        pnlCustomizeSection.setBorder(BorderFactory.createTitledBorder("DRINK CUSTOMIZATION"));
        pnlCustomizeSection.setLayout(new BorderLayout());
        pnlCustomizeSection.add(pnlCustomizeForm, BorderLayout.CENTER);
        pnlCustomizeSection.setAlignmentX(LEFT_ALIGNMENT);
        pnlCustomizeSection.setMaximumSize(new Dimension(Integer.MAX_VALUE, pnlCustomizeSection.getPreferredSize().height + 40));
        pnlLeftColumn.add(pnlCustomizeSection);

        // ----- PASTRY SELECTION section: pastry, quantity, live total, add button -----
        JPanel pnlPastryForm = new JPanel();
        pnlPastryForm.setBorder(new EmptyBorder(8, 10, 8, 10));
        pnlPastryForm.setLayout(new GridBagLayout());

        JLabel lblPastry = new JLabel("Pastry");
        GridBagConstraints gbc_lblPastry = new GridBagConstraints();
        gbc_lblPastry.gridx = 0;
        gbc_lblPastry.gridy = 0;
        gbc_lblPastry.anchor = GridBagConstraints.WEST;
        gbc_lblPastry.insets = new Insets(5, 4, 5, 12);
        pnlPastryForm.add(lblPastry, gbc_lblPastry);

        cmbPastry = new JComboBox<String>();
        cmbPastry.setModel(new DefaultComboBoxModel<String>(new String[] {
            "Croissant", "Chocolate Chip Cookie", "Cinnamon Roll", "Bagel"
        }));
        cmbPastry.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updatePastryTotal();
            }
        });
        GridBagConstraints gbc_cmbPastry = new GridBagConstraints();
        gbc_cmbPastry.gridx = 1;
        gbc_cmbPastry.gridy = 0;
        gbc_cmbPastry.weightx = 1.0;
        gbc_cmbPastry.fill = GridBagConstraints.HORIZONTAL;
        gbc_cmbPastry.insets = new Insets(5, 4, 5, 4);
        pnlPastryForm.add(cmbPastry, gbc_cmbPastry);

        JLabel lblQuantity = new JLabel("Quantity");
        GridBagConstraints gbc_lblQuantity = new GridBagConstraints();
        gbc_lblQuantity.gridx = 0;
        gbc_lblQuantity.gridy = 1;
        gbc_lblQuantity.anchor = GridBagConstraints.WEST;
        gbc_lblQuantity.insets = new Insets(5, 4, 5, 12);
        pnlPastryForm.add(lblQuantity, gbc_lblQuantity);

        spnPastryQty = new JSpinner();
        spnPastryQty.setModel(new SpinnerNumberModel(1, 1, 20, 1));
        spnPastryQty.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                updatePastryTotal();
            }
        });
        GridBagConstraints gbc_spnPastryQty = new GridBagConstraints();
        gbc_spnPastryQty.gridx = 1;
        gbc_spnPastryQty.gridy = 1;
        gbc_spnPastryQty.weightx = 1.0;
        gbc_spnPastryQty.fill = GridBagConstraints.HORIZONTAL;
        gbc_spnPastryQty.insets = new Insets(5, 4, 5, 4);
        pnlPastryForm.add(spnPastryQty, gbc_spnPastryQty);

        JLabel lblPastryTotalCaption = new JLabel("Pastry total");
        GridBagConstraints gbc_lblPastryTotalCaption = new GridBagConstraints();
        gbc_lblPastryTotalCaption.gridx = 0;
        gbc_lblPastryTotalCaption.gridy = 2;
        gbc_lblPastryTotalCaption.anchor = GridBagConstraints.WEST;
        gbc_lblPastryTotalCaption.insets = new Insets(5, 4, 5, 12);
        pnlPastryForm.add(lblPastryTotalCaption, gbc_lblPastryTotalCaption);

        lblPastryTotal = new JLabel("$2.50");
        GridBagConstraints gbc_lblPastryTotal = new GridBagConstraints();
        gbc_lblPastryTotal.gridx = 1;
        gbc_lblPastryTotal.gridy = 2;
        gbc_lblPastryTotal.weightx = 1.0;
        gbc_lblPastryTotal.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblPastryTotal.insets = new Insets(5, 4, 5, 4);
        pnlPastryForm.add(lblPastryTotal, gbc_lblPastryTotal);

        JButton btnAddPastry = new JButton("Add pastry to order");
        btnAddPastry.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addPastryToOrder();
            }
        });
        GridBagConstraints gbc_btnAddPastry = new GridBagConstraints();
        gbc_btnAddPastry.gridx = 0;
        gbc_btnAddPastry.gridy = 3;
        gbc_btnAddPastry.gridwidth = 2;
        gbc_btnAddPastry.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnAddPastry.insets = new Insets(6, 4, 5, 4);
        pnlPastryForm.add(btnAddPastry, gbc_btnAddPastry);

        JPanel pnlPastrySection = new JPanel();
        pnlPastrySection.setBorder(BorderFactory.createTitledBorder("PASTRY SELECTION"));
        pnlPastrySection.setLayout(new BorderLayout());
        pnlPastrySection.add(pnlPastryForm, BorderLayout.CENTER);
        pnlPastrySection.setAlignmentX(LEFT_ALIGNMENT);
        pnlPastrySection.setMaximumSize(new Dimension(Integer.MAX_VALUE, pnlPastrySection.getPreferredSize().height + 40));
        pnlLeftColumn.add(pnlPastrySection);

        // ----- ORDER TIP section: tip percentage radio buttons -----
        JPanel pnlTipForm = new JPanel();
        pnlTipForm.setBorder(new EmptyBorder(8, 10, 8, 10));
        pnlTipForm.setLayout(new GridBagLayout());

        JLabel lblTipCaption = new JLabel("Tip applies to subtotal");
        GridBagConstraints gbc_lblTipCaption = new GridBagConstraints();
        gbc_lblTipCaption.gridx = 0;
        gbc_lblTipCaption.gridy = 0;
        gbc_lblTipCaption.anchor = GridBagConstraints.WEST;
        gbc_lblTipCaption.insets = new Insets(5, 4, 5, 12);
        pnlTipForm.add(lblTipCaption, gbc_lblTipCaption);

        JPanel pnlTips = new JPanel();

        tipGroup = new ButtonGroup();

        JRadioButton rdbtnNoTip = new JRadioButton("No tip");
        rdbtnNoTip.setSelected(true);
        rdbtnNoTip.setActionCommand("0");
        rdbtnNoTip.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                applySelectedTip();
                refreshReceiptPreview();
            }
        });
        tipGroup.add(rdbtnNoTip);
        pnlTips.add(rdbtnNoTip);

        JRadioButton rdbtnTip10 = new JRadioButton("10%");
        rdbtnTip10.setActionCommand("10");
        rdbtnTip10.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                applySelectedTip();
                refreshReceiptPreview();
            }
        });
        tipGroup.add(rdbtnTip10);
        pnlTips.add(rdbtnTip10);

        JRadioButton rdbtnTip15 = new JRadioButton("15%");
        rdbtnTip15.setActionCommand("15");
        rdbtnTip15.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                applySelectedTip();
                refreshReceiptPreview();
            }
        });
        tipGroup.add(rdbtnTip15);
        pnlTips.add(rdbtnTip15);

        JRadioButton rdbtnTip20 = new JRadioButton("20%");
        rdbtnTip20.setActionCommand("20");
        rdbtnTip20.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                applySelectedTip();
                refreshReceiptPreview();
            }
        });
        tipGroup.add(rdbtnTip20);
        pnlTips.add(rdbtnTip20);

        GridBagConstraints gbc_pnlTips = new GridBagConstraints();
        gbc_pnlTips.gridx = 1;
        gbc_pnlTips.gridy = 0;
        gbc_pnlTips.weightx = 1.0;
        gbc_pnlTips.fill = GridBagConstraints.HORIZONTAL;
        gbc_pnlTips.insets = new Insets(5, 4, 5, 4);
        pnlTipForm.add(pnlTips, gbc_pnlTips);

        JPanel pnlTipSection = new JPanel();
        pnlTipSection.setBorder(BorderFactory.createTitledBorder("ORDER TIP"));
        pnlTipSection.setLayout(new BorderLayout());
        pnlTipSection.add(pnlTipForm, BorderLayout.CENTER);
        pnlTipSection.setAlignmentX(LEFT_ALIGNMENT);
        pnlTipSection.setMaximumSize(new Dimension(Integer.MAX_VALUE, pnlTipSection.getPreferredSize().height + 40));
        pnlLeftColumn.add(pnlTipSection);

        // ----- ACTIONS section: checkout and clear-order buttons -----
        // Two full-width buttons stacked vertically, matching the wide
        // "Add drink/pastry" buttons in the forms above.
        JPanel pnlActions = new JPanel();
        pnlActions.setBorder(new EmptyBorder(8, 10, 8, 10));
        pnlActions.setLayout(new GridLayout(2, 1, 8, 8));

        JButton btnCheckout = new JButton("Checkout");
        btnCheckout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                checkout();
            }
        });
        pnlActions.add(btnCheckout);

        JButton btnClear = new JButton("Clear order");
        btnClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearOrder();
            }
        });
        pnlActions.add(btnClear);

        JPanel pnlActionsSection = new JPanel();
        pnlActionsSection.setBorder(BorderFactory.createTitledBorder("ACTIONS"));
        pnlActionsSection.setLayout(new BorderLayout());
        pnlActionsSection.add(pnlActions, BorderLayout.CENTER);
        pnlActionsSection.setAlignmentX(LEFT_ALIGNMENT);
        pnlActionsSection.setMaximumSize(new Dimension(Integer.MAX_VALUE, pnlActionsSection.getPreferredSize().height + 40));
        pnlLeftColumn.add(pnlActionsSection);

        // ----- ITEMIZED RECEIPT panel (right side): scrolling list of
        // receipt rows plus the subtotal / tip / total footer -----
        JPanel pnlReceipt = new JPanel();
        pnlReceipt.setBorder(BorderFactory.createTitledBorder("ITEMIZED RECEIPT"));
        pnlReceipt.setLayout(new BorderLayout(8, 8));

        JPanel pnlReceiptBody = new JPanel();
        pnlReceiptBody.setBorder(new EmptyBorder(8, 10, 8, 10));
        pnlReceiptBody.setLayout(new BorderLayout(8, 8));
        pnlReceipt.add(pnlReceiptBody, BorderLayout.CENTER);

        JLabel lblReceiptTitle = new JLabel("RECEIPT");
        lblReceiptTitle.setFont(lblReceiptTitle.getFont().deriveFont(Font.BOLD, 22f));
        pnlReceiptBody.add(lblReceiptTitle, BorderLayout.NORTH);

        pnlReceiptItems = new JPanel();
        pnlReceiptItems.setBorder(new EmptyBorder(4, 0, 4, 0));
        pnlReceiptItems.setLayout(new BoxLayout(pnlReceiptItems, BoxLayout.Y_AXIS));

        JLabel lblEmptyOrder = new JLabel("Your order is empty.");
        pnlReceiptItems.add(lblEmptyOrder);

        JScrollPane scrollReceiptItems = new JScrollPane(pnlReceiptItems);
        pnlReceiptBody.add(scrollReceiptItems, BorderLayout.CENTER);

        JPanel pnlTotals = new JPanel();
        pnlTotals.setLayout(new GridBagLayout());
        pnlReceiptBody.add(pnlTotals, BorderLayout.SOUTH);

        JLabel lblSubtotalCaption = new JLabel("Subtotal");
        GridBagConstraints gbc_lblSubtotalCaption = new GridBagConstraints();
        gbc_lblSubtotalCaption.gridx = 0;
        gbc_lblSubtotalCaption.gridy = 0;
        gbc_lblSubtotalCaption.weightx = 1.0;
        gbc_lblSubtotalCaption.anchor = GridBagConstraints.WEST;
        gbc_lblSubtotalCaption.insets = new Insets(7, 4, 7, 20);
        pnlTotals.add(lblSubtotalCaption, gbc_lblSubtotalCaption);

        lblSubtotal = new JLabel("$0.00");
        GridBagConstraints gbc_lblSubtotal = new GridBagConstraints();
        gbc_lblSubtotal.gridx = 1;
        gbc_lblSubtotal.gridy = 0;
        gbc_lblSubtotal.anchor = GridBagConstraints.EAST;
        gbc_lblSubtotal.insets = new Insets(7, 4, 7, 4);
        pnlTotals.add(lblSubtotal, gbc_lblSubtotal);

        JLabel lblTipCaptionTotals = new JLabel("Tip");
        GridBagConstraints gbc_lblTipCaptionTotals = new GridBagConstraints();
        gbc_lblTipCaptionTotals.gridx = 0;
        gbc_lblTipCaptionTotals.gridy = 1;
        gbc_lblTipCaptionTotals.weightx = 1.0;
        gbc_lblTipCaptionTotals.anchor = GridBagConstraints.WEST;
        gbc_lblTipCaptionTotals.insets = new Insets(7, 4, 7, 20);
        pnlTotals.add(lblTipCaptionTotals, gbc_lblTipCaptionTotals);

        lblTip = new JLabel("$0.00");
        GridBagConstraints gbc_lblTip = new GridBagConstraints();
        gbc_lblTip.gridx = 1;
        gbc_lblTip.gridy = 1;
        gbc_lblTip.anchor = GridBagConstraints.EAST;
        gbc_lblTip.insets = new Insets(7, 4, 7, 4);
        pnlTotals.add(lblTip, gbc_lblTip);

        JLabel lblTotalCaption = new JLabel("TOTAL");
        GridBagConstraints gbc_lblTotalCaption = new GridBagConstraints();
        gbc_lblTotalCaption.gridx = 0;
        gbc_lblTotalCaption.gridy = 2;
        gbc_lblTotalCaption.weightx = 1.0;
        gbc_lblTotalCaption.anchor = GridBagConstraints.WEST;
        gbc_lblTotalCaption.insets = new Insets(7, 4, 7, 20);
        pnlTotals.add(lblTotalCaption, gbc_lblTotalCaption);

        lblTotal = new JLabel("$0.00");
        lblTotal.setFont(lblTotal.getFont().deriveFont(Font.BOLD, 18f));
        GridBagConstraints gbc_lblTotal = new GridBagConstraints();
        gbc_lblTotal.gridx = 1;
        gbc_lblTotal.gridy = 2;
        gbc_lblTotal.anchor = GridBagConstraints.EAST;
        gbc_lblTotal.insets = new Insets(7, 4, 7, 4);
        pnlTotals.add(lblTotal, gbc_lblTotal);

        // Split the window: scrollable form column on the left, receipt on the right.
        JScrollPane scrollLeftColumn = new JScrollPane(pnlLeftColumn);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollLeftColumn, pnlReceipt);
        splitPane.setResizeWeight(0.52);
        splitPane.setDividerLocation(0.52);
        splitPane.setBorder(null);
        contentPane.add(splitPane, BorderLayout.CENTER);

        // Seed the initial UI state: populate drinks for the default category
        // and show the starting prices/totals.
        updateDrinksForCategory();
        updatePastryTotal();
        refreshReceiptPreview();
    }

    // ======================================================================
    // Event handlers and backend integration - Danielle Major
    // Everything below connects the WindowBuilder UI to the order backend
    // (Order, Drink, Pastry): live price updates, add/remove items, the
    // dynamic receipt panel, tips, and checkout.
    // ======================================================================

    /**
     * Repopulates the drink combo box to match the selected category
     * (coffee drinks vs. teas), disables sprinkles for tea (unchecking
     * them if needed), and refreshes the live drink price.
     */
    private void updateDrinksForCategory() {
        String category = (String) cmbCategory.getSelectedItem();
        cmbDrinkName.removeAllItems();
        if ("Coffee".equalsIgnoreCase(category)) {
            cmbDrinkName.addItem("Latte");
            cmbDrinkName.addItem("Espresso");
            cmbDrinkName.addItem("Doppio");
            cmbDrinkName.addItem("Ristretto");
            cmbDrinkName.addItem("Lungo");
        } else if ("Tea".equalsIgnoreCase(category)) {
            cmbDrinkName.addItem("Black Tea");
            cmbDrinkName.addItem("Green Tea");
            cmbDrinkName.addItem("Herbal Tea");
            cmbDrinkName.addItem("Matcha");
        }
        boolean sprinklesAllowed = !"Tea".equalsIgnoreCase(category);
        chkSprinkles.setEnabled(sprinklesAllowed);
        if (!sprinklesAllowed) chkSprinkles.setSelected(false);
        calculateDrinkPrice();
    }

    /**
     * Recalculates the live per-drink price label from the current UI
     * selections. Safely does nothing while the UI is still being built
     * or the drink list is mid-repopulation.
     */
    public void calculateDrinkPrice() {
        if (lblLiveDrinkPrice == null || cmbDrinkName.getSelectedItem() == null) return;
        Drink preview = buildCurrentDrinkFromUI();
        lblLiveDrinkPrice.setText(String.format("$%.2f each", preview.calculatePrice()));
    }

    /**
     * Builds a {@link Drink} from the current form selections, adds it to the
     * order, appends a matching row (with image) to the receipt panel,
     * refreshes the totals, and confirms with an "Item Added" popup.
     */
    public void addDrinkToOrder() {
        Drink drink = buildCurrentDrinkFromUI();
        int drinkIndex = currentOrder.getDrinkCount();
        currentOrder.addDrink(drink);
        String imageFile = getDrinkImageFile((String) cmbCategory.getSelectedItem(),
                (String) cmbDrinkName.getSelectedItem(),
                selected(temperatureGroup, "Hot"),
                (String) cmbColdFoam.getSelectedItem());
        addReceiptItem(drink.toString(), drink.getItemTotal(), imageFile, "drink", drinkIndex);
        refreshReceiptPreview();

        // Show item added pop-up
        showItemAddedPopup((String) cmbDrinkName.getSelectedItem(), imageFile);
    }

    /**
     * Builds a {@link Pastry} from the pastry form, adds it to the order,
     * appends a receipt row with the pastry's image, refreshes the totals,
     * and confirms with an "Item Added" popup.
     */
    public void addPastryToOrder() {
        String name = (String) cmbPastry.getSelectedItem();
        int quantity = (Integer) spnPastryQty.getValue();
        double unitPrice = getPastryPrice(name);
        Pastry pastry = new Pastry(name, quantity, unitPrice);
        int pastryIndex = currentOrder.getPastryCount();
        currentOrder.addPastry(pastry);
        String imageFile = getPastryImageFile(name);
        addReceiptItem(pastry.toString(), pastry.getItemTotal(), imageFile, "pastry", pastryIndex);
        refreshReceiptPreview();

        // Show item added pop-up
        showItemAddedPopup(name, imageFile);
    }

    /**
     * Shows a small confirmation dialog with the item's image and name
     * after it has been added to the order.
     *
     * @param itemName  display name of the item that was added
     * @param imageFile file name of the item's image under /images
     */
    private void showItemAddedPopup(String itemName, String imageFile) {
        JPanel popupPanel = new JPanel(new BorderLayout(10, 10));
        popupPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(100, 100));
        setScaledIcon(imageLabel, "/images/" + imageFile, itemName);
        popupPanel.add(imageLabel, BorderLayout.CENTER);

        JLabel textLabel = new JLabel("<html><center><b>" + itemName + "</b><br/>Added to order</center></html>");
        textLabel.setHorizontalAlignment(SwingConstants.CENTER);
        textLabel.setFont(new Font("Arial", Font.BOLD, 14));
        popupPanel.add(textLabel, BorderLayout.SOUTH);

        JOptionPane.showMessageDialog(this, popupPanel, "Item Added", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Runs the checkout flow: applies the selected tip, asks for a payment
     * method (cancel aborts checkout), shows the final itemized receipt from
     * {@link Order#generateReceipt()}, then resets the UI for the next order.
     */
    public void checkout() {
        applySelectedTip();

        // Show payment options dialog
        String[] paymentOptions = {"Credit Card", "Debit Card", "Cash"};
        int paymentChoice = JOptionPane.showOptionDialog(this,
                "Select payment method:",
                "Payment",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                paymentOptions,
                paymentOptions[0]);

        if (paymentChoice < 0) return; // User cancelled

        String paymentMethod = paymentOptions[paymentChoice];

        // Show receipt with payment info
        String receipt = currentOrder.generateReceipt() + "\n\nPayment Method: " + paymentMethod;
        JTextArea receiptText = new JTextArea(receipt, 18, 38);
        receiptText.setEditable(false);
        receiptText.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JOptionPane.showMessageDialog(this, new JScrollPane(receiptText),
                "Order Complete", JOptionPane.INFORMATION_MESSAGE);

        clearOrder();
    }

    /**
     * Discards the current order and resets every control to its default
     * value (small hot coffee, no extras, quantity 1, no tip), empties the
     * receipt panel, and refreshes all live price readouts.
     */
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

    /**
     * Snapshots every drink-related control into a new {@link Drink}.
     * Radio group selections are read via their action commands, with
     * sensible fallbacks if nothing is selected.
     *
     * @return a Drink reflecting the current form state
     */
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

    /**
     * Reads the selected radio button's action command from a button group.
     *
     * @param group    the button group to inspect
     * @param fallback value to return when nothing is selected
     * @return the selected action command, or the fallback
     */
    private String selected(ButtonGroup group, String fallback) {
        return group.getSelection() == null ? fallback : group.getSelection().getActionCommand();
    }

    /**
     * Selects the radio button in a group whose action command matches
     * the given value. Used when resetting the form to defaults.
     *
     * @param group the button group to search
     * @param value the action command of the radio button to select
     */
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

    /**
     * Pushes the currently selected tip percentage (from the tip radio
     * group's action command, e.g. "15" -> 0.15) into the order.
     */
    private void applySelectedTip() {
        if (tipGroup != null && tipGroup.getSelection() != null) {
            currentOrder.setTipPercentage(
                    Integer.parseInt(tipGroup.getSelection().getActionCommand()) / 100.0);
        }
    }

    /**
     * Recomputes and displays the subtotal, tip, and total labels from the
     * current order. Safe to call during construction (no-ops until the
     * labels exist).
     */
    private void refreshReceiptPreview() {
        if (currentOrder == null || lblSubtotal == null) return;
        applySelectedTip();
        lblSubtotal.setText(String.format("$%.2f", currentOrder.calculateSubtotal()));
        lblTip.setText(String.format("$%.2f", currentOrder.calculateTip()));
        lblTotal.setText(String.format("$%.2f", currentOrder.calculateTotal()));
    }

    /**
     * Appends one row to the visual receipt: item image on the left, the
     * itemized description in the middle, and the line total plus a red
     * remove ("X") button on the right. Also records metadata so the remove
     * button can later delete the right item from the order.
     *
     * @param details   multi-line item description (from the item's toString)
     * @param total     line total in dollars
     * @param imageFile image file name under /images
     * @param itemType  "drink" or "pastry" (used when removing)
     * @param index     the item's index within its list in the order
     */
    private void addReceiptItem(String details, double total, String imageFile, final String itemType, final int index) {
        // If the only thing showing is the "empty order" placeholder, clear it first.
        if (pnlReceiptItems.getComponentCount() == 1
                && pnlReceiptItems.getComponent(0) instanceof JLabel
                && "Your order is empty.".equals(((JLabel) pnlReceiptItems.getComponent(0)).getText())) {
            pnlReceiptItems.removeAll();
            receiptItemMetadata.clear();
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

        JButton removeBtn = new JButton("\u2715");
        removeBtn.setPreferredSize(new Dimension(30, 30));
        removeBtn.setFont(removeBtn.getFont().deriveFont(Font.BOLD, 18f));
        removeBtn.setForeground(java.awt.Color.RED);
        removeBtn.setFocusPainted(false);
        removeBtn.setBorderPainted(false);
        removeBtn.setContentAreaFilled(false);
        removeBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        final int itemIndex = receiptItemMetadata.size();
        removeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeItemFromOrder(itemType, index, itemIndex);
            }
        });

        JPanel rightPanel = new JPanel(new BorderLayout(5, 0));
        rightPanel.add(amount, BorderLayout.CENTER);
        rightPanel.add(removeBtn, BorderLayout.EAST);

        row.add(image, BorderLayout.WEST);
        row.add(description, BorderLayout.CENTER);
        row.add(rightPanel, BorderLayout.EAST);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        Map<String, Object> metadata = new HashMap<String, Object>();
        metadata.put("itemType", itemType);
        metadata.put("index", index);
        metadata.put("panel", row);
        receiptItemMetadata.add(metadata);

        pnlReceiptItems.add(row);
        pnlReceiptItems.add(Box.createVerticalStrut(6));
        pnlReceiptItems.revalidate();
        pnlReceiptItems.repaint();
    }

    /**
     * Removes an item from the backing order, then rebuilds the whole
     * receipt panel so the remaining rows keep correct indexes.
     *
     * @param itemType   "drink" or "pastry"
     * @param orderIndex index of the item within its list in the order
     * @param uiIndex    index of the row in the receipt metadata (unused after rebuild)
     */
    private void removeItemFromOrder(String itemType, int orderIndex, int uiIndex) {
        if ("drink".equals(itemType)) {
            currentOrder.removeDrink(orderIndex);
        } else if ("pastry".equals(itemType)) {
            currentOrder.removePastry(orderIndex);
        }

        // Rebuild receipt display
        rebuildReceipt();
    }

    /**
     * Clears the receipt panel and regenerates one row per item still on
     * the order (drinks first, then pastries). Shows the "empty order"
     * placeholder when nothing remains, then refreshes the totals.
     */
    private void rebuildReceipt() {
        pnlReceiptItems.removeAll();
        receiptItemMetadata.clear();

        int drinkIndex = 0;
        for (Drink drink : currentOrder.getDrinks()) {
            String imageFile = getDrinkImageFile(drink.getDrinkCategory(), drink.getDrinkName(), drink.getTemperature(), "None");
            addReceiptItem(drink.toString(), drink.getItemTotal(), imageFile, "drink", drinkIndex);
            drinkIndex++;
        }

        int pastryIndex = 0;
        for (Pastry pastry : currentOrder.getPastries()) {
            String imageFile = getPastryImageFile(pastry.getName());
            addReceiptItem(pastry.toString(), pastry.getItemTotal(), imageFile, "pastry", pastryIndex);
            pastryIndex++;
        }

        if (pnlReceiptItems.getComponentCount() == 0) {
            JLabel emptyLabel = new JLabel("Your order is empty.");
            emptyLabel.setAlignmentX(LEFT_ALIGNMENT);
            pnlReceiptItems.add(emptyLabel);
        }

        pnlReceiptItems.revalidate();
        pnlReceiptItems.repaint();
        refreshReceiptPreview();
    }

    /**
     * Updates the live pastry line total (unit price x quantity).
     * Safe to call during construction (no-ops until the label exists).
     */
    private void updatePastryTotal() {
        if (lblPastryTotal == null) return;
        String name = (String) cmbPastry.getSelectedItem();
        int quantity = (Integer) spnPastryQty.getValue();
        lblPastryTotal.setText(String.format("$%.2f", getPastryPrice(name) * quantity));
    }

    /**
     * Looks up the menu price for a pastry.
     *
     * @param name pastry name
     * @return $1.00 for cookies and bagels, $2.50 for everything else
     */
    private double getPastryPrice(String name) {
        if ("Chocolate Chip Cookie".equals(name) || "Bagel".equals(name)) return 1.00;
        return 2.50;
    }

    /**
     * Picks the receipt image for a drink. Cold foam takes priority, then
     * drink-specific images (matcha/teas), otherwise a hot or cold coffee
     * image based on temperature.
     *
     * @param category    "Coffee" or "Tea"
     * @param drink       drink name
     * @param temperature "Hot", "Iced", or "Frozen"
     * @param coldFoam    cold foam flavor, or "None"
     * @return image file name under /images
     */
    private String getDrinkImageFile(String category, String drink, String temperature, String coldFoam) {
        if (!"None".equalsIgnoreCase(coldFoam)) return "cold_foam.jpg";
        if ("Matcha".equalsIgnoreCase(drink)) {
            return "Hot".equalsIgnoreCase(temperature) ? "hot_matcha.jpg" : "matcha.jpg";
        }
        if ("Green Tea".equalsIgnoreCase(drink)) return "green_tea.jpg";
        if ("Black Tea".equalsIgnoreCase(drink)) return "black_tea.jpg";
        return "Hot".equalsIgnoreCase(temperature) ? "hot_coffee.jpg" : "cold_coffee.jpg";
    }

    /**
     * Picks the receipt image for a pastry (croissant image by default).
     *
     * @param pastry pastry name
     * @return image file name under /images
     */
    private String getPastryImageFile(String pastry) {
        if ("Chocolate Chip Cookie".equals(pastry)) return "choco_cookie.jpg";
        if ("Cinnamon Roll".equals(pastry)) return "cinnamon_rolls.jpg";
        if ("Bagel".equals(pastry)) return "bagel.jpg";
        return "croissamt.jpg";
    }

    /**
     * Loads an image from the classpath, scales it to 72x72, and sets it on
     * the label. If the resource is missing, shows the fallback text instead
     * so the UI still renders.
     *
     * @param label        label to receive the icon
     * @param resourcePath classpath resource path (e.g. "/images/bagel.jpg")
     * @param fallbackText text to show when the image cannot be found
     */
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
}
