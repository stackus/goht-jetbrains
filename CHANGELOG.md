# Changelog

All notable changes to this plugin will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [0.1.8] - 2026-10-17

### Added

- "Generate GoHT File(s)" action in the project view context menu.
- "GoHT LSP" settings pane under Settings > Tools.
- Support for the GoHT v0.9 template syntax:
	- `@ifslot`, `@noslot`, and `@eachslot` directives in Haml, Slim, and EGO
		templates.
	- Go highlighting in `:go` filters.
	- Tailwind-style class shorthand with variants, bracketed values, and
		opacity, such as `.hover:bg-red-500`, `.w-[12px]`, and `.text-red/80`.
	- Framework attribute names such as `@click`, `:class`, `x-on:click`, and
		`data-[state]`.
	- HTML-style boolean and conditional (`?`) attributes.
	- Slim inline tags such as `div: span`.
	- Escaped `\#{}` interpolation.
	- Comments that cover the lines nested beneath them.

### Changed

- Syntax highlighting now uses a native lexer for Haml, Slim, and EGO templates
	and no longer depends on a TextMate grammar.

## [0.1.7] - 2026-05-22

### Added
- Initial release with new lexer; No longer depending on a TextMate grammar
- "Generate GoHT File(s)" action to context menu
- "GoHT LSP" setting pane under Settings -> Tools

## [0.1.2] - 2024-02-21

### Fixed

- Removed the image from the plugin description.

## [0.1.1] - 2024-02-17

### Added

- LSP support.
- Syntax highlighting.
- Code completion.
