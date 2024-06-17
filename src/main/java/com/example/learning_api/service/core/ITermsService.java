package com.example.learning_api.service.core;

import com.example.learning_api.dto.request.term.CreateTermRequest;
import com.example.learning_api.entity.sql.database.TermsEntity;

public interface ITermsService {
    void addTerm(CreateTermRequest body);
    void updateTerm(TermsEntity body);
    void deleteTerm(String id);
}
