package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "summary_record")
public class SummaryRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String summary;

    public SummaryRecord() {
    }

    public SummaryRecord(String url, String summary) {
        this.url = url;
        this.summary = summary;

    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getSummary() {
        return summary;
    }
}
