# Android Architecture Blueprints v2

Todo App is a project to showcase MVVM architectural approach to developing Android apps.

In this branch you'll find:
*   Kotlin **[Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html)** for background operations.
*   A single-activity architecture, using the **[Navigation component](https://developer.android.com/guide/navigation/navigation-getting-started)** to manage fragment operations.
*   A presentation layer that contains a fragment (View) and a **ViewModel** per screen.
*   Reactive UIs using **LiveData** observables and **Data Binding** (and **View Binding**).
*   A **data layer** with a repository and a local data source that is queried with the operation that can listen for future updates. Thanks to Room Flow integration.

Clone the repository:

```
git clone https://github.com/pavankumarthati/TodoApp.git
```
This step checks out the master branch.
