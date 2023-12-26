package melowave;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import melowave.model.Song;
import melowave.service.SongService;

@SpringBootTest
@AutoConfigureMockMvc
@Disabled
public class SongControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SongService songService;

    @Test
    public void testGetSongById() throws Exception {
        Long songId = 1L;
        Song mockSong = new Song(1L, "Test title", "Test artist", "Test genre", new byte[0]);

        when(songService.getSongById(songId)).thenReturn(mockSong);

        mockMvc.perform(get("/song/find/{id}", songId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(songId))
                .andExpect(jsonPath("$.title").value("Test title"))
                .andExpect(jsonPath("$.title").value("Test artist"))
                .andExpect(jsonPath("$.title").value("Test genre"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testDeleteSong() throws Exception {
        Long songId = 1L;

        when(songService.deleteSong(songId)).thenReturn(true);

        mockMvc.perform(delete("/song/delete/{id}", songId))
                .andExpect(status().isNoContent())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testGetSongsByParameters() throws Exception {
        List<Song> mockSongs = Arrays.asList(
                new Song(1L, "Song1", "Artist1", "Genre1", new byte[0]),
                new Song(2L, "Song2", "Artist2", "Genre2", new byte[0])
        );

        when(songService.getSongsByParameters(any(), any(), any(), anyInt())).thenReturn(mockSongs);

        mockMvc.perform(get("/song/search")
                .param("title", "Song1")
                .param("artist", "Artist1")
                .param("genre", "Genre1")
                .param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Song1"))
                .andExpect(jsonPath("$[0].artist").value("Artist1"))
                .andExpect(jsonPath("$[0].genre").value("Genre1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].title").value("Song2"))
                .andExpect(jsonPath("$[1].artist").value("Artist2"))
                .andExpect(jsonPath("$[1].genre").value("Genre2"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testGetArtists() throws Exception {
        List<String> mockArtists = Arrays.asList("Artist1", "Artist2");

        when(songService.getArtists()).thenReturn(mockArtists);

        mockMvc.perform(get("/song/artists"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0]").value("Artist1"))
                .andExpect(jsonPath("$[1]").value("Artist2"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testGetGenres() throws Exception {
        List<String> mockGenres = Arrays.asList("Genre1", "Genre2");

        when(songService.getGenres()).thenReturn(mockGenres);

        mockMvc.perform(get("/song/genres"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0]").value("Genre1"))
                .andExpect(jsonPath("$[1]").value("Genre2"))
                .andDo(MockMvcResultHandlers.print());
    }
}
