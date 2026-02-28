package it.edu.iisgubbio.reviewer.reviewer.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UploadResponse(String status, String message, String jobId) {

    public static UploadResponse ok(String message, String jobId) {
        return new UploadResponse("ok", message, jobId);
    }

    public static UploadResponse error(String message) {
        return new UploadResponse("error", message, null);
    }
}
