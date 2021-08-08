package org.morrah77.amazon_score.service;

import org.springframework.http.ResponseEntity;

public interface IDataService {
    ResponseEntity getData(String keyword);
}
