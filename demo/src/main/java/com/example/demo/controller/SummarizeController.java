package com.example.demo.controller;

import com.example.demo.model.SummaryRecord;
import com.example.demo.service.SummarizeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SummarizeController {

    private final SummarizeService summarizeService;

    public SummarizeController(SummarizeService summarizeService) {
        this.summarizeService = summarizeService;
    }

    @PostMapping("/summarize")
    public SummaryResponse summarize(@RequestBody SummaryRequest request) {
        return summarizeService.summarize(request.getUrl());
    }

    @GetMapping("/history")
    public List<SummaryRecord> getHistory() {
        return summarizeService.getHistory();
    }
}
