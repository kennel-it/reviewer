package it.edu.informatica.mobilita;

/****************************************************************************
 * Esegue test in base al diagramma UML
 *
 * FIXME: controlla meglio che impostino i parametri nei costruttori
 *
 * per il toString usa la distanza di Levenstein per accettare stringhe con
 *    diversità 1 e compara eliminando spazi e facendo il tolower
 *
 * questa classe va copiata in altro progetto perché i pacchetti
 * hanno una denominazione diversa rispetto a quelli chiesti agli studenti
 ***************************************************************************/
public class TesterMobilita {

    private static final String ERRORE = "ERRORE";
    private static final String OK = "OK";

    public static void main(String[] args) {
        try {
            System.out.println(" ===== MezzoDiTrasporto ====================");
            testMezzoDiTrasporto();
        }catch(Error e){  // se la classe non implementa correttamente i metodi si cade qui
            System.out.println(e.getMessage());
        }
        try {
            System.out.println(" ===== AMuscoli ====================");
            testAMuscoli();
        }catch(Error e){
            System.out.println(e.getMessage());
        }
    }

    public static int distanzaLevenshtein(String s1, String s2) {
        s1 = s1.replaceAll(" ", "").toLowerCase();
        s2 = s2.replaceAll(" ", "").toLowerCase();

        int[][] distanza = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            distanza[i][0] = i;
        }
        for (int j = 0; j <= s2.length(); j++) {
            distanza[0][j] = j;
        }

        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    distanza[i][j] = distanza[i - 1][j - 1];
                } else {
                    distanza[i][j] = 1 + Math.min(distanza[i - 1][j], Math.min(distanza[i][j - 1], distanza[i - 1][j - 1]));
                }
            }
        }
        return distanza[s1.length()][s2.length()];
    }

    public static void brontolaStringa(String s1, String s2) {
        int dist = distanzaLevenshtein(s1, s2);
        if (dist<2) {
            System.out.println("toString "+OK);
        } else {
            System.out.println("toString "+dist+" errori → " + s1);
        }
    }


    public static void testMezzoDiTrasporto() {
        MezzoDiTrasporto m1 = new MezzoDiTrasporto();
        MezzoDiTrasporto m2 = new MezzoDiTrasporto("mdt", 100);

        brontolaStringa(m2.toString(),"mezzoditrasporto:mdt,100.0€");
        m1.setNome("pluto");
        System.out.println("get/set nome "+(m1.getNome().equals("pluto") ? OK : ERRORE ));
        m1.setCosto(2);
        System.out.println("get/set costo "+(m1.getCosto() == 2 ? OK : ERRORE ));
    }

    public static void testAMuscoli() {
        @SuppressWarnings("unused")
        AMuscoli am1 = new AMuscoli();
        AMuscoli am2 = new AMuscoli("am", 20, "bicipiti");
        brontolaStringa(am2.toString(),"veicoloamuscoli:am,20.0€(usabicipiti)");
        System.out.println("parteAlta "+(am2.parteAlta() ? OK : ERRORE ));
        am2.setMuscoliCoinvolti("xx");
        System.out.println("get/set peso "+(am2.getMuscoliCoinvolti().equals("xx") ? OK : ERRORE ));
    }

    public static void testAMotore() {
        AMotore am1 = new AMotore();
        AMotore am2 = new AMotore("am2", 1000, 10, "benzina");
        brontolaStringa(am2.toString(),
                "Mezzo motorizzato: am2 (1000.0€) che emette 10.0 db di rumore, consuma benzina");
        System.out.println("possibileCentriAbitati "+(am2.possibileCentriAbitati() ? OK : ERRORE ));
        am1.setRumorosita(88);
        System.out.println("get/set rumorosità "+(am1.getRumorosita()==88 ? OK : ERRORE ));
        am1.setCarburante("olio");
        System.out.println("get/set carburante "+(am1.getCarburante().equals("olio") ? OK : ERRORE ));
    }

}