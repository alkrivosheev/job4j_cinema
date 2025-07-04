# job4j_cinema

## Описание проекта

Проект "Кинотеатр" представляет собой веб-приложение для бронирования билетов в кинотеатре. Пользователи могут просматривать расписание сеансов, информацию о фильмах и покупать билеты. Приложение включает функционал регистрации и авторизации, а также разделение прав доступа: только зарегистрированные пользователи могут совершать покупки. 

Основные возможности:
- Просмотр расписания сеансов и списка фильмов.
- Покупка билетов на выбранные места.
- Обработка успешных и неудачных попыток бронирования.
- Регистрация и вход в систему.

## Стек технологий

- **Java 17**
- **Spring Boot 2.7.x**
- **Thymeleaf** (для шаблонов HTML)
- **Bootstrap 5** (стилизация интерфейса)
- **Liquibase** (управление миграциями БД)
- **Sql2o** (работа с PostgreSQL)
- **PostgreSQL 14** (СУБД)

## Требования к окружению

Для запуска проекта необходимо установить:
- **JDK 17**
- **Maven 3.8+**
- **PostgreSQL 14+**
- **Браузер** (рекомендуется Chrome/Firefox)

## Запуск проекта

1. **Создание базы данных:**
   ```sql
   CREATE DATABASE cinema;
   
2. **Настройка подключения к БД:**
	Измените параметры в src/main/resources/application.yml:
	spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/cinema
    username: ваш_логин
    password: ваш_пароль
	
3.	**Запуск приложения:**
В корне проекта выполните:	
mvn spring-boot:run

4.	**Открытие в браузере:**
	Перейдите по адресу:
	http://localhost:8080
	
Взаимодействие с приложением
Главная страница
![img.png](img.png)Главная страница
Общая информация о кинотеатре.

Кинотека
![img_2.png](img_2.png) Кинотека
Список фильмов в прокате

Расписание сеансов
![img_1.png](img_1.png)Расписание
Список доступных сеансов с фильмами.

Покупка билета
![img_4.png](img_4.png)Покупка билета
Выбор места и подтверждение бронирования.

Успешная покупка
![img_5.png](img_5.png)Успех
Подтверждение успешного бронирования.

Неудачная покупка
![img_6.png](img_6.png)Ошибка
Сообщение о занятом месте.

Авторизация пользователя
![img_3.png](img_3.png)

Контакты
По вопросам и предложениям:
📧 Email: alkrivosheev@gmail.com