# Toddler Jukebox

**Toddler Jukebox** is a toddler-safe Android app that turns your phone into an interactive music player. Children can play music by simply tapping NFC cards — no need to navigate menus or touch the screen.

Perfect for giving your toddler autonomy over music choices without having to introduce screen time.

## Features

- **Kiosk Mode**

  - Locks the phone into immersive mode to prevent accidental exits or interruptions.

- **Spotify Integration**

  - Uses the Spotify App Remote SDK to play songs from your own Spotify Premium account.

- **NFC Playback Triggers**
  - Scan NFC cards to instantly start a specific track. No screen interaction required.

## Getting Started

### 1. App Installation

- Clone this repo and open it in **Android Studio**
- Connect your Android phone (Pixel or similar NFC-enabled device)
- Click **Run** to deploy the app

> Requires Android 8.0 (API 26) or higher  
> Spotify app must be installed and logged in

---

### 2. Register Your Spotify App

Since this app is not distributed through the Play Store, you'll use your own Spotify Developer App:

1. Visit [Spotify Developer Dashboard](https://developer.spotify.com/dashboard)
2. Click **“Create an App”**
3. Set any name/description
4. Under **Redirect URIs**, add: `toddlerjukebox://callback`
5. Copy your **Client ID**
6. Past the client ID into the `local.properties` file, under the `spotifyClientId` property

### 3. Write NFC Tags

Each NFC card contains the Spotify URI of the track you want to play. Any NFC Tag supporting the NDEF protocol can be used.

Using Spotify:

1.  Find the Track ID of your desired track: - Display the sharable link to a track - Check the copied URL: `https://open.spotify.com/track/<track_id>`
2.  Copy the Spotify Track ID, e.g. `7kM7JiA5Ak58dj6V52onnc`

Using an NFC writer (e.g. NFC Tools): 

3. Tap Write → Add a record → Text
4. Paste the Track ID, e.g. `7kM7JiA5Ak58dj6V52onnc`

> Each tag must contain only 1 NDEF Text record containing only the track ID

### 4. Use the NFC Tags to play your songs!

1. Open the Toddler Jukebox app
2. Bring the NFC Tag close to your phone's NFC detector to start playing music!
