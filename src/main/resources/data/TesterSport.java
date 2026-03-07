package it.edu.iisgubbio.oggetti.sport;

import java.util.List;

import it.edu.iisgubbio.oggetti.ClassTestManager;

/**
 * Classe di test
 * @hidden
 */
public class TesterSport {
    public List<String> doTest(boolean console) {
	    ClassTestManager m = new ClassTestManager("it.edu.iisgubbio.oggetti.sport", console);

        // --- Atleta ---
        // Luigi: eta=24, pesoKg=78.0, altezzaM=182.0, anniEsperienza=6
        m.classe = "Atleta";
        try {
            Object p1 = m.crea("Luigi", 24, 78.0, 182.0, 6);
            m.stampa("costruttore (\"Luigi\",24,78.0,182.0,6)", true);
            m.stampa("toString()", "Luigi, di 24anni (con 6 di esperienza) e 78.0kg di peso per un altezza di 182.0m", p1.toString(), true);
            // calcolaRecupero: base=4.0, bonusEsp=1.8 → 4.0-1.8=2.2
            m.stampa("calcolaRecupero(40,false)", 2.2, m.chiama(p1, "calcolaRecupero", 40, false));
            // calcolaRecupero: base=10.0, *2=20.0, bonusEsp=1.8 → 20.0-1.8=18.2
            m.stampa("calcolaRecupero(100,true)", 18.2, m.chiama(p1, "calcolaRecupero", 100, true));
            // calcolaIdoneita: 100 -10[bmi<18.5] +3[bonusEsp=min(3,10)] = 93.0
            m.stampa("calcolaIdoneita(60,false)", 93.0, m.chiama(p1, "calcolaIdoneita", 60, false));
            // calcolaIdoneita: 100 -10[bmi] -20[fumo] -5[(80-70)*0.5] +3[bonusEsp] = 68.0
            m.stampa("calcolaIdoneita(80,true)",  68.0, m.chiama(p1, "calcolaIdoneita", 80, true));
        } catch (Exception e) {
            m.stampa(e);
        }

        // --- Maratoneta ---
        // Marco: eta=35, pesoKg=70.0, altezzaM=1.80, anniEsperienza=10, asfalto, altitudine=1000
        m.classe = "Maratoneta";
        try {
            Object mar = m.crea("Marco", 35, 70.0, 1.80, 10, "asfalto", 1000);
            m.stampa("costruttore(\"Marco\",35,70.0, 1.80, 10, \"asfalto\",1000)", true);
            m.stampa("toString()", "Maratoneta{nome='Marco', superficie='asfalto', altitudine=1000m, eta=35}", mar.toString(), true);
            m.stampa("getSuperficiePrediletta()", "asfalto", m.chiama(mar, "getSuperficiePrediletta"));
            m.stampa("getAltitudineAllenamento()", 1000, m.chiama(mar, "getAltitudineAllenamento"));
            // calcolaRecupero: base=6.0, alt<1500(no), eta>30→+2.5=8.5, bonusEsp=min(3.0,5)=3.0→-3.0=5.5
            m.stampa("calcolaRecupero(60,false)", 5.5, m.chiama(mar, "calcolaRecupero", 60, false));
            // calcolaRecupero: base=12.0, *2=24.0, +2.5=26.5, bonusEsp=3.0→-3.0=23.5
            m.stampa("calcolaRecupero(120,true)", 23.5, m.chiama(mar, "calcolaRecupero", 120, true));
            // calcolaRitmo: 4.5, noCaldo, noSterrato, alt<1500(no), bmiOk, bonusEsp=min(0.25,0.3)=0.25 → 4.25
            m.stampa("calcolaRitmo(42,false)",    4.25, m.chiama(mar, "calcolaRitmo", 42, false));
        } catch (Exception e) {
            m.stampa(e);
        }

        // --- Nuotatore ---
        // Sara: eta=28, pesoKg=62.0, altezzaM=1.72, anniEsperienza=8, farfalla, vasca50m=true
        m.classe = "Nuotatore";
        try {
            Object nuo = m.crea("Sara", 28, 62.0, 1.72, 8, "farfalla", true);
            m.stampa("costruttore(\"Sara\", 28, 62.0, 1.72, 8, \"farfalla\", true)", true);
            m.stampa("toString()", "Nuotatore{nome='Sara', stile='farfalla', vasca50m=true, eta=28}", nuo.toString(), true);
            m.stampa("getStile()",  "farfalla", m.chiama(nuo, "getStile"));
            m.stampa("isVasca50m()", true, m.chiama(nuo, "isVasca50m"));
            // calcolaRecupero: base=1.0, *1.4=1.4, eta≤30, bonusEsp=min(2.4,5)=2.4 → 1.4-2.4=-1→max=1.0
            m.stampa("calcolaRecupero(10,false)", 1.0, m.chiama(nuo, "calcolaRecupero", 10, false));
            // calcolaRecupero: base=5.0, *1.4=7.0, *2=14.0, bonusEsp=2.4 → 14.0-2.4=11.6
            m.stampa("calcolaRecupero(50,true)",  11.6, m.chiama(nuo, "calcolaRecupero", 50, true));
            // calcolaTempo: vel=1.6, base=100/1.6=62.5, noCorsia, virate=(100/50)-1=1, +1*0.8=63.3
            m.stampa("calcolaTempo(100,false)",   63.3, m.chiama(nuo, "calcolaTempo", 100, false));
            // calcolaTempo: base=62.5, *1.03=64.375, +0.8=65.175
            m.stampa("calcolaTempo(100,true)",    65.175, m.chiama(nuo, "calcolaTempo", 100, true));
        } catch (Exception e) {
            m.stampa(e);
        }
        return m.getMessaggi();
    }

    public static void main(String[] args) {
        TesterSport tester = new TesterSport();
        tester.doTest(true);
    }
}
