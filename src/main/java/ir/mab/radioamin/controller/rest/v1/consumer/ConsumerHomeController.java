package ir.mab.radioamin.controller.rest.v1.consumer;

import ir.mab.radioamin.config.ApiBaseEndpoints;
import ir.mab.radioamin.entity.*;
import ir.mab.radioamin.exception.ResourceNotFoundException;
import ir.mab.radioamin.model.enums.MusicType;
import ir.mab.radioamin.model.enums.TopicType;
import ir.mab.radioamin.model.res.HomeTopicsResponse;
import ir.mab.radioamin.model.res.SuccessResponse;
import ir.mab.radioamin.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = ApiBaseEndpoints.VersionOne.CONSUMER)
public class ConsumerHomeController {
    SingerRepository singerRepository;
    AlbumRepository albumRepository;
    UserRepository userRepository;
    MusicRepository musicRepository;
    GenreRepository genreRepository;
    MoodRepository moodRepository;
    PlaylistRepository playlistRepository;

    @Autowired
    public ConsumerHomeController(
            SingerRepository singerRepository,
            AlbumRepository albumRepository,
            UserRepository userRepository,
            MusicRepository musicRepository,
            GenreRepository genreRepository,
            MoodRepository moodRepository,
            PlaylistRepository playlistRepository

    ) {
        this.singerRepository = singerRepository;
        this.albumRepository = albumRepository;
        this.userRepository = userRepository;
        this.musicRepository = musicRepository;
        this.genreRepository = genreRepository;
        this.moodRepository = moodRepository;
        this.playlistRepository = playlistRepository;
    }

    @GetMapping("/home-topics")
    SuccessResponse<HomeTopicsResponse> getHomeTopics(
            Authentication authentication
    ) {

        List<HomeTopicsResponse.Topic> topics = new ArrayList<>();

        List<Singer> top20Singers = singerRepository.findTop20ByOrderByFollowCountsDesc();
        List<Music> featuredMusics = new ArrayList<>();
        for (Singer singer: top20Singers){
            if (!singer.getMusics().isEmpty())
                if (singer.getMusics().iterator().next().getMusicType() == MusicType.VOCAL)
                    featuredMusics.add(singer.getMusics().iterator().next());
        }
        HomeTopicsResponse.Topic topic = new HomeTopicsResponse.Topic();
        topic.setTitle("Featured Musics");
        topic.setTopicType(TopicType.MUSIC);
        topic.setMusics(featuredMusics);
        topics.add(topic);

        List<Music> top20NewReleases = musicRepository.findTop20ByMusicTypeIsOrderByIdDesc(MusicType.VOCAL);
        topic = new HomeTopicsResponse.Topic();
        topic.setTitle("New Releases");
        topic.setTopicType(TopicType.MUSIC);
        topic.setMusics(top20NewReleases);
        topics.add(topic);

        User user = userRepository.findUserByEmail(authentication.getName()).orElseThrow(
                () -> new ResourceNotFoundException("User", String.valueOf(authentication.getName()), "userId"));

        List<Music> quickPicks = new ArrayList<>();
        for (Follow follow: user.getFollows()){
            if (!follow.getSinger().getMusics().isEmpty())
                if (follow.getSinger().getMusics().iterator().next().getMusicType() == MusicType.VOCAL)
                    quickPicks.add(follow.getSinger().getMusics().iterator().next());
        }

        int remain = 20 - quickPicks.size();

        if (remain > 0){
            List<Music> top20 = musicRepository.findTop20ByMusicTypeIs(MusicType.VOCAL);
            for (int i=0; i<remain; i++){
                if (top20.size() > i)
                    quickPicks.add(top20.get(i));
            }
        }

        topic = new HomeTopicsResponse.Topic();
        topic.setTitle("Quick Picks");
        topic.setTopicType(TopicType.QUICK_PICK);
        topic.setMusics(quickPicks);
        topics.add(topic);


        List<Album> top20Albums = albumRepository.findTop20By();
        topic = new HomeTopicsResponse.Topic();
        topic.setTitle("Top Albums");
        topic.setTopicType(TopicType.ALBUM);
        topic.setAlbums(top20Albums);
        topics.add(topic);

        topic = new HomeTopicsResponse.Topic();
        topic.setTitle("Top Singers");
        topic.setTopicType(TopicType.SINGER);
        topic.setSingers(top20Singers);
        topics.add(topic);

        List<Music> top20Videos = musicRepository.findTop20ByMusicTypeIs(MusicType.VIDEO);
        topic = new HomeTopicsResponse.Topic();
        topic.setTitle("Recommended Music Videos");
        topic.setTopicType(TopicType.MUSIC_VIDEO);
        topic.setMusics(top20Videos);
        topics.add(topic);

        List<Playlist> top20Playlists = playlistRepository.findTop20By();
        topic = new HomeTopicsResponse.Topic();
        topic.setTitle("Top Playlists");
        topic.setTopicType(TopicType.PLAYLIST);
        topic.setPlaylists(top20Playlists);
        topics.add(topic);

        return new SuccessResponse<>("success", new HomeTopicsResponse(topics));
    }
}
