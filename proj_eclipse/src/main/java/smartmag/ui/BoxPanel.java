package smartmag.ui;

import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;
import javax.swing.text.MaskFormatter;

import net.miginfocom.swing.MigLayout;
import smartmag.data.Box;
import smartmag.models.ProductModel;

public class BoxPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final NumberFormat integerFmt;
	protected static final NumberFormat indirizzoFmt;
	private static final NonNegNumVerifier numVerifier;
	private static final FormatVerifier formatVerifier;

	static {

		integerFmt = NumberFormat.getIntegerInstance();
		integerFmt.setGroupingUsed(false); // Disabilito i separatori migliaia

		indirizzoFmt = new DecimalFormat("#0.0##");
		indirizzoFmt.setMinimumFractionDigits(1);
		indirizzoFmt.setMaximumFractionDigits(3);

		formatVerifier = new FormatVerifier();
		numVerifier = new NonNegNumVerifier();
	}

	private JTextField ftfIndirizzo;
	private JFormattedTextField ftfIdProdotto;
	private JEditorPane editorDescr;
	private JFormattedTextField ftfQta;

	public BoxPanel() {
		this("", -1, 0, true, true);
	}

	public BoxPanel(Box b, boolean editableq, boolean editablep) {
		this(b.getIndirizzo(), b.getProd().getId(), b.getQuantità(), editableq,
				editablep);
	}

	public BoxPanel(String indirizzo, int idProdotto, int qta,
			boolean editableq, boolean editablep) {
		setLayout(new MigLayout("wrap 2", "[][100px:100px,grow]",
				"[][][grow][][]"));

		// ID
		JLabel lblIndirizzo = new JLabel("Indirizzo");
		add(lblIndirizzo, "alignx trailing");

		ftfIndirizzo = new JTextField();
		ftfIndirizzo.setInputVerifier(formatVerifier);
		ftfIndirizzo.setEditable(false);
		ftfIndirizzo.setText(indirizzo);
		ftfIndirizzo.setToolTipText("L'Indirizzo del box");
		lblIndirizzo.setLabelFor(ftfIndirizzo);
		add(ftfIndirizzo, "growx");

		// ID prodotto
		JLabel lblId = new JLabel("Prodotto");
		add(lblId, "alignx trailing");

		ftfIdProdotto = new JFormattedTextField(integerFmt);
		ftfIdProdotto.setInputVerifier(numVerifier);
		ftfIdProdotto.setEditable(editablep);
		ftfIdProdotto.setValue(idProdotto);
		ftfIdProdotto.setToolTipText("L'ID del prodotto");
		lblId.setLabelFor(ftfIdProdotto);
		add(ftfIdProdotto, "growx");

		// Quantita
		JLabel lblQta = new JLabel("Quantità");
		add(lblQta, "alignx trailing");

		ftfQta = new JFormattedTextField(integerFmt);
		ftfQta.setInputVerifier(numVerifier);
		ftfQta.setEditable(editableq);
		ftfQta.setValue(qta);
		ftfQta.setToolTipText("Quantità di prodotto disponibile");
		lblQta.setLabelFor(ftfQta);
		add(ftfQta, "growx");

	}

	public void addIdValueChangeListener(PropertyChangeListener listener) {
		ftfIndirizzo.addPropertyChangeListener("value", listener);
	}

	public String getDescr() {
		return editorDescr.getText();
	}

	public int getIdProdotto() {
		var val = ftfIdProdotto.getValue();
		if (val instanceof Number i)
			return i.intValue();
		return -1;
	}

	public String getIndirizzo() {
		return ftfIndirizzo.getText();
	}

	public int getQta() {
		var val = ftfQta.getValue();
		if (val instanceof Number i)
			return i.intValue();
		return -1;
	}

	public Box getBox() {
		return new Box(getIndirizzo(), getQta(),
				ProductModel.getProdottoFromId(getIdProdotto()));
	}

	private static MaskFormatter createStringFormat() {

		try {
			MaskFormatter formatter = new MaskFormatter(
					"^[A-Z]+(-([1-9]\\d*|0)){2}$");
			return formatter;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}

class NonNegNumVerifier extends InputVerifier {
	@Override
	public boolean verify(JComponent input) {
		float n;
		try {
			n = ProdPanel.floatFmt.parse(((JTextField) input).getText())
					.floatValue();
		} catch (ParseException e) {
			return false;
		}
		return n >= 0f;
	}

}

class FormatVerifier extends InputVerifier {
	/*
	 * @Override public boolean shouldYieldFocus(JComponent input) { boolean
	 * valid = verify(input); if (valid) { return true; } else {
	 * Toolkit.getDefaultToolkit().beep(); return false; } }
	 */

	@Override
	public boolean verify(JComponent input) {
		if (input instanceof JTextComponent) {
			JTextComponent textComponent = (JTextComponent) input;
			String inputText = textComponent.getText();

			String regex = "^[A-Z]+(-([1-9]\\d*|0)){2}$";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(inputText);

			return matcher.matches();
		}
		return false;
	}
}
