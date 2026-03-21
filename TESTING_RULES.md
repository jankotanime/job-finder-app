# Testing Rules

Zasady pisania testów obowiązujące w projekcie `job-finder-app`. Reguły oparte są na wzorcach stosowanych w istniejących testach i mają na celu zachowanie spójności, czytelności i łatwości utrzymania całego zestawu testów.

---

## 1. Jedna asercja na test

Każdy test weryfikuje dokładnie **jedną właściwość** lub **jeden efekt uboczny**. Dzięki temu nazwa testu w pełni opisuje to, co sprawdza, a po nieudanym teście od razu wiadomo, co poszło nie tak.

**Źle** – wiele asercji w jednym teście:

```java
@Test
void tryToLogin_shouldReturnToken_whenCredentialsAreValid() {
    when(defaultLoginValidation.userValidation(TEST_EMAIL)).thenReturn(testUser);
    when(passwordConfiguration.verifyPassword(TEST_PASSWORD, testUser.getPasswordHash())).thenReturn(true);
    when(refreshTokenServiceDefault.createTokensSet(testUser)).thenReturn(testTokenResponse);

    TokenResponseDto result = loginService.tryToLogin(createValidLoginRequest());

    assertNotNull(result.accessToken());
    assertNotNull(result.refreshToken());
    assertNotNull(result.refreshTokenId());
    assertFalse(result.accessToken().isEmpty());
    assertFalse(result.refreshToken().isEmpty());
}
```

**Dobrze** – każda właściwość ma osobny test:

```java
@Test
void tryToLogin_shouldReturnNonNullAccessToken_whenCredentialsAreValid() {
    when(defaultLoginValidation.userValidation(TEST_EMAIL)).thenReturn(testUser);
    when(passwordConfiguration.verifyPassword(TEST_PASSWORD, testUser.getPasswordHash())).thenReturn(true);
    when(refreshTokenServiceDefault.createTokensSet(testUser)).thenReturn(testTokenResponse);
    TokenResponseDto result = loginService.tryToLogin(createValidLoginRequest());
    assertNotNull(result.accessToken());
}

@Test
void tryToLogin_shouldReturnNonNullRefreshToken_whenCredentialsAreValid() {
    when(defaultLoginValidation.userValidation(TEST_EMAIL)).thenReturn(testUser);
    when(passwordConfiguration.verifyPassword(TEST_PASSWORD, testUser.getPasswordHash())).thenReturn(true);
    when(refreshTokenServiceDefault.createTokensSet(testUser)).thenReturn(testTokenResponse);
    TokenResponseDto result = loginService.tryToLogin(createValidLoginRequest());
    assertNotNull(result.refreshToken());
}
```

Analogicznie, jeśli testowany jest zarówno wynik metody jak i interakcja z mockiem, tworzymy **dwa osobne testy**:

```java
@Test
void deleteUser_shouldDeleteUser_whenUserExists() {
    UUID userId = testUser.getId();
    when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
    userService.deleteUser(userId);
    verify(userRepository, times(1)).delete(testUser);
}

@Test
void deleteUser_shouldFindUserBeforeDeleting_whenUserExists() {
    UUID userId = testUser.getId();
    when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
    userService.deleteUser(userId);
    verify(userRepository, times(1)).findById(userId);
}
```

---

## 2. Nazewnictwo testów

Metody testowe stosują konwencję: `nazwaMetody_shouldOczekiwaneZachowanie_whenWarunek`.

```java
void tryToLogin_shouldReturnToken_whenCredentialsAreValid()
void tryToLogin_shouldThrowBusinessException_whenLoginDataIsEmpty()
void deleteUser_shouldThrowBusinessException_whenUserNotFound()
void getJobById_shouldReturnJob_whenJobExists()
void createJob_shouldThrowBusinessException_whenCandidateNotChosen()
void getAllUsers_shouldReturnEmptyPage_whenNoUsersExist()
```

Segment `_when` / `_given` jest opcjonalny, gdy warunek wynika jasno z nazwy, ale jeśli istnieje kilka wariantów tego samego wywołania – zawsze go dodajemy.

Testy integracyjne stosują konwencję BDD:

```java
void shouldReturnUsersList_whenAdminRequestsAllUsers()
void shouldReturn403_whenUnauthorizedUserAttemptsToDeleteAccount()
void shouldCreateJob_whenOfferHasChosenCandidate()
void shouldThrowBusinessException_whenRegisteringWithDuplicateEmail()
```

