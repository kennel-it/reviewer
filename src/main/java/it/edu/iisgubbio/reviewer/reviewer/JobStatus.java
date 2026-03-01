package it.edu.iisgubbio.reviewer.reviewer;

import it.edu.iisgubbio.reviewer.reviewer.dto.JobStatusOperation;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/****************************************************************************
 * Lo stato del lavoro comprende sia lo stato vero e proprio
 * che una specie di giornale delle operaiozni eseguite
 ***************************************************************************/
public class JobStatus {

    public enum State { PENDING, RUNNING, DONE, ERROR }

    private volatile State status;
    private final Instant registeredAt;
    private final List<JobStatusOperation> operations = Collections.synchronizedList(new ArrayList<>());

    public JobStatus() {
        this.status = State.PENDING;
        this.registeredAt = Instant.now();
    }

    public State getStatus() {
        return status;
    }

    public Instant getRegisteredAt() {
        return registeredAt;
    }

    public void setStatus(State status) {
        this.status = status;
    }

    public void addOperation(JobStatusOperation operation) {
        operations.add(operation);
    }

    public List<JobStatusOperation> getOperations() {
        return List.copyOf(operations);
    }

    /** Conta quante operazioni hanno esito positivo (ok == true) */
    public long getScore() {
        return operations.stream().filter(JobStatusOperation::ok).count();
    }
}
