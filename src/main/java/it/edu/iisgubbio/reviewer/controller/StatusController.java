package it.edu.iisgubbio.reviewer.controller;

import it.edu.iisgubbio.reviewer.model.JobStatus;
import it.edu.iisgubbio.reviewer.service.JobRegistry;
import it.edu.iisgubbio.reviewer.service.TesterRegistry;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
public class StatusController {

    private final JobRegistry jobBroker;
    private final TesterRegistry testerRegistry;

    public StatusController(JobRegistry jobBroker, TesterRegistry testerRegistry) {
        this.jobBroker = jobBroker;
        this.testerRegistry = testerRegistry;
    }

    @GetMapping("/status/{jobId}")
    public ResponseEntity<JobStatus> status(@PathVariable String jobId) {
        return jobBroker.getStatus(jobId)
                // se l'Optional non è vuoto lo mappa in una risposta OK con il corpo specificato
                .map(s -> ResponseEntity.ok(s))
                // orElse se riceve un Optional con un valore lo usa o altrimenti mette quello fornito come parametro
                .orElse(ResponseEntity.notFound().build());
    }

    /** Restituisce la lista dei pacchetti che hanno un tester registrato */
    @GetMapping("/testers")
    public List<Map<String, Object>> testers() {
        return testerRegistry.getTesters().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> {
                    Map<String, Object> info = new LinkedHashMap<>();
                    info.put("packageName", e.getKey());
                    info.put("testerClass", e.getValue());
                    return info;
                })
                .toList();
    }

    /** Restituisce un riepilogo degli ultimi 20 job registrati */
    @GetMapping("/status/recent")
    public List<Map<String, Object>> recentJobs() {
        return jobBroker.getRecentJobs(20).stream()
                .map(entry -> {
                    Map<String, Object> info = new LinkedHashMap<>();
                    info.put("jobId", entry.getKey());
                    info.put("packageName", entry.getValue().getPackageName());
                    info.put("status", entry.getValue().getStatus());
                    info.put("registeredAt", entry.getValue().getRegisteredAt());
                    info.put("score", entry.getValue().getScore());
                    return info;
                })
                .toList();
    }
}
