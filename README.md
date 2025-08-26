# ScalarBudget
**ScalarBudget** is a modern, open-source desktop budgeting application built with Java and JavaFX. It features a clean, distraction-free dark mode interface, making it easy on the eyes for extended use. ScalarBudget is designed for simplicity, privacy, and flexibility, allowing you to manage your personal or household finances efficiently on your own device.

## Features
- **Dark Mode Only**: ScalarBudget is always in dark mode, providing a visually comfortable experience for all users, day or night.
- **Simple, Intuitive UI**: Add, edit, and review your budget items with ease. The interface is designed to be minimal and user-friendly.
- **Flexible Periods**: Track expenses and income by day, week, month, year, and more. All totals are normalized to your selected period.
- **Image Support**: Optionally add images to your budget items for quick visual identification. (WIP)
- **Local Data Storage**: All your data is stored locally in JSON files—no cloud, no accounts, no tracking.
- **Open Source**: Licensed under the GPLv3. Contributions and forks are welcome!

## Getting Started
### Prerequisites
- Java 21 or newer
- Maven (for building from source)

## File Format
- Budgets are stored as JSON arrays of items. Each item includes:
  - `name`: String
  - `amount`: Number
  - `period`: String (e.g., "month", "week")
  - `usingImage`: Boolean
  - `imagePath`: String (optional)

## License
This project is licensed under the [GNU GPLv3](https://www.gnu.org/licenses/gpl-3.0.html).

## Contributing
Pull requests, bug reports, and feature suggestions are welcome! Please open an issue or submit a PR on GitHub.

## Authors
- Lemon_Juiced (original author)

## Acknowledgements
- Built with [JavaFX](https://openjfx.io/)
- JSON parsing by [Jackson](https://github.com/FasterXML/jackson)

