package sb.service.test;


import sb.exception.AccountNotFoundException;
import sb.exception.RestApiException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import sb.model.*;
import sb.service.AccountsService;
import sb.service.SavingsGoalsService;
import sb.util.RestHelper;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class AccountsServiceTest {
    private final String accountUid = "accountUid";
    @Mock
    @Autowired
    private RestHelper restHelper;

    @InjectMocks
    @Autowired
    private AccountsService accountsService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void givenvalidAccountId_whenGetAccountIsCalled_ReturnsAccount() throws RestApiException, AccountNotFoundException {
        List<Account> accountList = new ArrayList<>();
        accountList.add(Account.builder().accountUid(accountUid).accountType("type").currency("GBP").build());
        AccountsList accountsList = new AccountsList(accountList);
        when(restHelper.performGet(any(), any())).thenReturn(ResponseEntity.ok(accountsList));
        Assert.assertEquals(accountUid, accountsService.getAccount(accountUid).getAccountUid());
    }

    @Test(expected = AccountNotFoundException.class)
    public void givenInvalidAccountId_whenGetAccountIsCalled_ThrowsException() throws RestApiException, AccountNotFoundException {
        List<Account> accountList = new ArrayList<>();
        accountList.add(Account.builder().accountUid("accountUID2").accountType("type").currency("GBP").build());
        AccountsList accountsList = new AccountsList(accountList);
        when(restHelper.performGet(any(), any())).thenReturn(ResponseEntity.ok(accountsList));
        accountsService.getAccount(accountUid);
    }

    @Test(expected = RestApiException.class)
    public void givenGetAccountIsCalled_whenStarlingAPIThrowsException_ThrowsRestApiException() throws RestApiException, AccountNotFoundException {
        when(restHelper.performGet(any(), any())).thenThrow(new RestClientException("test"));
        accountsService.getAccount(accountUid);
    }

    @Test
    public void whenGetAllAccountsIsCalled_ReturnsAllAccounts() throws RestApiException {
        List<Account> accountList = new ArrayList<>();
        accountList.add(Account.builder().accountUid(accountUid).accountType("type").currency("GBP").build());
        AccountsList accountsList = new AccountsList(accountList);
        when(restHelper.performGet(any(), any())).thenReturn(ResponseEntity.ok(accountsList));
        AccountsList accountsListActual = accountsService.getAllAccountsForCustomer();
        Assert.assertEquals(1, accountsListActual.getAccounts().size());
        Assert.assertEquals(accountUid, accountsListActual.getAccounts().get(0).getAccountUid());
    }

    @Test(expected = RestApiException.class)
    public void givenGetAllAccountsIsCalled_whenStarlingApiThrowsException_throwsRestApiException() throws RestApiException {
        when(restHelper.performGet(any(), any())).thenThrow(new RestClientException("test"));
        accountsService.getAllAccountsForCustomer();
    }

}
