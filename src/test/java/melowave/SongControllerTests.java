package melowave;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import melowave.controller.SongController;
import melowave.model.Song;
import melowave.service.SongService;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import java.util.Base64;

@WebMvcTest(SongController.class)
public class SongControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private SongService songService;

    @InjectMocks
    private SongController songController;

    @Test
    public void testGetSongById() throws Exception {
        Long songId = 1L;
        Song song = new Song();
        song.setId(songId);
        when(songService.getSongById(eq(songId))).thenReturn(song);

        mockMvc.perform(MockMvcRequestBuilders.get("/song/find/{id}", songId))
               .andExpect(MockMvcResultMatchers.status().isOk())
               .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(songId));
    }

    @Test
    public void testGetSongByIdNotFound() throws Exception {
        Long songId = 2L;
        when(songService.getSongById(eq(songId))).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/song/find/{id}", songId))
               .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testStreamSong() throws Exception {
        Long songId = 1L;
        Song song = new Song();
        song.setId(songId);
        song.setAudio(new byte[]{1, 2, 3});
        when(songService.getSongById(eq(songId))).thenReturn(song);
        String credentials = "hamza:hamza";
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
        
        mockMvc.perform(MockMvcRequestBuilders.get("/song/stream/{songId}", songId)
                    .header("Authorization", "Basic " + encodedCredentials))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_OCTET_STREAM));
        
    }
}
