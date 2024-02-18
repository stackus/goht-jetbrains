# GoHT for JetBrains

![Build](https://github.com/stackus/goht-jetbrains/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/23783-goht.svg)](https://plugins.jetbrains.com/plugin/23783-goht)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/23783-goht.svg)](https://plugins.jetbrains.com/plugin/23783-goht)

<!-- Plugin description -->
This plugin provides support for [GoHT](https://goht.dev/).

Features:
- Basic LSP Support
- Syntax Highlighting
- Code Completion

<!-- Plugin description end -->

## Requirements

### LSPs
**GoPls**: You should install the Go extension into VSCode which will install this LSP as well. You may also manually install it by running:

```bash
go install golang.org/x/tools/gopls@latest
```

**GoHT**: You should install the latest version of [GoHT](https://github.com/stackus/goht) which will include the LSP. You may manually install it by running:

```bash
go install github.com/stackus/goht/cmd/goht@latest
```

#### Note
Both LSPs must be in your PATH for this extension to work. You may need to include the `~/go/bin` directory in your PATH if it is not already there.

### Configuration
There is no configuration for this extension.

## Known Issues
- Some syntax highlighting is not quite correct.