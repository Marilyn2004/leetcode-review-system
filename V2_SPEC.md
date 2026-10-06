# LeetCode Coach V2 — Product & Engineering Specification

## 1. Product Vision

LeetCode Coach is not intended to be only a CRUD application or a simple spaced-repetition tracker.

V2 evolves the project into a personalized algorithm-practice coach that helps users answer three questions:

1. What should I practice today?
2. Why should I practice these problems?
3. What should I review next based on my actual learning history?

The system should gradually build a model of the user's strengths, weaknesses, practice history, and learning state, then use that information to generate useful daily practice recommendations.

The recommendation engine should remain deterministic and explainable at its core. AI/LLM features may later improve explanations and coaching interactions, but the LLM must not become the source of truth for scheduling or learning state.


---

# 2. Current V1 Baseline

The existing application is already a Spring Boot application.

Current stack includes:

- Java 21
- Spring Boot
- Spring MVC
- Spring Data JPA
- PostgreSQL
- Maven
- REST API
- Railway deployment
- Existing automated tests

V2 should evolve the existing application incrementally.

Do NOT rewrite the project from scratch.


---

# 3. V2 Core Domain Model

V2 separates four concepts that must not be conflated:

## 3.1 Problem

A canonical algorithm problem in the system catalog.

Example:

- LeetCode 1
- Two Sum
- EASY
- Arrays
- Hash Table

A Problem describes the problem itself.

It does NOT represent the user's learning state.


## 3.2 Pattern

An algorithmic technique or problem-solving pattern.

Examples:

- Hash Table
- Two Pointers
- Sliding Window
- Binary Search
- Prefix Sum
- Heap / Priority Queue
- DFS
- BFS
- Backtracking
- Dynamic Programming

A problem may belong to multiple patterns.

A pattern may belong to many problems.

Therefore:

Problem <-> Pattern is many-to-many.


## 3.3 PracticeAttempt

A historical record representing one time the user worked on a problem.

Examples of information that may eventually be stored:

- problem
- attemptedAt
- solved
- duration
- hintsUsed
- confidence
- difficulty experienced
- notes

PracticeAttempt is historical/event data.

Attempts should not overwrite one another.


## 3.4 LearningState

A derived representation of the user's current learning status for a problem and/or pattern.

Possible information includes:

- mastery level
- confidence
- number of attempts
- recent failures
- recent successes
- last practiced
- next recommended review
- weakness score

LearningState should ultimately be derived from practice history rather than manually serving as the raw history itself.


---

# 4. Architectural Principle

The V2 learning loop should eventually look like:

Problem Catalog
      ↓
Practice Attempts
      ↓
Learning State
      ↓
Recommendation Engine
      ↓
Daily Practice Plan
      ↓
New Practice Attempts
      ↺

This loop is the core of LeetCode Coach V2.


---

# 5. Problem Catalog Principles

The Problem catalog should become stable reference data.

Problems should no longer be treated primarily as user-created records.

Each catalog problem should eventually contain enough metadata for recommendation logic.

Candidate fields include:

- internal database ID
- external problem ID
- title
- slug
- difficulty
- source/platform
- problem URL
- patterns/topics
- optional metadata required by later recommendation logic

Exact schema decisions should favor clean domain modeling rather than blindly preserving the V1 schema.


---

# 6. Problem Source / Licensing Requirement

Before importing or bundling a large external problem dataset, the project must explicitly verify that the source and license permit the intended use.

Do NOT:

- scrape LeetCode pages as part of the application
- copy proprietary problem statements
- import an external dataset merely because it exists on GitHub
- assume public availability means redistribution is allowed

For the project catalog, prefer metadata only, such as:

- problem ID
- title
- slug
- difficulty
- URL
- algorithmic patterns/topics

Do not store full proprietary problem descriptions unless there is a clearly compatible license.

Any external dataset introduced into the repository must have:

1. a documented source
2. a documented license
3. a short explanation of what data is used
4. attribution where required

If licensing is unclear, do not import it.


---

# 7. Recommendation Philosophy

The recommendation system should eventually combine signals such as:

- due reviews
- weak patterns
- weak problems
- recent practice
- repetition history
- difficulty progression
- diversity of practice
- user goals
- available time

Recommendations must be explainable.

Example:

"Review Two Sum because it is due today and Hash Table is currently one of your weaker patterns."

The initial recommendation engine should be deterministic.

LLMs should not directly decide the user's learning state or silently alter scheduling data.


---

# 8. Daily Plan / Coach Input

A future V2 feature will allow the user to describe today's situation naturally.

Examples:

"I have about 45 minutes today."

