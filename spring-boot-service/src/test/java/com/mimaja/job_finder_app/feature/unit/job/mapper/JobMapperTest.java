package com.mimaja.job_finder_app.feature.unit.job.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static com.mimaja.job_finder_app.feature.unit.job.mockdata.JobMockData.createTestJob;
import static com.mimaja.job_finder_app.feature.unit.job.mockdata.JobMockData.createTestJobWithPhoto;
import static com.mimaja.job_finder_app.feature.unit.user.mockdata.UserMockData.createTestUser;
import static com.mimaja.job_finder_app.feature.unit.user.mockdata.UserMockData.createTestUserWithProfilePhoto;

import java.lang.reflect.Field;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mimaja.job_finder_app.feature.job.dto.JobResponseDto;
import com.mimaja.job_finder_app.feature.job.mapper.JobMapper;
import com.mimaja.job_finder_app.feature.job.mapper.JobMapperImpl;
import com.mimaja.job_finder_app.feature.job.model.Job;
import com.mimaja.job_finder_app.feature.offer.tag.mapper.TagMapper;
import com.mimaja.job_finder_app.feature.user.model.User;

@ExtendWith(MockitoExtension.class)
@DisplayName("JobMapper - Unit Tests")
class JobMapperTest {

    private JobMapper jobMapper;
    private TagMapper tagMapperMock;

    @BeforeEach
    void setUp() throws Exception {
        setupJobMapperWithMocks();
    }

    private void setupJobMapperWithMocks() throws Exception {
        tagMapperMock = mock(TagMapper.class);
        JobMapperImpl jobMapperImpl = new JobMapperImpl();
        injectField(jobMapperImpl, "tagMapper", tagMapperMock);
        jobMapper = jobMapperImpl;
    }

    private void injectField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    // ==================== toResponseDto Tests ====================

    @Test
    @DisplayName("Should return null when mapping null Job to JobResponseDto")
    void testToResponseDto_shouldReturnNull_whenNullJobProvided() {
        // given
        Job job = null;

        // when
        JobResponseDto result = jobMapper.toResponseDto(job);

        // then
        assertNull(result, "Should return null for null input");
    }

    @Test
    @DisplayName("Should not be null when mapping valid Job to JobResponseDto")
    void testToResponseDto_shouldReturnNonNullDto_whenValidJobProvided() {
        // given
        Job job = createTestJob();

        // when
        JobResponseDto result = jobMapper.toResponseDto(job);

        // then
        assertNotNull(result, "JobResponseDto should not be null");
    }

    @Test
    @DisplayName("Should map id correctly when mapping Job to JobResponseDto")
    void testToResponseDto_shouldMapIdCorrectly_whenValidJobProvided() {
        // given
        Job job = createTestJob();

        // when
        JobResponseDto result = jobMapper.toResponseDto(job);

        // then
        assertThat(result.id()).isEqualTo(job.getId());
    }

    @Test
    @DisplayName("Should map title correctly when mapping Job to JobResponseDto")
    void testToResponseDto_shouldMapTitleCorrectly_whenValidJobProvided() {
        // given
        Job job = createTestJob();

        // when
        JobResponseDto result = jobMapper.toResponseDto(job);

        // then
        assertThat(result.title()).isEqualTo(job.getTitle());
    }

    @Test
    @DisplayName("Should map description correctly when mapping Job to JobResponseDto")
    void testToResponseDto_shouldMapDescriptionCorrectly_whenValidJobProvided() {
        // given
        Job job = createTestJob();

        // when
        JobResponseDto result = jobMapper.toResponseDto(job);

        // then
        assertThat(result.description()).isEqualTo(job.getDescription());
    }

    @Test
    @DisplayName("Should map dateAndTime correctly when mapping Job to JobResponseDto")
    void testToResponseDto_shouldMapDateAndTimeCorrectly_whenValidJobProvided() {
        // given
        Job job = createTestJob();

        // when
        JobResponseDto result = jobMapper.toResponseDto(job);

        // then
        assertThat(result.dateAndTime()).isEqualTo(job.getDateAndTime());
    }

