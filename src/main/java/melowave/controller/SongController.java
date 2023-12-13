package melowave.controller;

import lombok.RequiredArgsConstructor;
import melowave.model.Song;
import melowave.service.SongService;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/song")
@CrossOrigin(origins = "https://hamza-okutucu.github.io")
@RequiredArgsConstructor
public class SongController {

    private final Logger logger = LoggerFactory.getLogger(SongController.class);
    private final SongService songService;

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<Song>> getSongs() {
        logger.info("Attempting to get all songs");
        List<Song> songs = songService.getSongs();
        logger.info("Retrieved {} songs", songs.size());
        return new ResponseEntity<>(songs, HttpStatus.OK);
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<Song> getSongById(@PathVariable Long id) {
        logger.info("Attempting to get song by ID: {}", id);
        Song song = songService.getSongById(id);

        if (song == null) {
            logger.error("Song not found with ID: {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        logger.info("Retrieved song with ID: {}", id);
        return new ResponseEntity<>(song, HttpStatus.OK);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<Song> createSong(@RequestBody Song song) {
        logger.info("Attempting to create a new song with title: {}", song.getTitle());
        Song newSong = songService.createSong(song);

        if (newSong == null) {
            logger.error("Song already exists.");
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        logger.info("Created a new song with ID: {}", newSong.getId());
        return new ResponseEntity<>(newSong, HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<Song> updateSong(@PathVariable Long id, @RequestBody Song updatedSong) {
        logger.info("Attempting to update song with ID: {}", id);
        Song song = songService.updateSong(id, updatedSong);

        if (song == null) {
            logger.error("Song not found with ID: {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        logger.info("Updated song with ID: {}", id);
        return new ResponseEntity<>(song, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteSong(@PathVariable Long id) {
        logger.info("Attempting to delete song with ID: {}", id);
        boolean deleted = songService.deleteSong(id);
        
        if (!deleted) {
            logger.error("Song not found with ID: {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        logger.info("Deleted song with ID: {}", id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Song>> getSongsByParameters(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "artist", required = false) String artist,
            @RequestParam(value = "genre", required = false) String genre,
            @RequestParam(value = "page", defaultValue = "0") int page
    ) {
        logger.info("Attempting to search songs with parameters");
        List<Song> songs = songService.getSongsByParameters(title, artist, genre, page);
        logger.info("Retrieved {} songs", songs.size());
        return new ResponseEntity<>(songs, HttpStatus.OK);
    }

    @GetMapping("/search/count")
    public ResponseEntity<Long> countSongsByParameters(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "artist", required = false) String artist,
            @RequestParam(value = "genre", required = false) String genre
    ) {
        logger.info("Attempting to count songs with parameters");
        long count = songService.countSongsByParameters(title, artist, genre);
        logger.info("Counted {} songs", count);
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @GetMapping("/stream/{songId}")
    public ResponseEntity<Resource> streamSong(@PathVariable Long songId) {
        logger.info("Attempting to stream song with ID: {}", songId);
        Song song = songService.getSongById(songId);
    
        if (song == null) {
            logger.warn("Song not found with ID: {}", songId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
            
        ByteArrayResource resource = new ByteArrayResource(song.getAudio());
    
        logger.info("Streaming song with ID: {} - Title: {}", songId, song.getTitle());
    
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(song.getAudio().length)
                .body(resource);
    }
    
    @GetMapping("/status")
    public ResponseEntity<String> getStatus() {
        return new ResponseEntity<>("Looks good !", HttpStatus.OK);
    }

    @GetMapping("/download/{songId}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Resource> downloadSong(@PathVariable Long songId) {
        logger.info("Attempting to download song with ID: {}", songId);
        Song song = songService.getSongById(songId);

        if (song == null) {
            logger.warn("Song not found with ID: {}", songId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ByteArrayResource resource = new ByteArrayResource(song.getAudio());

        logger.info("Downloading song with ID: {} - Title: {}", songId, song.getTitle());
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + song.getTitle() + ".mp3")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(song.getAudio().length)
                .body(resource);
    }
    
    @GetMapping("/artists")
    public ResponseEntity<List<String>> getArtists() {
        logger.info("Attempting to get all artists");
        List<String> artists = songService.getArtists();
        logger.info("Retrieved {} artists", artists.size());
        return new ResponseEntity<>(artists, HttpStatus.OK);
    }
    
    @GetMapping("/genres")
    public ResponseEntity<List<String>> getGenres() {
        logger.info("Attempting to get all genres");
        List<String> genres = songService.getGenres();
        logger.info("Retrieved {} genres", genres.size());
        return new ResponseEntity<>(genres, HttpStatus.OK);
    }
}