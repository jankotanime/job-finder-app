package com.mimaja.job_finder_app.feature.unit.offer.tag.filterspecification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mimaja.job_finder_app.feature.offer.tag.dto.TagFilterRequestDto;
import com.mimaja.job_finder_app.feature.offer.tag.filterspecification.TagFilterSpecification;
import com.mimaja.job_finder_app.feature.offer.tag.model.Tag;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"unchecked", "rawtypes"})
class TagFilterSpecificationTest {
    private static final String TAG_NAME = "Java";

    @Mock private Root<Tag> root;
    @Mock private CriteriaQuery<?> query;
    @Mock private CriteriaBuilder cb;
    @Mock private Predicate predicate;
    @Mock private Predicate conjunction;

    @BeforeEach
    void stubConjunction() {
        lenient().when(cb.and(any(Predicate[].class))).thenReturn(conjunction);
        lenient().when(query.distinct(true)).thenReturn((CriteriaQuery) query);
    }

    @Test
    void filter_shouldCallDistinctOnQuery_whenInvoked() {
        // given
        TagFilterRequestDto dto = new TagFilterRequestDto(null, null);
        Specification<Tag> spec = TagFilterSpecification.filter(dto);

        // when
        spec.toPredicate(root, query, cb);

        // then
        verify(query).distinct(true);
    }

    @Test
    void filter_shouldReturnConjunction_whenNameNullAndCategoriesNull() {
        // given
        TagFilterRequestDto dto = new TagFilterRequestDto(null, null);
        Specification<Tag> spec = TagFilterSpecification.filter(dto);

        // when
        Predicate result = spec.toPredicate(root, query, cb);

        // then
        assertThat(result).isSameAs(conjunction);
    }

    @Test
    void filter_shouldApplyLikeOnLowerName_whenNameProvided() {
        // given
        TagFilterRequestDto dto = new TagFilterRequestDto(TAG_NAME, null);
        Path namePath = mock(Path.class);
        Expression<String> lowerName = mock(Expression.class);
        when(root.get("name")).thenReturn(namePath);
        when(cb.lower(namePath)).thenReturn(lowerName);
        when(cb.like(lowerName, "%java%")).thenReturn(predicate);
        Specification<Tag> spec = TagFilterSpecification.filter(dto);

        // when
        spec.toPredicate(root, query, cb);

        // then
        verify(cb).like(lowerName, "%java%");
    }

    @Test
    void filter_shouldNotApplyLike_whenNameBlank() {
        // given
        TagFilterRequestDto dto = new TagFilterRequestDto("   ", null);
        Specification<Tag> spec = TagFilterSpecification.filter(dto);

        // when
        spec.toPredicate(root, query, cb);

        // then
        verify(cb, never()).like(any(), any(String.class));
    }

    @Test
    void filter_shouldJoinCategoryAndApplyIn_whenCategoriesProvided() {
        // given
        String[] categories = {"IT", "HR"};
        TagFilterRequestDto dto = new TagFilterRequestDto(null, categories);
        Join categoryJoin = mock(Join.class);
        Path namePath = mock(Path.class);
        when(root.join("category", JoinType.INNER)).thenReturn(categoryJoin);
        when(categoryJoin.get("name")).thenReturn(namePath);
        when(namePath.in((Object[]) categories)).thenReturn(predicate);
        Specification<Tag> spec = TagFilterSpecification.filter(dto);

        // when
        spec.toPredicate(root, query, cb);

        // then
        verify(root).join("category", JoinType.INNER);
    }

    @Test
    void filter_shouldNotJoinCategory_whenCategoriesNull() {
        // given
        TagFilterRequestDto dto = new TagFilterRequestDto(null, null);
        Specification<Tag> spec = TagFilterSpecification.filter(dto);

        // when
        spec.toPredicate(root, query, cb);

        // then
        verify(root, never()).join(eq("category"), any(JoinType.class));
    }

    @Test
    void filter_shouldApplyBothPredicates_whenNameAndCategoriesProvided() {
        // given
        String[] categories = {"IT"};
        TagFilterRequestDto dto = new TagFilterRequestDto(TAG_NAME, categories);
        Path namePath = mock(Path.class);
        Expression<String> lowerName = mock(Expression.class);
        Join categoryJoin = mock(Join.class);
        Path categoryNamePath = mock(Path.class);
        when(root.get("name")).thenReturn(namePath);
        when(cb.lower(namePath)).thenReturn(lowerName);
        when(cb.like(lowerName, "%java%")).thenReturn(predicate);
        doReturn(categoryJoin).when(root).join("category", JoinType.INNER);
        when(categoryJoin.get("name")).thenReturn(categoryNamePath);
        when(categoryNamePath.in((Object[]) categories)).thenReturn(predicate);
        Specification<Tag> spec = TagFilterSpecification.filter(dto);

        // when
        spec.toPredicate(root, query, cb);

        // then
        verify(cb).like(lowerName, "%java%");
    }
}
