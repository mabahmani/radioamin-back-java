package ir.mab.radioamin.controller.rest.v1.admin;

import ir.mab.radioamin.config.ApiBaseEndpoints;
import ir.mab.radioamin.entity.Mood;
import ir.mab.radioamin.exception.ResourceAlreadyExistsException;
import ir.mab.radioamin.exception.ResourceNotFoundException;
import ir.mab.radioamin.model.res.SuccessResponse;
import ir.mab.radioamin.repository.MoodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(path = ApiBaseEndpoints.VersionOne.ADMIN)
public class AdminMoodController {
    MoodRepository moodRepository;

    @Autowired
    public AdminMoodController(MoodRepository moodRepository) {
        this.moodRepository = moodRepository;
    }

    @GetMapping(value = "/mood")
    SuccessResponse<Page<Mood>> findMoods(
            @RequestParam(value = "sort", required = false, defaultValue = "name") String sort,
            @RequestParam(value = "direction", required = false, defaultValue = "ASC") Sort.Direction direction,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "5") Integer size){

        return new SuccessResponse<>("success",moodRepository.findAll(PageRequest.of(page, size, direction, sort)));

    }

    @GetMapping(value = "/mood/count")
    SuccessResponse<Long> moodCount(){
        return new SuccessResponse<>("number of moods", moodRepository.count());
    }

    @PostMapping(value = "/mood")
    @ResponseStatus(HttpStatus.CREATED)
    SuccessResponse<Mood> createMood(@Valid @RequestBody Mood mood){

        if (moodRepository.existsByName(mood.getName())) {
            throw new ResourceAlreadyExistsException("mood", mood.getName());
        }

        return new SuccessResponse<>("mood created", moodRepository.save(mood));
    }

    @PutMapping(value = "/mood/{id}")
    SuccessResponse<Mood> updateMood(@PathVariable("id") Long id, @Valid @RequestBody Mood mood){

        Mood newMood = moodRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("mood", String.valueOf(id), "id"));

        if (!newMood.getName().equals(mood.getName())){
            if (moodRepository.existsByName(mood.getName())) {
                throw new ResourceAlreadyExistsException("mood", mood.getName());
            }
            newMood.setName(mood.getName());
        }
        newMood.setGenres(mood.getGenres());

        return new SuccessResponse<>("mood updated", moodRepository.save(newMood));
    }

    @DeleteMapping(value = "/mood/{id}")
    SuccessResponse<Boolean> deleteMood(@PathVariable("id") Long id){

        moodRepository.deleteById(id);
        return new SuccessResponse<>("mood deleted", true);
    }
}
