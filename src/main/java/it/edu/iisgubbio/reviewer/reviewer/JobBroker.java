package it.edu.iisgubbio.reviewer.reviewer;

import it.edu.iisgubbio.reviewer.reviewer.dto.JobStatusOperation;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/****************************************************************************
 * Memorizza lo stato dei job e genera nuovi id
 ***************************************************************************/
@Service // serve a Spring per poter fare injection
public class JobBroker {

    private final ConcurrentHashMap<String, JobStatus> jobs = new ConcurrentHashMap<>();

    public void register(String jobId) {
        jobs.put(jobId, new JobStatus());
    }

    public void setState(String jobId, JobStatus.State state) {
        JobStatus job = jobs.get(jobId);
        if (job != null) job.setStatus(state);
    }

    public void addOperation(String jobId, JobStatusOperation operation) {
        JobStatus job = jobs.get(jobId);
        if (job != null) job.addOperation(operation);
    }

    public Optional<JobStatus> getStatus(String jobId) {
        return Optional.ofNullable(jobs.get(jobId));
    }

    private static final Path WORK_ROOT = Path.of(System.getProperty("java.io.tmpdir"), "reviewer");

    /************************************************************************
     * Restituisce un ID disponibile basato su quello richiesto dall'utente.
     * Se {@code requestedId} è già occupato da una cartella esistente,
     * prova {@code requestedId-1}, {@code requestedId-2}, ecc.
     ***********************************************************************/
    public String generateId(String requestedId) {
        if (!Files.exists(WORK_ROOT.resolve(requestedId))) {
            return requestedId;
        }
        int suffix = 1;
        while (true) {
            String candidate = requestedId + "-" + suffix;
            if (!Files.exists(WORK_ROOT.resolve(candidate))) {
                return candidate;
            }
            suffix++;
        }
    }
}
