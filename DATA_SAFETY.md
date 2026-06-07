# Data Safety — Play Console Answers

Go to: Play Console → Your App → Policy → App content → Data safety

---

## Section 1: Data Collection and Security

**Does your app collect or share any of the required user data types?**
→ YES

**Is all of the user data collected by your app encrypted in transit?**
→ YES (app is fully offline, no network transmission)

**Do you provide a way for users to request that their data is deleted?**
→ YES (Settings → Delete Account wipes all data)

---

## Section 2: Data Types

### Personal info
| Data type | Collected | Shared | Required / Optional | Purpose |
|-----------|-----------|--------|---------------------|---------|
| Name      | Yes       | No     | Required            | App functionality (display in profile) |
| Email address | Yes   | No     | Required            | App functionality (login identity) |

**Storage**: On-device only (SharedPreferences). Never sent to any server.

### Photos and videos
| Data type | Collected | Shared | Required / Optional | Purpose |
|-----------|-----------|--------|---------------------|---------|
| Photos    | Yes       | No     | Optional            | App functionality (item images) |

**Storage**: On-device only (app internal storage). Never sent to any server.

### App activity
| Data type | Collected | Shared | Required / Optional | Purpose |
|-----------|-----------|--------|---------------------|---------|
| App interactions (stock logs) | Yes | No | Required | App functionality (activity history) |

**Storage**: On-device only (Room/SQLite). Never sent to any server.

### Financial info
| Data type | Collected | Shared | Required / Optional | Purpose |
|-----------|-----------|--------|---------------------|---------|
| None      | No        | No     | —                   | — |

---

## Section 3: Data Sharing
**Is any data shared with third parties?**
→ NO

---

## Section 4: Data Handling Practices
- Data is collected: **Yes**
- Data is shared: **No**
- Data can be deleted by user: **Yes** (Settings → Delete Account)
- Data is encrypted in transit: **Yes** (no transit — offline app)
- Data collection is required: **Yes** (name + email for login)

---

## Notes for Play Console form
- Under each data type, select: **"Collected"**, NOT "Shared"
- For all items select purpose: **"App functionality"**
- For all items select: **"Required"** except Photos which is **"Optional"**
- Under "Is this data processed ephemerally?": **No** (stored on device)
- Under "Is this data collected from all users or specific users?": **All users** (name/email), **Some users** (photos)
