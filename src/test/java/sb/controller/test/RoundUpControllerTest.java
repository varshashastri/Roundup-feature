package sb.controller.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import sb.beans.ObjectMapperProvider;
import sb.controller.RoundupController;
import sb.exception.AccountNotFoundException;
import sb.exception.RestApiException;
import sb.exception.SavingsGoalMoneyTransferException;
import sb.model.*;
import sb.service.AccountsService;
import sb.service.RoundupService;
import sb.service.SavingsGoalsService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class RoundUpControllerTest {
    public static final String ACCOUNT_UID = "accountUid";
    @Autowired
    @Mock
    public AccountsService accountsService;
    @Autowired
    @Mock
    public SavingsGoalsService savingsGoalsService;
    @Autowired
    @Mock
    public RoundupService roundupService;
    @Autowired
    @Mock
    ObjectMapper objectMapper;
    @InjectMocks
    @Autowired
    private RoundupController roundupController;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void givenTransferToSavingsForGivenAccountIsCalled_WhenValidInputs_thenTransferIsSuccessful() throws RestApiException, AccountNotFoundException, SavingsGoalMoneyTransferException {
        when(roundupService.getAccountRoundUp(any(), any())).thenReturn(5);
        when(accountsService.getAccount(any())).thenReturn(Account.builder().accountUid(ACCOUNT_UID).build());
        when(savingsGoalsService.getOrCreateSavingsGoal(any(), any())).thenReturn("transferUid");
        when(savingsGoalsService.transferAmountToSavingsGoal(any(), any(), any())).thenReturn("transferUid");
        ResponseEntity<String> response = roundupController.transferToSavingsForGivenAccount(ACCOUNT_UID);
        Assert.assertEquals("transferUid", response.getBody());
    }

    @Test
    public void givenTransferToSavingsForGivenAccountIsCalled_WhenAccountNotPresent_thenthrowsInternalError() throws RestApiException, AccountNotFoundException, SavingsGoalMoneyTransferException {
        when(roundupService.getAccountRoundUp(any(), any())).thenReturn(5);
        when(accountsService.getAccount(any())).thenThrow(new AccountNotFoundException("test"));
        when(savingsGoalsService.getOrCreateSavingsGoal(any(), any())).thenReturn("transferUid");
        when(savingsGoalsService.transferAmountToSavingsGoal(any(), any(), any())).thenReturn("transferUid");

        ResponseEntity<String> response = roundupController.transferToSavingsForGivenAccount(ACCOUNT_UID);
        Assert.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void givenTransferToSavingsForGivenAccountIsCalled_WhenCannotTransferAmount_thenthrowsInternalError() throws RestApiException, AccountNotFoundException, SavingsGoalMoneyTransferException {
        when(roundupService.getAccountRoundUp(any(), any())).thenReturn(5);
        when(accountsService.getAccount(any())).thenReturn(Account.builder().accountUid(ACCOUNT_UID).build());
        when(savingsGoalsService.getOrCreateSavingsGoal(any(), any())).thenReturn("transferUid");
        when(savingsGoalsService.transferAmountToSavingsGoal(any(), any(), any())).thenThrow(new SavingsGoalMoneyTransferException("test"));

        ResponseEntity<String> response = roundupController.transferToSavingsForGivenAccount(ACCOUNT_UID);
        Assert.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void givenTransferToSavingsForGivenAccountIsCalled_WhenCannotGetSavingsGoal_thenthrowsInternalError() throws RestApiException, AccountNotFoundException, SavingsGoalMoneyTransferException {
        when(roundupService.getAccountRoundUp(any(), any())).thenReturn(5);
        when(accountsService.getAccount(any())).thenReturn(Account.builder().accountUid(ACCOUNT_UID).build());
        when(savingsGoalsService.getOrCreateSavingsGoal(any(), any())).thenThrow(new RestApiException("test"));
        when(savingsGoalsService.transferAmountToSavingsGoal(any(), any(), any())).thenReturn("transferUid");

        ResponseEntity<String> response = roundupController.transferToSavingsForGivenAccount(ACCOUNT_UID);
        Assert.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void givenTransferToSavingsForGivenAccountIsCalled_WhenAccountRoundupFails_thenthrowsInternalError() throws RestApiException, AccountNotFoundException, SavingsGoalMoneyTransferException {
        when(roundupService.getAccountRoundUp(any(), any())).thenThrow(new RestApiException("test"));
        when(accountsService.getAccount(any())).thenReturn(Account.builder().accountUid(ACCOUNT_UID).build());
        when(savingsGoalsService.getOrCreateSavingsGoal(any(), any())).thenReturn("transferUid");
        when(savingsGoalsService.transferAmountToSavingsGoal(any(), any(), any())).thenReturn("transferUid");

        ResponseEntity<String> response = roundupController.transferToSavingsForGivenAccount(ACCOUNT_UID);
        Assert.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void givenTransferToSavingsForAllAccountsIsCalled_WhenValidInputs_thenreturnsAccountroundups() throws RestApiException, SavingsGoalMoneyTransferException, JsonProcessingException {
        Map<String, Integer> accountRoundups = new HashMap<>();
        accountRoundups.put("test", 1);
        String expectedString = "{\"TransferUIDs\":[\"transferUid\"]}";
        when(roundupService.getAccountRoundups(any())).thenReturn(accountRoundups);
        when(savingsGoalsService.getOrCreateSavingsGoal(any(), any())).thenReturn("transferUid");
        when(savingsGoalsService.transferAmountToSavingsGoal(any(), any(), any())).thenReturn("transferUid");
        when(objectMapper.writeValueAsString(any())).thenReturn(expectedString);

        ResponseEntity<String> response = roundupController.transferToSavingsForAllAccounts();
        Assert.assertEquals(expectedString, response.getBody());
    }

    @Test
    public void givenTransferToSavingsForAllAccountsIsCalled_WhenInValidInputs_thenInternalServerError() throws RestApiException, SavingsGoalMoneyTransferException, JsonProcessingException {
        Map<String, Integer> accountRoundups = new HashMap<>();
        accountRoundups.put("test", 1);
        String expectedString = "{\"TransferUIDs\":[\"transferUid\"]}";
        when(roundupService.getAccountRoundups(any())).thenReturn(accountRoundups);
        when(savingsGoalsService.getOrCreateSavingsGoal(any(), any())).thenReturn("transferUid");
        when(savingsGoalsService.transferAmountToSavingsGoal(any(), any(), any())).thenReturn("transferUid");
        when(objectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException(""){});

        ResponseEntity<String> response = roundupController.transferToSavingsForAllAccounts();
        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void givenTransferToSavingsForAllAccountsIsCalled_WhencantgetAccountRoundups_thenthrowsInternalServerError() throws RestApiException, SavingsGoalMoneyTransferException {
        Map<String, Integer> accountRoundups = new HashMap<>();
        accountRoundups.put("test", 1);
        when(roundupService.getAccountRoundups(any())).thenThrow(new RestApiException("test"));
        when(savingsGoalsService.getOrCreateSavingsGoal(any(), any())).thenReturn("transferUid");
        when(savingsGoalsService.transferAmountToSavingsGoal(any(), any(), any())).thenReturn("transferUid");

        ResponseEntity<String> response = roundupController.transferToSavingsForAllAccounts();
        Assert.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @Test
    public void givenTransferToSavingsForAllAccountsIsCalled_WhenCantCreateSavingsGoal_thenthrowsInternalServerError() throws RestApiException, SavingsGoalMoneyTransferException {
        Map<String, Integer> accountRoundups = new HashMap<>();
        accountRoundups.put("test", 1);
        when(roundupService.getAccountRoundups(any())).thenReturn(accountRoundups);
        when(savingsGoalsService.getOrCreateSavingsGoal(any(), any())).thenThrow(new RestApiException("test"));
        when(savingsGoalsService.transferAmountToSavingsGoal(any(), any(), any())).thenReturn("transferUid");

        ResponseEntity<String> response = roundupController.transferToSavingsForAllAccounts();
        Assert.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void givenTransferToSavingsForAllAccountsIsCalled_WhenCantTransferAmount_thenThrowsInternalServerError() throws RestApiException, SavingsGoalMoneyTransferException {
        Map<String, Integer> accountRoundups = new HashMap<>();
        accountRoundups.put("test", 1);
        when(roundupService.getAccountRoundups(any())).thenReturn(accountRoundups);
        when(savingsGoalsService.getOrCreateSavingsGoal(any(), any())).thenReturn("transferUid");
        when(savingsGoalsService.transferAmountToSavingsGoal(any(), any(), any())).thenThrow(new SavingsGoalMoneyTransferException("test"));

        ResponseEntity<String> response = roundupController.transferToSavingsForAllAccounts();
        Assert.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void givenGetRoundupsAmountForAllAccountIsCalled_whenValidInputs_thenroundupsAvailableAreReturned() throws RestApiException, JsonProcessingException {
        List<Account> accountList = new ArrayList<>();
        String expectedResult = "{\"accountUID2\":1}";
        accountList.add(Account.builder().accountUid("accountUID2").accountType("type").currency("GBP").build());
        AccountsList accountsList = new AccountsList(accountList);
        Map<String, Integer> accountRoundups = new HashMap<>();
        accountRoundups.put("accountUID2", 1);
        when(accountsService.getAllAccountsForCustomer()).thenReturn(accountsList);
        when(roundupService.getAccountRoundups(accountsList)).thenReturn(accountRoundups);
        when(objectMapper.writeValueAsString(any())).thenReturn((new ObjectMapper()).writeValueAsString(accountRoundups));
        ResponseEntity<String> result = roundupController.getRoundupAmountsForAllAccounts();
        Assert.assertEquals(expectedResult, result.getBody());
    }

    @Test
    public void givenGetRoundupsAmountForAllAccountIsCalled_whenInValidInputs_thenThrowsInternalServerError() throws RestApiException, JsonProcessingException {
        List<Account> accountList = new ArrayList<>();
        String expectedResult = "{\"accountUID2\":1}";
        accountList.add(Account.builder().accountUid("accountUID2").accountType("type").currency("GBP").build());
        AccountsList accountsList = new AccountsList(accountList);
        Map<String, Integer> accountRoundups = new HashMap<>();
        accountRoundups.put("accountUID2", 1);
        when(accountsService.getAllAccountsForCustomer()).thenReturn(accountsList);
        when(roundupService.getAccountRoundups(accountsList)).thenReturn(accountRoundups);
        when(objectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("test"){});
        ResponseEntity<String> result = roundupController.getRoundupAmountsForAllAccounts();
        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
    }
    @Test
    public void givenGetRoundupsAmountForGivenAccountIsCalled_whengetAllAccountsForCustomerthrowsException_thenInternalServerError() throws RestApiException {
        Map<String, Integer> accountRoundups = new HashMap<>();
        accountRoundups.put("accountUID2", 1);
        when(accountsService.getAllAccountsForCustomer()).thenThrow(new RestApiException("test"));
        when(roundupService.getAccountRoundups(any())).thenReturn(accountRoundups);
        ResponseEntity<String> result = roundupController.getRoundupAmountsForAllAccounts();
        Assert.assertEquals(result.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void givenGetRoundupsAmountForGivenAccountIsCalled_whenValidInputs_thenroundupsAvailableAreReturned() throws RestApiException, AccountNotFoundException {
        Account account = Account.builder().accountUid("accountUID2").accountType("type").currency("GBP").build();
        when(accountsService.getAccount(any())).thenReturn(account);
        when(roundupService.getAccountRoundUp(any(), any())).thenReturn(1);
        ResponseEntity<Integer> result = roundupController.getRoundupsAmountForGivenAccount(ACCOUNT_UID);
        Assert.assertEquals(1, (int) result.getBody());
    }

    @Test
    public void givengetRoundupsAmountForGivenAccountIsCalled_whenExceptionIsThrown_thenInternalServerError() throws RestApiException, AccountNotFoundException {
        when(accountsService.getAccount(any())).thenThrow(new AccountNotFoundException("test"));
        when(roundupService.getAccountRoundUp(any(), any())).thenReturn(1);
        ResponseEntity<Integer> result = roundupController.getRoundupsAmountForGivenAccount(ACCOUNT_UID);
        Assert.assertEquals(result.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
}
