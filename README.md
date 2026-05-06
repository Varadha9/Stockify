# 📦 MrManager — Retail Inventory App

> A modern, offline-first Android inventory management app built for small retail businesses. Track stock, scan barcodes, get low-stock alerts, and export reports — all from your phone.

---

## 📱 Screenshots

> _Add screenshots here once available_

---

## ✨ Features

### 🔐 Authentication
- Register with name, email, and password (hashed with salt using `PasswordUtils`)
- Persistent login via `SharedPreferences`
- Change password from Settings with current password verification
- Secure logout that clears all session data

### 🚀 Onboarding
- 3-slide onboarding screen shown only on first launch
- Swipeable `ViewPager2` slides with animated dot indicators
- Skip or step through slides with Next / Get Started buttons

### 🏠 Dashboard
- Time-based greeting (Good Morning / Afternoon / Evening)
- Live stats: total items in stock, low stock count, total inventory value
- 7-day inventory value trend line chart (MPAndroidChart, cubic bezier)
- Recent activity feed from stock change logs
- Quick action cards: Add Item, View Inventory, Scan Barcode, Delete Item
- Bottom nav badges showing total item count and low stock count

### 📋 Inventory Management
- Full list of all inventory items with search and real-time filtering
- Category filter chips (dynamically generated from your data)
- Sort by: Name A–Z, Low stock first, Quantity (low/high), Price (low/high)
- Tap any item to edit it directly via `EditItemActivity`
- Bulk quantity adjustment: select multiple items, enter a delta (e.g. `+5` or `-2`), confirm and apply
- Export full inventory to CSV and share via any app
- Empty state UI when no items match filters

### ➕ Add / ✏️ Edit Items
- Fields: Name, Category, SKU, Price, Quantity, Low Stock Threshold, Supplier, Description, Image
- SKU can be auto-filled from a barcode scan
- Image picker from device gallery
- All changes logged to `StockLog` for activity history

### 🗑️ Delete Items
- Search and select items to delete
- Confirmation dialog before deletion

### 📷 Barcode Scanner
- Camera-based barcode scanning via ZXing (`zxing-android-embedded`)
- Portrait-locked scanner using `CaptureActivityPortrait`
- Scanned barcode auto-fills the SKU field in Add Item
- Camera permission handled at runtime

### 📊 Reports
- Summary cards: total units, total inventory value, low stock count
- Category breakdown (units per category)
- Supplier breakdown (units per supplier)
- Recent activity log (last 8 stock events with timestamps)
- Pie chart: inventory distribution by category
- Bar chart: stock levels for top 8 items

### ⚙️ Settings
- User profile card with avatar initial, name, and email
- Currency selector: INR ₹, USD $, EUR €, GBP £, JPY ¥ (persisted globally)
- Export inventory as CSV
- Backup full data (inventory + logs + snapshots) to a JSON file
- Restore from a JSON backup (with confirmation dialog)
- Change password
- Privacy Policy link
- Logout

### 🔔 Low Stock Notifications
- Background `WorkManager` job runs every 24 hours
- Sends a push notification listing items below their low stock threshold
- Notification includes a quick "Update Stock" action button
- Handles `POST_NOTIFICATIONS` permission for Android 13+

---

## 🏗️ Architecture & Tech Stack

| Layer | Technology |
|---|---|
| Language | Java |
| Min SDK | 24 (Android 7.0) |
| Target SDK | 35 (Android 15) |
| Database | Room (SQLite) |
| Background Work | WorkManager |
| Charts | MPAndroidChart v3.1.0 |
| Barcode Scanning | ZXing Android Embedded 4.3.0 |
| UI Components | Material Design 3, ConstraintLayout, RecyclerView, ViewPager2 |
| Navigation | Bottom Navigation + Fragment transactions |
| Threading | Custom `AppExecutor` (single background thread) |
| Data Export | CSV via `FileProvider`, JSON backup/restore |

---

## 🗄️ Database Schema (Room)

### `inventory_table` — `InventoryItem`
| Column | Type | Description |
|---|---|---|
| `id` | INTEGER (PK) | Auto-generated |
| `name` | TEXT | Item name |
| `category` | TEXT | Category label |
| `sku` | TEXT | Stock Keeping Unit / barcode |
| `price` | REAL | Unit price |
| `quantity` | INTEGER | Current stock count |
| `lowStockThreshold` | INTEGER | Alert threshold |
| `supplier` | TEXT | Supplier name |
| `description` | TEXT | Item notes |
| `imagePath` | TEXT | Local image file path |

