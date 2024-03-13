package smartmag.ui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLIntegrityConstraintViolationException;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.NumberFormatter;

import smartmag.data.Box;
import smartmag.data.Prodotto;
import smartmag.models.BoxModel;

public class AssegnaProdottoDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private JFormattedTextField qta;
	private JTextField indirizzoBox;
	private BoxModel boxModel;

	/**
	 * costruttore finestra di dialogo per assegnare un prodotto ad un box
	 * 
	 * @param parent
	 * @param p
	 */
	public AssegnaProdottoDialog(Frame parent, Prodotto p) {
		super(parent, "assegna prodotto: " + p.getId() + " ad un box", true);
		NumberFormat format = NumberFormat.getIntegerInstance();
		NumberFormatter formatter = new NumberFormatter(format);
		formatter.setValueClass(Integer.class);
		formatter.setMinimum(0);
		BoxModel.refreshDataFromDb();

		qta = new JFormattedTextField(formatter);
		qta.setColumns(10);
		indirizzoBox = new JTextField(10);

		JButton conferma = new JButton("conferma");
		JButton annulla = new JButton("annulla");

		conferma.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String idBox = indirizzoBox.getText();
				int quantita = (Integer) qta.getValue();
				if (quantita < 0) {
					JOptionPane.showMessageDialog(AssegnaProdottoDialog.this,
							"La quantità deve essere positiva.", "Errore",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (!idBox.matches("^[A-Z]+(-([1-9]\\d*|0)){2}$")) {
					JOptionPane.showMessageDialog(AssegnaProdottoDialog.this,
							"L'indirizzo deve rispettare il formato specificato, esempio: A-1-1.",
							"Errore", JOptionPane.ERROR_MESSAGE);
					return;
				}
				Box b = new Box(idBox, quantita, p);
				if (BoxModel.esistenzaBoxModel(idBox)) {
					boxModel = BoxModel.getBoxModelFromIndirizzo(idBox);
					if (boxModel.getBox().getQuantità() > 0) {
						JOptionPane.showMessageDialog(
								AssegnaProdottoDialog.this,
								"Box giá occupato da un altro prodotto",
								"Errore", JOptionPane.ERROR_MESSAGE);
						return;
					} else if (boxModel.getBox().getQuantità() == 0) {
						boxModel.cambiaProdotto(p);
						boxModel.setQuantita(quantita);
					}
				} else {
					try {
						BoxModel.createBox(b);
					} catch (SQLIntegrityConstraintViolationException
							| IllegalArgumentException e1) {
						JOptionPane.showMessageDialog(
								AssegnaProdottoDialog.this,
								"Indirizzo giá esistente", "Errore",
								JOptionPane.ERROR_MESSAGE);
					}
				}
				dispose();
			}

		});

		annulla.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		JPanel panel = new JPanel(new GridLayout(3, 2));
		panel.add(new JLabel("indirizzo Box"));
		panel.add(indirizzoBox);
		panel.add(new JLabel("quantità:"));
		panel.add(qta);
		panel.add(conferma);
		panel.add(annulla);

		setLayout(new BorderLayout());
		add(panel, BorderLayout.CENTER);

		setSize(300, 150);
		setLocationRelativeTo(parent);
	}

}
