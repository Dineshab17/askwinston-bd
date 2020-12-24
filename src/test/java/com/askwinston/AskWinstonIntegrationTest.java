package com.askwinston;

/*import com.askwinston.model.User;
import com.askwinston.service.UserService;
import com.askwinston.web.secuity.JwtService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("inmemory")
public class AskWinstonIntegrationTest {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@MockBean
	private UserService userService;

	@Autowired
	private JwtService jwtService;

	@Test
	public void forbidden403WithoutToken() {
		ResponseEntity<String> responseEntity = restTemplate.getForEntity(url("/user/profile"), String.class);

		Assert.assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
	}

	@Test
	public void forbidden403WithWrongRole() {
		HttpHeaders headers = new HttpHeaders();
		String token = jwtService.createToken(123L, "my@email.org", User.Authority.ADMIN);
		headers.add("Authorization", "Bearer " + token);
		HttpEntity<String> requestEntity = new HttpEntity<>(headers);
		ResponseEntity<String> responseEntity = restTemplate.exchange(url("/user/profile"), HttpMethod.GET,
				requestEntity, String.class);

		Assert.assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
	}

	@Test
	public void ok200WithCorrectRole() {
		User user = new User();
		user.setId(123L);
		user.setEmail("my@email.org");
		user.setAuthority(User.Authority.PATIENT);
		Mockito.when(userService.getById(123L)).thenReturn(user);

		HttpHeaders headers = new HttpHeaders();
		String token = jwtService.createToken(123L, "my@email.org", User.Authority.PATIENT);
		headers.add("Authorization", "Bearer " + token);
		HttpEntity<String> requestEntity = new HttpEntity<>(headers);
		ResponseEntity<String> responseEntity = restTemplate.exchange(url("/user/profile"), HttpMethod.GET,
				requestEntity, String.class);

		Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	}

	private String url(String url) {
		return "http://localhost:" + port + url;
	}
}*/