# gitstick

gitstick is a distributed bug tracking tool integrated with git.
It provides a web server and command line interface for managing tickets.

All tickets are stored in the *gitstick* branch by default.
Hence, a project's bugs and source code are both part of the same repository.
gitstick focuses on collaboration, and supports multiple users through the web interface.

gitstick is written in Scala and jQuery.

## Installation

*Java Runtime Environment* (1.6 or higher) is required.

Download the latest binary distribution from the [downloads page](http://github.com/darth10/gitstick/downloads).
Extract the archive, and add the output folder to your PATH variable.

## Usage

Run gitstick in a new directory, or an existing git repository. 
A default user is created depending on your global or local git user configuration; the default password is the user name.

    gitstick

Open `http://localhost:8080/` in a browser, and enter your username and password.

Note that you should use your default username and password the first time you run gitstick in a repository.
In open mode, the password is not checked. To use open mode, specify the `--open` option.

Run `gitstick -h` for all options.

## Documentation

Visit the [wiki](http://darth10.github.com/gitstick) for more information.
To generate the API documentation, use `sbt doc`.

## Contributing

[sbt](http://github.com/harrah/xsbt/wiki) is required.
It's recommended that you use Eclipse or Emacs (with Ensime).

You can start a development server by using the `container:start` sbt command.
Use the `test` command to run all tests.

The project can be packaged into a war using `package-war`, or a jar using `assembly`.

Use the `eclipse` or `ensime generate` sbt commands to generate project files for Eclipse and Emacs respectively.

## Credits

* [Ticgit-Ng](http://github.com/schacon/ticgit/wiki)
* [Scalatra](http://www.scalatra.org/)
* [JGit](http://www.eclipse.org/jgit/)
* [Jetty](http://jetty.codehaus.org/jetty/)