---

## 3. Ekstrakcja stałych wartości do pól `private static final`

Literały łańcuchowe i liczbowe używane w testach wyciągamy do pól `private static final` na poziomie klasy. Eliminuje to "magic values", ułatwia zmianę wartości w jednym miejscu i pozwala nadać im znaczącą nazwę.

**Źle:**

```java
when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(testUser));
assertEquals("user@example.com", result.getEmail());
```

**Dobrze:**

```java
private static final String TEST_EMAIL       = "user@example.com";
private static final String TEST_USERNAME    = "testuser";
private static final String TEST_PASSWORD    = "password123";
private static final int    TEST_PHONE       = 123456789;

// ...

when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));
assertEquals(TEST_EMAIL, result.getEmail());
```

Stałe współdzielone między wieloma klasami testowymi umieszczamy w dedykowanej klasie danych testowych (patrz punkt 7), np.:

```java
// CoreMockData.java
public static final String API_PATH           = "/";
public static final String TEST_ERROR_MESSAGE = "Error Message";
public static final String TEST_CODE          = "TEST_CODE";
```

---

## 4. Metoda `@BeforeEach` tylko dla danych wspólnych dla wszystkich testów

W metodzie `setUp()` umieszczamy wyłącznie te dane, których potrzebuje **zdecydowana większość lub wszystkie** testy w klasie. Jeśli jakaś zmienna lub mock jest potrzebny tylko w kilku testach, inicjalizujemy ją **lokalnie** w tych testach lub wyciągamy do prywatnej metody pomocniczej. Metoda `setUp()` musi być opatrzona adnotacją `@BeforeEach` – **nie wywołujemy jej ręcznie** wewnątrz każdego testu.

**Źle** – `setUp()` bez adnotacji, wywoływana ręcznie i inicjalizująca zmienne potrzebne tylko w wybranych testach:

```java
// Brak @BeforeEach – setUp() wywoływane ręcznie w każdym teście
void setUp() {
    testJob = createTestJob();
    testJobDispatcher = createTestJobDispatcher(); // używane tylko w części testów!
    jobService = new JobServiceDefault(jobRepository, offerService, fileManagementService);
}

@Test
void getJobById_shouldReturnJob_whenJobExists() {
    setUp(); // ręczne wywołanie – błąd
    when(jobRepository.findById(jobId)).thenReturn(Optional.of(testJob));
    Job result = jobService.getJobById(jobId);
    assertNotNull(result);
}
```

**Dobrze** – `setUp()` adnotowana `@BeforeEach`, inicjalizuje tylko dane potrzebne wszędzie; zmienne lokalne tam, gdzie są potrzebne:

```java
@BeforeEach
void setUp() {
    testUser = createTestUser();
    testTokenResponse = createTestTokenResponse();
}

@Test
void tryToLogin_shouldReturnToken_whenCredentialsAreValid() {
    when(defaultLoginValidation.userValidation(TEST_EMAIL)).thenReturn(testUser);
    when(passwordConfiguration.verifyPassword(TEST_PASSWORD, testUser.getPasswordHash())).thenReturn(true);
    when(refreshTokenServiceDefault.createTokensSet(testUser)).thenReturn(testTokenResponse);
    TokenResponseDto result = loginService.tryToLogin(createValidLoginRequest());
    assertNotNull(result);
}

@Test
void tryToLogin_shouldThrowBusinessException_whenPasswordHashIsNull() {
    User userWithoutPassword = createTestUser();
    userWithoutPassword.setPasswordHash(null); // lokalna zmienna – tylko ten test jej potrzebuje
    when(defaultLoginValidation.userValidation(TEST_EMAIL)).thenReturn(userWithoutPassword);
    assertThrows(BusinessException.class, () -> loginService.tryToLogin(createValidLoginRequest()));
}
```

Jeśli klasa testowa jest mała i zawiera kilka testów o zupełnie różnych setupach, `@BeforeEach` można pominąć w całości – każdy test samodzielnie konfiguruje wszystko, czego potrzebuje.

---

## 5. Ekstrakcja konfiguracji mocków i tworzenia obiektów do prywatnych metod pomocniczych

Gdy kilka testów wymaga identycznej (lub bardzo podobnej) konfiguracji mocków, która nie pasuje do `@BeforeEach` (bo nie jest potrzebna wszędzie), wyciągamy ją do prywatnej metody pomocniczej.

