package com.example.bio.dto;

import lombok.Data;

@Data
public class TrajectoryAnalysisResponse {
    private String migrationPattern;

    public String getBehavioralTraits() {
        return behavioralTraits;
    }

    public void setBehavioralTraits(String behavioralTraits) {
        this.behavioralTraits = behavioralTraits;
    }

    public String getConservationSuggestions() {
        return conservationSuggestions;
    }

    public void setConservationSuggestions(String conservationSuggestions) {
        this.conservationSuggestions = conservationSuggestions;
    }

    public String getHabitatPreference() {
        return habitatPreference;
    }

    public void setHabitatPreference(String habitatPreference) {
        this.habitatPreference = habitatPreference;
    }

    public String getMigrationPattern() {
        return migrationPattern;
    }

    public void setMigrationPattern(String migrationPattern) {
        this.migrationPattern = migrationPattern;
    }

    private String habitatPreference;
    private String behavioralTraits;
    private String conservationSuggestions;
}