# gitstick [![Build Status](https://secure.travis-ci.org/darth10/gitstick.png?branch=master)](https://travis-ci.org/darth10/gitstick)

gitstick is a distributed bug tracking tool integrated with git.
It provides a web server and command line interface for managing tickets.
gitstick is based on [Ticgit](https://github.com/jeffWelling/ticgit), and uses the same file format and directory structure.
However, it focuses mainly on collaboration, and supports multiple users through the web interface.

All tickets are stored in the *gitstick* branch by default.
Hence, a project's bugs and source code are both part of the same repository.

gitstick is written in Scala and jQuery.

## Installation

gitstick can be installed as a standalone application or a WAR package. As it uses JRE, it's cross-platform.

### Requirements
1. *Java Runtime Environment* (1.6 or higher) is required.
2. *sbt* (0.11.3 or higher) is required.

### Step-by-step installation
1. Clone the git repository. `git clone git@github.com:darth10/gitstick.git`
2. Go to the cloned directory. `cd gitstick`
3. For a standalone application, run `sbt assembly` to create a JAR, and add the repository to your `PATH` environment variable.
4. For a WAR package, run `sbt package-war` to create a WAR, and deploy the WAR on any Java application server.
Also, if you want to use custom configuration values, modify the in the `web.xml` file (in `src/main/webapp/WEB-INF`) before generating the WAR package.

## Browser compatibility

gitstick is developed primarily for WebKit-based browsers (like Google Chrome) and Gecko-based browsers (like Mozilla Firefox).
Other browsers *may* support it, but it's not recommended.

## Usage

Run `gitstick` in a new directory, or an existing git repository.
A default user is created depending on your git user configuration; the default password is the user name *in verbatim*.
For example, if your git user name is configured as *MyUser*, then your gitstick user name and password are also *MyUser* by default.

    $ gitstick

Open `http://localhost:8080/` in a browser, and enter your username and password. Here's a screenshot of the UI.

![gitstick](https://raw.github.com/darth10/gitstick/master/imgs/screenshot1.png)

Note that you should use your default username and password the first time you run gitstick in a repository.
In open mode, the password is not checked. To use open mode, specify the `--open` option.

Run `gitstick -h` to show all available options.

    $ gitstick -h
    Usage: gitstick [command] [<git repository path>]

    Commands:
        -u, --show-users                 Show users
        -g, --show-tags                  Show tags
        -t, --show-tickets               Show tickets
            --tag=T1[,T2,...]            Filter tickets by tag(s)
            --alltags                    Show tickets with all tags
            --notags                     Show tickets with no tags
            --assigned=A1[,A2,...]       Filter tickets by assignee(s)
            --unassigned                 Show unassigned tickets
            --state=S1[,S2,...]          Filter tickets by state(s)
        -a, --add-users=U1[,U2,...]      Add or edit user(s)
        -r, --rm-users=U1[,U2,...]       Delete user(s)
        -n, --new-ticket=TICKETTITLE     Add a new ticket
        -b, --branch=BRANCH              Switch to a branch
            --empty                      Create empty branch if no branch exists
            --debug                      Debug mode
            --[no-]open                  Open/Secure mode
            --[no-]server                Start/Skip server
        -p, --port=PORT                  Set server port
        -l, --log=LOGDIRPATH             Set log directory path
        -h, --help                       Show help

## Documentation

To generate the API documentation, use `sbt doc`.

## Contributing

[sbt](http://github.com/harrah/xsbt/wiki) is required.
It's recommended that you use Eclipse or Emacs (with Ensime).

* You can start the development server by using the `container:start` sbt command.
* Use the `test` command to run all tests.
* The project can be packaged into a war using `package-war`, or a jar using `assembly`.
* Use the `eclipse` or `ensime generate` sbt commands to generate project files for Eclipse and Emacs respectively.
* For code formatting, use the `eclipse-formatter-javascript.xml` for JavaScript files, and the `eclipse-formatter-scala.properties` formatter for Scala files. These files can be found in the `formatters/` directory. Note that the Scala code formatter is provided by Scala IDE for Eclipse by default, and has the same formatting style as the default Ensime code formatter.

## Credits

* [Ticgit-Ng](https://github.com/jeffWelling/ticgit)
* [Scalatra](http://www.scalatra.org/)
* [JGit](http://www.eclipse.org/jgit/)
* [Jetty](http://jetty.codehaus.org/jetty/)
