package it.edu.iisgubbio.reviewer.reviewer;

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
                testers.put("TesterMobilita", new Tester("TesterMobilita.java",bytes));
            }
        } catch (IOException e) {
            // TODO: metti errore nel log
        }
    }

    // TODO: l'idea sarebbe in futuro di recuperare i tester in maniera più furba
    public Tester getTesterFor(String nome){
        return testers.get("TesterMobilita");
    }

}
