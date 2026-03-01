package it.edu.iisgubbio.reviewer.reviewer.controller;

import it.edu.iisgubbio.reviewer.reviewer.JobBroker;
import it.edu.iisgubbio.reviewer.reviewer.JobStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
public class Status {

    private final JobBroker jobBroker;

    public Status(JobBroker jobBroker) {
        this.jobBroker = jobBroker;
    }

    @GetMapping("/status/{jobId}")
    public ResponseEntity<JobStatus> status(@PathVariable String jobId) {
        return jobBroker.getStatus(jobId)
                // se l'Optional non è vuoto lo mappa in una risposta OK con il corpo specificato
                .map(s -> ResponseEntity.ok(s))
                // orElse se riceve un Optional con un valore lo usa o altrimenti mette quello fornito come parametro
                .orElse(ResponseEntity.notFound().build());
    }

    /** Restituisce un riepilogo degli ultimi 20 job registrati */
    @GetMapping("/status/recent")
    public List<Map<String, Object>> recentJobs() {
        return jobBroker.getRecentJobs(15).stream()
                .map(entry -> {
                    Map<String, Object> info = new LinkedHashMap<>();
                    info.put("jobId", entry.getKey());
                    info.put("status", entry.getValue().getStatus());
                    info.put("registeredAt", entry.getValue().getRegisteredAt());
                    info.put("score", entry.getValue().getScore());
                    return info;
                })
                .toList();
    }
}
