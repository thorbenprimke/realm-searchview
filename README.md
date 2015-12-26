# A Search View with Result list powered by Realm

A simple and powerful `EditText` and `RecyclerView` UI component.
It's build on top of `RealmRecyclerView` and `RealmBasedRecyclerViewAdapter`.

It's easily customizable via layout attributes and adapter parameters.

##How To Include It:

```
	repositories {
        // ...
        maven { url "https://jitpack.io" }
    }
```

```
	dependencies {
	        compile 'com.github.thorbenprimke:realm-searchview:0.9.4'
	}
```

##Demo

![Screenshot](https://raw.githubusercontent.com/thorbenprimke/realm-searchview/master/extra/screencast-demo-app.gif)

## How To Get Started:

The `RealmSearchView` is a wrapper around a `EditText` and `RealmRecyclerView`. The `RealmSearchAdapter` has added functionality for the filtering of the Realm. 

##RealmSearchView

The snippet below shows how to include the `RealmSearchView` in your  layout file.

```
    <co.moonmonkeylabs.realmsearchview.RealmSearchView
        android:id="@+id/search_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:rsvHint="@string/search_hint"
        />
```

The `RealmSearchView` can be with two attributes:

* `rsvHint`: This is the search box hint. It a string reference.

* `rsvClearDrawable`: This is for the clear drawable. It's a drawable reference.

##RealmSearchAdapter

The adapter does all the filtering. The following list of parameters are available to customize the filtering. 

* `filterKey`: The filterKey is required as it is the columnName that the results are filtered by.

* `useContains`: If true, uses `contains`, otherwise uses `beginsWith`.

* `useCaseSensitive`: If true, ensures that the filter is respecting casing. If false, ignores any casing.

* `sortAscending`: If true, ascending, otherwise descending.

* `sortKey`: The columnName by which the results should be sorted.

* `basePredicate`: The basePredicate is used to filter the results whenever the searchBar is empty.

The `RealmSearchAdapter` has two constructors. One that only takes the `filterKey` parameter and one that takes all parameters.
In addition, the adapter has to be provided with a valid instance of Realm. It is used throughout the life of view to requery the results. 


##Feedback/More Features:
I would love to hear your feedback. Do you find the `RealmSearchView` useful? What functionality are you missing? Open a `Github` issue and let me know. Thanks!


## License
```
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Included dependencies are:
Realm (https://github.com/realm/realm-java)
realm-recyclerview (https://github.com/thorbenprimke/realm-recyclerview)
In Example:
Jackson (https://github.com/FasterXML/jackson-databind/)
Butterknife (https://github.com/JakeWharton/butterknife)
```

