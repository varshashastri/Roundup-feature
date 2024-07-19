package sb.service.test;

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
import sb.exception.RestApiException;
import sb.model.Amount;
import sb.model.Transaction;
import sb.model.TransactionsList;
import sb.service.SavingsGoalsService;
import sb.service.TransactionsService;
import sb.util.RestHelper;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class TransactionServiceTest {
    private final String accountUid = "accountUid";
    private final String accountDefaultCategory = "accountDefaultCategory";

    @Value("${transactions.url.prefix}")
    private String transactionsUrlPrefix;

    @Value("${transactions.suffix}")
    private String transactionsUrlSuffix;

    @Mock
    @Autowired
    private RestHelper restHelper;

    @InjectMocks
    @Autowired
    private TransactionsService transactionsService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void whenGetAllTransactionsForAccountCalled_thenCorrectTransactionsAreReturned() throws RestApiException {
        List<Transaction> feedList = new ArrayList<>();
        feedList.add(Transaction
                .builder()
                .amount(Amount
                        .builder()
                        .currency("GBP")
                        .minorUnits(1).build())
                .feedItemUid("feedItemUid").build());
        TransactionsList transactionsList = TransactionsList.builder().feedItems(feedList).build();
        when(restHelper.performGet(any(), any())).thenReturn(ResponseEntity.ok(transactionsList));
        TransactionsList transactionsListActual = transactionsService.getAllTransactionsForAccount(accountUid, accountDefaultCategory);
        Assert.assertNotNull(transactionsListActual);
        Assert.assertEquals(1, transactionsListActual.getFeedItems().size());
        Assert.assertEquals(1, transactionsListActual.getFeedItems().get(0).getAmount().getMinorUnits());
        Assert.assertEquals("GBP", transactionsListActual.getFeedItems().get(0).getAmount().getCurrency());
    }

    @Test(expected = RestApiException.class)
    public void givenGetAllTransactionsForAccountCalled_whenStarlingApiThrowsAnException_thenRestApiExceptionIsThrown() throws RestApiException {
        when(restHelper.performGet(any(), any())).thenThrow(new RestClientException("test"));
        transactionsService.getAllTransactionsForAccount(accountUid, accountDefaultCategory);
    }
}
