package it.edu.iisgubbio.reviewer.service;

import it.edu.iisgubbio.reviewer.model.Tester;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/****************************************************************************
 * Gestisce le classi di prova presenti nella cartella "resources/data"
 ***************************************************************************/
@Service
public class TesterRegistry {

    private static final Logger log = Logger.getLogger(TesterRegistry.class.getName());
    private final HashMap<String, Tester> testers = new HashMap<>();

    private Tester manager;

    public TesterRegistry() {
        var resolver = new PathMatchingResourcePatternResolver();
        try {
            Resource[] resources = resolver.getResources("classpath:/data/*.java");
            for (Resource res : resources) {
                String filename = res.getFilename();
                if (filename != null && filename.indexOf("Tester") > -1) {
                    String testerName = filename.replace(".java", "");
                    byte[] bytes = res.getInputStream().readAllBytes();
                    String pacchetto = leggiPacchetto(bytes);
                    if (pacchetto != null) {
                        testers.put(pacchetto, new Tester(testerName, bytes));
                        log.info("Caricato tester: " + testerName + " per pacchetto " + pacchetto);
                    } else {
                        log.warning("Pacchetto non trovato in " + filename + ", file ignorato");
                    }
                }else{
                    log.info(filename + " non è un tester, file ignorato");
                }
            }
        } catch (IOException e) {
            log.severe("Errore durante la scansione di resources/data: " + e.getMessage());
        }
        try (var in = getClass().getResourceAsStream("/data/ClassTestManager.java")) {
            if (in != null) {
                byte[] bytes = in.readAllBytes();
                manager = new Tester("ClassTestManager", bytes);
                log.info("ClassTestManager caricato con successo");
            }
        } catch (IOException e) {
            log.severe("Impossibile caricare ClassTestManager: " + e.getMessage());
        }
    }

    // Legge la dichiarazione "package ..." dal sorgente Java
    private String leggiPacchetto(byte[] bytes) {
        for (String riga : new String(bytes).lines().toList()) {
            String trimmed = riga.trim();
            if (trimmed.startsWith("package ") && trimmed.endsWith(";")) {
                return trimmed.substring(8, trimmed.length() - 1).trim();
            }
        }
        return null;
    }

    public Tester getTesterFor(String pacchetto) {
        return testers.get(pacchetto);
    }

    public Tester getManager() {
        return manager;
    }

    /** Restituisce una mappa pacchetto → nome classe tester */
    public Map<String, String> getTesters() {
        var result = new HashMap<String, String>();
        testers.forEach((pkg, tester) -> result.put(pkg, tester.nomeClasse()));
        return result;
    }

}
