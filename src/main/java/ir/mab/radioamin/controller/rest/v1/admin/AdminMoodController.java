package ir.mab.radioamin.controller.rest.v1.admin;

import ir.mab.radioamin.config.ApiBaseEndpoints;
import ir.mab.radioamin.model.res.SuccessResponse;
import ir.mab.radioamin.repository.MoodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = ApiBaseEndpoints.VersionOne.ADMIN)
public class AdminMoodController {
    MoodRepository moodRepository;

    @Autowired
    public AdminMoodController(MoodRepository moodRepository) {
        this.moodRepository = moodRepository;
    }

    @GetMapping(value = "/mood/count")
    SuccessResponse<Long> moodCount(){
        return new SuccessResponse<>("number of moods", moodRepository.count());
    }

}
