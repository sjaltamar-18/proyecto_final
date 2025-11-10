package com.unimag.edu.proyecto_final.api.dto;

import java.io.Serializable;
import java.util.Map;

public class ConfiDtos {
    public record ConfigCreateRequest(String key, String value) implements Serializable {
    }

    public record ConfigResponse(String key, String value) implements Serializable {
    }

    public record ConfigUpdateRequest(String value) implements Serializable {
    }
}
