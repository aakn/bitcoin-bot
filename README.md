Bitcoin Bot
-----------

[![CircleCI](https://circleci.com/gh/aakn/bitcoin-bot/tree/master.svg?style=svg&circle-token=bcd3e1adfbfa093acfee5a9c5d03ee4d925b6630)](https://circleci.com/gh/aakn/bitcoin-bot/tree/master)

A bot written in Scala to make some moneyzzz

Usage
-----

### Development

Install [SBT][sbt-install]. (Scala will also be installed for this project.)

```bash
### Running locally
sbt run

### Running tests
sbt test
```

API's
-----

```
# Backtesting API
GET /bot/backtest

```

Tech Stack
----------

1. [Scala][scala]
1. [Play Framework][play-framework]
1. [SBT][sbt]
1. [Akka][akka]
1. [Hystrix][hystrix]


-------

[scala]: http://www.scala-lang.org/
[sbt-install]: http://www.scala-sbt.org/release/docs/Installing-sbt-on-Mac.html
[play-framework]: https://www.playframework.com/documentation/2.6.x/ScalaHome
[sbt]: http://www.scala-sbt.org
[akka]: https://akka.io/docs/
[hystrix]: http://github.com/netflix/hystrix
