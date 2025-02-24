package com.flourish.integration.plantdata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Represents the paged response from the Perenual API's species-list call.
 *
 * <p>Includes pagination information and the list of plant species data.</p>
 *
 * @see PlantListDto
 *
 * @author
 *   Joar Eliasson
 * @version
 *   1.1.0
 * @since
 *   2025-02-19
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlantListResponseDto {

    @JsonProperty("data")
    private List<PlantListDto> data;

    @JsonProperty("to")
    private Integer to;

    @JsonProperty("per_page")
    private Integer perPage;

    @JsonProperty("current_page")
    private Integer currentPage;

    @JsonProperty("from")
    private Integer from;

    @JsonProperty("last_page")
    private Integer lastPage;

    @JsonProperty("total")
    private Integer total;

    public List<PlantListDto> getData() {
        return data;
    }

    public void setData(List<PlantListDto> data) {
        this.data = data;
    }

    public Integer getTo() {
        return to;
    }

    public void setTo(Integer to) {
        this.to = to;
    }

    public Integer getPerPage() {
        return perPage;
    }

    public void setPerPage(Integer perPage) {
        this.perPage = perPage;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getFrom() {
        return from;
    }

    public void setFrom(Integer from) {
        this.from = from;
    }

    public Integer getLastPage() {
        return lastPage;
    }

    public void setLastPage(Integer lastPage) {
        this.lastPage = lastPage;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
