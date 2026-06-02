# Contributing to Stockify

Thank you for considering contributing to Stockify! 🎉

## How Can I Contribute?

### 🐛 Reporting Bugs

Before creating a bug report, please check existing issues to avoid duplicates.

**When submitting a bug report, include:**
- Clear, descriptive title
- Steps to reproduce the issue
- Expected vs actual behavior
- Screenshots (if applicable)
- Device info: Android version, device model
- App version

### 💡 Suggesting Features

Feature suggestions are welcome! Please include:
- Clear description of the feature
- Why it would be useful
- Examples of how it would work
- Mockups or sketches (optional but helpful)

### 🔧 Pull Requests

1. **Fork the repo** and create a branch from `main`
2. **Make your changes**:
   - Follow the existing code style (Java conventions)
   - Add comments for complex logic
   - Test your changes thoroughly
3. **Commit your changes**:
   - Use clear, descriptive commit messages
   - Follow conventional commits: `feat:`, `fix:`, `docs:`, `style:`, `refactor:`, `test:`, `chore:`
4. **Push to your fork** and submit a pull request

**PR Requirements:**
- ✅ Code compiles without errors
- ✅ App runs on Android 7.0+ (API 24+)
- ✅ No breaking changes to existing features (unless discussed)
- ✅ Update README if adding new features
- ✅ Screenshots/recordings for UI changes

## Development Setup

```bash
# Clone your fork
git clone https://github.com/YOUR_USERNAME/Stockify.git
cd Stockify

# Open in Android Studio
# Let Gradle sync complete
# Run on device or emulator
```

### Project Structure
- `app/src/main/java/com/example/v2/` — Java source files
- `app/src/main/res/` — UI layouts, drawables, strings
- `app/build.gradle.kts` — App-level dependencies
- `gradle/libs.versions.toml` — Dependency version catalog

### Code Style
- Use **4 spaces** for indentation (no tabs)
- Opening braces on same line: `if (condition) {`
- Use meaningful variable names
- Keep methods focused and small
- Add JavaDoc for public methods

## Commit Message Format

```
<type>: <subject>

<body (optional)>
```

**Types:**
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code formatting (no logic change)
- `refactor`: Code restructuring
- `test`: Adding tests
- `chore`: Build process, dependencies

**Examples:**
```
feat: add dark mode support to dashboard

fix: resolve crash on barcode scan with no camera permission

docs: update README with installation instructions
```

## Questions?

Open an issue with the `question` label or reach out via GitHub Discussions.

---

**Thank you for contributing!** 💙
