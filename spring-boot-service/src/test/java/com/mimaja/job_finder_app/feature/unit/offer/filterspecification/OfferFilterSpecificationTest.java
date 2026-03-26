package com.mimaja.job_finder_app.feature.unit.offer.filterspecification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mimaja.job_finder_app.feature.application.model.Application;
import com.mimaja.job_finder_app.feature.offer.dto.OfferFilterRequestDto;
import com.mimaja.job_finder_app.feature.offer.filterspecification.OfferFilterSpecification;
import com.mimaja.job_finder_app.feature.offer.model.Offer;
import com.mimaja.job_finder_app.feature.offer.tag.category.model.Category;
import com.mimaja.job_finder_app.feature.offer.tag.model.Tag;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"unchecked", "rawtypes"})
class OfferFilterSpecificationTest {
    private static final UUID USER_ID = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
    private static final LocalDateTime FIRST_DATE = LocalDateTime.of(2025, 1, 1, 0, 0);
    private static final LocalDateTime LAST_DATE = LocalDateTime.of(2025, 12, 31, 23, 59);
    private static final double MIN_SALARY = 1000.0;
    private static final double MAX_SALARY = 5000.0;
    private static final UUID TAG_ID = UUID.fromString("11111111-2222-3333-4444-555555555555");
    private static final UUID CATEGORY_ID = UUID.fromString("66666666-7777-8888-9999-aaaaaaaaaaaa");

    @Mock private Root<Offer> root;
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
        OfferFilterRequestDto dto =
                new OfferFilterRequestDto(null, null, null, null, null, null, null);
        Specification<Offer> spec = OfferFilterSpecification.filter(dto);

        // when
        spec.toPredicate(root, query, cb);

