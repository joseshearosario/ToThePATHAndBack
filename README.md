ToThePATHAndBack
================

This project is an Android application that will give directions to your nearest entrance at your nearest Port Authority Trans-Hudson (PATH) subway system.  (PATH runs between New York City and New Jersey) http://en.wikipedia.org/wiki/Port_Authority_Trans-Hudson

In this repository, are the source files for a new Android application. The plan is to have a initial relase that will obtain the user's location and direct them to the nearest entrance of their three nearest PATH stations.

You will need to have a Mapquest app key in order to query the APIs. For obvious reasons, I won't be providing mine in the source code. You also need to download and import the Google Play Services library. This application will not function properly without those two requirements fulfilled.

About half of my code is commented for human eyes. By my next committ I can confidently say that all of my code will have readable comments, as well as anything written after that.

I'd like to give credit to the creators of OSMDroid (https://code.google.com/p/osmdroid/) and OSMBonusPack (https://code.google.com/p/osmbonuspack/)! Without making their jar files public I'd wouldn't be able to include a map in my application, and be able to display directions on that map for the user to follow to the destination. Awesome work, thanks!
