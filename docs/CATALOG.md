# Milestone 1 catalog

This branch adds a local/test catalog foundation. It has not been deployed to Railway.

## Source and permission basis

The 12 manually selected entries in `src/main/resources/catalog/problems.tsv` use metadata from
[neetcode-gh/leetcode `.problemSiteData.json`](https://github.com/neetcode-gh/leetcode/blob/main/.problemSiteData.json),
reviewed on 2026-10-06. The upstream repository explicitly distributes its software and associated
files under the [MIT license](https://github.com/neetcode-gh/leetcode/blob/main/LICENSE),
Copyright (c) 2022 neetcode-gh. That license permits use, modification and redistribution subject
to retaining the notice. The complete upstream notice is included as
`src/main/resources/catalog/NEETCODE_LICENSE.txt` and packaged with the resource.

Only titles, difficulty labels, problem link slugs, numeric identifiers (from the upstream `code`
field), and a legacy classification are retained. URLs are constructed from the slugs. Canonical
pattern assignments are independently curated for this project; they are not a claim to reproduce
LeetCode's official topic taxonomy. No problem statements, examples, solutions, videos, or source
code are bundled. The application makes no calls to LeetCode or to the upstream repository.
The MIT permission basis applies to the selected upstream metadata, not a license to LeetCode's
proprietary statements or other content. Any future source requires separate provenance/license review.

## Contents

12 problems: 6 Easy, 5 Medium, 1 Hard. Canonical patterns: Arrays, Hash Table, Two Pointers,
Sliding Window, Binary Search, BFS, DFS, Backtracking, Dynamic Programming,
Heap / Priority Queue, and Prefix Sum.

The resource is a small tab-separated file with eight columns. `patterns` contains semicolon-separated
`slug:display name` pairs. `legacyPattern` is explicitly supplied separately; it is never synchronized
with canonical patterns. To expand the catalog, add reviewed rows and update this provenance document.
There is no generic ingestion framework.

## APIs

- `GET /catalog/problems`: catalog entries, ordered by internal ID ascending.
- `GET /catalog/problems/{id}`: catalog detail by internal ID; 404 for missing or legacy-only records.
- `GET /catalog/patterns`: persisted patterns, ordered by slug.

Optional list filters use exact matches: `difficulty=Easy|Medium|Hard` and `pattern=<canonical-slug>`.
Unknown filter values return an empty list. Filters combine with AND.

```bash
curl 'http://localhost:8080/catalog/problems?difficulty=Medium&pattern=hash-table'
```

Problem responses contain `id`, `platform`, `externalProblemId`, `title`, `slug`, `difficulty`,
`url`, and `patterns` (each with `id`, `slug`, `name`). They contain no legacy learning/review state.
IDs are database-assigned, not LeetCode IDs. Identified catalog records have all four new identity/link
fields populated. Legacy unidentified rows remain nullable and are excluded from catalog reads.

## Local loading

Loading is disabled by default. Enable both the `local` profile and `catalog.seed.enabled=true`.
The startup runner refuses non-loopback datasource URLs. Use an explicit local datasource:

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://127.0.0.1:5432/leetcode_review \
./mvnw spring-boot:run -Dspring-boot.run.arguments='--spring.profiles.active=local --catalog.seed.enabled=true'
```

Configure your local database username/password through the existing datasource environment variables.
The loader runs transactionally, reuses external identities and pattern slugs, and adds missing
associations. Repeated loading produces no duplicates. Existing notes, solved state, review counters,
dates and legacy pattern text are preserved. Metadata, slug identity, or pattern-name conflicts fail
loading and roll back the batch rather than silently overwriting records. Database uniqueness provides
an additional constraint; concurrent duplicate loads may fail rather than retry.

## Compatibility and schema

Hibernate `ddl-auto=update` remains unchanged for normal local development. It adds nullable catalog
columns, `patterns`, and `problem_patterns`. Unique constraints cover platform/external ID,
platform/problem slug, pattern slug, and problem/pattern pairs. Legacy pattern strings are not backfilled.
There is no migration script or production backfill.

V1 endpoints and JSON stay unchanged. POST constructs a fresh legacy entity and ignores supplied
IDs, preventing merge into existing catalog records. V1 PUT retains catalog identities and associations but still
updates shared title/difficulty and legacy state. Catalog records therefore remain editable through V1
for compatibility; immutable catalog administration is deferred. Review sessions and scheduling remain
V1 behavior. Loading records increases V1 total/difficulty/legacy-pattern counts; untouched seeds add
no solved, reviewed, or due counts.

Do not deploy this milestone or point this branch at Railway: Hibernate may change schema at startup
regardless of whether loading is enabled. A controlled production rollout with a proper migration
strategy (for example Flyway or Liquibase) is future production-hardening work.

## Tests

Create a dedicated disposable local PostgreSQL database, owned by your local development user:

```bash
createdb -h 127.0.0.1 -p 5432 leetcode_review_test
./mvnw test
```

`LocalTestDatabaseConfiguration` constructs the datasource at localhost port 5432, database
`leetcode_review_test`, with the existing local username and an empty password. Every full Spring
test context imports it. It is not configuration-bound: external environment variables, system
properties and Spring application JSON cannot redirect the pool. Boot datasource auto-configuration
backs off before Hibernate starts. The test properties file supplies defaults, not the safety boundary.
Adjust the test-only configuration if local credentials differ. The suite uses `create-drop`; never
use a database containing valuable data. Tests cover real PostgreSQL constraints,
relationships, catalog queries/API shapes, loader idempotency/conflicts and V1 regressions. No additional
dependencies, Docker, or remote services are required.
