# Play Store Submission Checklist

## ✅ Code / Build — Done
- [x] `applicationId = "com.stockify.inventory"` (no com.example)
- [x] `versionCode = 1`, `versionName = "1.0.0"`
- [x] `targetSdk = 35`, `minSdk = 24`
- [x] `isMinifyEnabled = true`, `isShrinkResources = true` (ProGuard enabled)
- [x] Release signing config via `local.properties`
- [x] `android:usesCleartextTraffic="false"`
- [x] `allowBackup="false"`
- [x] All activities `exported="false"` except launcher
- [x] `READ_MEDIA_IMAGES` guarded with `minSdkVersion="33"`
- [x] `android.hardware.camera` marked `required="false"`
- [x] FileProvider used for all file sharing (no raw file:// URIs)
- [x] Passwords hashed with PBKDF2 + salt (never plaintext)
- [x] Theme named `Theme.Stockify` (not `Theme.V2`)
- [x] All user-visible strings in `strings.xml` (fully localisation-ready)
- [x] Custom app icon (warehouse + trend line, dark gradient)
- [x] Privacy policy URL live: https://varadha9.github.io/stockify-privacy/
- [x] Delete Account feature in Settings (Play policy Dec 2023)
- [x] Data Safety notice shown at registration

---

## ✅ Play Console — Store Listing

### Required fields
- [ ] **App name**: Stockify — Inventory Manager
- [ ] **Short description** (80 chars max):
  > Track stock, scan barcodes, get low-stock alerts. Offline inventory for small businesses.
- [ ] **Full description** (4000 chars max): see `STORE_DESCRIPTION.md`
- [ ] **App icon** (512x512 PNG): export from `ic_launcher_foreground.xml` + background
- [ ] **Feature graphic** (1024x500 PNG): create banner with app name + icon
- [ ] **Screenshots** (min 2, max 8 per device type): phone screenshots required
- [ ] **Category**: Business (or Productivity)
- [ ] **Email address**: varadmandhare924@gmail.com
- [ ] **Privacy policy URL**: https://varadha9.github.io/stockify-privacy/

---

## ✅ Play Console — App Content

### Data Safety (fill this in Play Console)
See `DATA_SAFETY.md` for exact answers.

### Content Rating
- Complete the IARC questionnaire
- Expected rating: **Everyone** (no violence, no user-generated content, no ads)

### Target Audience
- Target age: **18+** (business app)
- App not directed at children → select "Adults"

### Ads
- Does your app contain ads? **No**

---

## ✅ Play Console — Release

- [ ] Create release in **Internal Testing** first
- [ ] Upload signed AAB (`./gradlew bundleRelease`)
- [ ] Fix any pre-launch report issues
- [ ] Promote to **Production** once satisfied

---

## Build commands

```bash
# Debug APK
./gradlew assembleDebug

# Release AAB (recommended for Play Store)
./gradlew bundleRelease

# Release APK (for direct distribution)
./gradlew assembleRelease
```
