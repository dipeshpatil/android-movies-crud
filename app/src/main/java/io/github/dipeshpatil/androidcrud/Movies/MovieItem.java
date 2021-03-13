package io.github.dipeshpatil.androidcrud.Movies;

public class MovieItem {
    private String title;
    private String poster;
    private String plot;

    public MovieItem(String title, String poster, String plot) {
        this.title = title;
        this.poster = poster;
        this.plot = plot;
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
