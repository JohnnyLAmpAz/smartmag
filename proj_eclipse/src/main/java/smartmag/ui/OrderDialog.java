package smartmag.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLIntegrityConstraintViolationException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;

import smartmag.data.Ordine;
import smartmag.data.Prodotto;
import smartmag.data.StatoOrdine;
import smartmag.data.TipoOrdine;
import smartmag.models.OrderModel;
import smartmag.models.ProductModel;
import smartmag.ui.table_models.ProductOrderTableModel;

public class OrderDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private final JPanel contentPanel = new JPanel();
	private JComboBox<TipoOrdine> comboTipo;
	private JComboBox<Prodotto> comboProdotti;
	private JTextField campoQuantità;
	private JButton btnInserisciProdott;
	private JButton btnEliminaProdotto;
	private JButton btnInserisciOrdine;
	private final HashMap<Prodotto, Integer> prodotti;
	private ProductOrderTableModel modelloTabProductOrder;
	private JTable tableProdotti;
	private OrderModel orderModel;
	private int orderId;
	private StatoOrdine orderStatus;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		OrderDialog dialog = new OrderDialog();
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);
	}

	/**
	 * Costruttore del JDialog usato per la modifica di un ordine già esistente
	 */
	public OrderDialog(OrderModel om) {
		this.orderModel = om;

		if (om == null)
			prodotti = new HashMap<Prodotto, Integer>();
		else
			prodotti = om.getOrdine().getProdotti();

		this.modelloTabProductOrder = new ProductOrderTableModel(prodotti);

		setBounds(100, 100, 501, 570);
		setResizable(false);
		setContentPane(contentPanel);
		contentPanel.setLayout(null);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		{
			JLabel lblTipo = new JLabel("Tipo");
			lblTipo.setFont(new Font("Tahoma", Font.PLAIN, 15));
			lblTipo.setBounds(10, 11, 84, 33);
			contentPanel.add(lblTipo);
		}

		comboTipo = new JComboBox<TipoOrdine>();
		comboTipo.setModel(
				new DefaultComboBoxModel<TipoOrdine>(TipoOrdine.values()));
		comboTipo.setSelectedItem(TipoOrdine.OUT);
		comboTipo.setBounds(154, 11, 319, 33);
		contentPanel.add(comboTipo);

		Vector<Prodotto> vectorProdotti = new Vector<>(
				ProductModel.getAllProducts());

		comboProdotti = new JComboBox<Prodotto>(vectorProdotti);

		comboProdotti.setRenderer(new ListCellRenderer<Prodotto>() {

			@Override
			public Component getListCellRendererComponent(
					JList<? extends Prodotto> list, Prodotto value, int index,
					boolean isSelected, boolean cellHasFocus) {
				String s = "";
				if (value != null)
					s = "ID: " + value.getId() + " " + value.getNome();
				return new JLabel(s);
			}
		});

		comboProdotti.setBounds(154, 326, 319, 33);
		contentPanel.add(comboProdotti);

		JLabel lblProdotti = new JLabel("Prodotti");
		lblProdotti.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblProdotti.setBounds(10, 326, 84, 33);
		contentPanel.add(lblProdotti);

		JLabel lblQuantit = new JLabel("Quantità");
		lblQuantit.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblQuantit.setBounds(10, 370, 84, 33);
		contentPanel.add(lblQuantit);

		campoQuantità = new JTextField();
		campoQuantità.setColumns(10);
		campoQuantità.setText("1");
		campoQuantità.setBounds(154, 370, 319, 33);
		contentPanel.add(campoQuantità);

		btnInserisciProdott = new JButton("Inserisci prodotto");
		btnInserisciProdott.setBackground(new Color(255, 255, 255));
		btnInserisciProdott.setBounds(154, 414, 151, 41);
		contentPanel.add(btnInserisciProdott);

		modelloTabProductOrder = new ProductOrderTableModel(prodotti);

		btnEliminaProdotto = new JButton("Elimina prodotto");
		btnEliminaProdotto.setBackground(new Color(255, 255, 255));
		btnEliminaProdotto.setBounds(322, 414, 151, 41);
		contentPanel.add(btnEliminaProdotto);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 60, 463, 250);
		contentPanel.add(scrollPane);

		JPanel panel = new JPanel();
		panel.setBounds(0, 466, 485, 62);
		contentPanel.add(panel);
		panel.setLayout(null);

		btnInserisciOrdine = new JButton("Inserisci ordine");
		btnInserisciOrdine.setBackground(new Color(255, 255, 255));
		btnInserisciOrdine.setBounds(10, 11, 262, 42);
		btnInserisciOrdine
				.setBorder(BorderFactory.createLineBorder(Color.cyan));
		panel.add(btnInserisciOrdine);

		{
			JButton exitButton = new JButton("Esci");
			exitButton.setBounds(380, 11, 95, 42);
			panel.add(exitButton);
			exitButton.setActionCommand("exit");
			exitButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});

		}
		btnInserisciOrdine.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TipoOrdine tipo = (TipoOrdine) comboTipo.getSelectedItem();

				Ordine o = new Ordine(orderId, tipo, orderStatus,
						LocalDate.now(), null, prodotti);

				try {
					if (orderModel != null) {
						orderModel.updateOrdine(o);
						setVisible(false);
					} else {
						try {
							OrderModel.create(o);
							setVisible(false); // chiude il JDialog una volta
												// inserito l'ordine
						} catch (IllegalArgumentException e2) {
							JOptionPane.showMessageDialog(OrderDialog.this,
									"Inserire dati ordine validi!",
									"Ordine non valido",
									JOptionPane.ERROR_MESSAGE);
						}
					}
				} catch (SQLIntegrityConstraintViolationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

		});

		tableProdotti = new JTable(modelloTabProductOrder);
		// TODO capire come mai non viene visualizzato in WindowBuilder
		scrollPane.setViewportView(tableProdotti); // da commentare per la
		// visualizzazione in
		// WindowBuilder

		btnInserisciProdott.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Prodotto prod = (Prodotto) comboProdotti.getSelectedItem();
				prodotti.put(prod, Integer.parseInt(campoQuantità.getText()));
				modelloTabProductOrder.fireTableDataChanged();
			}
		});

		btnEliminaProdotto.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Prodotto prod = getProdAt(tableProdotti.getSelectedRow(),
						prodotti);
				prodotti.remove(prod);
				modelloTabProductOrder.fireTableDataChanged();
			}
		});

		// Modifica ordine
		if (om != null) {
			orderId = om.getOrdine().getId();
			orderStatus = om.getOrdine().getStato();
			setTitle("Ordine #%d del %s".formatted(orderId,
					om.getOrdine().getDataEmissione().toString()));

			comboTipo.setSelectedItem(om.getOrdine().getTipo());
			comboTipo.setEnabled(false);

			btnInserisciOrdine.setText("Applica modifiche");
		}

		// Nuovo ordine
		if (om == null) {
			orderId = OrderModel.getNextAvailableOrderId();
			orderStatus = StatoOrdine.IN_ATTESA;
			setTitle("Nuovo Ordine #" + Integer.toString(orderId));
		}

	}

	/**
	 * Costruttore del JDialog usato per la creazione di un nuovo ordine
	 */
	public OrderDialog() {
		this(null);
	}

	/**
	 * Ritorna il prodotto della riga selezionata dalla tabella dei prodotti
	 * relativi ad un ordine
	 * 
	 * @param index indice della riga selezionata
	 * @param prod  prodotto corrispondente alla riga selezionata
	 */
	private static Prodotto getProdAt(int index,
			HashMap<Prodotto, Integer> prod) {
		// Riga (Utente)

		int i = 0;
		for (Prodotto p : prod.keySet()) {
			if (index == i) {
				return p;
			}
			i++;
		}
		return null;
	}
}
