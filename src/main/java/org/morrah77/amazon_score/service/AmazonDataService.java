package org.morrah77.amazon_score.service;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.morrah77.amazon_score.domain.AmazonResponseEntity;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * https://completion.amazon.com/search/complete?search-alias=aps&client=amazon-search-cli&mkt=1&q=smart
 *
 * Response:
 * Content-Type: text/javascript;charset=UTF-8
 * ["smart",["smart watch","smart tv","smart light bulbs","32 in smart tv","samsung smart watch","amazon smart plug","owlet smart sock 3","smart plugs","smart sweets","smart lock"],[{},{},{},{},{},{},{},{},{},{}],[],"3VYP3ZRG5R3GO"]
 */
@Service
@Qualifier("AmazonDataService")
public class AmazonDataService implements IDataService {
    // TODO Add a REST template configuration instead of counting on default one
    @Autowired
    RestTemplate template;

    // it's better than Lombok @Slf4j which generates a `private static final` class member doing it really inconvenient for testing
    @Autowired
    Logger log;

    static String DATA_ENDPOINT_TEMPLATE = "https://completion.amazon.com/search/complete?search-alias=aps&client=amazon-search-cli&mkt=1&q=%s";
    @Override
    public ResponseEntity<AmazonResponseEntity> getData(String keyword) {
        ResponseEntity<String> result = template.getForEntity(
                createRequestUrl(keyword),
                String.class,
                new HashMap<>());
        log.debug(Optional.ofNullable(result).orElse(new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR)).toString());
        try {
            Gson gson = new Gson();
            Object[] array = gson.fromJson(result.getBody(), Object[].class);
            List<Object> entry = new ArrayList<>();
            for (int i = 0; i < array.length; i++) {
                entry.add(array[i]);
            }
            AmazonResponseEntity amazonResponseEntity = new AmazonResponseEntity(entry);
            return new ResponseEntity<AmazonResponseEntity>(amazonResponseEntity, result.getStatusCode());
        } catch ( JsonSyntaxException e) {
            return new ResponseEntity<AmazonResponseEntity>(new AmazonResponseEntity(Arrays.asList(e.getMessage())), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // TODO don't forget to validate/encode the keyword!!!
    String createRequestUrl(String keyword) {
        return String.format(DATA_ENDPOINT_TEMPLATE, keyword);
    }
}
