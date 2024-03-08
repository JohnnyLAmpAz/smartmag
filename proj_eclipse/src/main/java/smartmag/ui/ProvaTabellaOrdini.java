package smartmag.ui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;

import smartmag.models.OrderModel;
import smartmag.models.ui.OrderTableModel;

public class ProvaTabellaOrdini extends JFrame {

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
					ProvaTabellaOrdini frame = new ProvaTabellaOrdini();
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
	public ProvaTabellaOrdini() {
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
		btnAggiungi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Richiama il Jdialog per poter inserire l'ordine
				OrderDialog dialog = new OrderDialog();
				dialog.setModal(true);
				dialog.setVisible(true);
			}

		});
		btnAggiungi.setBounds(22, 448, 163, 51);
		contentPane.add(btnAggiungi);

		JButton btnModifica = new JButton("Modifica");
		btnModifica.setBounds(238, 448, 163, 51);
		contentPane.add(btnModifica);
		btnModifica.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Richiama il JDialog per poter modificare i valori dell'ordine
				OrderDialog dialog = new OrderDialog(modello
						.getOrderModelAt(tabellaOrdini.getSelectedRow()));
				dialog.setModal(true);
				dialog.setVisible(true);
			}
		});

		JButton btnElimina = new JButton("Elimina");
		btnElimina.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				OrderModel om = modello
						.getOrderModelAt(tabellaOrdini.getSelectedRow());
				try {
					if (om != null)
						om.deleteOrdine();
				} catch (ParseException e1) {
					// TODO
				}

			}

		});
		btnElimina.setBounds(445, 448, 163, 51);
		contentPane.add(btnElimina);
	}
}
