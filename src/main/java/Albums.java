import java.util.ArrayList;

public class Albums {
    String albumName;
    ArrayList<Songs> songs = new ArrayList<>();

    public Albums(String albumName, String song) {
        this.albumName = albumName;
        songs.add(new Songs(song));
    }

    public void showAlbums() {
        System.out.println(albumName);
        for (Songs s : songs) {
            s.showSongs();
        }
    }
}
