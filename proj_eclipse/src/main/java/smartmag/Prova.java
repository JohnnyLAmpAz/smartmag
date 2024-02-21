package smartmag;

import smartmag.ui.ProdottiTableFrame;

public class Prova {

	public static void main(String[] args) {
//		new BasicWindow(getProjName(), 600, 400, false).setVisible(true);

//		ProdottoModel prodModel = new ProdottoModel();
//		Prodotto p = NewProdDialog.showNewProdDialog(null,
//				prodModel.getNextAvailableId());
//		if (p != null) {
//			prodModel.createProdotto(p);
//			JOptionPane.showMessageDialog(null,
//					"Prodotto aggiunto:\n" + p.toString());
//		}

		ProdottiTableFrame prodsFrame = new ProdottiTableFrame();
		prodsFrame.setVisible(true);
	}

	public static String getProjName() {
		return "SmartMag";
	}
}
