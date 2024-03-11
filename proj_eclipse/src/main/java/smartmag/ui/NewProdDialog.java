package smartmag.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;
import smartmag.data.Prodotto;

public class NewProdDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	public static Prodotto showNewProdDialog(JFrame parent, int initId) {
		NewProdDialog d = new NewProdDialog(parent, initId);
		d.setVisible(true); // Essendo modale la chiamata è bloccante
		return d.result;
	}

	private Prodotto result;

	/**
	 * @wbp.parser.constructor
	 */
	public NewProdDialog(JFrame parent) {
		this(parent, 0);
	}

	public NewProdDialog(JFrame parent, int initId) {
		this(parent, new ProdPanel(initId));
	}

	public NewProdDialog(JFrame parent, Prodotto p) {
		this(parent, new ProdPanel(p, true));
	}

	private NewProdDialog(JFrame parent, ProdPanel prodPanel) {
		super(parent, "Nuovo Prodotto", true); // true per modalità modale
		setMinimumSize(new Dimension(250, 300));
		setLocationRelativeTo(parent);

		// Set Icon
		ImageIcon logo = new ImageIcon("img/icon.png");
		this.setIconImage(logo.getImage());

		this.result = null;

		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new MigLayout("wrap 1", "[grow]", "[grow][]"));
		setContentPane(contentPane);

		contentPane.add(prodPanel, "grow");

		JPanel btnsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JButton btnAnnulla = new JButton("Annulla");
		btnAnnulla.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false); // Chiudi il dialog (restituisce null)
			}
		});
		JButton btnAdd = new JButton("Aggiungi");
		btnAdd.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Prodotto p = prodPanel.getProdotto();

				if (p.isValid()) {

					// Se i dati sono validi esci e restituisci il prodotto
					result = p;
					setVisible(false);
				} else {

					// Altrimenti mostra errore
					JOptionPane.showMessageDialog(NewProdDialog.this,
							"Inserire dei dati validi!", "Errore",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		btnsPanel.add(btnAnnulla);
		btnsPanel.add(btnAdd);
		contentPane.add(btnsPanel, "growx");
	}

	// TODO: check ID on change?
}