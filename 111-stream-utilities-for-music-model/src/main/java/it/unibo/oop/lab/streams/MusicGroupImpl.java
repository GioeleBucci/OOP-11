package it.unibo.oop.lab.streams;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 */
public final class MusicGroupImpl implements MusicGroup {

  private final Map<String, Integer> albums = new HashMap<>();
  private final Set<Song> songs = new HashSet<>();

  @Override
  public void addAlbum(final String albumName, final int year) {
    this.albums.put(albumName, year);
  }

  @Override
  public void addSong(final String songName, final Optional<String> albumName, final double duration) {
    if (albumName.isPresent() && !this.albums.containsKey(albumName.get())) {
      throw new IllegalArgumentException("invalid album name");
    }
    this.songs.add(new MusicGroupImpl.Song(songName, albumName, duration));
  }

  @Override
  public Stream<String> orderedSongNames() {
    return songs.stream()
        .map(s -> s.songName)
        .sorted();
  }

  @Override
  public Stream<String> albumNames() {
    return albums.keySet().stream();
  }

  @Override
  public Stream<String> albumInYear(final int year) {
    return albums.entrySet().stream()
        .filter(entry -> entry.getValue() == year)
        .map(entry -> entry.getKey());
  }

  private int countSongsInOptionalAlbum(final Optional<String> albumName) {
    return (int) songs.stream()
        .map(s -> s.albumName)
        .filter(a -> a.equals(albumName))
        .count();
  }

  @Override
  public int countSongs(final String albumName) {
    return countSongsInOptionalAlbum(Optional.of(albumName));
  }

  @Override
  public int countSongsInNoAlbum() {
    return countSongsInOptionalAlbum(Optional.empty());
  }

  @Override
  public OptionalDouble averageDurationOfSongs(final String albumName) {
    return songs.stream()
        .filter(s -> s.albumName.equals(Optional.of(albumName)))
        .mapToDouble(s -> s.duration)
        .average();
  }

  @Override
  public Optional<String> longestSong() {
    return songs.stream()
        .sorted(Comparator.comparingDouble(s -> -s.duration))
        .map(s -> s.songName)
        .findFirst();
  }

  @Override
  public Optional<String> longestAlbum() {
    return Optional.of(albums.keySet().stream()
        .map(albumName -> Map.of(albumName, (countSongs(albumName) * averageDurationOfSongs(albumName).getAsDouble())))
        .max((e1, e2) -> (int) (e1.values().iterator().next() - e2.values().iterator().next()))
        .get().keySet().iterator().next());
  }

  private static final class Song {

    private final String songName;
    private final Optional<String> albumName;
    private final double duration;
    private int hash;

    Song(final String name, final Optional<String> album, final double len) {
      super();
      this.songName = name;
      this.albumName = album;
      this.duration = len;
    }

    public String getSongName() {
      return songName;
    }

    public Optional<String> getAlbumName() {
      return albumName;
    }

    public double getDuration() {
      return duration;
    }

    @Override
    public int hashCode() {
      if (hash == 0) {
        hash = songName.hashCode() ^ albumName.hashCode() ^ Double.hashCode(duration);
      }
      return hash;
    }

    @Override
    public boolean equals(final Object obj) {
      if (obj instanceof Song) {
        final Song other = (Song) obj;
        return albumName.equals(other.albumName) && songName.equals(other.songName)
            && duration == other.duration;
      }
      return false;
    }

    @Override
    public String toString() {
      return "Song [songName=" + songName + ", albumName=" + albumName + ", duration=" + duration + "]";
    }

  }

}
