package smartmag.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;

import smartmag.data.StatoOrdine;
import smartmag.data.TipoOrdine;
import smartmag.models.OrderModel;
import smartmag.models.ui.OrderTableModel;

public class TabellaOrdini extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	JTable tabellaOrdini;
	OrderTableModel modello = new OrderTableModel();
	JPanel btnPanel;

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
		contentPane.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane);

		tabellaOrdini = new JTable(modello);
		scrollPane.setViewportView(tabellaOrdini);

		btnPanel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) btnPanel.getLayout();
		flowLayout.setHgap(25);
		contentPane.add(btnPanel, BorderLayout.SOUTH);

		JButton btnAggiungi = new JButton("Aggiungi");
		btnAggiungi.setBorder(BorderFactory.createLineBorder(Color.green));
		btnAggiungi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Richiama il Jdialog per poter inserire l'ordine
				OrderDialog dialog = new OrderDialog();
				dialog.setModal(true);
				dialog.setVisible(true);
			}

		});
		btnPanel.add(btnAggiungi);

		JButton btnModifica = new JButton("Modifica");
		btnModifica.setBorder(BorderFactory.createLineBorder(Color.cyan));
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
					JOptionPane.showMessageDialog(TabellaOrdini.this,
							"Ordine non in attesa!", "Errore",
							JOptionPane.ERROR_MESSAGE);
			}
		});
		btnPanel.add(btnModifica);

		// Approvazione ordine OUT: passa da IN_ATTESA a IN_SVOLGIMENTO
		JButton btnApprova = new JButton("Approva");
		btnApprova.setBorder(BorderFactory.createLineBorder(Color.ORANGE));
		btnApprova.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				OrderModel om = OrderTableModel
						.getOrderModelAt(tabellaOrdini.getSelectedRow());
				if (om == null) {
					JOptionPane.showMessageDialog(TabellaOrdini.this,
							"Nessun ordine selezionato!", "Errore",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (om.getOrdine().getTipo() != TipoOrdine.OUT) {
					JOptionPane.showMessageDialog(TabellaOrdini.this,
							"Gli ordini di rifornimento vengono approvati dal "
									+ "magazziniere qualificato "
									+ "all'arrivo della merce!",
							"Errore", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (om.getOrdine().getStato() != StatoOrdine.IN_ATTESA) {
					JOptionPane.showMessageDialog(TabellaOrdini.this,
							"Ordine non in attesa!", "Errore",
							JOptionPane.ERROR_MESSAGE);
					return;
				}

				// Se è preparabile lo approvo (passa stato e genera movims)
				if (om.isPreparabile()) {
					om.approva();
					JOptionPane.showMessageDialog(TabellaOrdini.this,
							"Ordine approvato e movimentazioni generate.",
							"Ordine in lavorazione",
							JOptionPane.INFORMATION_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(TabellaOrdini.this,
							"Ordine non preparabile!", "Errore",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
		});
		btnPanel.add(btnApprova);

		JButton btnElimina = new JButton("Elimina");
		btnElimina.setBorder(BorderFactory.createLineBorder(Color.red));
		btnElimina.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				OrderModel om = OrderTableModel
						.getOrderModelAt(tabellaOrdini.getSelectedRow());
				try {
					if (om == null) {
						JOptionPane.showMessageDialog(TabellaOrdini.this,
								"Nessun ordine selezionato!", "Errore",
								JOptionPane.ERROR_MESSAGE);
						return;
					}

					if (om.getOrdine().getStato().equals(StatoOrdine.IN_ATTESA))
						om.deleteOrdine();
					else
						JOptionPane.showMessageDialog(TabellaOrdini.this,
								"Ordine non in attesa!", "Errore",
								JOptionPane.ERROR_MESSAGE);
				} catch (ParseException e1) {
					// TODO
				}
			}

		});
		btnPanel.add(btnElimina);
	}

	public JPanel getContentPane() {
		return contentPane;
	}
}
