package ir.mab.radioamin.controller.rest.v1.consumer;

import com.fasterxml.jackson.annotation.JsonView;
import ir.mab.radioamin.config.ApiBaseEndpoints;
import ir.mab.radioamin.entity.Singer;
import ir.mab.radioamin.exception.ResourceNotFoundException;
import ir.mab.radioamin.model.Views;
import ir.mab.radioamin.model.res.SuccessResponse;
import ir.mab.radioamin.repository.SingerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = ApiBaseEndpoints.VersionOne.CONSUMER)
public class ConsumerSingerController {

    SingerRepository singerRepository;

    @Autowired
    public ConsumerSingerController(SingerRepository singerRepository) {
        this.singerRepository = singerRepository;
    }

    @GetMapping("/singer")
    @JsonView({Views.Summary.class})
    SuccessResponse<Page<Singer>> getSingers(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "sort", required = false, defaultValue = "name") String sort,
            @RequestParam(value = "direction", required = false, defaultValue = "ASC") Sort.Direction direction,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "20") Integer size) {

        if (name != null){
            return new SuccessResponse<>("success", singerRepository.findSingersByNameContaining(name, PageRequest.of(page, size, Sort.by(direction, sort))));
        }


        return new SuccessResponse<>("success", singerRepository.findAll(PageRequest.of(page, size, Sort.by(direction, sort))));

    }

    @GetMapping("/singer/{id}")
    @JsonView({Views.Expand.class})
    SuccessResponse<Singer> getSinger(@PathVariable Long id) {

        Singer singer = singerRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException("singer", String.valueOf(id), "id")
        );
        return new SuccessResponse<>("success", singer);

    }
}
