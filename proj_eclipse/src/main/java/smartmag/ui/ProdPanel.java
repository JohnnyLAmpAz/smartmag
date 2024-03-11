package smartmag.ui;

import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.JEditorPane;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import net.miginfocom.swing.MigLayout;
import smartmag.data.Prodotto;

public class ProdPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	// TODO: oggetto Prodotto da syncare

	private static final NumberFormat integerFmt;
	protected static final NumberFormat floatFmt;
	private static final NonNegNumVerifier numVerifier;
	static {
		integerFmt = NumberFormat.getIntegerInstance();
		integerFmt.setGroupingUsed(false); // Disabilito i separatori migliaia

		floatFmt = new DecimalFormat("#0.0##");
		floatFmt.setMinimumFractionDigits(1);
		floatFmt.setMaximumFractionDigits(3);

		numVerifier = new NonNegNumVerifier();
	}

	private JFormattedTextField ftfId;
	private JTextField tfNome;
	private JEditorPane editorDescr;
	private JFormattedTextField ftfPeso;
	private JFormattedTextField ftfSoglia;

	public ProdPanel() {
		this(0, "", "", 0f, 0, true);
	}

	public ProdPanel(int initId) {
		this(initId, "", "", 0f, 0, true);
	}

	public ProdPanel(Prodotto p, boolean editable) {
		this(p.getId(), p.getNome(), p.getDescr(), p.getPeso(), p.getSoglia(),
				editable);
	}

	private ProdPanel(int id, String nome, String descr, float peso, int soglia,
			boolean editable) {
		setLayout(new MigLayout("wrap 2", "[][100px:100px,grow]",
				"[][][grow][][]"));

		// ID
		JLabel lblId = new JLabel("ID");
		add(lblId, "alignx trailing");

		ftfId = new JFormattedTextField(integerFmt);
		ftfId.setInputVerifier(numVerifier);
		ftfId.setEditable(editable);
		ftfId.setValue(id);
		ftfId.setToolTipText("L'ID del prodotto; numero intero non negativo.");
		lblId.setLabelFor(ftfId);
		add(ftfId, "growx");

		// Nome
		JLabel lblNome = new JLabel("Nome");
		add(lblNome, "alignx trailing");

		tfNome = new JTextField();
		tfNome.setEditable(editable);
		tfNome.setText(nome);
		tfNome.setToolTipText("Il nome del prodotto");
		lblNome.setLabelFor(tfNome);
		add(tfNome, "growx");

		// Descrizione
		// TODO: fix layout
		JLabel lblDescr = new JLabel("Descrizione");
		add(lblDescr, "alignx trailing,aligny top");

		editorDescr = new JEditorPane();
		editorDescr.setEditable(editable);
		editorDescr.setText(descr);
		editorDescr.setBorder(
				new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		editorDescr
				.setToolTipText("Testo che descrive il prodotto. (opzionale)");
		lblDescr.setLabelFor(editorDescr);
		add(editorDescr, "grow");

		// Peso
		JLabel lblPeso = new JLabel("Peso [kg]");
		add(lblPeso, "alignx trailing");

		ftfPeso = new JFormattedTextField(floatFmt);
		ftfPeso.setInputVerifier(numVerifier);
		ftfPeso.setEditable(editable);
		ftfPeso.setValue(peso);
		ftfPeso.setToolTipText("Peso del prodotto in kg.");
		lblPeso.setLabelFor(ftfPeso);
		add(ftfPeso, "growx");

		// Soglia
		JLabel lblSoglia = new JLabel("Soglia");
		add(lblSoglia, "alignx trailing");

		ftfSoglia = new JFormattedTextField(integerFmt);
		ftfSoglia.setInputVerifier(numVerifier);
		ftfSoglia.setEditable(editable);
		ftfSoglia.setValue(soglia);
		ftfSoglia.setToolTipText("Quantità minima desiderabile sotto la quale "
				+ "viene consigliato il rifornimento.");
		lblSoglia.setLabelFor(ftfSoglia);
		add(ftfSoglia, "growx");

	}

	public void addIdValueChangeListener(PropertyChangeListener listener) {
		ftfId.addPropertyChangeListener("value", listener);
	}

	public String getDescr() {
		return editorDescr.getText();
	}

	public int getId() {
		var val = ftfId.getValue();
		if (val instanceof Number i)
			return i.intValue();
		return -1;
	}

	public String getNome() {
		return tfNome.getText();
	}

	public float getPeso() {
		var val = ftfPeso.getValue();
		if (val instanceof Number f)
			return f.floatValue();
		return -1;
	}

	// TODO: ChangeEvent (uno solo) per tutti i campi così poi da valutare
	// validità

	public Prodotto getProdotto() {
		return new Prodotto(getId(), getNome(), getDescr(), getPeso(),
				getSoglia());
	}

	public int getSoglia() {
		var val = ftfSoglia.getValue();
		if (val instanceof Number i)
			return i.intValue();
		return -1;
	}
}
