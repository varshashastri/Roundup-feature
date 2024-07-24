package sb.service;

import lombok.NonNull;
import sb.exception.RestApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import sb.model.TransactionsList;
import sb.util.RestHelper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;


@Service
public class TransactionsService {

    @Value("${transactions.url.prefix}")
    private String transactionsUrlPrefix;

    @Value("${transactions.suffix}")
    private String transactionsUrlSuffix;
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());//use slf4j with spring.

    @Autowired
    private RestHelper restHelper;
    private final DateTimeFormatter customPattern = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public TransactionsList getAllTransactionsForAccount(@NonNull final String accountUid, @NonNull final String accountDefaultCategory) throws RestApiException {
        try {
            final String customFormattedDateNow = LocalDateTime.now().format(customPattern);
            final String customFormattedDateLastWeek = LocalDateTime.now().minusWeeks(1).format(customPattern);
            final String url = transactionsUrlPrefix + accountUid + "/category/" + accountDefaultCategory + "/transactions-between?minTransactionTimestamp="+customFormattedDateLastWeek+"&maxTransactionTimestamp="+customFormattedDateNow;
            return restHelper.performGet(url, TransactionsList.class).getBody();
        } catch (RestClientException ex) {
            logger.error("savings goal couldn't be created" + ex.getMessage());
            logger.trace(Arrays.toString(ex.getStackTrace()));
            throw new RestApiException(ex.getMessage());
        }
    }
}
