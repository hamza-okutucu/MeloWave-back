package melowave.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import melowave.model.Song;

public interface SongRepo extends JpaRepository<Song, Long> {
    Optional<Song> findSongById(Long id);
    Optional<Song> findSongByTitle(String title);
    boolean existsById(Long id);
    boolean existsByTitleAndArtist(String title, String artist);
    void deleteById(Long id);
    Page<Song> findAll(Specification<Song> specification, Pageable pageRequest);
    long count(Specification<Song> specification);
    @Query("SELECT DISTINCT genre FROM Song WHERE genre IS NOT NULL")
    List<String> findGenres();
    @Query("SELECT DISTINCT artist FROM Song WHERE artist IS NOT NULL")
    List<String> findArtists();
}