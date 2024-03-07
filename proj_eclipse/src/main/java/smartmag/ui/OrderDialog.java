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
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;

import smartmag.data.Ordine;
import smartmag.data.Prodotto;
import smartmag.data.StatoOrdine;
import smartmag.data.TipoOrdine;
import smartmag.models.OrderModel;
import smartmag.models.ProductModel;

public class OrderDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField campoID;
	private JTextField campoDataEm;
	private JTextField campoDataCo;
	private JComboBox<StatoOrdine> comboStato;
	private JComboBox<TipoOrdine> comboTipo;
	JComboBox<Prodotto> comboProdotti;

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
		setBounds(100, 100, 492, 540);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

		campoID = new JTextField();
		campoID.setBounds(169, 27, 257, 33);
		contentPanel.add(campoID);
		campoID.setColumns(10);
		campoID.setText(Integer.toString(om.getOrdine().getId()));
		campoID.setEditable(false);

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
			campoDataEm.setBounds(169, 159, 257, 33);
			contentPanel.add(campoDataEm);
			campoDataEm.setText(om.getOrdine().getDataEmissione().toString());
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
			campoDataCo.setBounds(169, 203, 257, 33);
			contentPanel.add(campoDataCo);
			campoDataCo
					.setText(om.getOrdine().getDataCompletamento().toString());
		}

		comboStato = new JComboBox<StatoOrdine>();
		comboStato.setModel(new DefaultComboBoxModel(StatoOrdine.values()));
		comboStato.setBounds(169, 71, 257, 33);
		contentPanel.add(comboStato);
		comboStato.setSelectedItem(om.getOrdine().getStato());

		comboTipo = new JComboBox<>();
		comboTipo.setModel(new DefaultComboBoxModel(TipoOrdine.values()));
		comboTipo.setBounds(169, 115, 257, 33);
		contentPanel.add(comboTipo);
		comboTipo.setSelectedItem(om.getOrdine().getTipo());

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
		JButton btnInserisciMod = new JButton("InserisciMod");
		btnInserisciMod.setBounds(47, 377, 135, 54);
		contentPanel.add(btnInserisciMod);
		btnInserisciMod.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String id1 = campoID.getText();
				int id = Integer.parseInt(campoID.getText());
				String co = campoDataCo.getText();
				String em = campoDataEm.getText();
				StatoOrdine stato = (StatoOrdine) comboStato.getSelectedItem();
				TipoOrdine tipo = (TipoOrdine) comboTipo.getSelectedItem();

				LocalDate ldco = LocalDate.parse(co);
				LocalDate ldem = LocalDate.parse(em);

				HashMap<Prodotto, Integer> prodotti = new HashMap<>();
				prodotti = om.getOrdine().getProdotti();
				System.out.println("ID: " + id1 + "co" + co + "em" + em
						+ "stato" + stato + "tipo" + tipo + "prod" + prodotti);
				Ordine o = new Ordine(id, tipo, stato, ldem, ldco, prodotti);

				try {
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

	}

	public OrderDialog() {

		setBounds(100, 100, 492, 540);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

		campoID = new JTextField();
		campoID.setBounds(169, 27, 257, 33);
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
			campoDataEm.setBounds(169, 159, 257, 33);
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
			campoDataCo.setBounds(169, 203, 257, 33);
			contentPanel.add(campoDataCo);
		}

		comboStato = new JComboBox();
		comboStato.setModel(new DefaultComboBoxModel(StatoOrdine.values()));
		comboStato.setBounds(169, 71, 257, 33);
		contentPanel.add(comboStato);

		comboTipo = new JComboBox();
		comboTipo.setModel(new DefaultComboBoxModel(TipoOrdine.values()));
		comboTipo.setBounds(169, 115, 257, 33);
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

		Vector<Prodotto> vectorProdotti = new Vector<>(
				ProductModel.getAllProduct());

		comboProdotti = new JComboBox<Prodotto>(
				vectorProdotti);

		JButton btnInserisciOrdine = new JButton("Inserisci");
		btnInserisciOrdine.setBounds(47, 377, 135, 54);
		contentPanel.add(btnInserisciOrdine);
		btnInserisciOrdine.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String id1 = campoID.getText();
				int id = Integer.parseInt(campoID.getText());
				String co = campoDataCo.getText();
				String em = campoDataEm.getText();
				StatoOrdine stato = (StatoOrdine) comboStato.getSelectedItem();
				TipoOrdine tipo = (TipoOrdine) comboTipo.getSelectedItem();
				Prodotto prod = (Prodotto) comboProdotti.getSelectedItem();
				LocalDate ldco = LocalDate.parse(co);
				LocalDate ldem = LocalDate.parse(em);

				System.out.println("ID: " + id1 + "co" + co + "em" + em
						+ "stato" + stato + "tipo" + tipo + "prod" + prod);
				HashMap<Prodotto, Integer> prodotti = new HashMap<>();
				prodotti.put(prod, 1);
				Ordine o = new Ordine(id, tipo, stato, ldem, ldco, prodotti);

				try {
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

		comboProdotti.setBounds(169, 247, 257, 33);
		contentPanel.add(comboProdotti);

		JLabel lblProdotti = new JLabel("Prodotti");
		lblProdotti.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblProdotti.setBounds(8, 247, 84, 33);
		contentPanel.add(lblProdotti);

	}

}
