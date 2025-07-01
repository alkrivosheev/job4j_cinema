package ru.job4j.cinema.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    /**
     * Тест проверяет успешный поиск пользователя по email и паролю.
     * Ожидается:
     * Когда переданы корректные email и пароль, должен вернуться соответствующий пользователь.
     */
    @Test
    void whenFindByEmailAndPasswordCorrectThenUserFound() {
        User user = new User(1, "User Name", "email@example.com", "password");

        when(userRepository.findByEmailAndPassword("email@example.com", "password"))
                .thenReturn(Optional.of(user));

        Optional<User> result = userService.findByEmailAndPassword("email@example.com", "password");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("email@example.com");
    }

    /**
     * Тест проверяет поиск пользователя по некорректным email и паролю.
     * Ожидается:
     * Когда переданы неверные учетные данные, должен вернуться пустой Optional.
     */
    @Test
    void whenFindByEmailAndPasswordIncorrectThenEmpty() {
        when(userRepository.findByEmailAndPassword("wrong@example.com", "wrong"))
                .thenReturn(Optional.empty());

        Optional<User> result = userService.findByEmailAndPassword("wrong@example.com", "wrong");

        assertThat(result).isEmpty();
    }

    /**
     * Тест проверяет успешный поиск пользователя по email.
     * Ожидается:
     * Когда передан существующий email, должен вернуться соответствующий пользователь.
     */
    @Test
    void whenFindByEmailExistsThenUserFound() {
        User user = new User(1, "User Name", "email@example.com", "password");

        when(userRepository.findByEmail("email@example.com"))
                .thenReturn(Optional.of(user));

        Optional<User> result = userService.findByEmail("email@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1);
    }

    /**
     * Тест проверяет поиск пользователя по несуществующему email.
     * Ожидается:
     * Когда передан неизвестный email, должен вернуться пустой Optional.
     */
    @Test
    void whenFindByEmailNotExistsThenEmpty() {
        when(userRepository.findByEmail("nonexistent@example.com"))
                .thenReturn(Optional.empty());

        Optional<User> result = userService.findByEmail("nonexistent@example.com");

        assertThat(result).isEmpty();
    }
}