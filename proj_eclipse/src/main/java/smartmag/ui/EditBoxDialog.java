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
import smartmag.data.Box;

public class EditBoxDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private final JPanel contentPanel = new JPanel();
	private Box result;

	public static Box showEditBoxDialog(JFrame parent, Box b) {
		EditBoxDialog d = new EditBoxDialog(parent, b);
		d.setVisible(true); // Essendo modale la chiamata è bloccante
		return d.result;
	}

	/**
	 * Crea finestra di dialogo per modificare un box
	 */
	public EditBoxDialog(JFrame parent, BoxPanel panel) {

		super(parent, "Modifica Qta", true); // true per modalità modale
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
				setVisible(false); // Chiudi il dialog (restituisce null)
			}
		});

		btnsPanel.add(btnAnnulla);

		JButton btnAdd = new JButton("Conferma");
		btnAdd.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Box b = panel.getBox();

				if (b.isValid()) {

					// Se i dati sono validi esci e restituisci il prodotto
					result = b;
					setVisible(false);
				} else {

					// Altrimenti mostra errore
					JOptionPane.showMessageDialog(EditBoxDialog.this,
							"Inserire dei dati validi!", "Errore",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		btnsPanel.add(btnAdd);
		contentPane.add(btnsPanel, "growx");
	}

	public EditBoxDialog(JFrame parent, Box b) {
		this(parent, new BoxPanel(b, true, false));
	}

}
