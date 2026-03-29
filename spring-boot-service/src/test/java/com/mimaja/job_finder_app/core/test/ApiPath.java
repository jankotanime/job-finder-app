package com.mimaja.job_finder_app.core.test;

public final class ApiPath {
    private static final String ID_SEGMENT = "/{%s}";
    private static final String OFFER_ID_SEGMENT = "/{offerId}";
    private static final String APPLICATION_ID_SEGMENT = "/{applicationId}";

    private static final String AUTH = "/auth";
    private static final String AUTH_REGISTER = AUTH + "/register";
    private static final String AUTH_LOGIN = AUTH + "/login";
    private static final String AUTH_GOOGLE = AUTH + "/google-auth";

    private static final String ADMIN = "/admin";
    private static final String USER = "/user";
    private static final String CATEGORY = "/category";
    private static final String TAG = "/tag";
    private static final String OFFER = "/offer";
    private static final String CV = "/cv";
    private static final String JOB = "/job";
    private static final String CONTRACT = "/contract";
    private static final String PASSWORD = "/password";
    private static final String PROFILE_COMPLETION_FORM = "/profile-completion-form";

    private ApiPath() {}

    public static String healthCheckPath() {
        return "/health-check";
    }

    public static String exampleErrorPath() {
        return "/example-error";
    }

    public static String authRegisterPath() {
        return AUTH_REGISTER;
    }

    public static String authLoginPath() {
        return AUTH_LOGIN;
    }

    public static String authGoogleLoginPath() {
        return AUTH_GOOGLE + "/login";
    }

    public static String authGoogleCheckUserExistencePath() {
        return AUTH_GOOGLE + "/check-user-existence";
    }

    public static String authGoogleRegisterPath() {
        return AUTH_GOOGLE + "/register";
    }

    public static String refreshRotatePath() {
        return "/refresh-token/rotate";
    }

    public static String adminUserPath() {
        return ADMIN + USER;
    }

    public static String adminUserPathWithId() {
        return adminUserPath() + formatId("userId");
    }

    public static String adminCategoryPath() {
        return ADMIN + CATEGORY;
    }

    public static String adminTagPath() {
        return ADMIN + TAG;
    }

    public static String categoryPath() {
        return CATEGORY;
    }

    public static String categoryPathWithId() {
        return categoryPath() + formatId("categoryId");
    }

    public static String tagPath() {
        return TAG;
    }

    public static String tagPathWithId() {
        return tagPath() + formatId("tagId");
    }

    public static String offerPath() {
        return OFFER;
    }

    public static String offerPathWithId() {
        return offerPath() + formatId("offerId");
    }

    public static String offerApplicationPath() {
        return OFFER + OFFER_ID_SEGMENT + "/application";
    }

    public static String offerApplicationPathWithIds() {
        return OFFER + OFFER_ID_SEGMENT + "/application" + APPLICATION_ID_SEGMENT;
    }

    public static String offerApplicationAcceptPath() {
        return offerApplicationPathWithIds() + "/accept";
    }

    public static String offerApplicationRejectPath() {
        return offerApplicationPathWithIds() + "/reject";
    }

    public static String jobDispatcherPath() {
        return JOB + formatId("jobId") + "/dispatcher";
    }

    public static String jobStartPath() {
        return JOB + formatId("jobId") + "/start-job";
    }

    public static String jobCreateFromOfferPath() {
        return JOB + formatId("offerId");
    }

    public static String contractByOfferPath() {
        return CONTRACT + "/by-offer" + formatId("offerId");
    }

    public static String cvPath() {
        return CV;
    }

    public static String cvPathWithId() {
        return cvPath() + formatId("cvId");
    }

    public static String jobOwnerPath() {
        return JOB + "/owner";
    }

    public static String jobContractorPath() {
        return JOB + "/contractor";
    }

    public static String jobPathWithId() {
        return JOB + formatId("jobId");
    }

    public static String contractPathWithId() {
        return CONTRACT + formatId("contractId");
    }

    public static String profileCompletionFormPath() {
        return PROFILE_COMPLETION_FORM;
    }

    public static String userUpdateEmailPath() {
        return USER + "/update/email";
    }

    public static String userUpdatePhoneNumberPath() {
        return USER + "/update/phone-number";
    }

    public static String passwordMobileUpdatePath() {
        return PASSWORD + "/mobile/update";
    }

    public static String passwordWebsiteSendEmailPath() {
        return PASSWORD + "/website/send-email";
    }

    public static String passwordWebsiteUpdatePath() {
        return PASSWORD + "/website/update";
    }

    public static String mePath() {
        return "/me";
    }

    public static String exceptionHttpMessageNotReadablePath() {
        return "/exception/http-message-not-readable";
    }

    public static String userUpdateUserDataPath() {
        return USER + "/update/user-data";
    }

    private static String formatId(String idName) {
        return ID_SEGMENT.formatted(idName);
    }
}
