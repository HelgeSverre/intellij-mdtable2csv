<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Markdown Table to CSV Changelog

## [Unreleased]

### Added

- Editor context-menu submenu **Markdown Table to CSV**, shown only when the caret is inside a Markdown (GFM) table.
- **Copy as CSV** — copy the table under the caret to the clipboard as CSV.
- **Open in New CSV Tab** — render the table to a new in-memory editor tab without writing to disk.
- **Save as CSV File…** — write the table to a chosen `.csv` file via a save dialog.
- Configurable CSV delimiter (Settings → Tools → Markdown Table to CSV).
- RFC 4180 escaping for cells containing the delimiter, quotes, or newlines.
- Balloon notification when a conversion finds no table content at the caret.
