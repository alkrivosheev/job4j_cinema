package ru.job4j.cinema.controller;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.service.UserService;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private UserService userService;

    private User testUser;

    @BeforeEach
    public void init() {
        testUser = new User();
        testUser.setId(1);
        testUser.setFullName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
    }

    /**
     * Тест проверяет корректное отображение страницы регистрации пользователя.
     * Ожидается:
     * - HTTP статус 200 (OK)
     * - Возвращение view с именем "users/register"
     */
    @Test
    public void whenGetRegisterPageThenReturnRegisterView() throws Exception {
        mvc.perform(get("/users/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/register"));
    }

    /**
     * Тест проверяет успешную регистрацию нового пользователя.
     * Ожидается:
     * - Редирект на главную страницу ("/index")
     * - HTTP статус 302 (Redirect)
     * - Пользователь успешно сохраняется в сервисе
     */
    @Test
    public void whenRegisterSuccessThenRedirectToIndex() throws Exception {
        when(userService.save(any(User.class))).thenReturn(Optional.of(testUser));

        mvc.perform(post("/users/register")
                        .flashAttr("user", testUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/index"));
    }

    /**
     * Тест проверяет обработку попытки регистрации с уже существующим email.
     * Ожидается:
     * - Возвращение страницы ошибки 500
     * - Наличие сообщения об ошибке в модели
     * - HTTP статус 200 (OK)
     */
    @Test
    public void whenRegisterWithExistingEmailThenReturnErrorPage() throws Exception {
        when(userService.save(any(User.class))).thenReturn(Optional.empty());

        mvc.perform(post("/users/register")
                        .flashAttr("user", testUser))
                .andExpect(status().isOk())
                .andExpect(view().name("errors/500"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    /**
     * Тест проверяет обработку исключения при регистрации пользователя.
     * Ожидается:
     * - Возвращение страницы ошибки 500
     * - Наличие сообщения об ошибке и stack trace в модели
     * - HTTP статус 200 (OK)
     */
    @Test
    public void whenRegisterWithExceptionThenReturnErrorPageWithStackTrace() throws Exception {
        String errorMessage = "Database error";
        when(userService.save(any(User.class))).thenThrow(new RuntimeException(errorMessage));

        mvc.perform(post("/users/register")
                        .flashAttr("user", testUser))
                .andExpect(status().isOk())
                .andExpect(view().name("errors/500"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attributeExists("stackTrace"));
    }

    /**
     * Тест проверяет корректное отображение страницы входа пользователя.
     * Ожидается:
     * - HTTP статус 200 (OK)
     * - Возвращение view с именем "users/login"
     */
    @Test
    public void whenGetLoginPageThenReturnLoginView() throws Exception {
        mvc.perform(get("/users/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/login"));
    }

    /**
     * Тест проверяет успешную аутентификацию пользователя.
     * Ожидается:
     * - Редирект на главную страницу ("/index")
     * - HTTP статус 302 (Redirect)
     * - Установка атрибута "user" в сессии
     */
    @Test
    public void whenLoginSuccessThenRedirectToIndexAndSetSession() throws Exception {
        when(userService.findByEmailAndPassword(testUser.getEmail(), testUser.getPassword()))
                .thenReturn(Optional.of(testUser));

        mvc.perform(post("/users/login")
                        .param("email", testUser.getEmail())
                        .param("password", testUser.getPassword()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/index"))
                .andExpect(request().sessionAttribute("user", testUser));
    }

    /**
     * Тест проверяет обработку неудачной попытки входа.
     * Ожидается:
     * - Возвращение страницы входа с сообщением об ошибке
     * - HTTP статус 200 (OK)
     * - Наличие атрибута "error" в модели
     */
    @Test
    public void whenLoginFailedThenReturnLoginPageWithError() throws Exception {
        when(userService.findByEmailAndPassword(anyString(), anyString()))
                .thenReturn(Optional.empty());

        mvc.perform(post("/users/login")
                        .param("email", "wrong@example.com")
                        .param("password", "wrong"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/login"))
                .andExpect(model().attributeExists("error"));
    }

    /**
     * Тест проверяет корректное завершение сессии пользователя.
     * Ожидается:
     * - Редирект на страницу входа ("/users/login")
     * - HTTP статус 302 (Redirect)
     * - Инвалидация текущей сессии
     */
    @Test
    public void whenLogoutThenInvalidateSessionAndRedirectToLogin() throws Exception {
        mvc.perform(get("/users/logout")
                        .sessionAttr("user", testUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/login"))
                .andExpect(result -> {
                    HttpSession session = result.getRequest().getSession(false);
                    assert session == null;
                });
    }
}