package it.edu.iisgubbio.reviewer.service;

import it.edu.iisgubbio.reviewer.model.Tester;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

/****************************************************************************
 * Gestisce le classi di prova presenti nella cartella "resources/data"
 ***************************************************************************/
@Service
public class TesterRegistry {


    private static final Logger log = Logger.getLogger(TesterRegistry.class.getName());
    private HashMap<String, Tester> testers = new HashMap<>();

    public TesterRegistry(){
        try (var in = getClass().getResourceAsStream("/data/TesterMobilita.java")) {
            if (in != null) {
                byte[] bytes = in.readAllBytes();
                testers.put("it.edu.iisgubbio.mobilita", new Tester("TesterMobilita",bytes));
            }
        } catch (IOException e) {
            log.severe("Impossibile caricare TesterMobilita: " + e.getMessage());
        }

        try (var in = getClass().getResourceAsStream("/data/TesterFattoria.java")) {
            if (in != null) {
                byte[] bytes = in.readAllBytes();
                testers.put("it.edu.iisgubbio.oggetti.fattoria", new Tester("TesterFattoria",bytes));
            }
        } catch (IOException e) {
            log.severe("Impossibile caricare TesterFattoria: " + e.getMessage());
        }

        try (var in = getClass().getResourceAsStream("/data/TesterSport.java")) {
            if (in != null) {
                byte[] bytes = in.readAllBytes();
                testers.put("it.edu.iisgubbio.oggetti.sport", new Tester("TesterSport",bytes));
            }
        } catch (IOException e) {
            log.severe("Impossibile caricare ResterSport: " + e.getMessage());
        }
    }

    public Tester getTesterFor(String pacchetto){
        return testers.get(pacchetto);
    }

}
