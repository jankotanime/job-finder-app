package com.mimaja.job_finder_app.feature.unit.offer.mockdata;

import com.mimaja.job_finder_app.feature.application.model.Application;
import com.mimaja.job_finder_app.feature.offer.dto.OfferCreateRequestDto;
import com.mimaja.job_finder_app.feature.offer.dto.OfferUpdateRequestDto;
import com.mimaja.job_finder_app.feature.offer.model.Offer;
import com.mimaja.job_finder_app.feature.offer.model.OfferStatus;
import com.mimaja.job_finder_app.feature.offer.tag.model.Tag;
import com.mimaja.job_finder_app.feature.user.model.User;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class OfferMockData {

    public static final String TEST_OFFER_TITLE = "Test Offer Title";
    public static final String TEST_OFFER_DESCRIPTION = "Test Offer Description";
    public static final Double TEST_OFFER_SALARY = 3000.0;
    public static final Double TEST_MIN_SALARY = 2000.0;
    public static final Double TEST_MAX_SALARY = 5000.0;
    public static final Double TEST_SALARY_BELOW_MIN = 1000.0;
    public static final Double TEST_SALARY_ABOVE_MAX = 6000.0;
    public static final int MAX_APPLICATIONS = 10;

    public static User createTestUser() {
        User user = new User();
        user.setId(UUID.randomUUID());
        return user;
    }

    public static Offer createTestOffer() {
        Offer offer = new Offer();
        offer.setId(UUID.randomUUID());
        offer.setStatus(OfferStatus.OPEN);
        offer.setOwner(createTestUser());
        offer.setPhoto(null);
        Set<Tag> tags = new HashSet<>();
        tags.add(createTestTag());
        offer.setTags(tags);
        offer.setApplications(new HashSet<>());
        offer.setContract(null);
        return offer;
    }

    public static Offer createTestOfferWithOwner(User owner) {
        Offer offer = new Offer();
        offer.setId(UUID.randomUUID());
        offer.setStatus(OfferStatus.OPEN);
        offer.setOwner(owner);
        offer.setPhoto(null);
        Set<Tag> tags = new HashSet<>();
        tags.add(createTestTag());
        offer.setTags(tags);
        offer.setApplications(new HashSet<>());
        offer.setContract(null);
        return offer;
    }

    public static Offer createTestOfferWithStatus(OfferStatus status) {
        Offer offer = new Offer();
        offer.setId(UUID.randomUUID());
        offer.setStatus(status);
        offer.setOwner(createTestUser());
        offer.setPhoto(null);
        Set<Tag> tags = new HashSet<>();
        tags.add(createTestTag());
        offer.setTags(tags);
        offer.setApplications(new HashSet<>());
        offer.setContract(null);
        return offer;
    }

    public static Tag createTestTag() {
        Tag tag = new Tag();
        tag.setId(UUID.randomUUID());
        tag.setName("TestTag");
        return tag;
    }

    public static Set<Tag> createTestTagSet() {
        Set<Tag> tags = new HashSet<>();
        tags.add(createTestTag());
        return tags;
    }

    public static OfferCreateRequestDto createTestOfferCreateRequestDto() {
        return new OfferCreateRequestDto(
                TEST_OFFER_TITLE,
                TEST_OFFER_DESCRIPTION,
                LocalDateTime.now().plusDays(1),
                TEST_OFFER_SALARY,
                10,
                new HashSet<UUID>()
        );
    }

    public static OfferUpdateRequestDto createTestOfferUpdateRequestDto() {
        return new OfferUpdateRequestDto(
                TEST_OFFER_TITLE,
                TEST_OFFER_DESCRIPTION,
                LocalDateTime.now().plusDays(1),
                TEST_OFFER_SALARY,
                10,
                new HashSet<UUID>()
        );
    }

    public static Offer createTestOfferWithApplications() {
        Offer offer = createTestOffer();
        Application application = new Application();
        HashSet<Application> applications = new HashSet<Application>();
        applications.add(application);
        offer.setApplications(applications);
        return offer;
    }

    public static Offer createTestOfferWithTags() {
        Offer offer = createTestOffer();
        Set<Tag> tags = new HashSet<>();
        tags.add(createTestTag());
        tags.add(createTestTag());
        offer.setTags(tags);
        return offer;
    }

    public static Offer createTestOfferWithPhoto() {
        Offer offer = createTestOffer();
        // Set a non-null photo for coverage of photo mapping
        com.mimaja.job_finder_app.feature.offer.offerphoto.model.OfferPhoto photo = 
            new com.mimaja.job_finder_app.feature.offer.offerphoto.model.OfferPhoto();
        offer.setPhoto(photo);
        return offer;
    }

    public static Offer createTestOfferWithContract() {
        Offer offer = createTestOffer();
        // Set a non-null contract for coverage of contract mapping
        com.mimaja.job_finder_app.feature.contract.model.Contract contract =
            new com.mimaja.job_finder_app.feature.contract.model.Contract();
        offer.setContract(contract);
        return offer;
    }
}
