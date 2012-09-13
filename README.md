# gitstick

gitstick is bug tracking tool integrated with git. It provides a web server, along with some command line utilities, for managing tickets.

Coming soon 

## Usage

Run gitstick in a new directory, or an existing git repository

    gitstick

Open `http://localhost:8080/` in a browser.
Enter credentials. In open mode, the password is not checked. A default user is created depending on your global or local git user configuration; the default password is the user name.

## Documentation

Run `sbt doc` to generate API documentation. Visit the [wiki](http://darth10.github.com/gitstick).

## Contributing to gitstick

Start server using the jetty container, or using `sbt run`.

    sbt
    > container:start

Run tests

    sbt test

Generate an eclipse project

    sbt eclipse

Coming soon

## Credits

* Ticgit-Ng
* Scalatra
* JGit

## Copyright

Copyright (c) 2012 Akhil Wali, under MIT license. See [LICENSE](./LICENSE.html).
