package com.example.learning_api.service.core.Impl;

import com.example.learning_api.dto.request.term.CreateTermRequest;
import com.example.learning_api.entity.sql.database.TermsEntity;
import com.example.learning_api.repository.database.TermsRepository;
import com.example.learning_api.service.common.ModelMapperService;
import com.example.learning_api.service.core.ITermsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;

@Service
@RequiredArgsConstructor
@Slf4j
public class TermService implements ITermsService {
    private final TermsRepository termsRepository;
    private final ModelMapperService modelMapperService;

    @Override
    public void addTerm(CreateTermRequest body) {
        try {
            TermsEntity termsEntity = modelMapperService.mapClass(body, TermsEntity.class);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            termsEntity.setStartDate(formatter.parse(body.getStartDate()));
            termsEntity.setEndDate(formatter.parse(body.getEndDate()));
            termsRepository.save(termsEntity);
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    @Override
    public void updateTerm(TermsEntity body) {
        if (termsRepository.findById(body.getId()).isEmpty()) {
            throw new RuntimeException("Term not found");
        }
        termsRepository.save(body);
    }

    @Override
    public void deleteTerm(String id) {
        termsRepository.deleteById(id);
        // TODO Auto-generated method stub
    }
}
