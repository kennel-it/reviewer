package it.edu.iisgubbio.reviewer.service;

import it.edu.iisgubbio.reviewer.model.JobStatus;
import it.edu.iisgubbio.reviewer.model.JobStatusOperation;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/****************************************************************************
 * Memorizza lo stato dei job e genera nuovi id
 ***************************************************************************/
@Service // serve a Spring per poter fare injection
public class JobRegistry {

    // le due strutture qui sotto crescono senza limite,
    // qui va bene soltnato perché il server viene spento a fine lezione
    private final ConcurrentHashMap<String, JobStatus> jobs = new ConcurrentHashMap<>();
    // Tiene traccia dell'ordine di inserimento dei job
    private final LinkedList<String> jobOrder = new LinkedList<>();

    public void register(String jobId) {
        jobs.put(jobId, new JobStatus());
        synchronized (jobOrder) {
            jobOrder.add(jobId);
        }
    }

    public void setState(String jobId, JobStatus.State state) {
        JobStatus job = jobs.get(jobId);
        if (job != null) job.setStatus(state);
    }

    public void setPackageName(String jobId, String packageName) {
        JobStatus job = jobs.get(jobId);
        if (job != null) job.setPackageName(packageName);
    }

    public void addOperation(String jobId, JobStatusOperation operation) {
        JobStatus job = jobs.get(jobId);
        if (job != null) job.addOperation(operation);
    }

    public Optional<JobStatus> getStatus(String jobId) {
        return Optional.ofNullable(jobs.get(jobId));
    }

    /** Restituisce gli ultimi {@code n} job registrati, dal più recente al meno recente */
    public List<Map.Entry<String, JobStatus>> getRecentJobs(int n) {
        List<String> ids;
        synchronized (jobOrder) {
            int from = Math.max(0, jobOrder.size() - n);
            ids = new ArrayList<>(jobOrder.subList(from, jobOrder.size()));
        }
        // invertiamo per avere prima i più recenti
        List<Map.Entry<String, JobStatus>> result = new ArrayList<>();
        for (int i = ids.size() - 1; i >= 0; i--) {
            String id = ids.get(i);
            JobStatus status = jobs.get(id);
            if (status != null) result.add(Map.entry(id, status));
        }
        return result;
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
