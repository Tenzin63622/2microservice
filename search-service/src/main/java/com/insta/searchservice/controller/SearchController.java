package com.insta.searchservice.controller;
import com.insta.searchservice.dto.SearchResponse;
import com.insta.searchservice.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/search") @RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;
    @GetMapping
    public ResponseEntity<SearchResponse> search(@RequestParam String q) {
        return ResponseEntity.ok(searchService.search(q));
    }
}
