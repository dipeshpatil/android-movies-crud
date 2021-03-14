package io.github.dipeshpatil.androidcrud.Movies;

public class MovieItem {
    private String title;
    private String poster;
    private String plot;
    private String genre;
    private String rating;
    private String year;

    public MovieItem(String title, String poster, String plot, String genre, String rating, String year) {
        this.title = title;
        this.poster = poster;
        this.plot = plot;
        this.genre = genre;
        this.rating = rating;
        this.year = year;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }
}