    @Test
    @DisplayName("Should map salary correctly when mapping Job to JobResponseDto")
    void testToResponseDto_shouldMapSalaryCorrectly_whenValidJobProvided() {
        // given
        Job job = createTestJob();

        // when
        JobResponseDto result = jobMapper.toResponseDto(job);

        // then
        assertThat(result.salary()).isEqualTo(job.getSalary());
    }

    @Test
    @DisplayName("Should map status correctly when mapping Job to JobResponseDto")
    void testToResponseDto_shouldMapStatusCorrectly_whenValidJobProvided() {
        // given
        Job job = createTestJob();

        // when
        JobResponseDto result = jobMapper.toResponseDto(job);

        // then
        assertThat(result.status()).isEqualTo(job.getStatus());
    }

    @Test
    @DisplayName("Should map owner correctly when mapping Job to JobResponseDto")
    void testToResponseDto_shouldMapOwnerCorrectly_whenJobHasOwner() {
        // given
        Job job = createTestJob();
        User owner = createTestUser();
        job.setOwner(owner);

        // when
        JobResponseDto result = jobMapper.toResponseDto(job);

        // then
        assertThat(result.owner()).isNotNull();
    }

    @Test
    @DisplayName("Should handle null owner when mapping Job to JobResponseDto")
    void testToResponseDto_shouldHandleNullOwner_whenOwnerIsNull() {
        // given
        Job job = createTestJob();
        job.setOwner(null);

        // when
        JobResponseDto result = jobMapper.toResponseDto(job);

        // then
        assertThat(result.owner()).isNull();
    }

    @Test
    @DisplayName("Should map contractor correctly when mapping Job to JobResponseDto")
    void testToResponseDto_shouldMapContractorCorrectly_whenJobHasContractor() {
        // given
        Job job = createTestJob();
        User contractor = createTestUser();
        job.setContractor(contractor);

        // when
        JobResponseDto result = jobMapper.toResponseDto(job);

        // then
        assertThat(result.contractor()).isNotNull();
    }

    @Test
    @DisplayName("Should handle null contractor when mapping Job to JobResponseDto")
    void testToResponseDto_shouldHandleNullContractor_whenContractorIsNull() {
        // given
        Job job = createTestJob();
        job.setContractor(null);

        // when
        JobResponseDto result = jobMapper.toResponseDto(job);

        // then
        assertThat(result.contractor()).isNull();
    }

