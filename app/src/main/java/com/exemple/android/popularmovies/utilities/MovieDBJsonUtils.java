package com.exemple.android.popularmovies.utilities;



public class MovieDBJsonUtils {
}

/*
        Response to the Get API Configuration request
        https://api.themoviedb.org/3/configuration?api_key=<<api_key>>

        HttpResponse<String> response = Unirest.get("https://api.themoviedb.org/3/configuration?api_key=e<<api_key>>")
        .body("{}")
        .asString();

        {
        "images": {
        "base_url": "http://image.tmdb.org/t/p/",
        "secure_base_url": "https://image.tmdb.org/t/p/",
        "backdrop_sizes": [
        "w300",
        "w780",
        "w1280",
        "original"
        ],
        "logo_sizes": [
        "w45",
        "w92",
        "w154",
        "w185",
        "w300",
        "w500",
        "original"
        ],
        "poster_sizes": [
        "w92",
        "w154",
        "w185",
        "w342",
        "w500",
        "w780",
        "original"
        ],
        "profile_sizes": [
        "w45",
        "w185",
        "h632",
        "original"
        ],
        "still_sizes": [
        "w92",
        "w185",
        "w300",
        "original"
        ]
        },
        "change_keys": [
        "adult",
        "air_date",
        "also_known_as",
        "alternative_titles",
        "biography",
        "birthday",
        "budget",
        "cast",
        "certifications",
        "character_names",
        "created_by",
        "crew",
        "deathday",
        "episode",
        "episode_number",
        "episode_run_time",
        "freebase_id",
        "freebase_mid",
        "general",
        "genres",
        "guest_stars",
        "homepage",
        "images",
        "imdb_id",
        "languages",
        "name",
        "network",
        "origin_country",
        "original_name",
        "original_title",
        "overview",
        "parts",
        "place_of_birth",
        "plot_keywords",
        "production_code",
        "production_companies",
        "production_countries",
        "releases",
        "revenue",
        "runtime",
        "season",
        "season_number",
        "season_regular",
        "spoken_languages",
        "status",
        "tagline",
        "title",
        "translations",
        "tvdb_id",
        "tvrage_id",
        "type",
        "video",
        "videos"
        ]
        }



        */

/*
Response for a single movie searh
        https://api.themoviedb.org/3/search/movie?api_key={api_key}&query=Jack+Reacher

        {
        "poster_path": "/IfB9hy4JH1eH6HEfIgIGORXi5h.jpg",
        "adult": false,
        "overview": "Jack Reacher must uncover the truth behind a major government conspiracy in order to clear his name. On the run as a fugitive from the law, Reacher uncovers a potential secret from his past that could change his life forever.",
        "release_date": "2016-10-19",
        "genre_ids": [
        53,
        28,
        80,
        18,
        9648
        ],
        "id": 343611,
        "original_title": "Jack Reacher: Never Go Back",
        "original_language": "en",
        "title": "Jack Reacher: Never Go Back",
        "backdrop_path": "/4ynQYtSEuU5hyipcGkfD6ncwtwz.jpg",
        "popularity": 26.818468,
        "vote_count": 201,
        "video": false,
        "vote_average": 4.19
        }

the id : 343611 item is used for querying the remaining details
        https://api.themoviedb.org/3/movie/343611?api_key={api_key}
*/
