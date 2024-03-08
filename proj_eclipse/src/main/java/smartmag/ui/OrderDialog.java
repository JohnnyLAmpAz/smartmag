package smartmag.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLIntegrityConstraintViolationException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Vector;

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
	 * Create the dialog.
	 */
	public OrderDialog(OrderModel om) {
		this.orderModel = om;

		if (om == null)
			prodotti = new HashMap<Prodotto, Integer>();
		else
			prodotti = om.getOrdine().getProdotti();

		this.modelloTabProductOrder = new ProductOrderTableModel(prodotti);

		setBounds(100, 100, 462, 711);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

		campoID = new JTextField();
		campoID.setBounds(152, 27, 274, 33);
		contentPanel.add(campoID);
		campoID.setColumns(10);

		JLabel lblNewLabel = new JLabel("ID ordine");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblNewLabel.setBounds(8, 27, 84, 33);
		contentPanel.add(lblNewLabel);
		{
			JLabel lblStato = new JLabel("Stato");
			lblStato.setFont(new Font("Tahoma", Font.PLAIN, 15));
			lblStato.setBounds(8, 71, 84, 33);
			contentPanel.add(lblStato);
		}
		{
			campoDataEm = new JTextField();
			campoDataEm.setColumns(10);
			campoDataEm.setBounds(152, 159, 274, 33);
			campoDataEm.setText(LocalDate.now().toString());
			contentPanel.add(campoDataEm);
		}
		{
			JLabel lblDataEm = new JLabel("Data emissione");
			lblDataEm.setFont(new Font("Tahoma", Font.PLAIN, 15));
			lblDataEm.setBounds(8, 159, 151, 33);
			contentPanel.add(lblDataEm);
		}
		{
			JLabel lblTipo = new JLabel("Tipo");
			lblTipo.setFont(new Font("Tahoma", Font.PLAIN, 15));
			lblTipo.setBounds(8, 115, 84, 33);
			contentPanel.add(lblTipo);
		}
		{
			JLabel lblDataCo = new JLabel("Data completamento");
			lblDataCo.setFont(new Font("Tahoma", Font.PLAIN, 15));
			lblDataCo.setBounds(8, 203, 151, 33);
			contentPanel.add(lblDataCo);
		}
		{
			campoDataCo = new JTextField();
			campoDataCo.setColumns(10);
			campoDataCo.setBounds(152, 203, 274, 33);
			contentPanel.add(campoDataCo);
		}

		comboStato = new JComboBox<StatoOrdine>();
		comboStato.setModel(
				new DefaultComboBoxModel<StatoOrdine>(StatoOrdine.values()));
		comboStato.setBounds(152, 71, 274, 33);
		contentPanel.add(comboStato);

		comboTipo = new JComboBox<TipoOrdine>();
		comboTipo.setModel(
				new DefaultComboBoxModel<TipoOrdine>(TipoOrdine.values()));
		comboTipo.setBounds(152, 115, 274, 33);
		contentPanel.add(comboTipo);

		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
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

		JButton btnInserisciOrdine = new JButton("Inserisci ordine");
		btnInserisciOrdine.setBounds(8, 576, 141, 54);
		contentPanel.add(btnInserisciOrdine);
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
				} catch (SQLIntegrityConstraintViolationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

		});

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

		comboProdotti.setBounds(152, 430, 274, 33);
		contentPanel.add(comboProdotti);

		JLabel lblProdotti = new JLabel("Prodotti");
		lblProdotti.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblProdotti.setBounds(8, 430, 84, 33);
		contentPanel.add(lblProdotti);

		JLabel lblQuantit = new JLabel("Quantità");
		lblQuantit.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblQuantit.setBounds(8, 474, 84, 33);
		contentPanel.add(lblQuantit);

		campoQuantità = new JTextField();
		campoQuantità.setColumns(10);
		campoQuantità.setBounds(152, 474, 274, 33);
		contentPanel.add(campoQuantità);

		btnInserisciProdott = new JButton("Inserisci prodotto");
		btnInserisciProdott.setBounds(152, 518, 133, 54);
		contentPanel.add(btnInserisciProdott);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(18, 257, 408, 149);
		contentPanel.add(scrollPane_1);

		modelloTabProductOrder = new ProductOrderTableModel(prodotti);

		tableProdotti = new JTable(modelloTabProductOrder);
		scrollPane_1.setViewportView(tableProdotti);

		btnEliminaProdotto = new JButton("Elimina prodotto");
		btnEliminaProdotto.setBounds(293, 518, 133, 54);
		contentPanel.add(btnEliminaProdotto);

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

	public OrderDialog() {
		this(null);
	}

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
