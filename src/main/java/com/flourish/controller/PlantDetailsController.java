package com.flourish.controller;

import com.flourish.domain.PlantDetails;
import com.flourish.service.PlantDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller that exposes endpoints for accessing plant details.
 *
 * <p>This controller provides a GET endpoint that returns detailed plant data in JSON format,
 * which can be used for searching, displaying plant care details, etc.</p>
 *
 * Example: GET /api/plants/123 returns the plant details for plant ID 123.
 *
 * @author
 *   Joar Eliasson
 * @version
 *   1.1.0
 * @since
 *   2025-02-25
 */
@RestController
@RequestMapping("/api/plants")
public class PlantDetailsController {

    private final PlantDetailsService plantDetailsService;

    @Autowired
    public PlantDetailsController(PlantDetailsService plantDetailsService) {
        this.plantDetailsService = plantDetailsService;
    }

    /**
     * Retrieves plant details for a given plant ID.
     *
     * @param id the plant ID.
     * @return a ResponseEntity containing the PlantDetails if found; otherwise, a 404 status.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PlantDetails> getPlantDetails(@PathVariable Long id) {
        return plantDetailsService.getPlantDetailsById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
