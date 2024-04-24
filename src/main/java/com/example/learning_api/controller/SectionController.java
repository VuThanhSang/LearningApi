package com.example.learning_api.controller;


import com.example.learning_api.constant.StatusCode;
import com.example.learning_api.dto.request.section.CreateSectionRequest;
import com.example.learning_api.dto.request.section.DeleteSectionRequest;
import com.example.learning_api.dto.request.section.UpdateSectionRequest;
import com.example.learning_api.dto.response.section.CreateSectionResponse;
import com.example.learning_api.dto.response.section.GetSectionsResponse;
import com.example.learning_api.model.ResponseAPI;
import com.example.learning_api.service.core.ISectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

import static com.example.learning_api.constant.RouterConstant.SECTION_BASE_PATH;

@RestController
@RequiredArgsConstructor
@RequestMapping(SECTION_BASE_PATH)
@Slf4j
public class SectionController {
    private final ISectionService sectionService;

    @PostMapping(path = "")
    public ResponseEntity<ResponseAPI<CreateSectionResponse>> createSection(@RequestBody @Valid CreateSectionRequest body) {
        try{
            CreateSectionResponse resDate = sectionService.createSection(body);
            ResponseAPI<CreateSectionResponse> res = ResponseAPI.<CreateSectionResponse>builder()
                    .timestamp(new Date())
                    .message("Create section successfully")
                    .data(resDate)
                    .build();
            return new ResponseEntity<>(res, StatusCode.CREATED);
        }
        catch (Exception e){
            ResponseAPI<CreateSectionResponse> res = ResponseAPI.<CreateSectionResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }

    }
    @PatchMapping(path = "/{sectionId}")
    public ResponseEntity<ResponseAPI<String>> updateSection(@RequestBody @Valid UpdateSectionRequest body, @PathVariable String sectionId) {
        try{
            body.setId(sectionId);
            sectionService.updateSection(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Update section successfully")
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }

    }
    @DeleteMapping(path = "/{sectionId}")
    public ResponseEntity<ResponseAPI<String>> deleteSection(@PathVariable String sectionId) {
        try{
            DeleteSectionRequest deleteSectionRequest = new DeleteSectionRequest();
            deleteSectionRequest.setId(sectionId);
            sectionService.deleteSection(deleteSectionRequest);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Delete section successfully")
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }

    }
    @GetMapping(path = "")
    public ResponseEntity<ResponseAPI<GetSectionsResponse>> getSections(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String classroomId
    ) {
        try{
            GetSectionsResponse resData = sectionService.getSections(page-1, size, classroomId);
            ResponseAPI<GetSectionsResponse> res = ResponseAPI.<GetSectionsResponse>builder()
                    .timestamp(new Date())
                    .message("Get sections successfully")
                    .data(resData)
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<GetSectionsResponse> res = ResponseAPI.<GetSectionsResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }

    }
}
