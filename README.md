Pokémon App (MVVM + Compose) — Manual Image Caching

An Android app that shows a paginated list of Pokémon, supports favorites, deleting from the list, and a detail screen that downloads & caches 20 images per Pokémon without Glide/Coil/Picasso.
Built with MVVM, Jetpack Compose, Room, and Retrofit.

Uses the public PokéAPI. Images are downloaded manually via HttpURLConnection and decoded with BitmapFactory, then displayed in Compose.

⸻

✨ Features
	•	Pokémon list with infinite scroll (offset/limit pagination).
	•	Manual image loading + disk cache (no Glide/Coil/Picasso).
	•	Detail screen with prefetch & cache of 20 images (sprites/artwork).
	•	Favorites persisted in Room (toggle on/off).
	•	Delete from list (soft delete stored locally; does not affect API).
	•	MVVM + Repository architecture with clear separation of concerns.
	•	Jetpack Compose UI, Navigation between list and detail.

⸻

📦 Tech Stack
	•	Kotlin, Coroutines
	•	Jetpack Compose UI (material3, navigation-compose)
	•	Lifecycle & ViewModel
	•	Room (favorites + soft-deletes)
	•	Retrofit + Gson (PokéAPI)
	•	Manual HTTP + caching for images (via HttpURLConnection, FileOutputStream, BitmapFactory)

⸻

🗂 Project Structure (high level)

app/
 └── src/main/java/com/task/pokemonapp/
     ├── data/
     │   ├── PokemonRepository.kt
     │   ├── PokemonRepositoryImpl.kt
     │   ├── network/
     │   │   ├── PokeApiService.kt
     │   │   └── model/ (PokemonListResponse, PokemonDetail, Sprites, ...)
     │   └── db/
     │       ├── AppDatabase.kt
     │       └── FavoritePokemonDao.kt, FavoritePokemon.kt
     ├── presentation/
     │   ├── pokemonList/
     │   │   ├── PokemonListViewModel.kt
     │   │   └── PokemonListScreen.kt (List + rows)
     │   └── pokemonDetail/
     │       ├── PokemonDetailViewModel.kt
     │       └── PokemonDetailScreen.kt
     ├── MainActivity.kt (Retrofit/Room setup + NavHost)
     └── ui/theme/ (Compose theme)


⸻

🧩 Architecture

MVVM + Repository
	•	View (Compose): PokemonListScreen, PokemonDetailScreen
	•	ViewModel: business/UI logic; exposes StateFlow to screens
	•	Repository: single source of truth; handles pagination, image caching, Room
	•	Data Sources: Retrofit service, Room DAO, file cache

UI (Compose) ──observes──► ViewModel ──calls──► Repository
                                        │
                                ┌───────┴────────┐
                              Retrofit        Room
                              (PokéAPI)       (favorites, deletes)


⸻

🔁 Pagination
	•	Repository.fetchNextPage() uses PokéAPI offset/limit (default 20).
	•	When the list approaches the end, PokemonListViewModel.loadNextPage() is called.
	•	All loaded pages are kept in-memory (simple cache) and exposed as a single list.

You can change page size via PokemonRepositoryImpl(pageSize = ...).

⸻

🖼 Manual Image Loading & Caching
	•	No Glide/Coil/Picasso.
	•	List thumbnails: Repository.getImageBitmapForPokemon(id)
	•	Constructs sprite URL:
https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/{id}.png
	•	Downloads using HttpURLConnection, stores file in context.cacheDir
	•	Decodes via BitmapFactory.decodeFile(...) → ImageBitmap for Compose
	•	In-memory Map<String, ImageBitmap> used for hot reuse
	•	Detail screen: PokemonDetailViewModel.loadPokemonDetail(name)
	•	Fetches detail (/pokemon/{name}), collects multiple sprite/artwork URLs
	•	Prefetches & caches 20 images to disk (same manual approach)
	•	UI reads files and displays via BitmapFactory → ImageBitmap

Cache lives in app cache dir; Android may clear it. Ideal for images we can re-download.

⸻

⭐ Favorites
	•	Room entity: FavoritePokemon(id, name)
	•	Toggle in list row; favorites are observed as a Flow<List<FavoritePokemon>>
	•	Persisted across app restarts

⸻

🗑 Deleting from the List (Soft Delete)
	•	Deleting an item marks it as locally deleted (stored in Room or in a local table/flag).
	•	The list screen filters out deleted items.
	•	This does not delete on the server (PokéAPI is read-only).
	•	You can add a menu action to restore or clear deleted items if desired.

⸻

🧪 Testing (suggested)
	•	ViewModel: Use a fake PokemonRepository to test pagination, toggles, errors.
	•	Repository:
	•	Replace API with a fake service (Retrofit MockWebServer).
	•	Replace DAO with in-memory Room.
	•	For image caching, point context.cacheDir to a temp folder and assert files appear.
	•	UI: Compose UI tests using compose-ui-test.


⸻

🔐 License & Attribution
	•	Data from PokéAPI. Please review their usage guidelines.
	•	Sprite images originate from the PokeAPI sprites repository.
	•	This project is for educational/demo purposes. Add your license file as needed.

⸻

🚀 Roadmap / Ideas
	•	Replace service locator with Hilt DI.
	•	Offline-first: persist list pages and thumbnails in Room/Files for true offline browsing.
	•	Pull-to-refresh.
	•	Search / filters.
	•	Image decode sampling to reduce memory footprint for large artwork.

⸻

❓FAQ

Why not use Coil/Glide?
Requirement: demonstrate manual loading/caching. We implement HTTP download + file cache + bitmap decode by hand.

What does “delete” do?
Local soft delete—hides the item on your device; server data is read-only.

How many images are cached on detail?
Up to 20 per Pokémon (sprites/artwork), saved under cacheDir.

