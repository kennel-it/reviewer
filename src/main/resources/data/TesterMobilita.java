package it.edu.iisgubbio.oggetti.mobilita;

import java.util.List;

import it.edu.iisgubbio.oggetti.ClassTestManager;

/**
 * Classe di test
 * @hidden
 */
public class TesterMobilita {

    public List<String> doTest(boolean console) {
        ClassTestManager m = new ClassTestManager("it.edu.iisgubbio.oggetti.mobilita", console);

        // --- MezzoDiTrasporto ---
        // Traino: nome="Traino", costo=1200.0
        m.classe = "MezzoDiTrasporto";
        try {
            Object mdt = m.crea("Traino", 1200.0);
            m.stampa("costruttore(\"Traino\",1200.0)", true);
            m.stampa("toString()", "Mezzo di trasporto: Traino, 1200.0€", mdt.toString(), true);
            // importoRata: 1200.0 / 4 = 300.0
            m.stampa("importoRata(4)", 300.0, m.chiama(mdt, "importoRata", 4));
            m.stampa("getNome()", "Traino", m.chiama(mdt, "getNome"));
            m.stampa("getCosto()", 1200.0, m.chiama(mdt, "getCosto"));
            m.chiama(mdt, "setNome", "Cargo");
            m.stampa("setNome(\"Cargo\") → getNome()", "Cargo", m.chiama(mdt, "getNome"));
            m.chiama(mdt, "setCosto", 900.0);
            m.stampa("setCosto(900.0) → getCosto()", 900.0, m.chiama(mdt, "getCosto"));
        } catch (Exception e) {
            m.stampa(e);
        }

        // --- AMuscoli ---
        // Kayak: nome="Kayak", costo=850.0, muscoliCoinvolti="BICIPITI" (→ stored lowercase "bicipiti")
        m.classe = "AMuscoli";
        try {
            Object am = m.crea("Kayak", 850.0, "BICIPITI");
            m.stampa("costruttore(\"Kayak\",850.0,\"BICIPITI\")", true);
            m.stampa("toString()", "Veicolo a muscoli: Kayak, 850.0€ (usa bicipiti)", am.toString(), true);
            m.stampa("parteAlta() [bicipiti]", true, m.chiama(am, "parteAlta"));
            m.stampa("getMuscoliCoinvolti()", "bicipiti", m.chiama(am, "getMuscoliCoinvolti"));
            m.chiama(am, "setMuscoliCoinvolti", "quadricipiti");
            m.stampa("setMuscoliCoinvolti(\"quadricipiti\") → parteAlta()", false, m.chiama(am, "parteAlta"));
            m.stampa("getMuscoliCoinvolti()", "quadricipiti", m.chiama(am, "getMuscoliCoinvolti"));
        } catch (Exception e) {
            m.stampa(e);
        }

        // --- AMotore ---
        // Vespa: nome="Vespa", costo=3000 (int), rumorosita=65.0, carburante="benzina"
        m.classe = "AMotore";
        try {
            Object amo = m.crea("Vespa", 3000, 65.0, "benzina");
            m.stampa("costruttore(\"Vespa\",3000,65.0,\"benzina\")", true);
            m.stampa("toString()", "Mezzo motorizzato: Vespa (3000.0€) che emette 65.0dB di rumore, consuma benzina", amo.toString(), true);
            // possibileCentriAbitati: 65 <= 68 → true
            m.stampa("possibileCentriAbitati() [65dB]", true, m.chiama(amo, "possibileCentriAbitati"));
            m.stampa("getRumorosita()", 65.0, m.chiama(amo, "getRumorosita"));
            m.stampa("getCarburante()", "benzina", m.chiama(amo, "getCarburante"));
            // setRumorosita(80.0): 80 > 68 → false
            m.chiama(amo, "setRumorosita", 80.0);
            m.stampa("setRumorosita(80.0) → possibileCentriAbitati()", false, m.chiama(amo, "possibileCentriAbitati"));
            m.chiama(amo, "setCarburante", "diesel");
            m.stampa("setCarburante(\"diesel\") → getCarburante()", "diesel", m.chiama(amo, "getCarburante"));
        } catch (Exception e) {
            m.stampa(e);
        }

        // --- Bicicletta ---
        // Leggera: costo=350.0, peso=7.5 (< 10 → leggera); numeroDiRapporti=0 (default)
        m.classe = "Bicicletta";
        try {
            Object bic = m.crea(350.0, 7.5);
            m.stampa("costruttore(350.0,7.5)", true);
            m.stampa("toString() [leggera,0 rapporti]", "Bicicletta: pesa 7.5kg (leggera) costa 350.0€ (ha 0 rapporti)", bic.toString(), true);
            m.stampa("leggera() [7.5kg]", true, m.chiama(bic, "leggera"));
            m.stampa("getPeso()", 7.5, m.chiama(bic, "getPeso"));
            m.stampa("getNumeroDiRapporti()", 0, m.chiama(bic, "getNumeroDiRapporti"));
            m.chiama(bic, "setNumeroDiRapporti", 21);
            m.stampa("setNumeroDiRapporti(21) → getNumeroDiRapporti()", 21, m.chiama(bic, "getNumeroDiRapporti"));
            m.stampa("toString() [leggera,21 rapporti]", "Bicicletta: pesa 7.5kg (leggera) costa 350.0€ (ha 21 rapporti)", bic.toString(), true);
            // Pesante: peso=14.0 >= 10
            Object bicP = m.crea(600.0, 14.0);
            m.stampa("costruttore(600.0,14.0)", true);
            m.stampa("leggera() [14.0kg]", false, m.chiama(bicP, "leggera"));
            m.stampa("toString() [pesante]", "Bicicletta: pesa 14.0kg (pesante) costa 600.0€ (ha 0 rapporti)", bicP.toString(), true);
        } catch (Exception e) {
            m.stampa(e);
        }

        // --- Skateboard ---
        // Normale: nome="Street", costo=120.0 → lunghezza=60 (default)
        m.classe = "Skateboard";
        try {
            Object sk = m.crea("Street", 120.0);
            m.stampa("costruttore(\"Street\",120.0)", true);
            m.stampa("toString() [normale,60cm]", "Skateboard normale: lungo 60cm costa 120.0€", sk.toString(), true);
            m.stampa("longboard() [60cm]", false, m.chiama(sk, "longboard"));
            m.stampa("getLunghezza()", 60, m.chiama(sk, "getLunghezza"));
            // Longboard: lunghezza=95 > 80
            Object lb = m.crea("Long", 200.0, 95);
            m.stampa("costruttore(\"Long\",200.0,95)", true);
            m.stampa("toString() [longboard,95cm]", "Skateboard longboard: lungo 95cm costa 200.0€", lb.toString(), true);
            m.stampa("longboard() [95cm]", true, m.chiama(lb, "longboard"));
            m.chiama(lb, "setLunghezza", 70);
            m.stampa("setLunghezza(70) → longboard()", false, m.chiama(lb, "longboard"));
        } catch (Exception e) {
            m.stampa(e);
        }

        // --- Barca ---
        // Otto: 8 remi, singoloRemo=false → 4 vogatori
        m.classe = "Barca";
        try {
            Object otto = m.crea("Otto", 5000.0, 8, false);
            m.stampa("costruttore(\"Otto\",5000.0,8,false)", true);
            m.stampa("toString() [due per vogatore]", "Barca: Otto con remi8 (due per vogatore)", otto.toString(), true);
            m.stampa("getNumeroRemi()", 8, m.chiama(otto, "getNumeroRemi"));
            m.stampa("getSingoloRemo()", false, m.chiama(otto, "getSingoloRemo"));
            // numeroVogatori: 8 / 2 = 4
            m.stampa("numeroVogatori() [8 remi, doppio]", 4, m.chiama(otto, "numeroVogatori"));
            // Canoa: 2 remi, singoloRemo=true → 2 vogatori
            Object canoa = m.crea("Canoa", 1200.0, 2, true);
            m.stampa("costruttore(\"Canoa\",1200.0,2,true)", true);
            m.stampa("toString() [singolo remo]", "Barca: Canoa con remi2", canoa.toString(), true);
            // numeroVogatori: singoloRemo=true → numeroDiRemi = 2
            m.stampa("numeroVogatori() [2 remi, singolo]", 2, m.chiama(canoa, "numeroVogatori"));
        } catch (Exception e) {
            m.stampa(e);
        }

        // --- Automobile ---
        // Panda: nome="Panda", costo=12000.0, porte=5; costruttore setta rumorosita=78; targa=null
        m.classe = "Automobile";
        try {
            Object auto = m.crea("Panda", 12000.0, 5);
            m.stampa("costruttore(\"Panda\",12000.0,5)", true);
            // targa non impostata → null
            m.stampa("toString() [targa null]", "Automobile: Panda [null]", auto.toString(), true);
            m.stampa("getNumeroPorte()", 5, m.chiama(auto, "getNumeroPorte"));
            // rumorosita=78 > 68 → false
            m.stampa("possibileCentriAbitati() [78dB]", false, m.chiama(auto, "possibileCentriAbitati"));
            // importoRata: 12000.0 / 12 = 1000.0
            m.stampa("importoRata(12)", 1000.0, m.chiama(auto, "importoRata", 12));
            m.chiama(auto, "setTarga", "AB123CD");
            m.stampa("setTarga(\"AB123CD\") → getTarga()", "AB123CD", m.chiama(auto, "getTarga"));
            m.stampa("toString() [targa settata]", "Automobile: Panda [AB123CD]", auto.toString(), true);
        } catch (Exception e) {
            m.stampa(e);
        }

        // --- Motocicletta ---
        // Ducati: nome="Ducati", costo=15000.0, cilindrata=900.0; numeroPosti=0 (default)
        m.classe = "Motocicletta";
        try {
            Object moto = m.crea("Ducati", 15000.0, 900.0);
            m.stampa("costruttore(\"Ducati\",15000.0,900.0)", true);
            m.stampa("toString()", "Motocicletta: Ducati cilindrata 900.0", moto.toString(), true);
            m.stampa("getCilindrata()", 900.0, m.chiama(moto, "getCilindrata"));
            // numeroPosti=0: puoTrasportare(0) → 0 > 0 → false → true
            m.stampa("puoTrasportare(0) [posti=0]", true, m.chiama(moto, "puoTrasportare", 0));
            // puoTrasportare(1) → 1 > 0 → false
            m.stampa("puoTrasportare(1) [posti=0]", false, m.chiama(moto, "puoTrasportare", 1));
            m.chiama(moto, "setNumeroPosti", 2);
            // puoTrasportare(2) → 2 > 2 → false → true
            m.stampa("setNumeroPosti(2) → puoTrasportare(2)", true, m.chiama(moto, "puoTrasportare", 2));
            // puoTrasportare(3) → 3 > 2 → false
            m.stampa("puoTrasportare(3) [posti=2]", false, m.chiama(moto, "puoTrasportare", 3));
        } catch (Exception e) {
            m.stampa(e);
        }

        // --- Motoslitta ---
        // Arctic: nome="Arctic", costo=8000.0, velocita=120.0; temperaturaMinima=0.0 (default); tempMinima(static)=-20
        m.classe = "Motoslitta";
        try {
            Object slitta = m.crea("Arctic", 8000.0, 120.0);
            m.stampa("costruttore(\"Arctic\",8000.0,120.0)", true);
            // temperaturaMinima non ancora impostata → 0.0
            m.stampa("toString() [temp=0.0°C]", "Motoslitta: Arctic, funziona fino a 0.0°C", slitta.toString(), true);
            m.chiama(slitta, "setTemperaturaMinima", -35.0);
            m.stampa("setTemperaturaMinima(-35.0) → getTemperaturaMinima()", -35.0, m.chiama(slitta, "getTemperaturaMinima"));
            m.stampa("toString() [temp=-35.0°C]", "Motoslitta: Arctic, funziona fino a -35.0°C", slitta.toString(), true);
            // puoFunzionare: confronta con tempMinima statica = -20
            // -10.0 >= -20 → true
            m.stampa("puoFunzionare(-10.0) [-10 >= -20]", true, m.chiama(slitta, "puoFunzionare", -10.0));
            // -25.0 < -20 → false
            m.stampa("puoFunzionare(-25.0) [-25 < -20]", false, m.chiama(slitta, "puoFunzionare", -25.0));
            // tempoDiPercorrenza: 240 / 120.0 = 2.0
            m.stampa("tempoDiPercorrenza(240) [240/120]", 2.0, m.chiama(slitta, "tempoDiPercorrenza", 240));
        } catch (Exception e) {
            m.stampa(e);
        }

        // --- Aereo ---
        // Boeing777: costo=0, distanzaMassima=0, reazione=false, quotaMassima=2400 (default Volante)
        m.classe = "Aereo";
        try {
            Object aereo = m.crea("Boeing777");
            m.stampa("costruttore(\"Boeing777\")", true);
            // distanzaMassima=0 (default)
            m.stampa("toString() [dist=0km]", "Aereo: Boeing777 percorrenza massima 0km", aereo.toString(), true);
            m.stampa("getDistanzaMassima()", 0, m.chiama(aereo, "getDistanzaMassima"));
            m.chiama(aereo, "setDistanzaMassima", 13000);
            m.stampa("setDistanzaMassima(13000) → getDistanzaMassima()", 13000, m.chiama(aereo, "getDistanzaMassima"));
            // puoRaggiungere: 10000 < 13000 → true
            m.stampa("puoRaggiungere(10000) [< 13000]", true, m.chiama(aereo, "puoRaggiungere", 10000));
            // 15000 < 13000 → false
            m.stampa("puoRaggiungere(15000) [> 13000]", false, m.chiama(aereo, "puoRaggiungere", 15000));
            m.stampa("getReazione() [default false]", false, m.chiama(aereo, "getReazione"));
            m.chiama(aereo, "setReazione", true);
            m.stampa("setReazione(true) → getReazione()", true, m.chiama(aereo, "getReazione"));
            // quotaMassima=2400 (da Volante) <= 3000 → no pressurizzazione
            m.stampa("getQuotaMassima() [default 2400]", 2400.0, m.chiama(aereo, "getQuotaMassima"));
            m.stampa("necessitaPressurizzazione() [2400m]", false, m.chiama(aereo, "necessitaPressurizzazione"));
            m.chiama(aereo, "setQuotaMassima", 10000.0);
            m.stampa("setQuotaMassima(10000.0) → necessitaPressurizzazione()", true, m.chiama(aereo, "necessitaPressurizzazione"));
        } catch (Exception e) {
            m.stampa(e);
        }

        // --- Deltaplano ---
        // Delta1: nome="Delta1", costo=3500.0, numeroPosti=1 → monoposto
        m.classe = "Deltaplano";
        try {
            Object delta = m.crea("Delta1", 3500.0, 1);
            m.stampa("costruttore(\"Delta1\",3500.0,1)", true);
            m.stampa("toString() [monoposto]", "Deltaplano: monoposto", delta.toString(), true);
            m.stampa("accettaPasseggero() [1 posto]", false, m.chiama(delta, "accettaPasseggero"));
            m.stampa("getNumeroDiPosti()", 1, m.chiama(delta, "getNumeroDiPosti"));
            m.chiama(delta, "setNumeroDiPosti", 2);
            m.stampa("setNumeroDiPosti(2) → accettaPasseggero()", true, m.chiama(delta, "accettaPasseggero"));
            m.stampa("toString() [biposto]", "Deltaplano: 2 posti", delta.toString(), true);
        } catch (Exception e) {
            m.stampa(e);
        }
        return m.getMessaggi();
    }

    public static void main(String[] args) {
        TesterMobilita tester = new TesterMobilita();
        tester.doTest(true);
    }
}
