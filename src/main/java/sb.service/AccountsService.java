package sb.service;

import lombok.NonNull;
import sb.exception.AccountNotFoundException;
import sb.exception.RestApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import sb.model.Account;
import sb.model.AccountsList;
import sb.util.RestHelper;

import java.util.Arrays;
import java.util.Optional;

@Service
public class AccountsService {

    @Value("${accounts.url}")
    private String accountsUrl;
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    private RestHelper restHelper;

    public AccountsList getAllAccountsForCustomer() throws RestApiException {
        try {
            return restHelper.performGet(accountsUrl, AccountsList.class).getBody();
        } catch (RestClientException ex) {
            logger.error("Couldn't read accounts" + ex.getMessage());
            logger.debug(Arrays.toString(ex.getStackTrace()));
            throw new RestApiException(ex.getMessage());
        }
    }

    public Account getAccount(@NonNull final String accountUid) throws RestApiException, AccountNotFoundException {
        final AccountsList accountsList = getAllAccountsForCustomer();
        final Optional<Account> accountOptional = accountsList.getAccounts()
                .stream()
                .filter(account -> account.getAccountUid().equals(accountUid))
                .findFirst();
        if (accountOptional.isPresent()) {
            return accountOptional.get();
        } else {
            logger.debug("Account" + accountUid + "is not present");
            throw new AccountNotFoundException("Could not find the account");
        }
    }
}
