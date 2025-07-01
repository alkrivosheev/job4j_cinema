package ru.job4j.cinema.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.job4j.cinema.model.Hall;
import ru.job4j.cinema.repository.HallRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HallServiceImplTest {

    @Mock
    private HallRepository hallRepository;

    @InjectMocks
    private HallServiceImpl hallService;

    /**
     * Тест проверяет поиск существующего кинозала по ID.
     * Ожидается:
     * - Возвращается Optional с ожидаемым кинозалом
     * - Найденный кинозал соответствует ожидаемому
     */
    @Test
    void whenFindByIdExistsThenReturnHall() {
        Hall expectedHall = new Hall(1, "Test Hall", 10, 15, "Description");
        when(hallRepository.findById(1)).thenReturn(Optional.of(expectedHall));

        Optional<Hall> actualHall = hallService.findById(1);

        assertThat(actualHall).isPresent();
        assertThat(actualHall.get()).isEqualTo(expectedHall);
    }

    /**
     * Тест проверяет поиск несуществующего кинозала по ID.
     * Ожидается:
     * - Возвращается пустой Optional
     */
    @Test
    void whenFindByIdNotExistsThenReturnEmpty() {
        when(hallRepository.findById(999)).thenReturn(Optional.empty());

        Optional<Hall> actualHall = hallService.findById(999);

        assertThat(actualHall).isEmpty();
    }

    /**
     * Тест проверяет получение всех кинозалов.
     * Ожидается:
     * - Возвращается коллекция всех кинозалов
     * - Количество и содержимое кинозалов соответствует ожидаемому
     */
    @Test
    void whenFindAllThenReturnAllHalls() {
        Hall hall1 = new Hall(1, "Hall 1", 5, 8, "Small hall");
        Hall hall2 = new Hall(2, "Hall 2", 10, 15, "Medium hall");
        List<Hall> expectedHalls = List.of(hall1, hall2);

        when(hallRepository.findAll()).thenReturn(expectedHalls);

        Collection<Hall> actualHalls = hallService.findAll();

        assertThat(actualHalls).hasSize(2);
        assertThat(actualHalls).containsExactlyInAnyOrderElementsOf(expectedHalls);
    }

    /**
     * Тест проверяет получение кинозалов при их отсутствии.
     * Ожидается:
     * - Возвращается пустая коллекция
     */
    @Test
    void whenFindAllEmptyThenReturnEmptyCollection() {
        when(hallRepository.findAll()).thenReturn(List.of());

        Collection<Hall> actualHalls = hallService.findAll();

        assertThat(actualHalls).isEmpty();
    }
}