"I haven't practiced for four days and want to get back into it."

"I have an interview next week and want to focus on arrays and graphs."

"I only have enough energy for something easy today."

The system should convert this context plus stored learning state into a practical daily plan.

The plan should NOT always follow a rigid template such as:

"time + exactly N problems."

Instead, the plan should adapt to what the user actually says.

If essential information is missing or ambiguous, the coach may ask a concise follow-up question rather than inventing constraints.


---

# 9. AI / LLM Boundary

LLM functionality belongs at the coaching layer.

Good future uses:

- understanding natural-language goals
- asking clarification questions
- explaining recommendations
- summarizing progress
- producing encouraging but concrete coaching feedback

The LLM should consume structured application state.

It should NOT replace:

- PostgreSQL persistence
- PracticeAttempt history
- LearningState calculations
- recommendation scoring
- scheduling rules

The application should remain useful even if the LLM provider is unavailable.


---

# 10. Analytics

Future analytics may include:

- problems practiced over time
- attempts over time
- success rate
- practice consistency
- pattern mastery
- strongest patterns
- weakest patterns
- due reviews
- difficulty distribution
- progress trends

Analytics should be calculated from structured application data.


---

# 11. V2 Milestones

## Milestone 1 — Problem Catalog Foundation

Goal:

Create a clean, legally defensible canonical problem catalog that later learning and recommendation features can depend on.

Scope:

- audit current Problem model and V1 assumptions
- establish canonical Problem catalog semantics
- establish Pattern as a first-class domain concept
- support Problem <-> Pattern many-to-many relationships
- define appropriate catalog metadata
- determine/document the source and licensing strategy
- populate an initial useful catalog using only legally appropriate metadata
- expose catalog data through appropriate backend APIs
- preserve existing functionality where reasonably possible
- add/update automated tests

Do NOT implement PracticeAttempt, LearningState, recommendation logic, or LLM features in this milestone.


## Milestone 2 — Practice History

Introduce PracticeAttempt as append-only historical practice data.

The application should be able to record multiple attempts for the same problem without destroying previous history.


## Milestone 3 — Learning State

Derive useful current learning state from practice history.

Establish problem-level and/or pattern-level mastery/weakness signals.


## Milestone 4 — Recommendation Engine

Build deterministic recommendation logic using catalog metadata, practice history, and learning state.

Recommendations should include reasons.


## Milestone 5 — AI Coach

Add a natural-language coaching layer.

The coach should interpret user goals/context and translate structured recommendations into a useful daily plan.

It may ask clarification questions when needed.


## Milestone 6 — Analytics

Add meaningful progress and learning analytics derived from stored history.


## Milestone 7 — Production V2

Production hardening may include:

- authentication / user isolation
- deployment improvements
- database migration strategy
- CI/CD
- Docker where useful
- observability
- API documentation
- production-quality frontend improvements
- security review


---

# 12. Engineering Constraints

Throughout V2:

1. Do not rewrite the existing application without a strong reason.
2. Prefer incremental refactoring.
3. Preserve working behavior unless the milestone intentionally replaces it.
4. Keep domain logic out of controllers.
5. Keep persistence concerns out of domain/recommendation logic where practical.
6. Avoid premature abstractions.
7. Add tests for meaningful new behavior.
8. Do not commit secrets or credentials.
9. Keep production deployment compatibility in mind.
10. Prefer explicit, understandable logic over unnecessarily clever implementations.
11. Do not introduce major dependencies unless they solve a concrete problem.
12. Keep milestone boundaries strict.


---

# 13. Data Migration Principle

V1 data may contain concepts that mix:

- problem metadata
- review state
- solved state
- review counters
- scheduling information

V2 should gradually separate these responsibilities.

Do not blindly map every V1 field into the canonical Problem entity.

When changing existing schema:

1. identify what the field actually represents
2. decide which V2 domain concept owns it
3. preserve user data where practical
4. document intentional compatibility breaks


---

# 14. Testing Expectations

Each milestone should leave the repository in a working state.

At minimum:

- existing relevant tests should continue to pass or be intentionally updated
- new domain behavior should have automated tests
- repository/service behavior should be tested where meaningful
- API changes should have tests where practical

Before declaring a milestone complete, run the full test suite.


---

# 15. Definition of V2 Success

LeetCode Coach V2 succeeds when it can eventually answer:

"What should I do today?"

using evidence from:

- what problems exist
- what patterns they train
- what the user has practiced
- how those attempts went
- what the user is weak at
- what is due for review
- what the user wants to accomplish today

and provide a clear explanation for the resulting plan.

The product should feel like a learning system, not merely a problem database.