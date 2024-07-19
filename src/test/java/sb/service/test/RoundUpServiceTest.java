package sb.service.test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestClientException;
import sb.exception.RestApiException;
import sb.model.*;
import sb.service.RoundupService;
import sb.service.SavingsGoalsService;
import sb.service.TransactionsService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;

@SpringBootTest
public class RoundUpServiceTest {
    public static final String ACCOUNT_UID = "accountUid";
    public static final String ACCOUNT_DEFAULT_CATEGORY = "accountDefaultCategory";
    @Mock
    @Autowired
    private TransactionsService transactionsService;
    @InjectMocks
    @Autowired
    private RoundupService roundupService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void givengetAccountRoundUpIsCalled_whenValidTransactions_thenReturnCorrectRoundUp() throws RestApiException {
        List<Transaction> feedList = new ArrayList<>();
        feedList.add(Transaction
                .builder()
                .amount(Amount
                        .builder()
                        .currency("GBP")
                        .minorUnits(95).build())
                .feedItemUid("feedItemUid").build());
        TransactionsList transactionsList = TransactionsList.builder().feedItems(feedList).build();

        when(transactionsService.getAllTransactionsForAccount(ACCOUNT_UID, ACCOUNT_DEFAULT_CATEGORY)).thenReturn(transactionsList);
        int roundup = roundupService.getAccountRoundUp(ACCOUNT_UID, ACCOUNT_DEFAULT_CATEGORY);
        Assert.assertEquals(5, roundup);
    }

    @Test(expected = RestApiException.class)
    public void givengetAccountRoundUpIsCalled_whenstarlingApiThrowsException_thenRestApiExceptionIsThrown() throws RestApiException {
        when(transactionsService.getAllTransactionsForAccount(ACCOUNT_UID, ACCOUNT_DEFAULT_CATEGORY)).thenThrow(new RestClientException("test"));
        roundupService.getAccountRoundUp(ACCOUNT_UID, ACCOUNT_DEFAULT_CATEGORY);
    }

    @Test
    public void givengetgetRoundUpMinorUnitsIsCalled_whenValidTransactions_thenReturnCorrectRoundUp() {
        List<Transaction> feedList = new ArrayList<>();
        feedList.add(Transaction
                .builder()
                .amount(Amount
                        .builder()
                        .currency("GBP")
                        .minorUnits(95).build())
                .feedItemUid("feedItemUid").build());
        TransactionsList transactionsList = TransactionsList.builder().feedItems(feedList).build();

        int roundup = roundupService.getRoundUpMinorUnits(transactionsList);
        Assert.assertEquals(5, roundup);
    }

    @Test
    public void givengetgetRoundUpMinorUnitsIsCalled_whenNoTransactions_thenReturnZero() {
        int roundup = roundupService.getRoundUpMinorUnits(new TransactionsList());
        Assert.assertEquals(0, roundup);
    }

    @Test
    public void givengetAccountRoundupsIsCalled_whenValidAccountList_thenReturnCorrectRoundUp() throws RestApiException {
        List<Account> accounts = new ArrayList<>();
        List<Transaction> feedList = new ArrayList<>();
        feedList.add(Transaction
                .builder()
                .amount(Amount
                        .builder()
                        .currency("GBP")
                        .minorUnits(95).build())
                .feedItemUid("feedItemUid").build());
        TransactionsList transactionsList = TransactionsList.builder().feedItems(feedList).build();
        when(transactionsService.getAllTransactionsForAccount(ACCOUNT_UID, ACCOUNT_DEFAULT_CATEGORY)).thenReturn(transactionsList);
        accounts.add(Account.builder().accountUid(ACCOUNT_UID).defaultCategory(ACCOUNT_DEFAULT_CATEGORY).build());
        Map<String, Integer> accountRoundUps = roundupService.getAccountRoundups(AccountsList.builder().accounts(accounts).build());
        Assert.assertTrue(accountRoundUps.containsKey(ACCOUNT_UID));
        int roundup = accountRoundUps.get(ACCOUNT_UID);
        Assert.assertEquals(5, roundup);
    }

    @Test(expected = RestApiException.class)
    public void givengetAccountRoundupsIsCalled_whenStarlingApiThrowsError_TheRestApiErrorIsThrown() throws RestApiException {
        List<Account> accounts = new ArrayList<>();
        accounts.add(Account.builder().accountUid(ACCOUNT_UID).defaultCategory(ACCOUNT_DEFAULT_CATEGORY).build());

        when(transactionsService.getAllTransactionsForAccount(ACCOUNT_UID, ACCOUNT_DEFAULT_CATEGORY)).thenThrow(new RestClientException("test"));
        roundupService.getAccountRoundups(AccountsList.builder().accounts(accounts).build());
    }
}
