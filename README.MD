`BarViewLayout` - ViewGroup который позволяет задавать 
стретегию измерения и лейаутинга с помощью 
вспомогательного класса `BarLayoutUpdater`

1) Добавь в app:build.gradle `implementation 'com.github.grishberg:strategyviewgroup:0.1.0'`
2) Создай основную разметку 
```
<com.grishberg.strategyviewgroup.BarViewGroup
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:custom="http://schemas.android.com/apk/res-auto"
    custom:layoutType="one_row"
    ... >
    
    <!-- child items -->
    
</com.grishberg.strategyviewgroup.BarViewGroup>
```
3) Создай альтернативные разметки с `BarViewGroup` которые будут содержать те же ID у view
4) Для смены разметки используй 
```
val barView = findViewById<BarViewGroup>(R.id.barView)
val twoRowsLayoutUpdater = BarLayoutUpdater(this, R.layout.two_row_bar)

twoRowsLayoutUpdater.applyTo(rootBarView)

```