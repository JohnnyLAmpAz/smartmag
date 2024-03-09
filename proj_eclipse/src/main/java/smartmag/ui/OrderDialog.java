package smartmag.ui;

import java.awt.BorderLayout;
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
import smartmag.models.ui.ProductOrderTableModel;

public class OrderDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private final JPanel contentPanel = new JPanel();
	private JTextField campoID;
	private JTextField campoDataEm;
	private JTextField campoDataCo;
	private JComboBox<StatoOrdine> comboStato;
	private JComboBox<TipoOrdine> comboTipo;
	private JComboBox<Prodotto> comboProdotti;
	private JTextField campoQuantità;
	private JButton btnInserisciProdott;
	private JButton btnEliminaProdotto;
	private final HashMap<Prodotto, Integer> prodotti;
	private OrderModel orderModel;
	private ProductOrderTableModel modelloTabProductOrder;
	private JTable tableProdotti;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			OrderDialog dialog = new OrderDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
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

		setBounds(100, 100, 496, 658);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

		campoID = new JTextField();
		campoID.setBounds(154, 11, 319, 33);
		contentPanel.add(campoID);
		campoID.setColumns(10);

		JLabel lblNewLabel = new JLabel("ID ordine");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblNewLabel.setBounds(10, 11, 84, 33);
		contentPanel.add(lblNewLabel);
		{
			JLabel lblStato = new JLabel("Stato");
			lblStato.setFont(new Font("Tahoma", Font.PLAIN, 15));
			lblStato.setBounds(10, 55, 84, 33);
			contentPanel.add(lblStato);
		}
		{
			campoDataEm = new JTextField();
			campoDataEm.setColumns(10);
			campoDataEm.setBounds(154, 143, 319, 33);
			campoDataEm.setText(LocalDate.now().toString());
			contentPanel.add(campoDataEm);
		}
		{
			JLabel lblDataEm = new JLabel("Data emissione");
			lblDataEm.setFont(new Font("Tahoma", Font.PLAIN, 15));
			lblDataEm.setBounds(10, 143, 151, 33);
			contentPanel.add(lblDataEm);
		}
		{
			JLabel lblTipo = new JLabel("Tipo");
			lblTipo.setFont(new Font("Tahoma", Font.PLAIN, 15));
			lblTipo.setBounds(10, 99, 84, 33);
			contentPanel.add(lblTipo);
		}
		{
			JLabel lblDataCo = new JLabel("Data completamento");
			lblDataCo.setFont(new Font("Tahoma", Font.PLAIN, 15));
			lblDataCo.setBounds(10, 187, 151, 33);
			contentPanel.add(lblDataCo);
		}
		{
			campoDataCo = new JTextField();
			campoDataCo.setColumns(10);
			campoDataCo.setBounds(154, 187, 319, 33);
			contentPanel.add(campoDataCo);
		}

		comboStato = new JComboBox<StatoOrdine>();
		comboStato.setModel(
				new DefaultComboBoxModel<StatoOrdine>(StatoOrdine.values()));
		comboStato.setBounds(154, 55, 319, 33);
		contentPanel.add(comboStato);

		comboTipo = new JComboBox<TipoOrdine>();
		comboTipo.setModel(
				new DefaultComboBoxModel<TipoOrdine>(TipoOrdine.values()));
		comboTipo.setBounds(154, 99, 319, 33);
		contentPanel.add(comboTipo);
		if (om != null) {
			campoID.setText(Integer.toString(om.getOrdine().getId()));
			campoID.setEditable(false);
			campoDataEm.setText(om.getOrdine().getDataEmissione().toString());
			LocalDate dataCompletamento = om.getOrdine().getDataCompletamento();
			if (dataCompletamento != null)
				campoDataCo.setText(dataCompletamento.toString());

			comboStato.setSelectedItem(om.getOrdine().getStato());
			comboTipo.setSelectedItem(om.getOrdine().getTipo());
		}
		if (om == null) {
			campoID.setText(
					Integer.toString(OrderModel.getNextAvailableOrderId()));
			campoID.setEditable(false);
		}

		Vector<Prodotto> vectorProdotti = new Vector<>(
				ProductModel.getAllProduct());

		comboProdotti = new JComboBox<Prodotto>(
				vectorProdotti);

		comboProdotti.setRenderer(new ListCellRenderer<Prodotto>() {

			@Override
			public Component getListCellRendererComponent(
					JList<? extends Prodotto> list, Prodotto value, int index,
					boolean isSelected, boolean cellHasFocus) {
				return new JLabel(
						"ID: " + value.getId() + " " + value.getNome());
			}
		});
		comboProdotti.addItemListener(
				e -> System.out.println(comboProdotti.getSelectedItem()));

		comboProdotti.setBounds(154, 414, 319, 33);
		contentPanel.add(comboProdotti);

		JLabel lblProdotti = new JLabel("Prodotti");
		lblProdotti.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblProdotti.setBounds(10, 414, 84, 33);
		contentPanel.add(lblProdotti);

		JLabel lblQuantit = new JLabel("Quantità");
		lblQuantit.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblQuantit.setBounds(10, 458, 84, 33);
		contentPanel.add(lblQuantit);

		campoQuantità = new JTextField();
		campoQuantità.setColumns(10);
		campoQuantità.setBounds(154, 458, 319, 33);
		contentPanel.add(campoQuantità);

		btnInserisciProdott = new JButton("Inserisci prodotto");
		btnInserisciProdott.setBackground(new Color(255, 255, 255));
		btnInserisciProdott.setBounds(154, 502, 151, 41);
		contentPanel.add(btnInserisciProdott);

		modelloTabProductOrder = new ProductOrderTableModel(prodotti);

		btnEliminaProdotto = new JButton("Elimina prodotto");
		btnEliminaProdotto.setBackground(new Color(255, 255, 255));
		btnEliminaProdotto.setBounds(322, 502, 151, 41);
		contentPanel.add(btnEliminaProdotto);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 237, 463, 156);
		contentPanel.add(scrollPane);

		JPanel panel = new JPanel();
		panel.setBounds(0, 558, 492, 58);
		contentPanel.add(panel);
		panel.setLayout(null);

		JButton btnInserisciOrdine = new JButton("Inserisci ordine");
		btnInserisciOrdine.setBackground(new Color(255, 255, 255));
		btnInserisciOrdine.setBounds(10, 11, 262, 42);
		btnInserisciOrdine
				.setBorder(BorderFactory.createLineBorder(Color.cyan));
		panel.add(btnInserisciOrdine);

		{
			JButton exitButton = new JButton("Esci");
			exitButton.setBounds(376, 11, 95, 42);
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
				String id1 = campoID.getText();
				int id = Integer.parseInt(campoID.getText());
				String co = campoDataCo.getText();
				String em = campoDataEm.getText();
				StatoOrdine stato = (StatoOrdine) comboStato.getSelectedItem();
				TipoOrdine tipo = (TipoOrdine) comboTipo.getSelectedItem();
				LocalDate ldco = null;
				LocalDate ldem = null;

				try {
					ldem = LocalDate.parse(em);
					if (co != null)
						ldco = LocalDate.parse(co);
					else
						ldco = null;
				} catch (Exception e1) {
					// TODO
				}

				System.out.println("ID: " + id1 + "co" + co + "em" + em
						+ "stato" + stato + "tipo" + tipo);

				Ordine o = new Ordine(id, tipo, stato, ldem, ldco, prodotti);

				try {
					if (orderModel != null)
						orderModel.updateOrdine(o);
					else
						OrderModel.create(o);
					dispose(); // chiude il JDialog una volta inserito l'ordine
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
				prodotti.put(prod,
						Integer.parseInt(campoQuantità.getText()));
				modelloTabProductOrder.fireTableDataChanged();
			}
		});

		btnEliminaProdotto.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Prodotto prod = getProdAt(
						tableProdotti.getSelectedRow(), prodotti);
				prodotti.remove(prod);
				modelloTabProductOrder.fireTableDataChanged();
			}
		});

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
