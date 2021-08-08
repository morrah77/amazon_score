package org.morrah77.amazon_score.service;

import org.morrah77.amazon_score.domain.AmazonResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScoreService implements IScoreService {
    @Autowired
    @Qualifier("AmazonDataService")
    IDataService service;

    @Override
    public int getScore(String keyword) {
        int result = 0;
        ResponseEntity<AmazonResponseEntity> data = service.getData(keyword);
        if ((data.hasBody()) && (data.getBody().getEntry().size() > 1)) {
            List<Object> matches = (List<Object>)(data.getBody().getEntry().get(1));
            result = calculateScore(matches);
        }
        return result;
    }

    int calculateScore(List<Object> matches) {
        return matches.size();
    }
}
