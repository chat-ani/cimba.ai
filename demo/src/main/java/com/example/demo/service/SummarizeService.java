package com.example.demo.service;

import org.example.SummarizeLibrary;
import com.example.demo.model.SummaryRecord;
import com.example.demo.repository.SummaryRepository;
import com.example.demo.controller.SummaryResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SummarizeService {

    private final SummaryRepository summaryRepository;

    public SummarizeService(SummaryRepository summaryRepository) {
        this.summaryRepository = summaryRepository;
    }

    public SummaryResponse summarize(String url) {
        // Print the URL to the console
        System.out.println("Received URL: " + url);
        // Call Scala library for crawling and summarizing
        String summary = SummarizeLibrary.summarize(url);
        System.out.println("Generated summary: " + summary);

        // Save to database
        SummaryRecord recordSummary = new SummaryRecord(url, summary);
        summaryRepository.save(recordSummary);

        return new SummaryResponse(summary);
    }

    public List<SummaryRecord> getHistory() {
        return summaryRepository.findAll();
    }
}
