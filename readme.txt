- не обновляются задачи исущности при возвращении во view
- при первом создании бд создаются две сущности
- вытекает из первого. При удалении задачи без перезахода он не удаляет потому что у нее не актульный id + почему то во view остается чекбокс пустой
- видел -9999999 id в БД

Пользователь хочет сделать переход между лоадером и контентом более плавным, чтобы не было резкого мелькания. Нужно предложить решения с анимацией.Понял! Проблема в том, что переход между лоадером и контентом **резкий** (мгновенный), поэтому выглядит дёргано.

Давай сделаем **плавную анимацию** между состояниями:

---

## Решение 1: Плавная анимация появления/исчезновения лоадера

```kotlin
@Composable
fun CalendarDialogLoading(
    status: PlanCalendarDialogLoading, 
    onNextContent: @Composable () -> Unit
) {
    val isLoading = when (status) {
        is PlanCalendarDialogLoading.Loading,
        is PlanCalendarDialogLoading.Saving,
        is PlanCalendarDialogLoading.Deleting -> true
        else -> false
    }
    
    AnimatedVisibility(
        visible = isLoading,
        enter = fadeIn(animationSpec = tween(300)),  // ← Плавное появление (300мс)
        exit = fadeOut(animationSpec = tween(150))   // ← Плавное исчезновение (150мс)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 50.dp)
                .height(100.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
    
    // Контент показываем всегда, но скрываем прозрачностью когда загрузка
    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(if (isLoading) 0f else 1f)  // ← Плавное скрытие контента
    ) {
        onNextContent()
    }
}
```

**Результат**: 
- Лоадер появляется плавно (fade in)
- Контент исчезает плавно (fade out)
- Нет резкого переключения ✅

---

## Решение 2: Crossfade между лоадером и контентом

```kotlin
@Composable
fun CalendarDialogLoading(
    status: PlanCalendarDialogLoading, 
    onNextContent: @Composable () -> Unit
) {
    val isLoading = when (status) {
        is PlanCalendarDialogLoading.Loading,
        is PlanCalendarDialogLoading.Saving,
        is PlanCalendarDialogLoading.Deleting -> true
        else -> false
    }
    
    AnimatedContent(
        targetState = isLoading,
        label = "Loading transition",
        transitionSpec = {
            fadeIn(animationSpec = tween(300)) togetherWith 
            fadeOut(animationSpec = tween(300))
        }
    ) { targetIsLoading ->
        if (targetIsLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 50.dp)
                    .height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        } else {
            onNextContent()
        }
    }
}
```

**Результат**: 
- Плавный crossfade между лоадером и контентом
- Выглядит как единая анимация ✅

---

## Решение 3: Shimmer эффект вместо лоадера (современно)

Вместо CircularProgressIndicator показать **shimmer** на пустых элементах:

```kotlin
@Composable
fun CalendarDialogLoading(
    status: PlanCalendarDialogLoading, 
    content: @Composable () -> Unit
) {
    val isLoading = when (status) {
        is PlanCalendarDialogLoading.Loading,
        is PlanCalendarDialogLoading.Saving -> true
        else -> false
    }
    
    if (isLoading) {
        // Shimmer эффект
        Box(
            modifier = Modifier
                .fillMaxSize()
                .shimmer()  // ← Нужна библиотека или свой shimmer
        ) {
            // Пустые прямоугольники на месте контента
            Column {
                Box(modifier = Modifier.fillMaxWidth().height(50.dp).background(Color.Gray))
                Spacer(Modifier.height(16.dp))
                Box(modifier = Modifier.fillMaxWidth().height(100.dp).background(Color.Gray))
            }
        }
    } else {
        content()
    }
}
```

**Но** это сложнее, нужно добавлять shimmer библиотеку.

---

## Мой совет

**Решение 2 (Crossfade)** — самое простое и выглядит хорошо!

Просто замени свой `CalendarDialogLoading` на:

```kotlin
@Composable
fun CalendarDialogLoading(
    status: PlanCalendarDialogLoading, 
    onNextContent: @Composable () -> Unit
) {
    val isLoading = when (status) {
        is PlanCalendarDialogLoading.Loading,
        is PlanCalendarDialogLoading.Saving,
        is PlanCalendarDialogLoading.Deleting -> true
        else -> false
    }
    
    AnimatedContent(
        targetState = isLoading,
        label = "Loading transition",
        transitionSpec = {
            fadeIn(animationSpec = tween(300)) togetherWith 
            fadeOut(animationSpec = tween(300))
        }
    ) { targetIsLoading ->
        if (targetIsLoading) {
            // Лоадер
            Box(...) { CircularProgressIndicator() }
        } else {
            // Контент
            onNextContent()
        }
    }
}
```

**Результат**: Переход будет плавным, без рывков ✅

---

**Вопрос**: Попробовать crossfade или что-то другое?