    @Test
    @DisplayName("Should map tags correctly when mapping Job to JobResponseDto")
    void testToResponseDto_shouldMapTagsCorrectly_whenJobHasTags() {
        // given
        Job job = createTestJob();

        // when
        JobResponseDto result = jobMapper.toResponseDto(job);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should handle null tags when mapping Job to JobResponseDto")
    void testToResponseDto_shouldHandleNullTags_whenTagsAreNull() {
        // given
        Job job = createTestJob();
        job.setTags(null);

        // when
        JobResponseDto result = jobMapper.toResponseDto(job);

        // then
        assertThat(result.tags()).isEmpty();
    }

    @Test
    @DisplayName("Should map photo correctly when mapping Job to JobResponseDto")
    void testToResponseDto_shouldMapPhotoCorrectly_whenJobHasPhoto() {
        // given
        Job job = createTestJobWithPhoto();

        // when
        JobResponseDto result = jobMapper.toResponseDto(job);

        // then
        assertThat(result.photo()).isNotNull();
    }

    @Test
    @DisplayName("Should handle null photo when mapping Job to JobResponseDto")
    void testToResponseDto_shouldHandleNullPhoto_whenPhotoIsNull() {
        // given
        Job job = createTestJob();
        job.setPhoto(null);

        // when
        JobResponseDto result = jobMapper.toResponseDto(job);

        // then
        assertThat(result.photo()).isNull();
    }

    // ==================== userToUserInJobResponseDto Tests ====================

    @Test
    @DisplayName("Should return null when mapping null User to UserInJobResponseDto")
    void testUserToUserInJobResponseDto_shouldReturnNull_whenNullUserProvided() {
        // given
        Job job = createTestJob();
        job.setOwner(null);

        // when
        JobResponseDto result = jobMapper.toResponseDto(job);

        // then
        assertThat(result.owner()).isNull();
    }

    @Test
    @DisplayName("Should map user correctly when user has profile photo")
    void testUserToUserInJobResponseDto_shouldMapUserWithProfilePhoto_whenProfilePhotoExists() {
        // given
        Job job = createTestJob();
        User userWithPhoto = createTestUserWithProfilePhoto();
        job.setOwner(userWithPhoto);

        // when
        JobResponseDto result = jobMapper.toResponseDto(job);

        // then
        assertThat(result.owner()).isNotNull();
    }

    @Test
    @DisplayName("Should handle null profile photo in user mapping")
    void testUserToUserInJobResponseDto_shouldHandleNullProfilePhoto_whenPhotoIsNull() {
        // given
        Job job = createTestJob();
        User user = createTestUser();
        user.setProfilePhoto(null);
        job.setOwner(user);

        // when
        JobResponseDto result = jobMapper.toResponseDto(job);

        // then
        assertThat(result.owner()).isNotNull();
        assertThat(result.owner().profilePhoto()).isNull();
    }

    @Test
    @DisplayName("Should map all user fields correctly in JobResponseDto")
    void testUserToUserInJobResponseDto_shouldMapAllUserFields_whenUserIsValid() {
        // given
        Job job = createTestJob();
        User user = createTestUser();
        job.setOwner(user);

        // when
        JobResponseDto result = jobMapper.toResponseDto(job);

        // then
        assertThat(result.owner().id()).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("Should map username correctly in user mapping")
    void testUserToUserInJobResponseDto_shouldMapUsername_whenUserIsValid() {
        // given
        Job job = createTestJob();
        User user = createTestUser();
        job.setOwner(user);

        // when
        JobResponseDto result = jobMapper.toResponseDto(job);

        // then
        assertThat(result.owner().username()).isEqualTo(user.getUsername());
    }

    @Test
    @DisplayName("Should map firstName correctly in user mapping")
    void testUserToUserInJobResponseDto_shouldMapFirstName_whenUserIsValid() {
        // given
        Job job = createTestJob();
        User user = createTestUser();
        job.setOwner(user);

        // when
        JobResponseDto result = jobMapper.toResponseDto(job);

        // then
        assertThat(result.owner().firstName()).isEqualTo(user.getFirstName());
    }

    @Test
    @DisplayName("Should map lastName correctly in user mapping")
    void testUserToUserInJobResponseDto_shouldMapLastName_whenUserIsValid() {
        // given
        Job job = createTestJob();
        User user = createTestUser();
        job.setOwner(user);

        // when
        JobResponseDto result = jobMapper.toResponseDto(job);

        // then
        assertThat(result.owner().lastName()).isEqualTo(user.getLastName());
    }

    @Test
    @DisplayName("Should map phoneNumber correctly in user mapping")
    void testUserToUserInJobResponseDto_shouldMapPhoneNumber_whenUserIsValid() {
        // given
        Job job = createTestJob();
        User user = createTestUser();
        job.setOwner(user);

        // when
        JobResponseDto result = jobMapper.toResponseDto(job);

        // then
        assertThat(result.owner().phoneNumber()).isEqualTo(user.getPhoneNumber());
    }

    // ==================== jobPhotoToJobPhotoResponseDto Tests ====================

    @Test
    @DisplayName("Should handle null job photo when mapping Job with photo to JobResponseDto")
    void testJobPhotoToJobPhotoResponseDto_shouldHandleNullPhoto_whenPhotoIsNull() {
        // given
        Job job = createTestJob();
        job.setPhoto(null);

        // when
        JobResponseDto result = jobMapper.toResponseDto(job);

        // then
        assertThat(result.photo()).isNull();
    }

    @Test
    @DisplayName("Should map job photo correctly when photo exists")
    void testJobPhotoToJobPhotoResponseDto_shouldMapPhoto_whenPhotoExists() {
        // given
        Job job = createTestJobWithPhoto();

        // when
        JobResponseDto result = jobMapper.toResponseDto(job);

        // then
        assertThat(result.photo()).isNotNull();
    }
}
