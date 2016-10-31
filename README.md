# Smart App Rate

![](preview/preview.png)

If the user rates below the threshold rating defined, the dialog will change into a feedback form. Otherwise, It will take the user to the
Google PlayStore.

**If you want the dialog to appear on Nth session of the app, just add the ```java session(N)``` to the dialog builder
 and move the code to the onCreate() method of your Activity class. The dialog will appear when the app is opened for Nth time.**

##How to use
Use the dialog builder class to customize the rating dialog to match your app's UI.

```java
final RatingDialog ratingDialog = new RatingDialog.Builder(this)
                        .icon(drawable)
                        .threshold(3)
                        .title("How was your experience with us?")
                        .titleTextColor(R.color.black)
                        .positiveButtonText("Not Now")
                        .negativeButtonText("Never")
                        .positiveButtonTextColor(R.color.white)
                        .negativeButtonTextColor(R.color.grey_500)
                        .formTitle("Submit Feedback")
                        .formHint("Tell us where we can improve")
                        .formSubmitText("Submit")
                        .formCancelText("Cancel")
                        .ratingBarColor(R.color.yellow)
                        .positiveButtonBackgroundColor(R.drawable.button_selector_positive)
                        .negativeButtonBackgroundColor(R.drawable.button_selector_negative)
                        .onRatingChanged(new RatingDialog.RatingDialogListener() {
                            @Override
                            public void onRatingSelected(float rating, boolean thresholdCleared) {

                            }
                        })
                        .onRatingBarFormSumbit(new RatingDialog.RatingDialogFormListener() {
                            @Override
                            public void onFormSubmitted(String feedback) {

                            }
                        }).build();

                ratingDialog.show();
```

```groovy
dependencies {
    compile 'com.codemybrainsout.rating:ratingdialog:1.0.2'
}
```

#License
```
Copyright (C) 2016 Code My Brains Out

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```