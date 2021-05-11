package ir.mab.radioamin.controller.rest.v1.admin;

import ir.mab.radioamin.config.ApiBaseEndpoints;
import ir.mab.radioamin.model.res.SuccessResponse;
import ir.mab.radioamin.repository.PlaylistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = ApiBaseEndpoints.VersionOne.ADMIN)
public class AdminPlaylistController {
    PlaylistRepository playlistRepository;

    @Autowired
    public AdminPlaylistController(PlaylistRepository playlistRepository) {
        this.playlistRepository = playlistRepository;
    }

    @GetMapping(value = "/playlist/count")
    SuccessResponse<Long> playlistCount(){
        return new SuccessResponse<>("number of playlists", playlistRepository.count());
    }

}
