package it.edu.iisgubbio.reviewer.service;

import it.edu.iisgubbio.reviewer.model.JobStatus;
import it.edu.iisgubbio.reviewer.model.JobStatusOperation;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/****************************************************************************
 * è l'oggetto che fa tutto il lavoro
 ***************************************************************************/
@Service
public class AnalysisWorker {

    private static final Logger log = Logger.getLogger(AnalysisWorker.class.getName());

    private final JobRegistry jobBroker;

    public AnalysisWorker(JobRegistry jobBroker) {
        this.jobBroker = jobBroker;
    }

    @Async
    public void analyze(Path workDir, String jobId, String testClassName) {
        jobBroker.setState(jobId, JobStatus.State.RUNNING);
        try {
            log.info("Analisi avviata per sessione '%s' in %s".formatted(jobId, workDir));

            // --- Fase 1: compilazione dei sorgenti ---
            boolean compiled = compile(workDir, jobId);
            if (!compiled) {
                jobBroker.setState(jobId, JobStatus.State.ERROR);
                return;
            }

            // --- Fase 2: classloader sulla cartella dei .class ---
            URL[] urls = { workDir.toUri().toURL() };
            try (URLClassLoader classLoader = new URLClassLoader(urls, getClass().getClassLoader())) {

                // --- Fase 3: esecuzione di doTest(false) sul Tester ---
                Class<?> testerClass = classLoader.loadClass(testClassName);
                Object testerInstance = testerClass.getDeclaredConstructor().newInstance();
                Method doTestMethod = testerClass.getMethod("doTest", boolean.class);

                List<String> lines;
                try {
                    //noinspection unchecked
                    lines = (List<String>) doTestMethod.invoke(testerInstance, false);
                } catch (InvocationTargetException e) {
                    log.warning("doTest ha lanciato un'eccezione: " + e.getCause());
                    lines = List.of();
                }

                // ogni riga diventa una JobStatusOperation
                lines.stream()
                        .filter(line -> !line.isBlank())
                        .forEach(line -> {
                            boolean ok;
                            String text;
                            if (line.endsWith("ERRORE")) {
                                ok = false;
                                text = line.substring(0, line.length() - "ERRORE".length()).stripTrailing();
                            } else if (line.endsWith("OK")) {
                                ok = true;
                                text = line.substring(0, line.length() - "OK".length()).stripTrailing();
                            } else {
                                ok = !line.contains("ERROR") && !line.contains("errori");
                                text = line;
                            }
                            jobBroker.addOperation(jobId, new JobStatusOperation(text, ok));
                        });
            }

            jobBroker.setState(jobId, JobStatus.State.DONE);
        } catch (Exception e) {
            log.severe("Analisi fallita per job %s: %s".formatted(jobId, e.getMessage()));
            jobBroker.setState(jobId, JobStatus.State.ERROR);
        }
    }

    /**************************************************************************
     * Compila tutti i .java presenti in workDir con il compilatore del JDK.
     * Tutti i sorgenti vengono passati in un'unica CompilationTask, così il
     * compilatore risolve correttamente le dipendenze tra classi (extends,
     * implements, ecc.). I diagnostici vengono poi raggruppati per file per
     * produrre un'operazione ok/ko per ciascuno.
     * Restituisce true solo se tutti i file compilano senza errori.
     *************************************************************************/
    private boolean compile(Path workDir, String jobId) throws IOException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            // il runtime non ha il JDK (es. solo JRE) — impossibile compilare
            jobBroker.addOperation(jobId, new JobStatusOperation("Compilatore Java non disponibile nel runtime", false));
            return false;
        }

        // raccoglie tutti i file java presenti in workDir e sottocartelle?
        List<Path> sources = Files.walk(workDir)
                .filter(p -> p.toString().endsWith(".java"))
                .toList();

        if (sources.isEmpty()) {
            jobBroker.addOperation(jobId, new JobStatusOperation("Nessun file .java trovato in " + workDir, false));
            return false;
        }

        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        try (StandardJavaFileManager fm = compiler.getStandardFileManager(null, null, null)) {
            Iterable<? extends JavaFileObject> units = fm.getJavaFileObjects(sources.toArray(new Path[0]));

            // -d workDir → i .class finiscono nella stessa cartella dei sorgenti
            // unica task con tutti i sorgenti: le dipendenze tra classi vengono risolte
            JavaCompiler.CompilationTask task = compiler.getTask(
                    null, fm, diagnostics,
                    List.of("-d", workDir.toString()),
                    null, units);

            task.call();
        }

        // raggruppa gli errori per nome file sorgente,
        // la parte sotto poi fa affidamento su questi gruppi per vedere se un file ha degli errori o ne ha zero
        Map<String, List<String>> errorsByFile = new java.util.LinkedHashMap<>();
        for (Diagnostic<? extends JavaFileObject> d : diagnostics.getDiagnostics()) {
            if (d.getKind() != Diagnostic.Kind.ERROR) continue;
            String filename = d.getSource() != null ? Path.of(d.getSource().getName()).getFileName().toString() : "?";
            errorsByFile.computeIfAbsent(filename, k -> new java.util.ArrayList<>()).add(d.getMessage(null));
        }

        // un'operazione per ogni sorgente caricato
        boolean allOk = true;
        for (Path source : sources) {
            String filename = source.getFileName().toString();
            List<String> errors = errorsByFile.get(filename);
            if (errors == null) {
                jobBroker.addOperation(jobId, new JobStatusOperation("Compilato: " + filename, true));
            } else {
                log.warning("Errori in %s: %s".formatted(filename, errors));
                jobBroker.addOperation(jobId, new JobStatusOperation("Errore compilazione: " + filename, false));
                allOk = false;
            }
        }
        return allOk;
    }
}
