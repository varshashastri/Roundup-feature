package sb.service;

import lombok.NonNull;
import sb.exception.RestApiException;
import sb.exception.SavingsGoalMoneyTransferException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import sb.model.Amount;
import sb.model.CreateSavingsGoalSuccess;
import sb.model.SavingsGoal;
import sb.model.TransferToSavingsGoalSuccess;
import sb.model.SavingsGoalList;
import sb.util.RestHelper;

import java.util.*;


@Service
public class SavingsGoalsService {

    public static final String AMOUNT = "amount";
    public static final String GBP = "GBP";
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());//use slf4j with spring.

    @Value("${savings.goals.url.prefix}")
    private String savingsGoalsUrlPrefix;

    @Value("${savingsgoals.suffix}")
    private String savingsGoalsSuffix;

    @Value("${add.money}")
    private String addMoneySuffix;

    @Autowired
    private RestHelper restHelper;

    //TBD: use personal laptop to use lombok and upload to git
    public String transferAmountToSavingsGoal(@NonNull final String accountUid, @NonNull final String savingsGoalUid, final Integer amountMinorUnits) throws SavingsGoalMoneyTransferException, RestApiException {
        try {
            Map<String, Amount> amount = new HashMap<>();
            amount.put(AMOUNT, new Amount(GBP, amountMinorUnits));
            String url = savingsGoalsUrlPrefix + accountUid + savingsGoalsSuffix + savingsGoalUid + addMoneySuffix + UUID.randomUUID();
            TransferToSavingsGoalSuccess transferToSavingsGoalSuccess = restHelper.performPut(url, amount, TransferToSavingsGoalSuccess.class).getBody();
            if (transferToSavingsGoalSuccess != null && transferToSavingsGoalSuccess.isSuccess()) {
                return transferToSavingsGoalSuccess.getTransferUid();
            } else {
                throw new SavingsGoalMoneyTransferException("Couldn't transfer money to savings goal");
            }
        } catch (RestClientException ex) {
            logger.error("savings goal couldn't be created" + ex.getMessage());
            throw new RestApiException(ex.getMessage());
        }
    }

    public String getOrCreateSavingsGoal(@NonNull final String accountUid, @NonNull final SavingsGoal savingsGoal) throws RestApiException {
        SavingsGoalList savingsGoals;
        logger.debug("Checking if savings goal" + savingsGoal.getName() + " is present");
        savingsGoals = getExistingSavingsGoalList(accountUid);
        Optional<SavingsGoal> savingsGoalOptional = Optional.ofNullable(savingsGoals)
                .map(SavingsGoalList::getSavingsGoalList)
                .flatMap(savingsGoals1 -> savingsGoals1.stream()
                        .filter(savingsGoal1 -> savingsGoal1.getName().equals(savingsGoal.getName()))
                        .findFirst());
        if (savingsGoalOptional.isPresent()) {
            return savingsGoalOptional.get().getSavingsGoalUid();
        } else {
            logger.debug("savings goal isn't present. Creating a new savingsGoal");
            return createNewSavingsGoal(accountUid, savingsGoal);
        }
    }

    private String createNewSavingsGoal(@NonNull final String accountUid, @NonNull final SavingsGoal savingsGoal) throws RestApiException {
        try {
            String url = savingsGoalsUrlPrefix + accountUid + savingsGoalsSuffix;
            CreateSavingsGoalSuccess savingsGoalReturn = restHelper.performPut(url, savingsGoal, CreateSavingsGoalSuccess.class).getBody();
            if (savingsGoalReturn != null && savingsGoalReturn.isSuccess()) {
                return savingsGoalReturn.getSavingsGoalUid();
            } else {
                throw new RestApiException("Savings goal could not be created");
            }
        } catch (RestClientException ex) {
            logger.error("savings goal couldn't be created" + ex.getMessage());
            logger.trace(Arrays.toString(ex.getStackTrace()));
            throw new RestApiException(ex.getMessage());
        }
    }

    private SavingsGoalList getExistingSavingsGoalList(@NonNull final String accountUid) throws RestApiException {
        SavingsGoalList savingsGoals;
        try {
            String url = savingsGoalsUrlPrefix + accountUid + savingsGoalsSuffix;
            savingsGoals = restHelper.performGet(url, SavingsGoalList.class).getBody();
        } catch (RestClientException ex) {
            logger.error("savings goals couldn't be retrieved" + ex.getMessage());
            logger.trace(Arrays.toString(ex.getStackTrace()));
            throw new RestApiException(ex.getMessage());
        }
        return savingsGoals;
    }

}
