package ir.mab.radioamin.controller.rest.v1.consumer;

import com.fasterxml.jackson.annotation.JsonView;
import ir.mab.radioamin.config.ApiBaseEndpoints;
import ir.mab.radioamin.entity.Music;
import ir.mab.radioamin.exception.ResourceNotFoundException;
import ir.mab.radioamin.model.Views;
import ir.mab.radioamin.model.enums.MusicType;
import ir.mab.radioamin.model.res.SuccessResponse;
import ir.mab.radioamin.repository.MusicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = ApiBaseEndpoints.VersionOne.CONSUMER)
public class ConsumerMusicController {

    MusicRepository musicRepository;

    @Autowired
    public ConsumerMusicController(MusicRepository musicRepository) {
        this.musicRepository = musicRepository;
    }

    @GetMapping("/music")
    @JsonView({Views.Summary.class})
    SuccessResponse<Page<Music>> getMusics(
            @RequestParam(value = "sort", required = false, defaultValue = "name") String sort,
            @RequestParam(value = "direction", required = false, defaultValue = "ASC") Sort.Direction direction,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "20") Integer size,
            @RequestParam(value = "musicType", required = false) String musicType) {

        if (musicType.equals(MusicType.VOCAL.toString())){
            return new SuccessResponse<>("success", musicRepository.findMusicByMusicTypeIs(MusicType.VOCAL,PageRequest.of(page, size, Sort.by(direction, sort))));

        }
        else if (musicType.equals(MusicType.VIDEO.toString())){
            return new SuccessResponse<>("success", musicRepository.findMusicByMusicTypeIs(MusicType.VIDEO,PageRequest.of(page, size, Sort.by(direction, sort))));
        }

        return new SuccessResponse<>("success", musicRepository.findAll(PageRequest.of(page, size, Sort.by(direction, sort))));
    }

    @GetMapping("/music/{id}")
    @JsonView({Views.Expand.class})
    SuccessResponse<Music> getMusic(@PathVariable Long id) {

        Music music = musicRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("music", String.valueOf(id), "id")
        );
        return new SuccessResponse<>("success", music);
    }
}
