package sb.util.test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import sb.model.Account;
import sb.model.AccountsList;
import sb.util.RestHelper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
public class RestHelperTest {
    @InjectMocks
    @Autowired
    private RestHelper restHelper;

    @Mock
    @Autowired
    private RestTemplate restTemplate;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void whengetHttpEntityWithHeadersIsCalled_returnsRightEntity() {
        HttpEntity<Account> entity = restHelper.getHttpEntityWithHeaders();
        Assert.assertNotNull(entity.getHeaders());
    }

    @Test
    public void whengetHttpEntityWithBodyHeadersIsCalled_returnsRightEntity() {
        HttpEntity<Account> entity = restHelper.getHttpEntityWithRequestBodyAndHeaders(Account.builder().build());
        Assert.assertNotNull(entity.getHeaders());
        Assert.assertNotNull(entity.getBody());
    }

    @Test
    public void whenperformPutCalled_thenReturnsSuccess() {
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class))).thenReturn(ResponseEntity.ok(AccountsList.builder().build()));
        ResponseEntity<AccountsList> res=restHelper.performPut("testurl",Account.builder().build(),AccountsList.class);
        Assert.assertTrue(res.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(200)));
        Assert.assertNotNull(res.getBody());
    }
    @Test
    public void whenperformGetCalled_thenReturnsSuccess() {
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class))).thenReturn(ResponseEntity.ok().build());
        ResponseEntity<AccountsList> res=restHelper.performGet("testurl", AccountsList.class);
        Assert.assertTrue(res.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(200)));
    }
}