```java
private void setupSecretKeyMock() {
    when(accessTokenSecretKeyManager.getSecretKey()).thenReturn("test-secret-key-for-testing-purposes");
}

private void setupValidRegisterMocks() {
    when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn("$2a$10$hashedpassword123");
    when(passwordConfiguration.passwordEncoder()).thenReturn(passwordEncoder);
    doNothing().when(registerDataManager).checkRegisterDataDefault(TEST_USERNAME, TEST_EMAIL, TEST_PHONE, TEST_PASSWORD);
    when(userRepository.save(any(User.class))).thenReturn(testUser);
    when(refreshTokenServiceDefault.createTokensSet(any(User.class))).thenReturn(testTokenResponse);
}

@Test
void tryToRegister_shouldReturnProperTokenStructure_whenCredentialsAreValid() {
    setupValidRegisterMocks();
    TokenResponseDto result = registerService.tryToRegister(createValidRegisterRequest());
    assertThat(result).isNotNull();
}

@Test
void tryToRegister_shouldHashPasswordBeforeSaving() {
    setupValidRegisterMocks();
    registerService.tryToRegister(createValidRegisterRequest());
    verify(passwordEncoder).encode(TEST_PASSWORD);
}
```

Tę samą zasadę stosujemy do **mocków tworzonych przez `mock()` wewnątrz metody testowej** – wyciągamy je do osobnych metod prywatnych, zamiast tworzyć inline:

**Źle** – mock tworzony bezpośrednio w ciele testu:

```java
@Test
void createJob_shouldSaveJobWithPhoto_whenOfferHasPhoto() {
    testOffer.setChosenCandidate(createTestUser());
    MultipartFile photo = mock(MultipartFile.class); // inline – niepotrzebnie zaśmieca test
    ProcessedFileDetails fileDetails = new ProcessedFileDetails("example-key", "example.jpg", MimeType.JPG, "example", 1024, null);

    when(jobRepository.findById(jobId)).thenReturn(Optional.of(testJob));
    when(fileManagementService.processFileDetails(any(), any())).thenReturn(fileDetails);
    when(jobRepository.save(any(Job.class))).thenReturn(testJob);

    jobService.createJob(testOffer);

    verify(fileManagementService, times(1)).uploadFile(any());
}
```

**Dobrze** – mock wyekstraktowany do prywatnej metody fabrycznej:

```java
@Test
void createJob_shouldSaveJobWithPhoto_whenOfferHasPhoto() {
    testOffer.setChosenCandidate(createTestUser());
    ProcessedFileDetails fileDetails = createTestFileDetails();

    when(jobRepository.findById(jobId)).thenReturn(Optional.of(testJob));
    when(fileManagementService.processFileDetails(any(), any())).thenReturn(fileDetails);
    when(jobRepository.save(any(Job.class))).thenReturn(testJob);

    jobService.createJob(testOffer);

    verify(fileManagementService, times(1)).uploadFile(any());
}

// ...

private MultipartFile createMockPhoto() {
    return mock(MultipartFile.class);
}

private ProcessedFileDetails createTestFileDetails() {
    return new ProcessedFileDetails("example-key", "example.jpg", MimeType.JPG, "example", 1024, null);
}
```

---

## 6. Struktura testu: Given / When / Then

Każdy test integracyjny piszemy w strukturze trzech faz, oddzielonych komentarzami:

```java
@Test
void shouldReturnNonEmptyUsersList_whenUsersExist() {
    // given
    UserFilterRequestDto filterDto = new UserFilterRequestDto(TEST_USERNAME, TEST_EMAIL, null, null);
    Pageable pageable = PageRequest.of(0, 10);
    Page<User> expectedPage = new PageImpl<>(List.of(testUser), pageable, 1);
    when(userRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);

    // when
    Page<User> result = userService.getAllUsers(filterDto, pageable);

    // then
    assertThat(result.getTotalElements()).isEqualTo(1);
}
```

---

## 7. Klasy danych testowych (MockData)

Dane testowe nie są tworzone inline w każdym teście z osobna – zamiast tego korzystamy z dedykowanych klas fabrycznych w pakiecie `mockdata`. Każda klasa odpowiada jednemu obszarowi domenowemu.

```
core/mockdata/CoreMockData.java
feature/unit/user/mockdata/UserMockData.java
feature/unit/job/mockdata/JobMockData.java
feature/unit/security/mockdata/SecurityMockData.java
```

Klasy te zawierają metody statyczne zwracające gotowe obiekty testowe:

