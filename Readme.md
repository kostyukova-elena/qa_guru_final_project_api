# Проект автоматизации тестирования для сайта [DEMOQA](https://demoqa.com/)

## Содержание:

- [Технологии и инструменты](#технологии-и-инструменты)
- [Примеры автотестов](#примеры-автотестов)
- [Локальный запуск тестов в Idea](#локальный-запуск-тестов-в-Idea)
- [Сборка в Jenkins](#сборка-в-jenkins)
- [Allure Report](#allure-report)
- [Уведомления о результатах тестов в Telegram](#уведомления-о-результатах-тестов-в-telegram)
- [Видео примера запуска тестов Selenoid](#ввидео-примера-запуска-тестов-selenoid)

## Технологии и инструменты

<p align="center">   
<a href="https://www.java.com/"><img src="files/images/java.jpg" width="50" height="50"  alt="Java"/></a>
<a href="https://www.jetbrains.com/idea/"><img src="files/images/IntelliJ_IDEA.png" width="50" height="50"  alt="IDEA"/></a>
<a href="https://github.com/"><img src="files/images/github.jpg" width="50" height="50"  alt="Github"/></a>   
<a href="https://gradle.org/"><img src="files/images/gradle.png" width="50" height="50"  alt="Gradle"/></a>
<a href="https://junit.org/junit5/"><img src="files/images/JUnit_5.png" width="100" height="50"  alt="JUnit 5"/></a>
<a href="https://selenide.org/"><img src="files/images/selenide.jpeg" width="50" height="50"  alt="Selenide"/></a>  
<a href="https://aerokube.com/selenoid/"><img src="files/images/selenoid.jpg" width="50" height="50"  alt="Selenoid"/></a>  
<a href="https://github.com/allure-framework/allure2"><img src="files/images/Allure.png" width="50" height="50"  alt="Allure"/></a>  
<a href="https://www.jenkins.io/"><img src="files/images/jenkins.jpg" width="50" height="50"  alt="Jenkins"/></a>
</p>

В проекте автотесты написаны на языке <code>Java</code> с использованием фреймворка для тестирования <code>Selenide</code>.
- В качестве сборщика был использован - <code>Gradle</code>.
- Использованы фреймворки <code>JUnit 5</code> и <code>Selenide</code>.
- Прогон автотестов запускается на удалённом сервере <code>Selenoid</code>.
- Запуск реализован через <code>Jenkins</code> с формированием Allure-отчета и отправкой результатов в <code>Telegram</code> при помощи бота.

## Примеры автотестов
-  Тест на создание нового пользователя с невалидным паролем
-  Тест на создание нового пользователя
-  Тест на повторное создание пользователя
-  Тест на генерацию токена для созданного
-  Тест на прохождение авторизации созданным пользователем
-  Тест на получение информации о созданном пользователе
-  Тест на удаление пользователя
-  Тест на повторное удаление пользователя


<a id="jenkins-build"></a>
## Сборка в Jenkins

[Сборка в Jenkins](https://jenkins.autotests.cloud/job/038-kostyukova_ea-Jenkins-final-project-web/)


## Allure Report
<p align="center">  
<img title="Allure TestOps Dashboard" src="files/images/Allure Report_1.jpg" width="791">
<img title="Allure TestOps Dashboard" src="files/images/Allure Report_2.jpg" width="791">  
</p>  

## Уведомления о результатах тестов в Telegram

<p align="center">
<img src="files/images/Allert-bot_api.png" alt="TestOps launch" width="400" height="400">
</p>
