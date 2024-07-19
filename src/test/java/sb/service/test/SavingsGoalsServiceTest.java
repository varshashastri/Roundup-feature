package sb.service.test;


import sb.exception.RestApiException;
import sb.exception.SavingsGoalMoneyTransferException;
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
import sb.service.SavingsGoalsService;
import sb.util.RestHelper;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class SavingsGoalsServiceTest {
    private final String accountUid = "accountUid";
    private final String savingsGoalUid = "savingsGoalUid";
    private final int amountMinorUnits = 1;
    String transferUidOriginal = "transferUid";
    @Value("${savings.goals.url.prefix}")
    private String savingsGoalsUrlPrefix;

    @Value("${savingsgoals.suffix}")
    private String savingsGoalsSuffix;
    @Mock
    @Autowired
    private RestHelper restHelper;

    @InjectMocks
    @Autowired
    private SavingsGoalsService savingsGoalsService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void givenvalidInputs_transferAmountToSavingsGoal_returnsTransferId() throws SavingsGoalMoneyTransferException, RestApiException {
        when(restHelper.performPut(any(), any(), any())).thenReturn(ResponseEntity.ok(new TransferToSavingsGoalSuccess("transferUid", true)));
        String transferUid = savingsGoalsService.transferAmountToSavingsGoal(accountUid, savingsGoalUid, amountMinorUnits);

        Assert.assertFalse(transferUid.isEmpty());
        Assert.assertEquals(transferUid, transferUidOriginal);
    }

    @Test(expected = RestApiException.class)
    public void giventransferAmountsToSavingsCalled_whenStarlingApiThrowsError_ThrowsCustomException() throws SavingsGoalMoneyTransferException, RestApiException {
        when(restHelper.performPut(any(), any(), any())).thenThrow(new RestClientException("test"));
        savingsGoalsService.transferAmountToSavingsGoal(accountUid, savingsGoalUid, amountMinorUnits);
    }

    @Test(expected = SavingsGoalMoneyTransferException.class)
    public void giventransferAmountsToSavingsCalled_whentransferFails_ThrowsCustomException() throws SavingsGoalMoneyTransferException, RestApiException {
        when(restHelper.performPut(any(), any(), any())).thenReturn(ResponseEntity.ok(new TransferToSavingsGoalSuccess("transferUid", false)));
        savingsGoalsService.transferAmountToSavingsGoal(accountUid, savingsGoalUid, amountMinorUnits);
    }

    @Test
    public void givengetOrCreateSavingsGoalIsCalled_whenSavingsGoalsIsntPresent_returnsNewSavingsGoalUid() throws RestApiException {
        when(restHelper.performGet(any(), any())).thenReturn(ResponseEntity.ok().build());
        when(restHelper.performPut(any(), any(), any())).thenReturn(ResponseEntity.ok(new CreateSavingsGoalSuccess(savingsGoalUid, true)));
        String savingsGoalUid = savingsGoalsService.getOrCreateSavingsGoal(accountUid, SavingsGoal.builder().name("savingsGoalUid").currency("GBP").build());
        Assert.assertFalse(savingsGoalUid.isEmpty());
    }

    @Test
    public void givengetOrCreateSavingsGoalIsCalled_whenSavingsGoalsIsPresent_returnsExistingSavingsGoalUid() throws RestApiException {
        SavingsGoal savingsGoal = SavingsGoal.builder().name("savingsGoalUid").currency("GBP").build();
        savingsGoal.setSavingsGoalUid(savingsGoalUid);
        List<SavingsGoal> savingsGoalList = new ArrayList<>();
        savingsGoalList.add(savingsGoal);
        when(restHelper.performGet(any(), any())).thenReturn(ResponseEntity.ok(new SavingsGoalList(savingsGoalList)));
        String savingsGoalUidReturned = savingsGoalsService.getOrCreateSavingsGoal(accountUid, savingsGoal);
        Assert.assertFalse(savingsGoalUidReturned.isEmpty());
        Assert.assertEquals(savingsGoalUid, savingsGoalUidReturned);
    }

    @Test(expected = RestApiException.class)
    public void givengetOrCreateSavingsGoalIsCalled_whenSavingsGoalsIsntPresentAndCouldntBeCreated_throwsRestApiException() throws RestApiException {
        when(restHelper.performGet(any(), any())).thenReturn(ResponseEntity.ok().build());
        when(restHelper.performPut(any(), any(), any())).thenThrow(new RestClientException("test"));
        savingsGoalsService.getOrCreateSavingsGoal(accountUid, SavingsGoal.builder().name("savingsGoalUid").currency("GBP").build());
    }

    @Test(expected = RestApiException.class)
    public void givengetOrCreateSavingsGoalIsCalled_whenSavingsGoalsIsPresentAndCouldntBeRetrieved_throwsRestApiException() throws RestApiException {
        when(restHelper.performGet(any(), any())).thenThrow(new RestClientException("test"));
        savingsGoalsService.getOrCreateSavingsGoal(accountUid, SavingsGoal.builder().name("savingsGoalUid").currency("GBP").build());
    }

}