```java
// UserMockData.java
public static User createTestUser() { ... }
public static User createTestUserWithProfilePhoto() { ... }
public static User createTestUserWithoutPassword() { ... }
```

Stałe wartości współdzielone między testami (np. adresy e-mail, ścieżki API) umieszczamy w tych klasach jako `public static final`:

```java
public static final String TEST_EMAIL    = "user@example.com";
public static final String TEST_USERNAME = "testuser";
public static final String API_PATH      = "/";
```

Metody i stałe z klas MockData importujemy **pojedynczo przez `static import`** i używamy bez kwalifikatora klasy. Nigdy nie wołamy `KlasaMockData.metoda()` bezpośrednio w teście.

**Źle:**
```java
import com.mimaja.job_finder_app.feature.unit.user.mockdata.UserMockData;
import com.mimaja.job_finder_app.core.mockdata.CoreMockData;

// ...

testUser = UserMockData.createTestUser();
when(userRepository.findByEmail(CoreMockData.TEST_EMAIL)).thenReturn(Optional.of(testUser));
```

**Dobrze:**
```java
import static com.mimaja.job_finder_app.feature.unit.user.mockdata.UserMockData.createTestUser;
import static com.mimaja.job_finder_app.feature.unit.user.mockdata.UserMockData.createTestUserWithProfilePhoto;
import static com.mimaja.job_finder_app.core.mockdata.CoreMockData.TEST_EMAIL;
import static com.mimaja.job_finder_app.core.mockdata.CoreMockData.API_PATH;

// ...

testUser = createTestUser();
when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));
```

---

## 8. Asercje – preferuj AssertJ

Preferujemy asercje z biblioteki AssertJ (`assertThat(...).isEqualTo(...)`) ze względu na czytelniejsze komunikaty błędów i możliwość łańcuchowania. JUnit 5 (`assertEquals`, `assertThrows`, `assertNull`) jest dopuszczalne, ale nie należy mieszać obu stylów w obrębie jednego testu.

```java
// AssertJ – preferowane
assertThat(result.getId()).isEqualTo(userId);
assertThat(result.getContent()).isEmpty();
assertThatThrownBy(() -> userService.getUserById(UUID.randomUUID()))
        .isInstanceOf(BusinessException.class);

// JUnit 5 – dopuszczalne
assertThrows(BusinessException.class, () -> loginService.tryToLogin(null),
        "Should throw exception for null request");
assertNotNull(result, "Created job should not be null");
assertNull(result.getProfilePhoto(), "Profile photo should be null when not set");
```

Przy asercjach dodajemy komunikat opisujący oczekiwanie zawsze wtedy, gdy sama asercja nie jest oczywista:

```java
assertThat(exception.getCode())
        .as("Exception code should indicate user not found")
        .isEqualTo(BusinessExceptionReason.USER_NOT_FOUND.getCode());

assertThat(decodedJWT.getExpiresAt())
        .as("Token should expire after issued at")
        .isAfter(decodedJWT.getIssuedAt());
```

---

## 9. Testy jednostkowe – Mockito

Testy jednostkowe adnotujemy `@ExtendWith(MockitoExtension.class)`. Mocki wstrzykujemy przez `@Mock` i `@InjectMocks` – nie używamy `MockitoAnnotations.openMocks(this)`.

```java
@ExtendWith(MockitoExtension.class)
class LoginServiceDefaultTest {
    @Mock private DefaultLoginValidation defaultLoginValidation;
    @Mock private PasswordConfiguration passwordConfiguration;
    @Mock private RefreshTokenServiceDefault refreshTokenServiceDefault;
    @InjectMocks private LoginServiceDefault loginService;
    // ...
}
```

Gdy klasa testowana nie obsługuje wstrzykiwania przez `@InjectMocks` (np. brak konstruktora bezargumentowego lub niestandardowy konstruktor), tworzymy instancję ręcznie w metodzie `@BeforeEach`:

```java
@BeforeEach
void setUp() {
    userService = new UserServiceDefault(
        userRepository,
        userMapper,
        registerDataManager,
        passwordConfiguration
    );
}
```

Styl konfiguracji mocków – BDD (`given/willReturn`) lub klasyczny (`when/thenReturn`) – może być stosowany zamiennie, ale zachowujemy spójność **w obrębie jednej klasy testowej**:

