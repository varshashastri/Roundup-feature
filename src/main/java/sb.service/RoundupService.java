package sb.service;

import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClientException;
import sb.exception.RestApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sb.model.Account;
import sb.model.AccountsList;
import sb.model.TransactionsList;

import java.util.*;


@Service
public class RoundupService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    private TransactionsService transactionsService;
    @Autowired
    private SavingsGoalsService savingsGoalsService;

    public Integer getRoundUpMinorUnits(@NonNull final TransactionsList transactionsList) {
        return Optional.of(transactionsList)
                .map(TransactionsList::getFeedItems)
                .map(transactions -> transactions.stream()
                        .map(transaction -> 100 - transaction.getAmount().getMinorUnits() % 100)
                        .reduce(Integer::sum)
                        .orElse(0))
                .orElse(0);//if there are no transactions then round up would be 0
    }

    public Map<String, Integer> getAccountRoundups(@NonNull final AccountsList accountsList) throws RestApiException {
        final Map<String, Integer> accountRoundups = new HashMap<>();
        final Optional<List<Account>> accounts = Optional.of(accountsList).map(AccountsList::getAccounts);
        if (accounts.isPresent()) {
            for (final Account account : accounts.get()) {
                final int roundUpMinorUnits = getAccountRoundUp(account.getAccountUid(), account.getDefaultCategory());
                accountRoundups.put(account.getAccountUid(), roundUpMinorUnits);
            }
            return accountRoundups;
        } else {
            throw new IllegalArgumentException("Accounts list is absent");
        }
    }


    public Integer getAccountRoundUp(@NonNull final String accountUid, @NonNull final String accountDefaultCategory) throws RestApiException {
        try {
            TransactionsList transactionsList = transactionsService.getAllTransactionsForAccount(accountUid, accountDefaultCategory);
            return getRoundUpMinorUnits(transactionsList);
        } catch (RestClientException ex) {
            logger.error("Error while getting transactions" + ex.getMessage());
            logger.trace(Arrays.toString(ex.getStackTrace()));
            throw new RestApiException(ex.getMessage());
        }
    }
}
