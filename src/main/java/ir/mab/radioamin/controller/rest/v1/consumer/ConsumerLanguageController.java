package ir.mab.radioamin.controller.rest.v1.consumer;

import ir.mab.radioamin.config.ApiBaseEndpoints;
import ir.mab.radioamin.entity.Language;
import ir.mab.radioamin.exception.ResourceNotFoundException;
import ir.mab.radioamin.model.res.SuccessResponse;
import ir.mab.radioamin.repository.LanguageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = ApiBaseEndpoints.VersionOne.CONSUMER)
public class ConsumerLanguageController {

    LanguageRepository languageRepository;

    @Autowired
    public ConsumerLanguageController(LanguageRepository languageRepository) {
        this.languageRepository = languageRepository;
    }

    @GetMapping("/language")
    SuccessResponse<Page<Language>> getLanguages(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "sort", required = false, defaultValue = "name") String sort,
            @RequestParam(value = "direction", required = false, defaultValue = "ASC") Sort.Direction direction,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "20") Integer size) {

        if (name != null){
            return new SuccessResponse<>("success", languageRepository.findLanguagesByNameContaining(name, PageRequest.of(page, size, Sort.by(direction, sort))));
        }

        return new SuccessResponse<>("success", languageRepository.findAll(PageRequest.of(page, size, Sort.by(direction, sort))));
    }

    @GetMapping("/language/{id}")
    SuccessResponse<Language> getLanguage(@PathVariable Long id) {

        Language language = languageRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException("language", String.valueOf(id), "id")
        );

        return new SuccessResponse<>("success", language);
    }
}
