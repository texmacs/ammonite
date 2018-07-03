# TeXmacs.scala
[![Join the chat at https://gitter.im/texmacs/Lobby](https://badges.gitter.im/texmacs/Lobby.svg)](https://gitter.im/texmacs/Lobby?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

TeXmacs.scala is a bridge library between TeXmacs and the Scala ecosystem.

## Installation
```
git clone git@github.com:texmacs/TeXmacs.scala.git
cd TeXmacs.scala
sbt assembly
cd target
cmake .. && make && make install
```

## Roadmap
### v1.0
A TeXmacs Session(REPL) with the following features:
+ Tab Completion
+ Experimental TeXmacs DSL
