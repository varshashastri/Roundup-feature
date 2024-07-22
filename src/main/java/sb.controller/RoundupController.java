package sb.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import sb.exception.AccountNotFoundException;
import sb.exception.RestApiException;
import sb.exception.SavingsGoalMoneyTransferException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import sb.model.Account;
import sb.model.AccountsList;
import sb.model.SavingsGoal;
import sb.model.TransactionsList;
import sb.service.AccountsService;
import sb.service.RoundupService;
import sb.service.SavingsGoalsService;
import sb.service.TransactionsService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/roundup")
public class RoundupController {

    public static final String SAVINGS_GOAL_NAME_PREFIX = "SavingsGoal";
    public static final String GBP_CURRENCY = "GBP";
    @Autowired
    public AccountsService accountsService;
    @Autowired
    public SavingsGoalsService savingsGoalsService;
    @Autowired
    public RoundupService roundupService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    private final ObjectMapper mapper = new ObjectMapper();

    @GetMapping("/getRoundUps/allAccounts")
    public ResponseEntity<String> getRoundupAmountsForAllAccounts() {
        try {
            AccountsList accountsList = accountsService.getAllAccountsForCustomer();
            Map<String, Integer> accountRoundups = roundupService.getAccountRoundups(accountsList);

            String returnString = mapper.writeValueAsString(accountRoundups);
            return ResponseEntity.ok(returnString);
        } catch (JsonProcessingException e) {
            logger.debug("Unable to convert the result to json");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (RestApiException e) {
            logger.debug(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getRoundUps/{accountUid}")
    public ResponseEntity<Integer> getRoundupsAmountForGivenAccount(@PathVariable("accountUid") final String accountUid) {
        try {
            Account account = accountsService.getAccount(accountUid);
            return ResponseEntity.ok(roundupService.getAccountRoundUp(account.getAccountUid(), account.getDefaultCategory()));
        } catch (RestApiException | AccountNotFoundException e) {
            logger.debug(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    @PutMapping("/transferToSavings/all")
    public ResponseEntity<String> transferToSavingsForAllAccounts() {
        try {
            AccountsList accountsList = accountsService.getAllAccountsForCustomer();
            Map<String, Integer> accountRoundups = roundupService.getAccountRoundups(accountsList);
            Map<String, List<String>> transferUids = new HashMap<>();
            List<String> transferUidList = new ArrayList<>();
            for (String accountUid : accountRoundups.keySet()) {
                String transferUid = transferMoneyToSavings(accountUid, accountRoundups.get(accountUid));
                transferUidList.add(transferUid);
            }
            transferUids.put("TransferUIDs", transferUidList);
            ObjectMapper mapper = new ObjectMapper();

            return ResponseEntity.ok(mapper.writeValueAsString(transferUids));
        } catch (SavingsGoalMoneyTransferException | RestApiException e) {
            logger.debug(e.getMessage());
            logger.debug("Money could not be transfered to savings goal");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (JsonProcessingException e) {
            logger.debug("Unable to convert the result to json");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/transferToSavings/{accountUid}")
    public ResponseEntity<String> transferToSavingsForGivenAccount(@PathVariable("accountUid") final String accountUid) {
        try {
            Account account = accountsService.getAccount(accountUid);
            Integer roundUp = roundupService.getAccountRoundUp(account.getAccountUid(), account.getDefaultCategory());
            String transferUid = transferMoneyToSavings(accountUid, roundUp);
            return ResponseEntity.ok(transferUid);
        } catch (RestApiException |
                 SavingsGoalMoneyTransferException e) {
            logger.debug(e.getMessage());
            logger.debug("Money could not be transfered to savings goal");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (AccountNotFoundException e) {
            logger.debug(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String transferMoneyToSavings(@NonNull final String accountUid, @NonNull final Integer accountRoundup) throws SavingsGoalMoneyTransferException, RestApiException {
        String savingsGoalUid = savingsGoalsService.getOrCreateSavingsGoal(accountUid, SavingsGoal.builder().name(SAVINGS_GOAL_NAME_PREFIX + accountUid).currency(GBP_CURRENCY).build());
        return savingsGoalsService.transferAmountToSavingsGoal(accountUid, savingsGoalUid, accountRoundup);
    }
}