        // then
        verify(query).distinct(true);
    }

    @Test
    void filter_shouldReturnConjunction_whenNoOptionalFiltersSet() {
        // given
        OfferFilterRequestDto dto =
                new OfferFilterRequestDto(null, null, null, null, null, null, null);
        Specification<Offer> spec = OfferFilterSpecification.filter(dto);

        // when
        Predicate result = spec.toPredicate(root, query, cb);

        // then
        assertThat(result).isSameAs(conjunction);
    }

    @Test
    void filter_shouldApplyUserExclusionPredicates_whenUserIdProvided() {
        // given
        OfferFilterRequestDto dto =
                new OfferFilterRequestDto(USER_ID, null, null, null, null, null, null);
        Path ownerPath = mock(Path.class);
        Path ownerIdPath = mock(Path.class);
        when(root.get("owner")).thenReturn(ownerPath);
        when(ownerPath.get("id")).thenReturn(ownerIdPath);
        when(cb.notEqual(ownerIdPath, USER_ID)).thenReturn(predicate);

        Subquery<UUID> sub = mock(Subquery.class);
        Root<Application> appRoot = mock(Root.class);
        Path appIdPath = mock(Path.class);
        Path offerPath = mock(Path.class);
        Path candidatePath = mock(Path.class);
        Path candidateIdPath = mock(Path.class);
        Predicate eqOffer = mock(Predicate.class);
        Predicate eqCandidate = mock(Predicate.class);
        Predicate existsInner = mock(Predicate.class);

        when(query.subquery(UUID.class)).thenReturn(sub);
        when(sub.from(Application.class)).thenReturn(appRoot);
        when(appRoot.get("id")).thenReturn(appIdPath);
        when(sub.select(appIdPath)).thenReturn(sub);
        when(appRoot.get("offer")).thenReturn(offerPath);
        when(appRoot.get("candidate")).thenReturn(candidatePath);
        when(candidatePath.get("id")).thenReturn(candidateIdPath);
        when(cb.equal(offerPath, root)).thenReturn(eqOffer);
        when(cb.equal(candidateIdPath, USER_ID)).thenReturn(eqCandidate);
        when(sub.where(eqOffer, eqCandidate)).thenReturn(sub);
        when(cb.exists(sub)).thenReturn(existsInner);
        when(cb.not(existsInner)).thenReturn(predicate);

        Specification<Offer> spec = OfferFilterSpecification.filter(dto);

        // when
        spec.toPredicate(root, query, cb);

        // then
        verify(cb).notEqual(ownerIdPath, USER_ID);
    }

    @Test
    void filter_shouldApplyFirstDatePredicate_whenFirstDateProvided() {
        // given
        OfferFilterRequestDto dto =
                new OfferFilterRequestDto(null, FIRST_DATE, null, null, null, null, null);
        Path datePath = mock(Path.class);
        when(root.get("dateAndTime")).thenReturn(datePath);
        when(cb.greaterThanOrEqualTo(datePath, FIRST_DATE)).thenReturn(predicate);
        Specification<Offer> spec = OfferFilterSpecification.filter(dto);

        // when
        spec.toPredicate(root, query, cb);

        // then
        verify(cb).greaterThanOrEqualTo(datePath, FIRST_DATE);
    }

    @Test
    void filter_shouldApplyLastDatePredicate_whenLastDateProvided() {
        // given
        OfferFilterRequestDto dto =
                new OfferFilterRequestDto(null, null, LAST_DATE, null, null, null, null);
        Path datePath = mock(Path.class);
        when(root.get("dateAndTime")).thenReturn(datePath);
        when(cb.lessThanOrEqualTo(datePath, LAST_DATE)).thenReturn(predicate);
        Specification<Offer> spec = OfferFilterSpecification.filter(dto);

        // when
        spec.toPredicate(root, query, cb);

        // then
        verify(cb).lessThanOrEqualTo(datePath, LAST_DATE);
    }

    @Test
    void filter_shouldApplyMinSalaryPredicate_whenMinSalaryProvided() {
        // given
        OfferFilterRequestDto dto =
                new OfferFilterRequestDto(null, null, null, MIN_SALARY, null, null, null);
        Path salaryPath = mock(Path.class);
        when(root.get("salary")).thenReturn(salaryPath);
        when(cb.greaterThanOrEqualTo(salaryPath, MIN_SALARY)).thenReturn(predicate);
        Specification<Offer> spec = OfferFilterSpecification.filter(dto);

        // when
        spec.toPredicate(root, query, cb);

        // then
        verify(cb).greaterThanOrEqualTo(salaryPath, MIN_SALARY);
    }

    @Test
    void filter_shouldApplyMaxSalaryPredicate_whenMaxSalaryProvided() {
        // given
        OfferFilterRequestDto dto =
                new OfferFilterRequestDto(null, null, null, null, MAX_SALARY, null, null);
        Path salaryPath = mock(Path.class);
        when(root.get("salary")).thenReturn(salaryPath);
        when(cb.lessThanOrEqualTo(salaryPath, MAX_SALARY)).thenReturn(predicate);
        Specification<Offer> spec = OfferFilterSpecification.filter(dto);

        // when
        spec.toPredicate(root, query, cb);

        // then
        verify(cb).lessThanOrEqualTo(salaryPath, MAX_SALARY);
    }

    @Test
    void filter_shouldJoinTagsAndApplyInPredicate_whenTagsProvided() {
        // given
        Set<UUID> tagIds = Set.of(TAG_ID);
        OfferFilterRequestDto dto =
                new OfferFilterRequestDto(null, null, null, null, null, null, tagIds);
        Join<Offer, Tag> tagJoin = mock(Join.class);
        Path tagIdPath = mock(Path.class);
        doReturn(tagJoin).when(root).join("tags", JoinType.INNER);
        when(tagJoin.get("id")).thenReturn(tagIdPath);
        when(tagIdPath.in(anyCollection())).thenReturn(predicate);
        Specification<Offer> spec = OfferFilterSpecification.filter(dto);

        // when
        spec.toPredicate(root, query, cb);

        // then
        verify(root).join("tags", JoinType.INNER);
    }

    @Test
    void filter_shouldJoinTagsAndCategories_whenCategoriesProvided() {
        // given
        Set<UUID> categoryIds = Set.of(CATEGORY_ID);
        OfferFilterRequestDto dto =
                new OfferFilterRequestDto(null, null, null, null, null, categoryIds, null);
        Join<Offer, Tag> tagJoin = mock(Join.class);
        Join<Tag, Category> categoryJoin = mock(Join.class);
        Path categoryIdPath = mock(Path.class);
        doReturn(tagJoin).when(root).join("tags", JoinType.INNER);
        doReturn(categoryJoin).when(tagJoin).join("category", JoinType.INNER);
        when(categoryJoin.get("id")).thenReturn(categoryIdPath);
        when(categoryIdPath.in(anyCollection())).thenReturn(predicate);
        Specification<Offer> spec = OfferFilterSpecification.filter(dto);

        // when
        spec.toPredicate(root, query, cb);

        // then
        verify(tagJoin).join("category", JoinType.INNER);
    }

    @Test
    void filter_shouldNotJoinTags_whenTagSetEmpty() {
        // given
        OfferFilterRequestDto dto =
                new OfferFilterRequestDto(null, null, null, null, null, null, Set.of());
        Specification<Offer> spec = OfferFilterSpecification.filter(dto);

        // when
        spec.toPredicate(root, query, cb);

        // then
        verify(root, never()).join(eq("tags"), any(JoinType.class));
    }

    @Test
    void filter_shouldNotJoinTags_whenCategorySetEmpty() {
        // given
        OfferFilterRequestDto dto =
                new OfferFilterRequestDto(null, null, null, null, null, Set.of(), null);
        Specification<Offer> spec = OfferFilterSpecification.filter(dto);

        // when
        spec.toPredicate(root, query, cb);

        // then
        verify(root, never()).join(eq("tags"), any(JoinType.class));
    }
}
