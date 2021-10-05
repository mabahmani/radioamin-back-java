package ir.mab.radioamin.model.res;

import ir.mab.radioamin.entity.Album;
import ir.mab.radioamin.entity.Music;
import ir.mab.radioamin.entity.Playlist;
import ir.mab.radioamin.entity.Singer;
import ir.mab.radioamin.model.enums.TopicType;

import java.util.List;

public class HomeTopicsResponse {

    List<Topic> topics;

    public HomeTopicsResponse(List<Topic> topics) {
        this.topics = topics;
    }

    public static class Topic{
        String title;
        TopicType topicType;
        List<Music> musics;
        List<Playlist> playlists;
        List<Album> albums;
        List<Singer> singers;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public TopicType getTopicType() {
            return topicType;
        }

        public void setTopicType(TopicType topicType) {
            this.topicType = topicType;
        }

        public List<Music> getMusics() {
            return musics;
        }

        public void setMusics(List<Music> musics) {
            this.musics = musics;
        }

        public List<Playlist> getPlaylists() {
            return playlists;
        }

        public void setPlaylists(List<Playlist> playlists) {
            this.playlists = playlists;
        }

        public List<Album> getAlbums() {
            return albums;
        }

        public void setAlbums(List<Album> albums) {
            this.albums = albums;
        }

        public List<Singer> getSingers() {
            return singers;
        }

        public void setSingers(List<Singer> singers) {
            this.singers = singers;
        }

        @Override
        public String toString() {
            return "Topic{" +
                    "title='" + title + '\'' +
                    ", topicType=" + topicType +
                    ", musics=" + musics +
                    ", playlists=" + playlists +
                    ", albums=" + albums +
                    ", singers=" + singers +
                    '}';
        }
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    @Override
    public String toString() {
        return "HomeTopicsResponse{" +
                "topics=" + topics +
                '}';
    }
}
