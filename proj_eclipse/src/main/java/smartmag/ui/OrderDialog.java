package smartmag.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
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

import smartmag.data.Prodotto;
import smartmag.data.StatoOrdine;
import smartmag.data.TipoOrdine;
import smartmag.models.ProductModel;

public class OrderDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField campoID;
	private JTextField campoDataEm;
	private JTextField campoDataCo;

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

		JComboBox comboStato = new JComboBox();
		comboStato.setModel(new DefaultComboBoxModel(StatoOrdine.values()));
		comboStato.setBounds(169, 71, 257, 33);
		contentPanel.add(comboStato);

		JComboBox comboTipo = new JComboBox();
		comboTipo.setModel(new DefaultComboBoxModel(TipoOrdine.values()));
		comboTipo.setBounds(169, 115, 257, 33);
		contentPanel.add(comboTipo);

		Vector<Prodotto> vectorProdotti = new Vector<>(
				ProductModel.getAllProduct());

		JComboBox<Prodotto> comboProdotti = new JComboBox<Prodotto>(
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

		comboProdotti.setBounds(169, 247, 257, 33);
		contentPanel.add(comboProdotti);

		JLabel lblProdotti = new JLabel("Prodotti");
		lblProdotti.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblProdotti.setBounds(8, 247, 84, 33);
		contentPanel.add(lblProdotti);
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
	}
}
