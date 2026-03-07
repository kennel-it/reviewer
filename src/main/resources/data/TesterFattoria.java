package it.edu.iisgubbio.oggetti.fattoria;

import java.util.List;

import it.edu.iisgubbio.oggetti.ClassTestManager;

public class TesterFattoria {

	public List<String> doTest(boolean console) {

	   	ClassTestManager m = new ClassTestManager("it.edu.iisgubbio.oggetti.fattoria", console);

        // --- Prodotto ---
        m.classe = "Prodotto";
        try {
            Object p1 = m.crea("carote", 2.5);
            m.stampa("toString()", "prodotto carote, 2.5€/kg", p1.toString(), true);
            m.stampa("calcolaPrezzo", 10.0, m.chiama(p1, "calcolaPrezzo", 4.0));

            // costruttore vuoto: campi rimangono null/0
            Object p2 = m.crea();
            m.stampa("toString()", "prodotto null, 0.0€/kg", p2.toString(), true);
        } catch (Exception e) {
            m.stampa(e);
        }

        // --- Ortaggio ---
        m.classe = "Ortaggio";
        try {
            Object o1 = m.crea("pomodoro", 2.0, true);
            m.stampa("toString()", "Ortaggio [biologico=true, nome=pomodoro, prezzoAlKg=2.0]", o1.toString(), true);
            m.stampa("calcolaPrezzo", 1.0, m.chiama(o1, "calcolaPrezzo", 0.5));

            Object o2 = m.crea("patata", 1.2, false);
            m.stampa("toString()", "Ortaggio [biologico=false, nome=patata, prezzoAlKg=1.2]", o2.toString(), true);
            m.stampa("calcolaPrezzo", 1.2, m.chiama(o2, "calcolaPrezzo", 1.0));
        } catch (Exception e) {
            m.stampa(e);
        }

        // --- Formaggio ---
        m.classe = "Formaggio";
        try {
            // costruttore con 4 parametri (dop esplicito)
            Object f1 = m.crea("Taleggio", 25.0, 1, true);
            m.stampa("toString()", "Formaggio Taleggio, prezzoAlKg=25.0, stagionato 1 mesi DOP", f1.toString(), true);
            m.stampa("calcolaPrezzo", 12.5, m.chiama(f1, "calcolaPrezzo", 0.5));

            Object f2 = m.crea("Ricotta", 4.0, 0, false);
            m.stampa("toString()", "Formaggio Ricotta, prezzoAlKg=4.0, stagionato 0 mesi", f2.toString(), true);
            m.stampa("calcolaPrezzo", 2.0, m.chiama(f2, "calcolaPrezzo", 0.5));

            // costruttore con 3 parametri (dop = false di default)
            Object f3 = m.crea("Asiago", 18.0, 6);
            m.stampa("toString()", "Formaggio Asiago, prezzoAlKg=18.0, stagionato 6 mesi", f3.toString(), true);
            m.stampa("calcolaPrezzo", 9.0, m.chiama(f3, "calcolaPrezzo", 0.5));
        } catch (Exception e) {
            m.stampa(e);
        }

        // --- Muffato (extends Formaggio, usa il toString di Formaggio) ---
        m.classe = "Muffato";
        try {
            Object mu = m.crea("Gorgonzola", 22.0, "Penicillium glaucum");
            m.stampa("toString()", "Formaggio Gorgonzola, prezzoAlKg=22.0, stagionato 0 mesi", mu.toString(), true);
            m.stampa("calcolaPrezzo", 11.0, m.chiama(mu, "calcolaPrezzo", 0.5));
        } catch (Exception e) {
            m.stampa(e);
        }

        // --- Carne ---
        m.classe = "Carne";
        try {
            Object c1 = m.crea("costarelle", 9.5, false, false);
            m.stampa("toString()", "costarelle 9.5€/Kg NON Kosher [carne rossa]", c1.toString(), true);
            m.stampa("calcolaPrezzo", 19.0, m.chiama(c1, "calcolaPrezzo", 2.0));

            Object c2 = m.crea("petto di pollo", 12.0, true, true);
            m.stampa("toString()", "petto di pollo 12.0€/Kg macellazione Kosher [carne bianca]", c2.toString(), true);
            m.stampa("calcolaPrezzo", 6.0, m.chiama(c2, "calcolaPrezzo", 0.5));
        } catch (Exception e) {
            m.stampa(e);
        }

        return m.getMessaggi();
	}

    public static void main(String[] args) {
        TesterFattoria tester = new TesterFattoria();
        tester.doTest(true);
    }

}