```java
// Classic style – stosowany w tym projekcie
when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
when(jobRepository.findById(jobId)).thenReturn(Optional.empty());
doNothing().when(registerDataManager).checkRegisterDataDefault(...);
doThrow(new BusinessException(BusinessExceptionReason.USER_NOT_FOUND)).when(userService).deleteUser(userId);
```

---

## 10. Testy integracyjne – klasy bazowe

Testy integracyjne **dziedziczą** z odpowiedniej klasy bazowej (`UserIntegrationTest`, `JobIntegrationTest` itp.), która sama dziedziczy z `IntegrationTest`. Klasa bazowa dostarcza kontener bazy danych (TestContainers), konfigurację bezpieczeństwa i metody pomocnicze (`createTestUser`, `getAuthToken` itp.).

```java
@Tag("IntegrationTest")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class UserControllerGetIntegrationTest extends UserIntegrationTest {

    @BeforeEach
    @Override
    protected void setUp() {
        super.setUp();
        initializeTestData();
    }

    // ...
}
```

W `@BeforeEach` testu integracyjnego zawsze wywołujemy `super.setUp()` przed własną inicjalizacją.

---

## 11. Testy parametryzowane

Gdy ten sam scenariusz testowy dotyczy wielu wartości wejściowych, używamy `@ParameterizedTest` z `@MethodSource`. Dostawca danych (`Stream<String>` lub `Stream<Arguments>`) umieszczamy w klasie MockData lub bezpośrednio w klasie testowej jako metodę `static`.

```java
@ParameterizedTest
@MethodSource("com.mimaja.job_finder_app.core.mockdata.CoreMockData#provideInvalidEmailScenarios")
void tryToRegister_shouldThrowBusinessException_whenEmailIsInvalid(String invalidEmail) {
    RegisterRequestDto request = createRegisterRequestWithEmail(invalidEmail);
    doThrow(new BusinessException(BusinessExceptionReason.WRONG_LOGIN_DATA))
            .when(registerDataManager).checkRegisterDataDefault(TEST_USERNAME, invalidEmail, TEST_PHONE, TEST_PASSWORD);

    assertThrows(BusinessException.class,
            () -> registerService.tryToRegister(request),
            "Should throw BusinessException for invalid email: " + invalidEmail);
}

// W CoreMockData:
public static Stream<String> provideInvalidEmailScenarios() {
    return Stream.of(
            "invalid-email",
            "missing-at-sign.com",
            "@nodomain",
            "");
}
```

---

## 12. Kolejność metod w klasie testowej

Metody prywatne umieszczamy **zawsze na końcu pliku**, po wszystkich metodach testowych. Kolejność metod w klasie testowej jest następująca:

1. Pola klasy (`private static final`, `@Mock`, `@InjectMocks`, pola instancji)
2. Metoda `@BeforeEach setUp()`
3. Metody testowe (`@Test`, `@ParameterizedTest`)
4. Prywatne metody pomocnicze (fabryki obiektów, konfiguracje mocków)

```java
@ExtendWith(MockitoExtension.class)
class LoginServiceDefaultTest {

    // 1. Pola klasy
    private static final String TEST_EMAIL    = "user@example.com";
    private static final String TEST_PASSWORD = "password123";

    @Mock private DefaultLoginValidation defaultLoginValidation;
    @Mock private PasswordConfiguration passwordConfiguration;
    @InjectMocks private LoginServiceDefault loginService;

    private User testUser;

    // 2. setUp
    @BeforeEach
    void setUp() {
        testUser = createTestUser();
    }

    // 3. Testy
    @Test
    void tryToLogin_shouldReturnToken_whenCredentialsAreValid() {
        setupValidLoginMocks();
        TokenResponseDto result = loginService.tryToLogin(createValidLoginRequest());
        assertNotNull(result);
    }

    @Test
    void tryToLogin_shouldThrowException_whenRequestIsNull() {
        assertThrows(NullPointerException.class, () -> loginService.tryToLogin(null));
    }

    // 4. Metody prywatne – zawsze na samym dole
    private void setupValidLoginMocks() {
        when(defaultLoginValidation.userValidation(TEST_EMAIL)).thenReturn(testUser);
        when(passwordConfiguration.verifyPassword(TEST_PASSWORD, testUser.getPasswordHash())).thenReturn(true);
    }

    private User createTestUser() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(TEST_EMAIL);
        user.setPasswordHash("$2a$10$hashedpassword123");
        return user;
    }

    private LoginRequestDto createValidLoginRequest() {
        return new LoginRequestDto(TEST_EMAIL, TEST_PASSWORD);
    }
}
```

