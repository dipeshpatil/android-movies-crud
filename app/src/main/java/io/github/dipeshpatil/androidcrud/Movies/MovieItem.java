package io.github.dipeshpatil.androidcrud.Movies;

public class MovieItem {
    private String title;
    private String poster;
    private String plot;
    private String genre;
    private String rating;
    private String year;
    private String title_slug;
    private String released;
    private String actors;
    private String directors;

    public MovieItem() {
    }

    public MovieItem(
            String title,
            String plot,
            String rating,
            String poster,
            String genre,
            String year,
            String released,
            String actors,
            String directors,
            String title_slug
    ) {
        this.title = title;
        this.poster = poster;
        this.plot = plot;
        this.genre = genre;
        this.rating = rating;
        this.year = year;
        this.released = released;
        this.actors = actors;
        this.directors = directors;
        this.title_slug = title_slug;
    }

    public String getTitle() {
        return title;
    }

    public String getPoster() {
        return poster;
    }

    public String getPlot() {
        return plot;
    }

    public String getGenre() {
        return genre;
    }

    public String getRating() {
        return rating;
    }

    public String getYear() {
        return year;
    }

    public String getTitle_slug() {
        return title_slug;
    }

    public String getReleased() {
        return released;
    }

    public String getActors() {
        return actors;
    }

    public String getDirectors() {
        return directors;
    }
}
