package com.ziyi.leetcodereviewsystem;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class CatalogLoader {
    private final ProblemRepository problems;
    private final PatternRepository patterns;
    public CatalogLoader(ProblemRepository problems, PatternRepository patterns) {
        this.problems = problems;
        this.patterns = patterns;
    }

    @Transactional
    public void load() throws IOException {
        try (var reader = new BufferedReader(new InputStreamReader(
                new ClassPathResource("catalog/problems.tsv").getInputStream(), StandardCharsets.UTF_8))) {
            Set<String> identities = new HashSet<>();
            Set<String> slugs = new HashSet<>();
            for (String line : reader.lines().skip(1).toList()) {
                String[] row = line.split("\t", -1);
                if (row.length != 8 || Arrays.stream(row).anyMatch(String::isBlank)
                        || !Set.of("Easy", "Medium", "Hard").contains(row[4])
                        || !row[1].matches("[1-9][0-9]*")
                        || !row[3].matches("[a-z0-9]+(?:-[a-z0-9]+)*")
                        || !row[5].equals("https://leetcode.com/problems/" + row[3] + "/")
                        || !row[0].equals("LEETCODE")) {
                    throw new IllegalStateException("Invalid curated catalog row");
                }
                if (!identities.add(row[0] + ":" + row[1]) || !slugs.add(row[0] + ":" + row[3])) {
                    throw new IllegalStateException("Duplicate curated catalog identity: " + row[1]);
                }
                Problem problem = problems.findByPlatformAndExternalProblemId(row[0], row[1]).orElse(null);
                var slugMatch = problems.findByPlatformAndSlug(row[0], row[3]);
                if (slugMatch.isPresent() && (problem == null || !slugMatch.get().getId().equals(problem.getId()))) {
                    throw new IllegalStateException("Catalog slug conflict: " + row[3]);
                }
                if (problem == null) {
                    problem = new Problem(null, row[2], row[4], row[6], null, 0, false, null, null);
                    problem.setPlatform(row[0]);
                    problem.setExternalProblemId(row[1]);
                    problem.setSlug(row[3]);
                    problem.setUrl(row[5]);
                } else if (!Objects.equals(problem.getTitle(), row[2]) || !Objects.equals(problem.getSlug(), row[3])
                        || !Objects.equals(problem.getDifficulty(), row[4]) || !Objects.equals(problem.getUrl(), row[5])) {
                    throw new IllegalStateException("Catalog metadata conflict: " + row[1]);
                }
                for (String value : row[7].split(";")) {
                    String[] pair = value.split(":", 2);
                    if (pair.length != 2 || !pair[0].matches("[a-z0-9]+(?:-[a-z0-9]+)*") || pair[1].isBlank()) {
                        throw new IllegalStateException("Invalid curated pattern: " + value);
                    }
                    Pattern pattern = patterns.findBySlug(pair[0]).orElseGet(() -> patterns.save(new Pattern(pair[0], pair[1])));
                    if (!pattern.getName().equals(pair[1])) {
                        throw new IllegalStateException("Pattern name conflict: " + pair[0]);
                    }
                    problem.getPatterns().add(pattern);
                }
                problems.save(problem);
            }
        }
    }
}
