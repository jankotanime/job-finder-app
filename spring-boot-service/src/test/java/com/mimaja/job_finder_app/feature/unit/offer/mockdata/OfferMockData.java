package com.mimaja.job_finder_app.feature.unit.offer.mockdata;

import com.mimaja.job_finder_app.feature.offer.model.Offer;
import com.mimaja.job_finder_app.feature.offer.model.OfferStatus;
import com.mimaja.job_finder_app.feature.offer.tag.model.Tag;
import com.mimaja.job_finder_app.feature.user.model.User;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class OfferMockData {

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
}
