# MangaVerse

A dark-themed Android starter app for discovering and reading **both manga and manhwa**.

## Included in this first version
- Search and filter catalogue for Manga / Manhwa
- Story detail pages
- Manga-style page reader and vertical manhwa reader demo
- Bookmarks and reading progress while the app is running
- GitHub Actions workflow to build an APK
- Release workflow for tags such as `v1.0.0`

## Important limitations
This is a starter/demo, not a production content platform. It uses fictional sample titles and placeholder reader panels. Bookmarks and progress are held in app memory and are not permanently stored. Creator uploads, user accounts, cloud database, reviews, and real chapter image hosting are not implemented yet. Only publish chapters you own or have permission to distribute.

## Build an APK through GitHub
1. Upload all files in this project to your `MangaVerse` repository, preserving folders.
2. Open the repository's **Actions** tab and enable workflows if prompted.
3. Select **Build MangaVerse APK** and press **Run workflow**.
4. When it finishes, open that workflow run and download `MangaVerse-debug-apk` under **Artifacts**.
5. To publish the APK in **Releases**, create a release using tag `v1.0.0`. The tag-triggered workflow attaches `app-debug.apk` to that release.

## App details
- Package: `com.mangaverse.app`
- Minimum Android: 7.0 (API 24)
- Build: Gradle, Kotlin, Jetpack Compose
