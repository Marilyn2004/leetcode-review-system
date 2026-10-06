package com.ziyi.leetcodereviewsystem;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/catalog")
public class CatalogController {
    private final CatalogService service;
    public CatalogController(CatalogService service) { this.service = service; }
    @GetMapping("/problems")
    public List<CatalogProblemResponse> list(@RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String pattern) { return service.list(difficulty, pattern); }
    @GetMapping("/problems/{id}")
    public CatalogProblemResponse get(@PathVariable Integer id) { return service.get(id); }
    @GetMapping("/patterns")
    public List<CatalogPatternResponse> patterns() { return service.patterns(); }
}
