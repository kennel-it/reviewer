package it.edu.iisgubbio.reviewer.reviewer;

import it.edu.iisgubbio.reviewer.reviewer.JobStatus.State;
import it.edu.iisgubbio.reviewer.reviewer.dto.JobStatusOperation;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.logging.Logger;

/****************************************************************************
 * è l'oggetto che fa tutto il lavoro
 ***************************************************************************/
@Service
public class AnalysisWorker {

    private static final Logger log = Logger.getLogger(AnalysisWorker.class.getName());

    private final JobBroker jobBroker;

    public AnalysisWorker(JobBroker jobBroker) {
        this.jobBroker = jobBroker;
    }

    @Async
    public void analyze(Path workDir, String jobId) {
        jobBroker.setState(jobId, JobStatus.State.RUNNING);
        try {
            log.info("Analisi avviata per sessione '%s' in %s".formatted(jobId, workDir));
            // TODO: analisi delle classi Java — per ora placeholder in 10 passi
            for (int i = 1; i <= 10; i++) {
                Thread.sleep(2000);
                jobBroker.addOperation(jobId, new JobStatusOperation("Passo %d di 10 completato".formatted(i), Math.random()>0.5));
            }
            jobBroker.setState(jobId, JobStatus.State.DONE);
        } catch (Exception e) {
            log.severe("Analisi fallita per job %s: %s".formatted(jobId, e.getMessage()));
            jobBroker.setState(jobId, JobStatus.State.ERROR);
        }
    }
}
