# Android Architecture - TODO App

Todo App is a project to showcase MVVM architectural approach to developing Android apps.

In this branch you'll find:
*   Kotlin **[Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html)** for background operations.
*   A single-activity architecture, using the **[Navigation component](https://developer.android.com/guide/navigation/navigation-getting-started)** to manage fragment operations.
*   A presentation layer that contains a fragment (View) and a **ViewModel** per screen.
*   Reactive UIs using **LiveData** observables and **Data Binding** (and **View Binding**).
*   A **data layer** with a repository and a local data source that is queried with the operation that can listen for future updates. Thanks to Room Flow integration.

Screenshots:

**Task List**
<p align="center">
<img src="https://github.com/pavankumarthati/TodoApp/blob/master/images/Task_list.jpg" width="300" height="650" alt="Illustration by Pavan Thati"/>
</p>

**Task Detail**
<p align="center">
<img src="https://github.com/pavankumarthati/TodoApp/blob/master/images/task_detail.jpg" width="300" height="650" alt="Illustration by Pavan Thati"/>
</p>

**Task (Add/Edit)**
<p align="center">
<img src="https://github.com/pavankumarthati/TodoApp/blob/master/images/add_edit_task.jpg" width="300" height="650" alt="Illustration by Pavan Thati"/>
</p>

Clone the repository:

```
git clone https://github.com/pavankumarthati/TodoApp.git
```
This step checks out the master branch.
