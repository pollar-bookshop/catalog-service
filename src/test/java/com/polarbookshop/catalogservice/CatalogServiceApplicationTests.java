package com.polarbookshop.catalogservice;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.polarbookshop.catalogservice.domain.Book;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration") // application-integratioin.yml에서 설정을 로드하기 위해 프로파일 활성화
@Testcontainers // 테스트 컨테이너의 자동 시작/중지를 활성화한다.
class CatalogServiceApplicationTests {
	private static KeycloakToken bjornTokens;
	private static KeycloakToken isabelleTokens;
	@Autowired
	private WebTestClient webTestClient;

	@Container // 테스트를 위한 키클록 컨테이너 정의 (test-resources.test-realm-config.json을 통해 초기화 됨)
	private static final KeycloakContainer keyCloakContainer = new KeycloakContainer("quay.io/keycloak/keycloak:19.0").withRealmImportFile("test-realm-config.json");

	@DynamicPropertySource // 키클록 발행자 URI가 테스트 키클록 인스턴스를 가리키도록 변경
	static void dynamicProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri",
				() -> keyCloakContainer.getAuthServerUrl() + "realms/PolarBookshop");
	}

	@BeforeAll
	static void generateAccessTokens() {
		WebClient webClient = WebClient.builder()
				.baseUrl(keyCloakContainer.getAuthServerUrl() + "realms/PolarBookshop/protocol/openid-connect/token")
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.build();

		// 각 사용자로 인증하고 엑세스 토큰을 얻는다.
		isabelleTokens = authenticateWith("isabelle", "password", webClient);
		bjornTokens = authenticateWith("bjorn", "password", webClient);
	}

	@Test
	void whenPostRequestThenBookCreated() {
		var expectedBook = Book.of("1231231231", "Title", "Author", 9.90, "Polarsophia");

		webTestClient
				.post()
				.uri("/books")
				.headers(headers -> headers.setBearerAuth(isabelleTokens.accessToken()))
				.bodyValue(expectedBook)
				.exchange()
				.expectStatus().isCreated()
				.expectBody(Book.class).value(actualBook -> {
					assertThat(actualBook).isNotNull();
					assertThat(actualBook.getIsbn()).isEqualTo(expectedBook.getIsbn());
				});
	}

	@Test
	void whenPostRequestUnauthenticatedThen401() {
		var expectedBook = Book.of("1231231231", "Title", "Author", 9.90, "Polarsophia");

		webTestClient
				.post()
				.uri("/books")
				.bodyValue(expectedBook)
				.exchange()
				.expectStatus().isUnauthorized();
	}

//	@Test
//	void whenPostRequestUnauthorizedThen403() {
//		var expectedBook = Book.of("1231231231", "Title", "Author", 9.90, "Polarsophia");
//
//		webTestClient
//				.post()
//				.uri("/books")
//				.headers(headers -> headers.setBearerAuth(bjornTokens.accessToken()))
//				.bodyValue(expectedBook)
//				.exchange()
//				.expectStatus().isForbidden();
//	}

	private static KeycloakToken authenticateWith(String username, String password, WebClient webClient) {
		return webClient
				.post()
				.body(
						BodyInserters.fromFormData("grant_type", "password")
								.with("client_id", "polar-test")
								.with("username", username)
								.with("password", password)
				)
				.retrieve()
				.bodyToMono(KeycloakToken.class)
				.block();
	}

	private record KeycloakToken(String accessToken) {
		@JsonCreator
		private KeycloakToken(@JsonProperty("access_token") final String accessToken) {
			this.accessToken = accessToken;
		}
	}
}
