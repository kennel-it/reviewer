package it.edu.iisgubbio.reviewer;

import java.io.IOException;
import java.util.HashMap;

import org.springframework.stereotype.Service;


/****************************************************************************
 * Gestisce le classi di prova presenti nella cartella "resources/data"
 ***************************************************************************/
@Service
public class TesterFinder {

    private HashMap<String, Tester> testers = new HashMap<>();

    public TesterFinder(){
        try (var in = getClass().getResourceAsStream("/data/TesterMobilita.java")) {
            if (in != null) {
                byte[] bytes = in.readAllBytes();
                testers.put("it.edu.iisgubbio.mobilita", new Tester("TesterMobilita",bytes));
            }
        } catch (IOException e) {
            // TODO: metti errore nel log
        }

        try (var in = getClass().getResourceAsStream("/data/TesterFattoria.java")) {
            if (in != null) {
                byte[] bytes = in.readAllBytes();
                testers.put("it.edu.iisgubbio.oggetti.fattoria", new Tester("TesterFattoria",bytes));
            }
        } catch (IOException e) {
            // TODO: metti errore nel log
        }
    }

    public Tester getTesterFor(String pacchetto){
        return testers.get(pacchetto);
    }

}
