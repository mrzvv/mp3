import java.util.ArrayList;

public class Artists {
    String artistName;
    ArrayList<Albums> albums = new ArrayList<>();

    public Artists(String artistName, String album, String song) {
        this.artistName = artistName;
        albums.add(new Albums(album, song));
    }

    public void showInfo() {
        System.out.println(artistName);
        for (Albums a : albums) {
            a.showAlbums();
        }
    }
}
