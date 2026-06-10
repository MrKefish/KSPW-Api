# Что такое KSPW-Api
Библиотека для простого использования SPWorlds api  
Поддерживает Java/Kotlin/Kotlin Multiplatform
## Установка maven
1) Добавьте репозиторий в pom.xml:
```
<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
```
2) Добавьте зависимость в pom.xml:
```
<dependency>
	    <groupId>com.github.MrKefish</groupId>
	    <artifactId>KSPW-Api</artifactId>
	    <version>VERSION</version>
	</dependency>
```
   
## Установка gradle
1) Добавьте репозиторий в pom.xml:
```
repositories {
    maven { url = uri("https://jitpack.io") }
}
```
2) Добавьте зависимость в pom.xml:
```
dependencies {
    implementation("com.github.MrKefish.KSPW-Api:KSPW-Api:1.0.0")
}
```
---
## Использование
Запишите token и id карты в объект SpCard.  
Пример на kotlin:
```
val yourCard = SpCard(id = "YOUR_ID", token = "YOUR_TOKEN")
```
Теперь вы можете пользоваться методами в SpWorldsApi.  
Пример на kotlin:
```
val cardMain = SpCard(id = "YOUR_ID", token = "YOUR_TOKEN")
try {
    val balance1 = SpWorldsApi.getBalance(cardMain)
    val profile = SpWorldsApi.getProfile(cardMain)
    val name = SpWorldsApi.getName(cardMain, "733184856489721896")
    val cards = SpWorldsApi.getCards(cardMain, "MrKefish")
    val transactionResult = SpWorldsApi.postTransaction(cardMain, "92550", 1, "Тестирование библиотеки")
    val webHookResult = SpWorldsApi.changeCardWebhook(cardMain, "example.org")

    println("Баланс: $balance1")
    println("Игрок: $profile")
    println("Имя: $name")
    println("Карты: $cards")
    println("Операция: $transactionResult")
    println("Webhook: $webHookResult")
    } catch (e: Exception) {
        println("Ошибка сети: ${e.message}")
    }
```
