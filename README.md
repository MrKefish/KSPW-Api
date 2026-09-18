# Что такое KSPW-Api
Библиотека для простого использования SPWorlds api  
Поддерживает Java/Kotlin/Kotlin Multiplatform
## Установка maven
1) Добавьте репозиторий в pom.xml:
```
<repositories>
    <repository>
        <id>central</id>
        <url>https://apache.org</url>
    </repository>
</repositories>
```
2) Добавьте зависимость в pom.xml:  
   "VERSION замените на последний стабильный релиз"
```
<dependency>
    <groupId>io.github.mrkefish</groupId>
    <artifactId>kspw-api</artifactId>
    <version>VERSION</version>
</dependency>

```
   
## Установка gradle
1) Добавьте репозиторий в pom.xml:  
```
repositories {
    mavenCentral()
}
```
2) Добавьте зависимость в pom.xml:  
   "VERSION замените на последний стабильный релиз"
```
dependencies {
	implementation("io.github.mrkefish:kspw-api:VERSION")
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
Пример на kotlin из coroutine контекста:
```kotlin
val cardMain = SpCard(id = "YOUR_ID", token = "YOUR_TOKEN")

val cardInfo = SpWorldsApi.getCardInfo(cardMain)
val profile = SpWorldsApi.getProfile(cardMain)
val name = SpWorldsApi.getName(cardMain, "733184856489721896")
val cards = SpWorldsApi.getCards(cardMain, "MrKefish")
val transactionResult = SpWorldsApi.postTransaction(cardMain, "92550", 1, "Тестирование библиотеки")
val webHookResult = SpWorldsApi.changeCardWebhook(cardMain, "example.org")

profile.fold(
	onSuccess = { response -> /*Ваш "K" код*/},
	onFailure = {exception -> /*Ваш кот(лин) 🐈‍⬛*/}
)
```
Пример на java:  

```java

SpCard cardMain = new SpCard("YOUR_ID", "YOUR_TOKEN");

SpWorldsApi.getCardInfoAsync(cardMain,  new SpCallback<BalanceResponse>() {
	@Override
	public void onSuccess(BalanceResponse balanceResponse) {
		// Ваш ☕ код
	}
	@Override
	public void onError(@NotNull Throwable throwable) {
		// Ваш кот 🐈
	}
});

Result<ProfileResponse> profileResult = SpWorldsApi.getProfileSync(cardMain);

if (profileResult.isSuccess()) {
  // Ваш ☕ код
} else {
  // Ваш кот 🐈
} 

```
