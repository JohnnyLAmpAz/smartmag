package smartmag.ui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;

import smartmag.data.StatoOrdine;
import smartmag.models.OrderModel;
import smartmag.models.ui.OrderTableModel;

public class TabellaOrdini extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable tabellaOrdini;
	private OrderTableModel modello = new OrderTableModel();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TabellaOrdini frame = new TabellaOrdini();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public TabellaOrdini() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 831, 547);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 39, 797, 360);
		contentPane.add(scrollPane);

		tabellaOrdini = new JTable(modello);
		scrollPane.setViewportView(tabellaOrdini);

		JButton btnAggiungi = new JButton("Aggiungi");
		btnAggiungi
				.setBorder(BorderFactory.createLineBorder(Color.green));
		btnAggiungi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Richiama il Jdialog per poter inserire l'ordine
				OrderDialog dialog = new OrderDialog();
				dialog.setModal(true);
				dialog.setVisible(true);
			}

		});
		btnAggiungi.setBounds(10, 448, 205, 51);
		contentPane.add(btnAggiungi);

		JButton btnModifica = new JButton("Modifica");
		btnModifica.setBounds(306, 448, 205, 51);
		btnModifica
				.setBorder(BorderFactory.createLineBorder(Color.cyan));
		contentPane.add(btnModifica);

		// TODO il pulsante modifica può essere premuto solo se lordine
		// selezionato è in attesa e non ha movimentazioni generate
		btnModifica.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Richiama il JDialog per poter modificare i valori dell'ordine
				OrderModel om = OrderTableModel
						.getOrderModelAt(tabellaOrdini.getSelectedRow());

				OrderDialog dialog = new OrderDialog(om);
				if (om.getOrdine().getStato().equals(StatoOrdine.IN_ATTESA)) {
					dialog.setModal(true);
					dialog.setVisible(true);
				} else
					System.out.println("Ordine non in attesa!");
			}
		});

		JButton btnElimina = new JButton("Elimina");
		btnElimina
				.setBorder(BorderFactory.createLineBorder(Color.red));
		btnElimina.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				OrderModel om = OrderTableModel
						.getOrderModelAt(tabellaOrdini.getSelectedRow());
				try {
					if (om != null)
						om.deleteOrdine();
				} catch (ParseException e1) {
					// TODO
				}
			}

		});
		btnElimina.setBounds(602, 448, 205, 51);
		contentPane.add(btnElimina);
	}
}
