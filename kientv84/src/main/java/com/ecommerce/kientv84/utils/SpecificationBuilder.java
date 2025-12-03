package com.ecommerce.kientv84.utils;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import org.springframework.data.jpa.domain.Specification;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

/**
 * Generic Specification builder có thể dùng cho nhiều entity
 */
public class SpecificationBuilder<T> {

    private final List<Specification<T>> specs = new ArrayList<>();

    public SpecificationBuilder<T> equal(String field, Object value) {
        if (value != null) {
            specs.add((root, query, cb) -> {
                Path<?> path = root;
                for (String part : field.split("\\.")) {
                    path = path.get(part);
                }
                return cb.equal(path, value);
            });
        }
        return this;
    }

    /**
     * likeIgnoreCase với unaccent
     */
    public SpecificationBuilder<T> likeIgnoreCase(String field, String value) {
        if (value != null && !value.isEmpty()) {

            String normalized = removeAccent(value.toLowerCase());

            specs.add((root, query, cb) -> {
                Path<String> path = root.get(field);

                // sử dụng hàm unaccent của PostgreSQL
                Expression<String> dbField = cb.function("unaccent", String.class, cb.lower(path));

                return cb.like(dbField, "%" + normalized + "%");
            });
        }
        return this;
    }

    /**
     * Tìm kiếm LIKE trên nhiều field, nối bằng OR
     */
    public SpecificationBuilder<T> likeAnyFieldIgnoreCase(String value, String... fields) {
        if (value != null && !value.isEmpty() && fields.length > 0) {
            String normalized = removeAccent(value.toLowerCase());

            Specification<T> spec = null;

            for (String field : fields) {
                Specification<T> fieldSpec = (root, query, cb) -> {
                    Path<String> path = root.get(field);
                    Expression<String> dbField = cb.function("unaccent", String.class, cb.lower(path));
                    return cb.like(dbField, "%" + normalized + "%");
                };

                spec = (spec == null) ? fieldSpec : spec.or(fieldSpec); // OR giữa các field
            }

            specs.add(spec);
        }
        return this;
    }


    public SpecificationBuilder<T> custom(Specification<T> spec) {
        if (spec != null) {
            specs.add(spec);
        }
        return this;
    }

    public Specification<T> build() {
        return specs.stream()
                .reduce((s1, s2) -> s1.and(s2))
                .orElse((root, query, cb) -> cb.conjunction());
    }

    /**
     * Hàm loại bỏ dấu ở Java để so sánh input với unaccent ở DB
     */
    private String removeAccent(String s) {
        if (s == null) return null;
        String normalized = Normalizer.normalize(s, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }
}
