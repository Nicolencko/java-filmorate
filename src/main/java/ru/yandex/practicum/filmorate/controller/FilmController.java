package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final static Logger log = LoggerFactory.getLogger(FilmController.class);

    Map<Integer, Film> filmMap = new HashMap<>();


    @GetMapping
    public Map<Integer, Film> getFilms() {
        return filmMap;
    }

    @PostMapping
    public void addFilm(@RequestBody Film film) throws ValidationException {

        // проверка названия
        String name = film.getName();
        boolean isNameNotEmpty = !name.isEmpty();

        // проверка максимальной длины описания - 200 символов
        String description = film.getDescription();
        boolean isDescriptionNormalLength = description.length() <= 200;

        // проверка даты релиза - не раньше 28 декабря 1895 года
        LocalDate releaseDate = film.getReleaseDate();
        LocalDate firstReleaseFilm = LocalDate.of(1895, 12, 28);
        boolean isReleaseDateAfterFirstRelease = releaseDate.isAfter(firstReleaseFilm);

        // проверка продолжительности фильма - должна быть положительной
        long duration = film.getDuration();
        boolean isDurationPositive = duration > 0;

        // если фильмов ещё нет - добавляем первый
        if (filmMap.size() == 0 && isNameNotEmpty && isDescriptionNormalLength && isReleaseDateAfterFirstRelease
        && isDurationPositive) {
            filmMap.put(film.getId(), film);

        // фильмы уже есть
        } else if (isNameNotEmpty && isDescriptionNormalLength && isReleaseDateAfterFirstRelease
                && isDurationPositive){
            for (Map.Entry<Integer, Film> integerFilmEntry : filmMap.entrySet()) {
                if (integerFilmEntry.getValue().getId() == film.getId()) {
                    System.out.println("Этот id уже занят");

                    log.info("Попытка добавить фильм с занятым id: название "
                            + film.getName() + " id " + film.getId());
                } else {
                    filmMap.put(film.getId(), film);

                    log.info("Добавлен новый фильм: " + film.getName());
                }
            }
        } else {
            log.info("Ошибка данных при добавлении фильма");

            throw new ValidationException("Ошибка данных при добавлении фильма");
        }
    }

    @PutMapping
    public void updateFilm(@RequestBody Film film) {

        for (Map.Entry<Integer, Film> integerFilmEntry : filmMap.entrySet()) {
            if (integerFilmEntry.getValue().getId() == film.getId()) {
                filmMap.replace(film.getId(), film);

                log.info("Данные фильма обновлены: " + film.getName());
            }
        }
    }
}