### `stock_log` — `StockLog`
Tracks every add, edit, delete, and bulk adjustment with item name, action type, detail, and timestamp.

### `value_snapshot` — `ValueSnapshot`
Daily snapshots of total inventory value, used to render the 7-day trend chart on the dashboard.

---

## 📁 Project Structure

```
app/src/main/java/com/example/v2/
├── MainActivity.java              # Bottom nav host, badge updates
├── LoginActivity.java             # Auth entry point, session check
├── RegisterActivity.java          # New user registration
├── OnboardingActivity.java        # First-launch onboarding slides
│
├── DashboardFragment.java         # Stats, trend chart, recent activity
├── InventoryFragment.java         # Item list, search, filter, bulk ops
├── ReportsFragment.java           # Charts and analytics
├── SettingsFragment.java          # Profile, currency, backup, logout
│
├── AddItemActivity.java           # Add new inventory item
├── EditItemActivity.java          # Edit existing item
├── DeleteItemActivity.java        # Delete item(s)
├── ScanActivity.java              # Barcode scanner
│
├── InventoryItem.java             # Room entity
├── StockLog.java                  # Room entity — activity log
├── ValueSnapshot.java             # Room entity — daily value snapshot
│
├── InventoryDao.java              # DB queries for inventory
├── StockLogDao.java               # DB queries for logs
├── ValueSnapshotDao.java          # DB queries for snapshots
├── InventoryDatabase.java         # Room database singleton
│
├── InventoryAdapter.java          # RecyclerView adapter (search, sort, select)
├── RecentActivityAdapter.java     # Dashboard activity feed adapter
│
├── InventoryAnalytics.java        # Records daily value snapshots
├── InventoryBackupManager.java    # JSON backup & restore logic
├── CsvExporter.java               # CSV export via FileProvider
├── CurrencyFormatter.java         # Currency symbol formatting
├── PasswordUtils.java             # Password hashing & verification
├── AppExecutor.java               # Background thread executor
├── LowStockWorker.java            # WorkManager low stock notifier
└── CaptureActivityPortrait.java   # Portrait barcode scanner activity
```

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog or newer
- Android device or emulator running API 24+
- JDK 11

### Clone & Run

```bash
git clone https://github.com/Varadha9/MrManager-RetailApp.git
cd MrManager-RetailApp
```

1. Open the project in Android Studio
2. Let Gradle sync complete
3. Run on a device or emulator (`Shift+F10`)

### Build Release APK

```bash
./gradlew assembleRelease
```

Or use the included `build-release.bat` on Windows.

> ⚠️ Configure your keystore in `app/build.gradle.kts` under `signingConfigs` before publishing.

---

## 📦 Dependencies

```toml
# UI
androidx.appcompat          1.7.0
com.google.android.material 1.11.0
androidx.constraintlayout   2.2.1
androidx.recyclerview       1.3.2
androidx.viewpager2         1.1.0
androidx.cardview           1.0.0

# Database
androidx.room               2.6.1

# Background
androidx.work               2.9.0

# Charts
com.github.PhilJay:MPAndroidChart  v3.1.0

# Barcode
com.journeyapps:zxing-android-embedded  4.3.0
```

---

## 🔒 Permissions

| Permission | Purpose |
|---|---|
| `CAMERA` | Barcode scanning |
| `READ_MEDIA_IMAGES` | Item image picker (API 33+) |
| `READ_EXTERNAL_STORAGE` | Item image picker (API ≤ 32) |
| `POST_NOTIFICATIONS` | Low stock push notifications (API 33+) |

---

## 🛡️ Security

- Passwords are hashed (not stored in plain text) using `PasswordUtils`
- No cleartext HTTP traffic (`android:usesCleartextTraffic="false"`)
- CSV and JSON files are shared via `FileProvider` (no direct file URI exposure)
- All activities except `LoginActivity` are `exported="false"`

---

## 🗺️ Roadmap

- [ ] Cloud sync / multi-device support
- [ ] Multiple user accounts
- [ ] Sales tracking and profit reports
- [ ] Item reorder reminders
- [ ] Dark mode support
- [ ] Tablet / large screen layout

---

## 📄 License

This project is for personal and educational use. All rights reserved © Varadha9.

---

## 🙋 Author

**Varadha** — [GitHub](https://github.com/Varadha9)
