# Changelog

All notable changes to Stockify are documented here.

Format follows [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

---

## [1.0.0] — 2025-07-11

### Added
- 🔐 User authentication — register, login, logout with hashed passwords
- 🚀 3-slide onboarding shown on first launch (ViewPager2)
- 🏠 Dashboard with live stats, 7-day value trend chart, recent activity feed
- 📋 Inventory list with real-time search, category filters, and sort options
- ➕ Add Item — name, category, SKU, price, quantity, low stock threshold, supplier, image
- ✏️ Edit Item — update all fields, photo picker, stock change log
- 🗑️ Delete Item — search, select, and confirm deletion
- 📷 Barcode scanner (ZXing) — auto-fills SKU field on scan
- 📊 Reports — pie chart (category mix), bar chart (stock levels), summary cards
- ⚙️ Settings — currency selector (INR/USD/EUR/GBP/JPY), CSV export, JSON backup/restore, change password, logout
- 🔔 Low stock push notifications via WorkManager (runs every 24 hours)
- 📦 Bulk quantity adjustment for multiple items at once
- 💾 Full JSON backup and restore
- 📤 CSV inventory export via FileProvider

### Tech Stack
- Java, Android SDK 35, Room (SQLite), WorkManager, MPAndroidChart, ZXing

---

## Unreleased

### Planned
- Cloud sync / multi-device support
- Dark mode
- Sales tracking and profit reports
- Multiple user accounts
- Item reorder reminders
- Tablet layout support
