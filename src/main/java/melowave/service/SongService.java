package melowave.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import melowave.model.Song;
import melowave.repository.SongRepo;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SongService {

    private final Logger logger = LoggerFactory.getLogger(SongService.class);
    private final SongRepo songRepo;

    public List<Song> getSongs() {
        logger.info("Fetching all songs");
        return songRepo.findAll();
    }

    public Song getSongById(Long id) {
        logger.info("Fetching song by ID: {}", id);
        Optional<Song> song = songRepo.findSongById(id);
        return song.orElse(null);
    }

    public Song createSong(Song song) {
        logger.info("Creating a new song with title: {}", song.getTitle());
        
        if (songRepo.existsByTitleAndArtist(song.getTitle(), song.getArtist())) {
            return null;
        }

        Song savedSong = songRepo.save(song);

        logger.info("New song created with ID: {}", savedSong.getId());
        return savedSong;
    }

    public Song updateSong(Long id, Song updatedSong) {
        logger.info("Updating song with ID: {}", id);

        Song existingSong = getSongById(id);

        existingSong.setTitle(updatedSong.getTitle());
        existingSong.setArtist(updatedSong.getArtist());
        existingSong.setGenre(updatedSong.getGenre());

        return songRepo.save(existingSong);
    }

    public boolean deleteSong(Long id) {
        logger.info("Deleting song with ID: {}", id);
        if (songRepo.existsById(id)) {
            songRepo.deleteById(id);
            return true;
        }
        return false;
    }
    
    public List<Song> getSongsByParameters(String title, String artist, String genre, int page) {
        logger.info("Fetching songs with parameters");
        
        Specification<Song> specification = Specification.where(null);

        if(title != null)
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get("title"), "%" + title + "%"));

        if(artist != null)
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("artist"), artist));

        if(genre != null)
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("genre"), genre));

        PageRequest pageRequest = PageRequest.of(page, 5);
        List<Song> songs = songRepo.findAll(specification, pageRequest).getContent();

        logger.info("Retrieved {} songs", songs.size());
        return songs;
    }
    
    public long countSongsByParameters(String title, String artist, String genre) {
        logger.info("Counting songs with parameters");

        Specification<Song> specification = Specification.where(null);

        if(title != null)
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get("title"), "%" + title + "%"));

        if(artist != null)
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("artist"), artist));

        if(genre != null)
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("genre"), genre));

        long count = songRepo.count(specification);

        logger.info("Counted {} songs", count);
        return count;
    }
    
    public List<String> getArtists() {
        List<String> artists = songRepo.findArtists();
        return artists;
    }

    public List<String> getGenres() {
        List<String> genres = songRepo.findGenres();
        return genres;
    }
}


