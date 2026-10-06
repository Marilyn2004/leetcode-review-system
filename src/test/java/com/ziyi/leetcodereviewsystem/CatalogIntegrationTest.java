package com.ziyi.leetcodereviewsystem;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.MediaType;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Import(LocalTestDatabaseConfiguration.class)
@AutoConfigureMockMvc
@Transactional
class CatalogIntegrationTest {
    @Autowired CatalogLoader loader;
    @Autowired CatalogService catalog;
    @Autowired ProblemService legacy;
    @Autowired ProblemRepository problems;
    @Autowired PatternRepository patterns;
    @Autowired EntityManager em;
    @Autowired MockMvc mvc;

    @Test void loaderIsIdempotentAndPreservesLearningState() throws Exception {
        loader.load();
        Problem p = problems.findByPlatformAndExternalProblemId("LEETCODE", "1").orElseThrow();
        p.setNotes("personal notes"); p.setSolved(true); p.setTimesReviewed(4);
        p.setLastReviewed(LocalDate.now()); p.setNextReviewDate(LocalDate.now().plusDays(14));
        p.setPattern("legacy independent value");
        loader.load(); em.flush(); em.clear();
        assertEquals(12, problems.count()); assertEquals(11, patterns.count());
        p = problems.findById(p.getId()).orElseThrow();
        assertEquals(4, p.getTimesReviewed()); assertTrue(p.isSolved());
        assertEquals("personal notes", p.getNotes()); assertEquals("legacy independent value", p.getPattern());
        assertEquals(LocalDate.now().plusDays(14), p.getNextReviewDate());
        assertEquals(2, p.getPatterns().size());
    }
    @Test void catalogUsesOnlyIdentifiedProblems() throws Exception {
        problems.save(new Problem(null, "Custom", "Easy", "Anything", null, 0, false, null, null));
        loader.load(); assertEquals(12, catalog.list(null, null).size());
        assertEquals(13, legacy.getProblems(null, null, null).size());
    }
    @Test void difficultyFiltering() throws Exception {
        loader.load();
        assertEquals(6, catalog.list("Easy", null).size());
        assertEquals(5, catalog.list("Medium", null).size());
        assertEquals(1, catalog.list("Hard", null).size());
    }
    @Test void patternFilteringFetchesAllPatternsWithoutDuplicates() throws Exception {
        loader.load(); em.flush(); em.clear();
        var result = catalog.list(null, "hash-table");
        assertEquals(3, result.size());
        assertEquals(3, result.stream().map(CatalogProblemResponse::id).distinct().count());
        assertTrue(result.stream().allMatch(p -> p.patterns().size() == 2));
    }
    @Test void combinedFiltersAndEmptyResults() throws Exception {
        loader.load();
        assertEquals(List.of("3"), catalog.list("Medium", "hash-table").stream()
                .map(CatalogProblemResponse::externalProblemId).toList());
        assertTrue(catalog.list("Hard", "binary-search").isEmpty());
        assertTrue(catalog.list(null, "missing").isEmpty());
    }
    @Test void catalogApiExcludesLegacyFields() throws Exception {
        loader.load();
        mvc.perform(get("/catalog/problems").param("difficulty", "Hard").param("pattern", "sliding-window"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].externalProblemId").value("239"))
            .andExpect(jsonPath("$[0].platform").value("LEETCODE"))
            .andExpect(jsonPath("$[0].patterns.length()").value(2))
            .andExpect(jsonPath("$[0].notes").doesNotExist())
            .andExpect(jsonPath("$[0].pattern").doesNotExist())
            .andExpect(jsonPath("$[0].solved").doesNotExist())
            .andExpect(jsonPath("$[0].timesReviewed").doesNotExist())
            .andExpect(jsonPath("$[0].lastReviewed").doesNotExist())
            .andExpect(jsonPath("$[0].nextReviewDate").doesNotExist());
    }
    @Test void detailAndPatternEndpoints() throws Exception {
        loader.load(); Integer id = catalog.list(null, null).getFirst().id();
        mvc.perform(get("/catalog/problems/{id}", id)).andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id)).andExpect(jsonPath("$.url").isString());
        mvc.perform(get("/catalog/patterns")).andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(11))
            .andExpect(jsonPath("$[0].slug").value("arrays"))
            .andExpect(jsonPath("$[0].problems").doesNotExist());
    }
    @Test void missingAndLegacyOnlyDetailsReturn404() throws Exception {
        Problem p = problems.save(new Problem(null, "Custom", "Easy", "Legacy", null, 0, false, null, null));
        mvc.perform(get("/catalog/problems/{id}", p.getId())).andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404));
        mvc.perform(get("/catalog/problems/999999")).andExpect(status().isNotFound());
    }
    @Test void legacyJsonAndPutRemainIndependent() throws Exception {
        loader.load(); Problem p = problems.findByPlatformAndExternalProblemId("LEETCODE", "1").orElseThrow();
        mvc.perform(get("/problems/{id}", p.getId())).andExpect(status().isOk())
            .andExpect(jsonPath("$.pattern").value("Arrays & Hashing"))
            .andExpect(jsonPath("$.timesReviewed").value(0))
            .andExpect(jsonPath("$.patterns").doesNotExist()).andExpect(jsonPath("$.platform").doesNotExist());
        mvc.perform(put("/problems/{id}", p.getId()).contentType(MediaType.APPLICATION_JSON).content("""
            {"title":"Two Sum","difficulty":"Easy","pattern":"Unrelated","solved":false,"timesReviewed":0,
             "platform":"OTHER","externalProblemId":"999","patterns":[]}
            """)).andExpect(status().isOk());
        assertEquals("Unrelated", p.getPattern()); assertEquals("LEETCODE", p.getPlatform());
        assertEquals("1", p.getExternalProblemId()); assertEquals(2, p.getPatterns().size());
        assertEquals(1, legacy.getProblems(null, "Unrelated", null).size());
        assertEquals(3, catalog.list(null, "hash-table").size());
    }
    @Test void sharedPatternsSurviveProblemDeletion() throws Exception {
        loader.load(); Problem p = problems.findByPlatformAndExternalProblemId("LEETCODE", "1").orElseThrow();
        legacy.deleteProblemById(p.getId()); em.flush(); em.clear();
        assertEquals(11, problems.count()); assertEquals(11, patterns.count());
        assertEquals(2, catalog.list(null, "hash-table").size());
    }
    @Test void databaseRejectsDuplicatePatternSlug() {
        patterns.saveAndFlush(new Pattern("arrays", "Arrays"));
        assertThrows(RuntimeException.class, () -> patterns.saveAndFlush(new Pattern("arrays", "Other")));
    }
    @Test void databaseRejectsDuplicateAssociation() throws Exception {
        loader.load(); em.flush();
        Problem p = problems.findByPlatformAndExternalProblemId("LEETCODE", "1").orElseThrow();
        Integer patternId = p.getPatterns().iterator().next().getId();
        assertThrows(RuntimeException.class, () -> em.createNativeQuery(
            "insert into problem_patterns(problem_id, pattern_id) values (:p, :t)")
            .setParameter("p", p.getId()).setParameter("t", patternId).executeUpdate());
    }
    @Test void databaseRejectsDuplicateExternalIdentity() throws Exception {
        loader.load();
        Problem duplicate = new Problem(null, "Other", "Easy", "Legacy", null, 0, false, null, null);
        duplicate.setPlatform("LEETCODE"); duplicate.setExternalProblemId("1"); duplicate.setSlug("other");
        assertThrows(RuntimeException.class, () -> problems.saveAndFlush(duplicate));
    }
    @Test void loaderRejectsMetadataConflicts() throws Exception {
        loader.load();
        problems.findByPlatformAndExternalProblemId("LEETCODE", "1").orElseThrow().setSlug("conflict");
        assertThrows(IllegalStateException.class, () -> loader.load());
    }
    @Test void loaderRejectsSlugConflicts() {
        Problem p = new Problem(null, "Other", "Easy", "Legacy", null, 0, false, null, null);
        p.setPlatform("LEETCODE"); p.setExternalProblemId("999"); p.setSlug("two-sum"); problems.save(p);
        assertThrows(IllegalStateException.class, () -> loader.load());
    }
    @Test void loaderRejectsPatternNameConflicts() {
        patterns.save(new Pattern("hash-table", "Conflicting Name"));
        assertThrows(IllegalStateException.class, () -> loader.load());
    }
    @Test void seedsAreNotDueAndReviewSchedulingStillWorks() throws Exception {
        loader.load(); assertTrue(legacy.getDueProblems().isEmpty());
        Problem p = problems.findByPlatformAndExternalProblemId("LEETCODE", "1").orElseThrow();
        legacy.markReviewed(p.getId());
        assertEquals(1, p.getTimesReviewed()); assertEquals(LocalDate.now().plusDays(1), p.getNextReviewDate());
        assertEquals(1, legacy.getReviewHistory(p.getId()).size()); assertEquals(2, p.getPatterns().size());
    }

    @Test void suppliedExistingPostIdCannotOverwriteCatalogProblem() throws Exception {
        loader.load();
        Problem original = problems.findByPlatformAndExternalProblemId("LEETCODE", "1").orElseThrow();
        Integer id = original.getId();
        CatalogProblemResponse before = catalog.get(id);
        mvc.perform(post("/problems").contentType(MediaType.APPLICATION_JSON).content("""
            {"id":%d,"title":"New legacy problem","difficulty":"Medium","pattern":"Legacy only",
             "notes":"new notes","timesReviewed":0,"solved":false,
             "platform":null,"externalProblemId":null,"slug":null,"url":null,"patterns":[]}
            """.formatted(id)))
            .andExpect(status().isOk()).andExpect(jsonPath("$.title").value("New legacy problem"))
            .andExpect(jsonPath("$.id").value(org.hamcrest.Matchers.not(id)));
        em.flush(); em.clear();
        assertEquals(13, problems.count());
        assertEquals(before, catalog.get(id));
        Problem unchanged = problems.findById(id).orElseThrow();
        assertEquals("Two Sum", unchanged.getTitle());
        assertEquals("Arrays & Hashing", unchanged.getPattern());
        assertEquals(2, unchanged.getPatterns().size());
        Problem created = problems.findFiltered("Medium", "Legacy only", false).getFirst();
        assertNotEquals(id, created.getId()); assertNull(created.getPlatform());
        assertTrue(created.getPatterns().isEmpty());
    }

    @Test void normalLegacyPostStillCreatesNewProblem() throws Exception {
        mvc.perform(post("/problems").contentType(MediaType.APPLICATION_JSON).content("""
            {"title":"Normal create","difficulty":"Easy","pattern":"Legacy pattern",
             "notes":"notes","timesReviewed":0,"solved":false}
            """))
            .andExpect(status().isOk()).andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.title").value("Normal create"))
            .andExpect(jsonPath("$.pattern").value("Legacy pattern"))
            .andExpect(jsonPath("$.platform").doesNotExist())
            .andExpect(jsonPath("$.patterns").doesNotExist());
        em.flush(); em.clear();
        assertEquals(1, problems.count());
        Problem created = problems.findFiltered(null, "Legacy pattern", false).getFirst();
        assertNotNull(created.getId()); assertEquals("notes", created.getNotes());
        assertNull(created.getPlatform()); assertTrue(created.getPatterns().isEmpty());
    }
}
