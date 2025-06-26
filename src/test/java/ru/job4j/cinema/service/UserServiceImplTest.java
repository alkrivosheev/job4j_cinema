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

    @Test
    void whenFindByEmailAndPasswordCorrectThenUserFound() {
        User user = new User(1, "User Name", "email@example.com", "password");

        when(userRepository.findByEmailAndPassword("email@example.com", "password"))
                .thenReturn(Optional.of(user));

        Optional<User> result = userService.findByEmailAndPassword("email@example.com", "password");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("email@example.com");
    }

    @Test
    void whenFindByEmailAndPasswordIncorrectThenEmpty() {
        when(userRepository.findByEmailAndPassword("wrong@example.com", "wrong"))
                .thenReturn(Optional.empty());

        Optional<User> result = userService.findByEmailAndPassword("wrong@example.com", "wrong");

        assertThat(result).isEmpty();
    }

    @Test
    void whenFindByEmailExistsThenUserFound() {
        User user = new User(1, "User Name", "email@example.com", "password");

        when(userRepository.findByEmail("email@example.com"))
                .thenReturn(Optional.of(user));

        Optional<User> result = userService.findByEmail("email@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1);
    }

    @Test
    void whenFindByEmailNotExistsThenEmpty() {
        when(userRepository.findByEmail("nonexistent@example.com"))
                .thenReturn(Optional.empty());

        Optional<User> result = userService.findByEmail("nonexistent@example.com");

        assertThat(result).isEmpty();
    }
}