package org.morrah77.amazon_score.service;

import org.morrah77.amazon_score.domain.AmazonResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class ScoreService implements IScoreService {
    static final int MAX_REQUESTS_NUMBER = 10;
    @Autowired
    @Qualifier("AmazonDataService")
    IDataService service;

    @Override
    public int getScore(String keyword) {
        int result = 0;
        String searchKeyword = keyword.toLowerCase(Locale.ROOT).replaceAll("\\s+", "+");
        ResponseEntity<AmazonResponseEntity> data = service.getData(searchKeyword);
        if ((data.hasBody()) && (data.getBody().getEntry().size() > 1)) {
            if (!searchKeyword.replaceAll("\\+", " ").equals((String) (data.getBody().getEntry().get(0)))) {
                return result;
            }
            List<Object> matches = (List<Object>)(data.getBody().getEntry().get(1));
            result = calculateScore((String) (data.getBody().getEntry().get(0)), matches);
        }
        return result;
    }

    int calculateScore(String keyword, List<Object> matches) {
        int reslut = 0;
        double intermediateResult = 0;
        double coefficient = getCoefficient(matches);
        for (Object o : matches) {
            if (((String)o).equals(keyword)) {
                return 100;
            }
            if (((String)o).startsWith(keyword)) {
                intermediateResult += coefficient / matches.size();
            }
        }
        reslut = (int)(intermediateResult * 100);
        return reslut;
    }

    double getCoefficient(List<Object> matches) {
        if (matches.size() >= MAX_REQUESTS_NUMBER) {
            return 0.4;
        }
        return 0.4 / (1 + MAX_REQUESTS_NUMBER - matches.size());
    }
}
