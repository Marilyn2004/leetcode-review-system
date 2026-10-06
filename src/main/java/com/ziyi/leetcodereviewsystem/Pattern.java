package com.ziyi.leetcodereviewsystem;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "patterns", uniqueConstraints = @UniqueConstraint(name = "uk_pattern_slug", columnNames = "slug"))
public class Pattern {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotBlank
    @jakarta.validation.constraints.Pattern(regexp = "[a-z0-9]+(?:-[a-z0-9]+)*")
    @Column(nullable = false)
    private String slug;
    @NotBlank
    @Column(nullable = false)
    private String name;

    protected Pattern() { }
    public Pattern(String slug, String name) { this.slug = slug; this.name = name; }
    public Integer getId() { return id; }
    public String getSlug() { return slug; }
    public String getName() { return name; }
}
