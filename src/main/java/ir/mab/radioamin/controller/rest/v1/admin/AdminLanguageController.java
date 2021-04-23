package ir.mab.radioamin.controller.rest.v1.admin;

import ir.mab.radioamin.config.ApiBaseEndpoints;
import ir.mab.radioamin.entity.Language;
import ir.mab.radioamin.exception.ResourceAlreadyExistsException;
import ir.mab.radioamin.exception.ResourceNotFoundException;
import ir.mab.radioamin.model.res.SuccessResponse;
import ir.mab.radioamin.repository.LanguageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = ApiBaseEndpoints.VersionOne.ADMIN)
public class AdminLanguageController {
    LanguageRepository languageRepository;

    @Autowired
    public AdminLanguageController(LanguageRepository languageRepository) {
        this.languageRepository = languageRepository;
    }

    @PostMapping("/language")
    @ResponseStatus(HttpStatus.CREATED)
    SuccessResponse<Language> createLanguage(@RequestParam("name") String name) {

        if (languageRepository.existsLanguageByName(name)){
            throw new ResourceAlreadyExistsException("language", name);
        }

        Language language = new Language();
        language.setName(name);

        return new SuccessResponse<>("language created", languageRepository.save(language));

    }

    @PutMapping("/language/{id}")
    SuccessResponse<Language> updateLanguage(
            @PathVariable Long id,
            @RequestParam(value = "name") String name) {

        if (languageRepository.existsLanguageByName(name)){
            throw new ResourceAlreadyExistsException("language", name);
        }

        Language language = languageRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException("language",String.valueOf(id),"id"));

        return new SuccessResponse<>("language updated", languageRepository.save(language));
    }

}
