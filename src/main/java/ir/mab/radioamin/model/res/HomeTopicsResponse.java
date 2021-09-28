package ir.mab.radioamin.model.res;

import ir.mab.radioamin.entity.Genre;
import ir.mab.radioamin.entity.Mood;
import ir.mab.radioamin.entity.Music;
import ir.mab.radioamin.entity.Playlist;
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
        List<Genre> genres;
        List<Mood> moods;

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

        public List<Genre> getGenres() {
            return genres;
        }

        public void setGenres(List<Genre> genres) {
            this.genres = genres;
        }

        public List<Mood> getMoods() {
            return moods;
        }

        public void setMoods(List<Mood> moods) {
            this.moods = moods;
        }

        @Override
        public String toString() {
            return "Topic{" +
                    "title='" + title + '\'' +
                    ", topicType=" + topicType +
                    ", musics=" + musics +
                    ", playlists=" + playlists +
                    ", genres=" + genres +
                    ", moods=" + moods +
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
