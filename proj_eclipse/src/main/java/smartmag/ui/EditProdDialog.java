package smartmag.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;
import smartmag.data.Prodotto;

public class EditProdDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private final JPanel contentPanel = new JPanel();
	private Prodotto result;

	public static Prodotto showEditProdDialog(JFrame parent, Prodotto p) {
		EditProdDialog d = new EditProdDialog(parent, p);
		d.setVisible(true); // Essendo modale la chiamata è bloccante
		return d.result;
	}

	/**
	 * Costruttore finestra di dialogo per modificare un prodotto
	 * 
	 */
	public EditProdDialog(JFrame parent, ProdPanel panel) {

		super(parent, "Modifica Prodotto", true);
		setMinimumSize(new Dimension(250, 300));
		setLocationRelativeTo(parent);

		this.result = null;

		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new MigLayout("wrap 1", "[grow]", "[grow][]"));
		setContentPane(contentPane);
		contentPane.add(panel, "grow");

		JPanel btnsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JButton btnAnnulla = new JButton("Annulla");
		btnAnnulla.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});

		JButton btnAdd = new JButton("Conferma");
		btnAdd.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Prodotto p = panel.getProdotto();

				if (p.isValid()) {
					result = p;
					setVisible(false);
				} else {

					JOptionPane.showMessageDialog(EditProdDialog.this,
							"Inserire dei dati validi!", "Errore",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		btnsPanel.add(btnAnnulla);
		btnsPanel.add(btnAdd);
		contentPane.add(btnsPanel, "growx");
	}

	public EditProdDialog(JFrame parent, Prodotto p) {
		this(parent, new ProdPanel(p, true));
	}

}
