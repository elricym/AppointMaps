AppointMaps (Android app)
===========

A calendar based on Map.

User can create appointments by simply long click and add a marker on the map. User can also add title and time with the marker. And appointmaps will send the user a noification with a reminer about the time to set off.

==========
Key points of this project:
1. Communication with server
When the user selects a location, the Android app will send the target and current locations to the server (Amazon cloud). And then the server will also send a request to the Google Maps sever to get direction information. When the server gets a response in JSON, the server will parse the JSON and get the duration time between two locations. Eventually, the app can get this value of time from the server and perform to the user. What is worth to mentioning is that Android SDK provides an complete http tool kit called HttpClient, which saves me a lot of time to learn how it really works in Android.

2. Communication between two activities:
This part takes me too much time. Because I didn't really want to use another activity to just get the title and time at first. I chose dialog to solve the problem but it didn't work. In fact, dialogs can't suspend the UI thread so a marker will be constructed before any input in the dialog. Finally, I have to use a activity as the input panel which can suspend the main thread.

3. Google Maps API
It is lucky that Goople provides a lot of samples of code on android. I took much time to read them and chose MapView, Marker and Location those 3 functions to implement. By the way, a part of Google Maps APIs provides android version. Some functions are not included such as Direction APIs (get direction and calculate duration).

4. Notifiation
Very easy to implement, but useful. 
